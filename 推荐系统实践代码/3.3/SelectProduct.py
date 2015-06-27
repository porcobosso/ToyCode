# coding=gbk
'''
选择用户反馈的物品

将评分>3定义为喜欢
'''
import pandas as pd
import numpy as np
import copy

#获取区分度
def getDiff(userRates,movie):
    like=dict()
    dislike=dict()
    unknown=dict()
    
    likeArray=[]
    dislikeArray=[]
    unknownArray=[]
    for user,ratings in userRates.items():
        if movie not in ratings:
            unknown[user]=ratings
            for mv,rating in ratings.items():
                unknownArray.append(rating)
        elif ratings[movie]>3:
            like[user]=ratings
            for mv,rating in ratings.items():
                if mv!=movie:
                    likeArray.append(rating)
        else:
            dislike[user]=ratings
            for mv,rating in ratings.items():
                if mv!=movie:
                    dislikeArray.append(rating)
    diff=0
    if len(likeArray)!=0:
        diff += np.var(likeArray)
    if len(dislikeArray)!=0:
        diff += np.var(dislikeArray)
    if len(unknownArray)!=0:
        diff += np.var(unknownArray)  
    return (diff,like,dislike,unknown)

def select(mvs,userRates,node,exceptMvs,lv):
    like=dict()
    dislike=dict()
    unknown=dict()
    
    maxDiff=-100
    bestmv=-1000
    for mv in mvs:
        if mv in exceptMvs:
            continue
        
        diff,tmpa,tmpb,tmpc=getDiff(userRates,mv)
        if diff>maxDiff:
            bestmv=mv
            maxDiff=diff
            like=tmpa
            dislike=tmpb
            unknown=tmpc
    
    exceptMvs.append(bestmv)
    node['movie']=bestmv
    print lv
    print node['tag']
    if (lv+1)<=3:
        node['like']={'tag':'like'}
        node['dislike']={'tag':'dislike'}
        node['unknown']={'tag':'unknown'}
        select(mvs,like,node['like'],copy.deepcopy(exceptMvs),lv+1)
        select(mvs,dislike,node['dislike'],copy.deepcopy(exceptMvs),lv+1)
        select(mvs,unknown,node['unknown'],copy.deepcopy(exceptMvs),lv+1)

data=pd.read_csv('data/ratings.dat',sep='::',nrows=80000,header=None)
data=data.ix[:,0:2]

groups=data.groupby([0])
#用rates[用户][物品]=评分形式组织数据
rates=dict()
for user,group in groups:
    rates[user]={a:b for a, b in group[[1,2]].itertuples(index=False)}

#得到物品列表    
movies = set([j for i,j,k in data.itertuples(index=False)])
root={'tag':'root'}
select(movies,rates,root,[],1)
print root