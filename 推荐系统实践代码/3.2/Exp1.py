# coding=gbk
'''
���ݼ���
BX-Users.csv�������û���ID��λ�ú����䡣
BX-Books.csv������ͼ���ISBN�����⡢���ߡ��������������������ԡ�
BX-Book-Ratings.csv�� �����û���ͼ���������Ϣ��

�Ƚ�����p(f,i)���ֶ��巽ʽ����[����<25]��[����>50]�����û��Ƽ���ǰ10����
'''
import pandas as pd
users=pd.read_csv('../data/BX-Users.csv',sep=';',dtype={'Age':float})
books=pd.read_csv('../data/BX-Books.csv',sep=';',escapechar='\\')
rates=pd.read_csv('../data/BX-Book-Ratings.csv',sep=';',nrows=80000,dtype={'Book-Rating':float})

#�����û�,ֻ�����û�ID
AgeL25=set(users[users.Age<25].ix[:,0])
AgeG50=set(users[users.Age>25].ix[:,0])

#���ֵ���ʽ�洢���id������
books={a:b for a, b in books[[0,1]].itertuples(index=False)}
rates=rates[rates['ISBN'].isin(books)]

RateL25=rates[rates['User-ID'].isin(AgeL25)]
RateG50=rates[rates['User-ID'].isin(AgeG50)]

#��һ�ַ�ʽ��ʹ�� 25�������û��������鼮��Ϊ��25�������û����Ƽ���50��������ͬ
#���ַ�ʽ�Ὣȫ����ζ������鼮�Ƽ���25�������û�

rankL25=dict()

#���鼮����
groups = RateL25.groupby(['ISBN'])
for book,group in groups:
    rankL25[book]=len(group)

recL25=[books[x[0]] for x in sorted(rankL25.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

rankG50=dict()

#���鼮����
groups = RateG50.groupby(['ISBN'])
for book,group in groups:
    rankG50[book]=len(group)

recG50=[books[x[0]] for x in sorted(rankG50.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

print recL25
print recG50

'''
���������������һ���ģ���Ϊ�������������ζ�����
['Wild Animus', 'The Lovely Bones: A Novel', 'The Da Vinci Code', "Harry Potter and the Sorcerer's Stone (Harry Potter (Paperback))", "She's Come Undone (Oprah's Book Club)"]
['Wild Animus', 'The Lovely Bones: A Novel', 'The Da Vinci Code', 'Life of Pi', 'Divine Secrets of the Ya-Ya Sisterhood: A Novel']
'''

#��2�з�ʽ����������25�������û�ռ������С˵�Ƽ���25�������û���50��������ͬ
#���ַ�ʽ���Խ��ȫ��������Ŷ���ᶼ���Ƽ�������
#���б���ֻ��һ������ʱ�������Ƽ����ؾͻ���1���ڷ�ĸ�м���alpha��Ϊ�˽���������
alpha=10

groups = rates.groupby(['ISBN'])
bookrates={book:len(group) for book,group in groups}

for book,rank in rankL25.items():
    rankL25[book]=rank/(bookrates[book]+alpha)
recL25=[books[x[0]] for x in sorted(rankL25.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

for book,rank in rankG50.items():
    rankG50[book]=rank/(bookrates[book]+alpha)
recG50=[books[x[0]] for x in sorted(rankG50.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

print recL25
print recG50