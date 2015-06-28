import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier

prng=np.random.RandomState(100)
x = np.zeros((200,10))
x[0:100,0:5]=prng.rand(100,5)*50
x[0:100,5:10]=prng.rand(100,5)

x[100:200,0:5]=-prng.rand(100,5)*50
x[100:200,5:10]=prng.rand(100,5)

y = np.zeros((200,1))
y[0:100,:] = 1
y[100:200,:] = 0

lrg = LogisticRegression(penalty='l2')
lrg.fit(x, y)
print lrg.coef_

fc=RandomForestClassifier(n_estimators=1000,max_depth =8)
fc.fit(x,y)
print fc.feature_importances_