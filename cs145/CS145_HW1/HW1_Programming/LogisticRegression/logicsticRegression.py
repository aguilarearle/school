import math
import numpy

import pandas as pd
from pandas import Series, DataFrame

from sklearn.linear_model import LogisticRegression

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
    #******************************************************
    ########## Feel Free to Add Helper Functions ##########
    #******************************************************


    def log_likelihood(self):
        ll = 0.0
        ##################### Please Fill Missing Lines Here #####################
        h_1 = 1 / ( 1 + exp(-(self.parameters[0] + self.parameters[1]*60 + self.parameters[2]*155)))
        h_2 = 1 / ( 1 + exp(-(self.parameters[0] + self.parameters[1]*64 + self.parameters[2]*135)))
        h_3 = 1 / ( 1 + exp(-(self.parameters[0] + self.parameters[1]*73 + self.parameters[2]*170)))
        ll = log(1 + h_1) + log(h_2) + log(h_3)
        return ll
    #******************************************************
    def gradients(self):
        gradients = []
        ##################### Please Fill Missing Lines Here #####################
        e_x1 = exp(self.parameters[0] + self.parameters[1]*60 + self.parameters[2]*155)
        e_x2 = exp(self.parameters[0] + self.parameters[1]*64 + self.parameters[2]*135)
        e_x3 = exp(self.parameters[0] + self.parameters[1]*73 + self.parameters[2]*170)
        p_x1 = - ( e_x1 / (1 + e_x1) )
        p_x2 = - ( e_x2 / (1 + e_x2) )
        p_x3 = - ( e_x3 / (1 + e_x3) )
        grad_1 = - p_x1 + (1 - p_x2) + (1- p_x3)
        grad_2 = - 60 * p_x1 + 64 * (1 - p_x2) + 73 * (1- p_x3)
        grad_3 = - 155 * p_x1 + 135 * (1 - p_x2) + 170 * (1- p_x3)
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
        self.parameters = self.parameters - self.alpha *  numpy.dot(numpy.linalg.inv(hessian), gradients)
        self.parameters = numpy.array(self.parameters)

        return self.parameters[0]
    #******************************************************
    def hessian(self):
        n = len(self.parameters)
        hessian = numpy.zeros((n, n))

        ##################### Please Fill Missing Lines Here #####################
        e_x1 = exp(self.parameters[0] + self.parameters[1] * 60 + self.parameters[2] * 155)
        e_x2 = exp(self.parameters[0] + self.parameters[1] * 64 + self.parameters[2] * 135)
        e_x3 = exp(self.parameters[0] + self.parameters[1] * 73 + self.parameters[2] * 170)
        p_x1 = - (e_x1 / (1 + e_x1))
        p_x2 = - (e_x2 / (1 + e_x2))
        p_x3 = - (e_x3 / (1 + e_x3))

        mat1 = numpy.matrix([[-p_x1,-60*p_x1,-155*p_x1],
                             [-p_x2,-64*p_x2,-135*p_x2],
                             [-p_x3,-73*p_x3,-170*p_x3]])
        mat2 = numpy.matrix([[1*(1-p_x1)  , 1*(1-p_x2)  , 1*(1-p_x3)],
                             [60*(1-p_x1) , 64*(1-p_x2) , 73*(1-p_x3)],
                             [155*(1-p_x1), 135*(1-p_x2), 170*(1-p_x3)]])

        hessian = numpy.dot(mat2, mat1)
        return hessian
#-------------------------------------------------------------------
#parameters = []
##################### Please Fill Missing Lines Here #####################
## initialize parameters
parameters = [.25, .25, .25]
print(parameters)

l = logistic(parameters)
parameters = l.iterate()
print(parameters)

l = logistic(parameters)
parameters = l.iterate()
print (parameters)

l = logistic(parameters)
parameters = l.iterate()
print (parameters)

l = logistic(parameters)
parameters = l.iterate()
print (parameters)

X = DataFrame([[1,60,155],[1,64,135],[1,73,170]])
Y = DataFrame([0,1,1])
print(X)

log_model = LogisticRegression()

log_model.fit(X,Y)

print(log_model.coef_)