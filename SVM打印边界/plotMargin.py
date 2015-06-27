# -*- coding: utf-8 -*-
import numpy as np
from scipy import optimize
import matplotlib.pyplot as plt

np.random.seed(1)
X = np.r_[np.random.randn(20, 2) - [2, 2], np.random.randn(20, 2) + [2, 2]]
y = [-1] * 20 + [1] * 20
plt.figure()
#plt.scatter(X[:, 0], X[:, 1])
plt.scatter(X[:, 0], X[:, 1], c=y, cmap=plt.cm.Paired)
#plt.axis('image')
#plt.show()

obj = lambda arg_w: 0.5 * np.dot(arg_w[1:], arg_w[1:])
jac = lambda arg_w: np.r_[0, arg_w[1:]]
cons = ({'type': 'ineq', 'fun': 
         lambda arg_w: y * (np.dot(np.c_[np.ones((X.shape[0], 1)), X] ,arg_w)) - 1})
res = optimize.minimize(obj, (0, 0, 0), jac=jac, constraints=cons, 
        method='SLSQP', options={'disp':False})
b, w = res.x[0], res.x[1:]

mins = np.min(X, axis=0)
maxs = np.max(X, axis=0)
xx = np.linspace(mins[0], maxs[0], 2)
yy = np.linspace(mins[1], maxs[1], 2)
X1, X2 = np.meshgrid(xx, yy)
Z = w[0] * X1 + w[1] * X2 + b
levels = [-1.0, 0.0, 1.0]
linestyles = ['dashed', 'solid', 'dashed']
colors = 'y'
plt.contour(X1, X2, Z, levels, colors=colors, linestyles=linestyles)

SV_indices = (abs(1 - y * (np.dot(X, w) + b)) < 1e-5)
SVs = X[SV_indices]
plt.scatter(SVs[:, 0], SVs[:, 1], s=80, facecolors='none')

plt.axis('image')
plt.show()