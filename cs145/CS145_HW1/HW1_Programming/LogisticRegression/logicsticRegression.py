import math
import numpy

import pandas as pd
from pandas import Series, DataFrame

from sklearn.linear_model import LogisticRegression

import random



#-------------------------------------------------------------------
def log(n):
    return math.log(n)
#-------------------------------------------------------------------
def exp(n):
    return math.exp(n)
#-------------------------------------------------------------------
class logistic:
    #******************************************************
    def __init__(self, parameters):
        self.parameters = parameters
        self.alpha = 1
        self.m = DataFrame([[60, 155], [64, 135], [73, 170]])
        self.m = (self.m - self.m.mean()) / self.m.std()

    #******************************************************
    ########## Feel Free to Add Helper Functions ##########
    #******************************************************


    def log_likelihood(self):
        ll = 0.0
        ##################### Please Fill Missing Lines Here #####################
        h_1 = 1 / ( 1 +
                    exp(-(self.parameters[0] +
                          self.parameters[1]*self.m.iloc[0,0] +
                          self.parameters[2]*self.m.iloc[0,1])))
        h_2 = 1 / ( 1 +
                    exp(-(self.parameters[0] +
                          self.parameters[1]*self.m.iloc[1,0] +
                          self.parameters[2]*self.m.iloc[1,1])))
        h_3 = 1 / ( 1 +
                    exp(-(self.parameters[0] +
                          self.parameters[1]*self.m.iloc[2,0] +
                          self.parameters[2]**self.m.iloc[2,1])))
        ll = log(1 + h_1) + log(h_2) + log(h_3)
        return ll
    #******************************************************
    def gradients(self):
        gradients = []
        ##################### Please Fill Missing Lines Here #####################
        e_x1 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[0,0] + self.parameters[2]*self.m.iloc[0,1])
        e_x2 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[1,0] + self.parameters[2]*self.m.iloc[1,1])
        e_x3 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[2,0] + self.parameters[2]*self.m.iloc[2,1])
        p_x1 =   e_x1 / (1 + e_x1)
        p_x2 =   e_x2 / (1 + e_x2)
        p_x3 =   e_x3 / (1 + e_x3)

        grad_1 = - p_x1 + (1 - p_x2) + (1 - p_x3)
        grad_2 = - self.m.iloc[0,0] * p_x1 + self.m.iloc[1,0] * (1 - p_x2) + self.m.iloc[2,0] * (1- p_x3)
        grad_3 = - self.m.iloc[0,1] * p_x1 + self.m.iloc[1,1] * (1 - p_x2) + self.m.iloc[2,1] * (1- p_x3)
        gradients.append(grad_1)
        gradients.append(grad_2)
        gradients.append(grad_3)
        return gradients
    #******************************************************
    def iterate(self):
        ##################### Please Fill Missing Lines Here #####################
        hessian = self.hessian()
        gradients = self.gradients()

##        gradients = numpy.array(gradients)
        self.parameters = self.parameters -  numpy.dot(numpy.linalg.inv(hessian), gradients)
        self.parameters = numpy.array(self.parameters)

        return self.parameters[0]
    #******************************************************
    def hessian(self):
        n = len(self.parameters)
        hessian = numpy.zeros((n, n))
        ##################### Please Fill Missing Lines Here #####################
        e_x1 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[0,0] + self.parameters[2]*self.m.iloc[0,1])
        e_x2 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[1,0] + self.parameters[2]*self.m.iloc[1,1])
        e_x3 = exp(self.parameters[0] + self.parameters[1]*self.m.iloc[2,0] + self.parameters[2]*self.m.iloc[2,1])

        p_x1 = e_x1 / (1 + e_x1)
        p_x2 = e_x2 / (1 + e_x2)
        p_x3 = e_x3 / (1 + e_x3)

        mat1 = numpy.matrix([[p_x1, self.m.iloc[0,0]*p_x1, self.m.iloc[0,1]*p_x1],
                             [p_x2, self.m.iloc[1,0]*p_x2, self.m.iloc[1,1]*p_x2],
                             [p_x3, self.m.iloc[2,0]*p_x3, self.m.iloc[2,1]*p_x3]])

        mat2 = numpy.matrix([[1  *(1-p_x1)  , 1*(1-p_x2)  , 1*(1-p_x3)],
                             [self.m.iloc[0,0]*(1-p_x1) , self.m.iloc[1,0]*(1-p_x2) , self.m.iloc[2,0]*(1-p_x3)],
                             [self.m.iloc[0,1]*(1-p_x1) , self.m.iloc[1,1]*(1-p_x2) , self.m.iloc[2,1]*(1-p_x3)]])

        hessian = numpy.dot(mat2, mat1)
        hessian = -1 * hessian
        return hessian
#-------------------------------------------------------------------
#parameters = []
##################### Please Fill Missing Lines Here #####################
X = DataFrame([[60,155],[64,135],[73,170]])

Y = DataFrame([0,1,1])
Y = numpy.ravel(Y)

## initialize parameters
parameters = [.25, .25, .25]
#parameters = numpy.random.random_sample(3)
print(parameters)

l = logistic(parameters)
parameters = l.iterate()
print(parameters)

l = logistic(parameters)
print(l.iterate())


X = DataFrame([[60,155],[64,135],[73,170]])


Y = DataFrame([0,1,1])
Y = numpy.ravel(Y)
X = (X - X.mean() )/ X.std()
log_model = LogisticRegression(random_state=1)

log_model.fit(X,Y)

print(log_model.intercept_, log_model.coef_)
