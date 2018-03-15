# coding:utf8
from pyspark import SparkContext
from pyspark import RDD
import numpy as np
from numpy.random import RandomState

import sys
reload(sys)
#设置默认编码为utf8，从spark rdd中取出中文词汇时需要编码为中文编码，否则不能保存成功
sys.setdefaultencoding('utf8')


"""
总结：
broadcast变量和需要用到broadcast变量的方法需要位于同一作用域

broadcast变量的unpersist会将存储broadcast变量的文件立即删除，
    而此时rdd并未被触发执行，当rdd执行时会发现没有broadcast变量，所以会报错，
    建议只在程序运行完成后，将broadcast变量 unpersist
"""

class PLSA:

    def __init__(self, data, sc, k, is_test=False, max_itr=1000, eta=1e-6):

        """
        init the algorithm

        :type data RDD
        :param data: 输入文章rdd，每条记录为一系列用空格分隔的词，如"我 爱 蓝天 我 爱 白云"
        :type max_itr int
        :param max_itr: 最大EM迭代次数
        :type is_test bool
        :param is_test: 是否为测试,是则rd = RandomState(1)，否则 rd = RandomState()
        :type sc SparkContext
        :param sc: spark context
        :type k int
        :param k : 主题个数
        :type eta float
        :param : 阈值，当log likelyhood的变化小于eta时，停止迭代
        :return : PLSA object
        """
        self.max_itr = max_itr
        self.k = sc.broadcast(k)
        self.ori_data = data.map(lambda x: x.split(' '))
        self.sc = sc
        self.eta = eta

        self.rd = sc.broadcast(RandomState(1) if is_test else RandomState())

    def train(self):

        #获取词汇字典 ，如{"我":1}
        self.word_dict_b = self._init_dict_()
        #将文本中词汇，转成词典中的index
        self._convert_docs_to_word_index()
        #初始化，每个主题下的单词分布
        self._init_probility_word_topic_()

        pre_l= self._log_likelyhood_()

        print "L(%d)=%.5f" %(0,pre_l)

        for i in range(self.max_itr):
            #更新每个单词主题的后验分布
            self._E_step_()
            #最大化下界
            self._M_step_()
            now_l = self._log_likelyhood_()

            improve = np.abs((pre_l-now_l)/pre_l)
            pre_l = now_l

            print "L(%d)=%.5f with %.6f%% improvement" %(i+1,now_l,improve*100)
            if improve <self.eta:
                break

    def _M_step_(self):
        """
        更新参数 p(z=k|d),p(w|z=k)
        :return: None
        """
        k = self.k
        v = self.v

        def update_probility_of_doc_topic(doc):
            """
            更新文章的主题分布
            """
            doc['topic'] = doc['topic'] - doc['topic']

            topic_doc = doc['topic']
            words = doc['words']
            for (word_index,word) in words.items():
                topic_doc += word['count']*word['topic_word']
            topic_doc /= np.sum(topic_doc)

            return {'words':words,'topic':topic_doc}

        self.data = self.data.map(update_probility_of_doc_topic)
        """
        rdd相当于一系列操作过程的结合，且前面的操作过程嵌套在后面的操作过程里，当这个嵌套超过大约60，spark会报错，
        这里每次M step都通过cache将前面的操作执行掉
        """
        self.data.cache()

        def update_probility_word_given_topic(doc):
            """
            更新每个主题下的单词分布
            """
            probility_word_given_topic = np.matrix(np.zeros((k.value,v.value)))

            words = doc['words']
            for (word_index,word) in words.items():
                probility_word_given_topic[:,word_index] += np.matrix(word['count']*word['topic_word']).T

            return probility_word_given_topic

        probility_word_given_topic = self.data.map(update_probility_word_given_topic).sum()
        probility_word_given_topic_row_sum = np.matrix(np.sum(probility_word_given_topic,axis=1))

        #使每个主题下单词概率和为1
        probility_word_given_topic = np.divide(probility_word_given_topic,probility_word_given_topic_row_sum)

        self.probility_word_given_topic = self.sc.broadcast(probility_word_given_topic)

    def _E_step_(self):
        """
        更新隐变量 p(z|w,d)-给定文章，和单词后，该单词的主题分布
        :return: None
        """
        probility_word_given_topic = self.probility_word_given_topic
        k = self.k

        def update_probility_of_word_topic_given_word(doc):
            topic_doc = doc['topic']
            words = doc['words']

            for (word_index,word) in words.items():
                topic_word = word['topic_word']
                for i in range(k.value):
                    topic_word[i] = probility_word_given_topic.value[i,word_index]*topic_doc[i]
                #使该单词各主题分布概率和为1
                topic_word /= np.sum(topic_word)
            return {'words':words,'topic':topic_doc}

        self.data = self.data.map(update_probility_of_word_topic_given_word)

    def  _init_probility_word_topic_(self):
        """
        init p(w|z=k)
        :return: None
        """
        #dict length(words in dict)
        m = self.v.value

        probility_word_given_topic = self.rd.value.uniform(0,1,(self.k.value,m))
        probility_word_given_topic_row_sum = np.matrix(np.sum(probility_word_given_topic,axis=1)).T

        #使每个主题下单词概率和为1
        probility_word_given_topic = np.divide(probility_word_given_topic,probility_word_given_topic_row_sum)

        self.probility_word_given_topic = self.sc.broadcast(probility_word_given_topic)

    def _convert_docs_to_word_index(self):

        word_dict_b = self.word_dict_b
        k = self.k
        rd = self.rd
        '''
        I wonder is there a better way to execute function with broadcast varible
        '''
        def _word_count_doc_(doc):
            wordcount ={}
            word_dict = word_dict_b.value
            for word in doc:
                if wordcount.has_key(word_dict[word]):
                    wordcount[word_dict[word]]['count'] += 1
                else:
                    #first one is the number of word occurance
                    #second one is p(z=k|w,d)
                    wordcount[word_dict[word]] = {'count':1,'topic_word': rd.value.uniform(0,1,k.value)}

            topics = rd.value.uniform(0, 1, k.value)
            topics = topics/np.sum(topics)
            return {'words':wordcount,'topic':topics}

        self.data = self.ori_data.map(_word_count_doc_)

    def _init_dict_(self):
        """
        init word dict of the documents,
        and broadcast it
        :return: None
        """
        words = self.ori_data.flatMap(lambda d: d).distinct().collect()
        word_dict = {w: i for w, i in zip(words, range(len(words)))}
        self.v = self.sc.broadcast(len(word_dict))
        return self.sc.broadcast(word_dict)

    def _log_likelyhood_(self):
        probility_word_given_topic = self.probility_word_given_topic
        k = self.k

        def likelyhood(doc):
            l = 0.0
            topic_doc = doc['topic']
            words = doc['words']

            for (word_index,word) in words.items():
                l += word['count']*np.log(np.matrix(topic_doc)*probility_word_given_topic.value[:,word_index])
            return l
        return self.data.map(likelyhood).sum()

    def save(self,f_word_given_topic,f_doc_topic):
        """
        保存模型结果 TODO 添加分布式保存结果
        :param f_word_given_topic: 文件路径，用于给定主题下词汇分布
        :param f_doc_topic: 文件路径，用于保存文档的主题分布
        :return:
        """
        doc_topic = self.data.map(lambda x:' '.join([str(q) for q in x['topic'].tolist()])).collect()
        probility_word_given_topic = self.probility_word_given_topic.value

        word_dict = self.word_dict_b.value
        word_given_topic = []

        for w,i in word_dict.items():
            word_given_topic.append('%s %s' %(w,' '.join([str(q[0]) for q in probility_word_given_topic[:,i].tolist()])))

        f1 = open (f_word_given_topic, 'w')

        for line in word_given_topic:
            f1.write(line)
            f1.write('\n')
        f1.close()

        f2 = open (f_doc_topic, 'w')

        for line in doc_topic:
            f2.write(line)
            f2.write('\n')
        f2.close()
