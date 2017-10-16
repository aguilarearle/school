import math
import numpy
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
        grad_1 = self.parameters[0]
        grad_1 = self.parameters[1]
        grad_1 = self.parameters[2]
        ##################### Please Fill Missing Lines Here #####################
        return gradients
    #******************************************************
    def iterate(self):
        ##################### Please Fill Missing Lines Here #####################
        return self.parameters
    #******************************************************
    def hessian(self):
        n = len(self.parameters)
        hessian = numpy.zeros((n, n))
        ##################### Please Fill Missing Lines Here #####################
        return hessian
#-------------------------------------------------------------------
parameters = []
##################### Please Fill Missing Lines Here #####################
## initialize parameters
parameters = [.25,.25, .25]
l = logistic(parameters)
parameters = l.iterate()
l = logistic(parameters)
print (l.iterate())