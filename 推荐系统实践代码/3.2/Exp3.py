# coding=gbk
import pandas as pd
from sklearn import cross_validation

users=pd.read_csv('data/BX-Users.csv',sep=';',dtype={'Age':float})
rates=pd.read_csv('data/BX-Book-Ratings.csv',sep=';',dtype={'Book-Rating':float})

#�ų�����Ϊ�ռ�С��3��ʹ���110����û���Ϣ���Լ���������Ϣ
users=users[(pd.notnull(users['Age']))&(users['Age']>0)&(users['Age']<100)]#ֻȡ�û�ID������
rates=rates[rates['User-ID'].isin(users['User-ID'])]#�ų����䲻�Ե��û�������

#��ַֻȡ����
def dealLocation(x):
    z=x['Location'].split(',')
    if len(z)<3:
        return 'False'
    else:
        return z[len(z)-1].strip()
users['Location']=users.apply(dealLocation,axis=1)
userdict = {a:(b,c) for a, b, c in users.itertuples(index=False)}

#�Ȱ��������飬Ȼ��������飬�����1��99�꣬����Ϊ20������
groups=users.groupby(['Location'])
userclass=dict()
userclassRec=dict()
for loc,group in groups:
    userclass[loc]=dict()
    userclassRec[loc]=dict()
    for i in range(20):
        userclass[loc][i]=set(group[(group['Age']>(i*5))&(group['Age']<=((i+1)*5))]['User-ID'])
        userclassRec[loc][i]=dict()

#�����ַ�Ϊ�������ѵ����
train,test=cross_validation.train_test_split(rates,test_size=0.2)
train = pd.DataFrame(train,columns=['User-ID', 'ISBN', 'Book-Rating'])
test = pd.DataFrame(test,columns=['User-ID', 'ISBN', 'Book-Rating'])

#����ÿ���û����Ƽ�
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

#�������ָ����û�������鼮
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