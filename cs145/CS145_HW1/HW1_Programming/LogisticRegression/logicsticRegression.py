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
        ##################### Please Fill Missing Lines Here #####################
        e_x1 = exp(self.parameters[0] + self.parameters[1]*60 + self.parameters[2]*155)
        e_x2 = exp(self.parameters[0] + self.parameters[1]*64 + self.parameters[2]*135)
        e_x3 = exp(self.parameters[0] + self.parameters[1]*73 + self.parameters[2]*170)
        p_x1 = - ( e_x1 / (1 + e_x1) )
        p_x2 = - ( e_x2 / (1 + e_x2) )
        p_x3 = - ( e_x3 / (1 + e_x3) )
        grad_1 = - P_x1 + (1 - p_x2) + (1- p_x3)
        grad_2 = - 60*P_x1 + 64*(1 - p_x2) + 73*(1- p_x3)
        grad_3 = - 155*P_x1 + 135*(1 - p_x2) + 170*(1- p_x3)
        gradients.append(grad_1)
        gradients.append(grad_2)
        gradients.append(grad_3)
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