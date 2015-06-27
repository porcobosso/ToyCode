# coding=gbk
import pandas as pd
import math

data=pd.read_csv('delicious.dat',sep='\t',header=None)

class SimpleTagBased:
    #{�û�1:{��ǩA��5,...}...}
    user_tag=dict()
    #�û����۹�����Ʒ�����Ƽ�ʱ���˵���Щ��Ʒ
    user_item=dict()
    #ÿ����ǩ��Ӧ����Ʒ
    tag_item=dict()
    tag_count=dict()
    item_count=dict()
    item_tag=dict()
    
    def __init__(self,data):
        for user,item,tags in data.itertuples(index=False):
            if type(tags)==float:
                continue
            if user not in self.user_item:
                self.user_item[user]=list()
            self.user_item[user].append(item)
            
            if item not in self.item_count:
                self.item_count[item]=0
            self.item_count[item]+=1
            
            if item not in self.item_tag:
                self.item_tag[item]=dict()
            
            tags=tags.split(' ')
            if user not in self.user_tag:
                self.user_tag[user]=dict()
            
            for tag in tags:
                tag = tag.lower()
                if tag not in self.user_tag[user]:
                    self.user_tag[user][tag]=0
                self.user_tag[user][tag]+=1
                
                if tag not in self.tag_item:
                    self.tag_item[tag]=dict()
                if item not in self.tag_item[tag]:
                    self.tag_item[tag][item]=0
                self.tag_item[tag][item]+=1
                
                if tag not in self.tag_count:
                    self.tag_count[tag]=0
                self.tag_count[tag]+=1
                
                if tag not in self.item_tag[item]:
                    self.item_tag[item][tag]=0
                self.item_tag[item][tag]+=1
                
    def recommend(self,user):
        viewedItem=self.user_item[user]
        rank=dict()
        
        utags = self.user_tag[user]
        for tag,weight in utags.items():
            for item,wt in self.tag_item[tag].items():
                if item in viewedItem:
                    continue
                if item not in rank:
                    rank[item]=0
                #�û�user����Ʒitem��ϲ�ó̶�
                rank[item]+=weight*1.0/math.log(1+self.tag_count[tag])*wt/math.log(1+self.item_count[item])
        rank=[a[0] for a in sorted(rank.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]];
        res = []
        for item in rank:
            #��Ʒ���������10����ǩ��Ϊ��Ʒ������
            res.append([a[0] for a in sorted(self.item_tag[item].items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:10]])
        #�û���õ�10����ǩ��Ϊ�û���Ȥ����
        userdesc = [a[0] for a in sorted(utags.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:10]]
        return (userdesc,res)
stb = SimpleTagBased(data=data)
userdesc,res = stb.recommend(104)
print userdesc
print res