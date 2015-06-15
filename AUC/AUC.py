import numpy as np
import matplotlib.pyplot as plt

from sklearn.linear_model import LogisticRegression
from sklearn import datasets
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve

digits = datasets.load_digits()

X, y = digits.data, digits.target
X = StandardScaler().fit_transform(X)

# classify small against large digits
y = (y > 4).astype(np.int)
X_train = X[:-400]
y_train = y[:-400]

X_test = X[-400:]
y_test = y[-400:]

lrg = LogisticRegression(penalty='l1')
lrg.fit(X_train, y_train)

y_test_prob=lrg.predict_proba(X_test)
P = np.where(y_test==1)[0].shape[0];
N  = np.where(y_test==0)[0].shape[0];

dt = 10001
TPR = np.zeros((dt,1))
FPR = np.zeros((dt,1))
for i in range(dt):
    y_test_p = y_test_prob[:,1]>=i*(1.0/(dt-1))
    TP = np.where((y_test==1)&(y_test_p==True))[0].shape[0];
    FN = P-TP;
    FP = np.where((y_test==0)&(y_test_p==True))[0].shape[0];
    TN = N - FP;
    TPR[i]=TP*1.0/P
    FPR[i]=FP*1.0/N



plt.plot(FPR,TPR,color='black')
plt.plot(np.array([[0],[1]]),np.array([[0],[1]]),color='red')
plt.show()

#use sklearn method
# fpr, tpr, thresholds = roc_curve(y_test,y_test_prob[:,1],pos_label=1)
# plt.plot(fpr,tpr,color='black')
# plt.plot(np.array([[0],[1]]),np.array([[0],[1]]),color='red')
# plt.show()

rank = y_test_prob[:,1].argsort()
rank = rank.argsort()+1
auc = (sum(rank[np.where(y_test==1)[0]])-(P*1.0*(P+1)/2))/(P*N);
print auc
print roc_auc_score(y_test, y_test_prob[:,1])



