# coding=gbk
'''
《推荐系统实践》  2.4.2 
学习item collaborative filter代码
Created on 2015-4-4
@author: porco_std
'''
import pandas as pd
from sklearn import cross_validation
import math

class ItemCF():
    def __init__(self,data,k):
        self.train=data
        self.k=k
        self.ui=self.user_item(self.train)
        self.iu = self.item_user(self.train)
        self.itemSimilarityMatrix()
    '''
          获取每个商品对应的用户（购买过该商品的用户）列表，如
          {商品A:[用户1，用户2，用户3],
                              商品B:[用户3，用户4，用户5]...}
    '''    
    def item_user(self,data):
        iu = dict()
        groups = data.groupby([1])
        for item,group in groups:
            iu[item]=set(group.ix[:,0])
        
        return iu
    
    '''
          获取每个用户对应的商品（用户购买过的商品）列表，如
          {用户1:[商品A:评分，商品B:评分，商品C:评分],
                              用户2:[商品D:评分，商品E:评分，商品F:评分]...}
    '''    
    def user_item(self,data):
        ui = dict()
        groups = data.groupby([0])
        for item,group in groups:
            ui[item]=dict()
            for i in range(group.shape[0]):
                ui[item][group.iget_value(i,1)]=group.iget_value(i,2)
        
        return ui
    
    def itemSimilarityMatrix(self):
        matrix = dict()
        for u,ps in self.ui.items():
            denominator = 1.0/math.log(1+len(ps));
            for p1 in ps.keys():
                for p2 in ps.keys():
                    if p1==p2:
                        continue
                    if p1 not in matrix:
                        matrix[p1]=dict()
                    if p2 not in matrix[p1]:
                        matrix[p1][p2]=0
                    
                    matrix[p1][p2] += denominator/math.sqrt(len(self.iu[p1])*len(self.iu[p2]))
        
        for p in matrix.keys():
            #对每个商品i,将其他商品j按其与i的相似度从大大小排序
            matrix[p] = sorted(matrix[p].items(),lambda x,y:cmp(x[1],y[1]),reverse=True);
            #正规化
            matrix[p] = [(x[0],x[1]/matrix[p][0][1]) for x in matrix[p]]
        self.M=matrix
    '''
            对用户user进行推荐
    '''
    def getRecommend(self,user):
        rank = dict()
        uItem=self.ui[user]#获取用户购买历史
        for uproduct,urank in uItem.items():
            uproduct_simi = self.M[uproduct][0:self.k]
            for p_simi in uproduct_simi:
                p = p_simi[0]
                simi = p_simi[1]
                if p in uItem:
                    continue
                if p not in rank:
                    rank[p]=0
                rank[p]+=urank*simi
        return rank
    
    def estimate(self,test):
        ui_test=self.user_item(test)
        unions = 0
        sumRec = 0
        sumTes = 0
        
        itemrec = set() 
        
        sumPopularity = 0
        for user in self.ui.keys():
            rank=self.getRecommend(user);
            itemtest = set()
            if user in ui_test:
                itemtest = set(ui_test[user].keys())
            sumRec += len(rank)
            sumTes += len(itemtest)
            for recItem in rank:
                sumPopularity += math.log(1+len(self.iu[recItem]))
                itemrec.add(recItem)
                if recItem in itemtest:
                    unions += 1;
        return unions*1.0/sumRec,unions*1.0/sumTes,len(itemrec)*1.0/len(self.iu.keys()),sumPopularity*1.0/sumRec
        
    

class Estimator():
    def __init__(self,data,ks,test_size=0.125,random_state=75):
        self.data=data
        self.ks=ks
        self.test_size=test_size
        self.random_state=random_state
    
    def estimate(self):
        precision=[]
        recall=[]
        coverage=[]
        novelty=[]
        for k in self.ks:
            train,test=cross_validation.train_test_split(data,test_size=self.test_size,random_state=self.random_state)
            train = pd.DataFrame(train)
            test = pd.DataFrame(test)
            
            rec = ItemCF(train,k)
            prec,reca,cover,nove=rec.estimate(test)
            precision.append(prec)
            recall.append(reca)
            coverage.append(cover)
            novelty.append(nove)
            
        return precision,recall,coverage,novelty
             
data=pd.read_csv('A:/anaconda/workspace/推荐系统实践/data/ratings.dat',sep='::',nrows=80000,header=None)
data=data.ix[:,0:2]

estimator=Estimator(data=data,ks=[50])
print estimator.estimate()