# coding=gbk
import pandas as pd
import math

data=pd.read_csv('delicious.dat',sep='\t',header=None)

item_tag=dict()
user_tag=dict()
for user,item,tags in data.itertuples(index=False):
    #如果tags为nan，跳过
    if type(tags)==float:
        continue
    
    if item not in item_tag:
        item_tag[item]=dict()
    if user not in user_tag:
        user_tag[user]=dict()
    
    tags=tags.split(' ')
    for tag in tags:
        tag = tag.lower()
        if tag not in item_tag[item]:
            item_tag[item][tag]=0
        item_tag[item][tag]+=1
        
        if tag not in user_tag[user]:
            user_tag[user][tag]=0
        user_tag[user][tag]+=1

def recommend(user,item,user_tag,item_tag,alpha):
    utgs = user_tag[user]
    itgs = item_tag[item]
    
    udesc = [a[0] for a in sorted(utgs.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:10]]
    idesc = [a[0] for a in sorted(itgs.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:10]]
    
    rank = dict()
    maxu = max(utgs.values())
    maxi = max(itgs.values())
    for tag,v in utgs.items():
        if tag not in rank:
            rank[tag] = 0
        rank[tag]+=(1-alpha)*v/maxu
    
    for tag,v in itgs.items():
        if tag not in rank:
            rank[tag] = 0
        rank[tag]+= alpha*v/maxi
    
    res=[a[0] for a in sorted(rank.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:10]];
    return udesc,idesc,res

udesc,idesc,res=recommend(104,33911,user_tag,item_tag,0.8)
print udesc
print idesc
print res