# coding=gbk
import pandas as pd
from sklearn import cross_validation

users=pd.read_csv('data/BX-Users.csv',sep=';',dtype={'Age':float})
rates=pd.read_csv('data/BX-Book-Ratings.csv',sep=';',dtype={'Book-Rating':float})

#排除年龄为空及小于3岁和大于110岁的用户信息，以及其评价信息
users=users[(pd.notnull(users['Age']))&(users['Age']>0)&(users['Age']<100)]#只取用户ID和年龄
rates=rates[rates['User-ID'].isin(users['User-ID'])]#排除年龄不对的用户的评价

#地址只取国籍
def dealLocation(x):
    z=x['Location'].split(',')
    if len(z)<3:
        return 'False'
    else:
        return z[len(z)-1].strip()
users['Location']=users.apply(dealLocation,axis=1)
userdict = {a:(b,c) for a, b, c in users.itertuples(index=False)}

#先按国籍分组，然后按年龄分组，年龄从1到99岁，划分为20个区间
groups=users.groupby(['Location'])
userclass=dict()
userclassRec=dict()
for loc,group in groups:
    userclass[loc]=dict()
    userclassRec[loc]=dict()
    for i in range(20):
        userclass[loc][i]=set(group[(group['Age']>(i*5))&(group['Age']<=((i+1)*5))]['User-ID'])
        userclassRec[loc][i]=dict()

#将评分分为测试组和训练组
train,test=cross_validation.train_test_split(rates,test_size=0.2)
train = pd.DataFrame(train,columns=['User-ID', 'ISBN', 'Book-Rating'])
test = pd.DataFrame(test,columns=['User-ID', 'ISBN', 'Book-Rating'])

#计算每类用户的推荐
groups=train.groupby(['ISBN'])
for book,group in groups:
    busers=set(group['User-ID'])
    for u in busers:
        uinfo = userdict[u]
        loc = uinfo[0]
        ageclass = int((uinfo[1]-1)/5)
        if book not in userclassRec[loc][ageclass]:
            userclassRec[loc][ageclass][book]=0
        userclassRec[loc][ageclass][book]+=1.0/(len(busers)+5)

#根据评分各个用户组里的书籍
for loc,ages in userclassRec.items():
    for age,books in ages.items():
        userclassRec[loc][age]=[i[0] for i in sorted(userclassRec[loc][age].items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:20]]
        

groups=test.groupby(['User-ID'])
total=0
accurate=0
for u,group in groups:
    uinfo = userdict[u]
    loc = uinfo[0]
    ageclass = int((uinfo[1]-1)/5)
    
    total += len(userclassRec[loc][ageclass])
    for book in set(group['ISBN']):
        if book in userclassRec[loc][ageclass]:
            accurate +=1

print accurate*1.0/total