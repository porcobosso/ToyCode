# coding=gbk
import pandas as pd
import math

data=pd.read_csv('delicious.dat',sep='\t',header=None)

item_tag=dict()
for user,item,tags in data.itertuples(index=False):
    #如果tags为nan，跳过
    if type(tags)==float:
        continue
    
    if item not in item_tag:
        item_tag[item]=dict()
    
    tags=tags.split(' ')
    for tag in tags:
        tag = tag.lower()
        if tag not in item_tag[item]:
            item_tag[item][tag]=0
        item_tag[item][tag]+=1

def recommend(taga,n,item_tag):
    nb=dict()
    nab=dict()
    na = 0
    
    l = len(item_tag)
    i=1
    for item,tags in item_tag.items():
        
        print i*1.0/l
        i+=1
        
        if taga not in tags:
            for tag,v in tags.items():
                if tag not in nb:
                    nb[tag]=0
                nb[tag]+=v*v
        else:
            av = tags[taga]
            na +=av*av
            for tag,v in tags.items():
                if tag==taga:
                    continue
                if tag not in nb:
                    nb[tag]=0
                nb[tag]+=v*v
                if tag not in nab:
                    nab[tag]=0
                nab[tag]+=av*v
    
    rank=dict()
    na = math.sqrt(na)
    for tag,v in nab.items():
        rank[tag]=v/na/math.sqrt(nb[tag])
        
    res = [a[0] for a in sorted(rank.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:n]]
    return res

tags=recommend('webdesign',10,item_tag)
print tags
        
