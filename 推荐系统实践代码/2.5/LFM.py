# coding=gbk
'''
ʵ��������ģ�ͣ�����ʽ���ݽ����Ƽ�
1.�����������ɸ�����
  -�����������൱��������
  -��ƷԽ���ţ�Խ�п��ܳ�Ϊ������
2.ʹ������ݶ��½��������²���
'''

import numpy as np
import pandas as pd
import random
from sklearn import cross_validation

class LFM():
    
    '''
            ��ʼ��������ģ��
           ������
    *F  �������ĸ���
    *N  ��������
    *data ѵ������,Ҫ��Ϊpandas��dataframe
    *alpha ����ݶ��½���ѧϰ����
    *r ���򻯲���
    *ratio ������/����������
    '''
    def __init__(self,data,F=100,N=1000,alpha=0.02,r=0.01,ratio=1):
        self.F=F
        self.N=N
        self.alpha=alpha
        self.r=r
        self.data=data
        self.ratio=ratio
    
    '''
          ��ʼ����Ʒ�أ���Ʒ������Ʒ���ֵĴ����������жȳ�����
    '''    
    def InitItemPool(self):
        self.itemPool=[]
        groups = self.data.groupby([1])
        for item,group in groups:
            for i in range(group.shape[0]):
                self.itemPool.append(item)
    
    '''
          ��ȡÿ���û���Ӧ����Ʒ���û����������Ʒ���б���
          {�û�1:[��ƷA����ƷB����ƷC],
                              �û�2:[��ƷD����ƷE����ƷF]...}
    ''' 
    def user_item(self,data):
        ui = dict()
        groups = data.groupby([0])
        for item,group in groups:
            ui[item]=set(group.ix[:,1])
        
        return ui
    
    '''
           ��ʼ����������Ӧ�Ĳ���
    numpy��array�洢������ʹ��dict�洢ÿ���û�����Ʒ����Ӧ����
    '''
    def initParam(self):
        users=set(self.data.ix[:,0])
        items=set(self.data.ix[:,1])
        
        self.Pdict=dict()
        self.Qdict=dict()
        for user in users:
            self.Pdict[user]=len(self.Pdict)
        
        for item in items:
            self.Qdict[item]=len(self.Qdict)
        
        self.P=np.random.rand(self.F,len(users))/10
        self.Q=np.random.rand(self.F,len(items))/10
    
    '''
            ʹ������ݶ��½��������²���
    '''
    def stochasticGradientDecent(self):
        alpha=self.alpha
        for i in range(self.N):
            for user,items in self.ui.items():
                ret=self.RandSelectNegativeSamples(items)
                for item,rui in ret.items():
                   p=self.P[:,self.Pdict[user]]
                   q=self.Q[:,self.Qdict[item]]
                   eui=rui-sum(p*q)
                   tmp=p+alpha*(eui*q-self.r*p)
                   self.Q[:,self.Qdict[item]]+=alpha*(eui*p-self.r*q)
                   self.P[:,self.Pdict[user]]=tmp
            alpha*=0.9
            print i
            
    def Train(self):
        self.InitItemPool()
        self.ui = self.user_item(self.data)
        self.initParam()
        self.stochasticGradientDecent()
    
    def Recommend(self,user,k):
        items=self.ui[user]
        p=self.P[:,self.Pdict[user]]
        
        rank = dict()
        for item,id in self.Qdict.items():
            if item in items:
                continue
            q=self.Q[:,id];
            rank[item]=sum(p*q)
        return sorted(rank.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:k-1];
    '''
            ���ɸ�����
    '''
    def RandSelectNegativeSamples(self,items):
        ret=dict()
        for item in items:
            #��������������Ϊ1
            ret[item]=1
        #��������������������
        negtiveNum = int(round(len(items)*self.ratio))
        
        N = 0
        while N<negtiveNum:
            item = self.itemPool[random.randint(0, len(self.itemPool) - 1)]
            if item in items:
                #������û��Ѿ�ϲ������Ʒ�б��У�����ѡ
                continue
            N+=1
            #����������Ϊ0
            ret[item]=0
        return ret

data=pd.read_csv('../data/ratings.dat',sep='::',nrows=10000,header=None)
data=data.ix[:,0:1]

train,test=cross_validation.train_test_split(data,test_size=0.2)
train = pd.DataFrame(train)
test = pd.DataFrame(test)

lfm = LFM(data=train)
lfm.Train()
lfm.Recommend(1, 10)