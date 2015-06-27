# coding=gbk
'''
数据集：
BX-Users.csv，包含用户的ID、位置和年龄。
BX-Book-Ratings.csv， 包含用户对图书的评分信息。

探索年龄划分方法
将年龄划分为三段，
'''
import pandas as pd
import math
users=pd.read_csv('../data/BX-Users.csv',sep=';',dtype={'Age':float})
rates=pd.read_csv('../data/BX-Book-Ratings.csv',sep=';',nrows=80000,dtype={'Book-Rating':float})

#排除年龄为空及小于3岁和大于110岁的用户信息，以及其评价信息
users=users[(pd.notnull(users['Age']))&(users['Age']>3)&(users['Age']<110)].ix[:,[0,2]]#只取用户ID和年龄
rates=rates[rates['User-ID'].isin(users['User-ID'])]#取错年龄为空用户的评价

def shanon(mid1,mid2,users,rates):
    class1 = set(users[users['Age']<mid1].ix[:,0])
    class2 = set(users[(users['Age']>=mid1)&(users['Age']<=mid2)].ix[:,0])
    class3 = set(users[users['Age']>mid2].ix[:,0])
    
    groups = rates.groupby(['ISBN'])
    shanon = 0
    c1rates=rates[rates['User-ID'].isin(class1)].shape[0]
    c2rates=rates[rates['User-ID'].isin(class2)].shape[0]
    c3rates=rates[rates['User-ID'].isin(class3)].shape[0]
    for book,group in groups:
        userset=set(group['User-ID'])
        total=len(group)
        c1len=len(userset.intersection(class1))*1.0/total
        c2len=len(userset.intersection(class2))*1.0/total
        c3len=1.0-c1len-c2len
        c1len/=c1rates
        c2len/=c2rates
        c3len/=c3rates
        
        c1len=c1len/(c1len+c2len+c3len)
        c2len=c2len/(c1len+c2len+c3len)
        c3len=c3len/(c1len+c2len+c3len)
        
        if c1len >0.0001:
            shanon += -c1len*math.log(c1len)
        
        if c2len >0.0001:
            shanon += -c2len*math.log(c2len)
        
        if c3len >0.0001:
            shanon += -c3len*math.log(c3len)
        
    return shanon/len(groups)

print shanon(20,55,users,rates)
print shanon(25,50,users,rates)
print shanon(30,45,users,rates)