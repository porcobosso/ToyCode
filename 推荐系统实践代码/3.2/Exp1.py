# coding=gbk
'''
数据集：
BX-Users.csv，包含用户的ID、位置和年龄。
BX-Books.csv，包含图书的ISBN、标题、作者、发表年代、出版社和缩略。
BX-Book-Ratings.csv， 包含用户对图书的评分信息。

比较两种p(f,i)两种定义方式，给[年龄<25]和[年龄>50]两类用户推荐的前10本书
'''
import pandas as pd
users=pd.read_csv('../data/BX-Users.csv',sep=';',dtype={'Age':float})
books=pd.read_csv('../data/BX-Books.csv',sep=';',escapechar='\\')
rates=pd.read_csv('../data/BX-Book-Ratings.csv',sep=';',nrows=80000,dtype={'Book-Rating':float})

#分类用户,只保存用户ID
AgeL25=set(users[users.Age<25].ix[:,0])
AgeG50=set(users[users.Age>25].ix[:,0])

#以字典形式存储书的id和名称
books={a:b for a, b in books[[0,1]].itertuples(index=False)}
rates=rates[rates['ISBN'].isin(books)]

RateL25=rates[rates['User-ID'].isin(AgeL25)]
RateG50=rates[rates['User-ID'].isin(AgeG50)]

#第一种方式，使用 25岁以下用户最热门书籍作为给25岁以下用户的推荐，50岁以上亦同
#这种方式会将全年龄段都热门书籍推荐给25岁以下用户

rankL25=dict()

#按书籍分组
groups = RateL25.groupby(['ISBN'])
for book,group in groups:
    rankL25[book]=len(group)

recL25=[books[x[0]] for x in sorted(rankL25.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

rankG50=dict()

#按书籍分组
groups = RateG50.groupby(['ISBN'])
for book,group in groups:
    rankG50[book]=len(group)

recG50=[books[x[0]] for x in sorted(rankG50.items(),lambda x,y:cmp(x[1],y[1]),reverse=True)[0:5]]

print recL25
print recG50

'''
结果中有三本书是一样的，因为这三本书各年龄段都热门
['Wild Animus', 'The Lovely Bones: A Novel', 'The Da Vinci Code', "Harry Potter and the Sorcerer's Stone (Harry Potter (Paperback))", "She's Come Undone (Oprah's Book Club)"]
['Wild Animus', 'The Lovely Bones: A Novel', 'The Da Vinci Code', 'Life of Pi', 'Divine Secrets of the Ya-Ya Sisterhood: A Novel']
'''

#第2中方式，将读者中25岁以下用户占比最大的小说推荐给25岁以下用户，50岁以上亦同
#这种方式可以解决全年龄段热门读物会都被推荐的问题
#当有本书只有一个读者时，它的推荐比重就会是1，在分母中加上alpha，为了解决这个问题
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