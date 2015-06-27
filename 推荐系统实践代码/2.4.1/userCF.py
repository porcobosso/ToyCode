'''
Created on 2015-3-30

@author: porco_std
'''
import pandas as pd
from sklearn import cross_validation
import math

class recommander():
    
    def __init__(self,data,k):
        self.train=data
        self.k=k
        self.ui=self.user_item(self.train)
        self.iu = self.item_user(self.train)
        self.simiMatrix=self.similarityMatrix()
    
    def item_user(self,data):
        iu = dict()
        groups = data.groupby([1])
        for item,group in groups:
            iu[item]=set(group.ix[:,0])
        
        return iu
    
    def user_item(self,data):
        ui = dict()
        groups = data.groupby([0])
        for item,group in groups:
            ui[item]=set(group.ix[:,1])
        
        return ui
    
    def similarityMatrix(self):
        matrix=dict()
        N = dict()
        for item,users in self.iu.items():
            add = 1.0/(1+math.log(len(users)))
            for v in users:
                if v not in N:
                    N[v] = 1
                else:
                    N[v] += 1
                
                for u in users:
                    if v==u:
                        continue
                    if v not in matrix:
                        matrix[v] = dict();
                    
                    if u not in matrix[v]:
                        matrix[v][u] = 0;
                        
                    matrix[v][u]+=add;
                        
        for v in matrix.keys():
            for u in matrix[v].keys():
                matrix[v][u] /= math.sqrt(N[u]*N[v])
            matrix[v] = sorted(matrix[v].items(),lambda x,y:cmp(x[1],y[1]),reverse=True);
            
        return matrix
    
    def getRecommend(self,user):
        userItem=self.ui[user]
        simiusers=self.simiMatrix[user]
        rank = dict()
        for i in range(len(simiusers)):
            if i>=self.k:
                break
            for item in self.ui[simiusers[i][0]]:
                if item in userItem:
                    continue
                if item not in rank:
                    rank[item]=0
                rank[item]+=simiusers[i][1]*1
        rank = sorted(rank.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:self.k];
        return [ele[0] for ele in rank]
    
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
                itemtest = ui_test[user]
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
            
            rec = recommander(train,k)
            prec,reca,cover,nove=rec.estimate(test)
            precision.append(prec)
            recall.append(reca)
            coverage.append(cover)
            novelty.append(nove)
            
        return precision,recall,coverage,novelty
        
        
data=pd.read_csv('../data/ratings.dat',sep='::',nrows=1000,header=None)
data=data.ix[:,0:1]

estimator=Estimator(data=data,ks=[5])
print estimator.estimate()

    