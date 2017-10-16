import numpy as np
import pandas as pd

def getDataframe(filePath):
    dataframe = pd.read_csv(filePath)
    y = dataframe['y']
    x = dataframe.drop('y', axis=1)
    return x, y

def applyZScore(dataframe):
    normalized_dataframe = dataframe
    ########## Please Fill Missing Lines Here ##########
 #   ones = normalized_dataframe.iloc[:,0]
#    normalized_dataframe = normalized_dataframe.drop(normalized_dataframe.columns[0], axis=1)
    normalized_dataframe = (normalized_dataframe - normalized_dataframe.mean())/normalized_dataframe.std()
#    normalized_dataframe = pd.concat([ones, normalized_dataframe], axis=1)
    return normalized_dataframe

def getBeta(train_x, train_y):
    beta = np.zeros(train_x.shape[1])
    ########## Please Fill Missing Lines Here ##########
    x_t_x = np.dot(np.transpose(train_x), train_x)
    x_t_y = np.dot(np.transpose(train_x), train_y) # might have to transpose y
    beta = np.dot(np.linalg.inv(x_t_x), x_t_y)
    return beta

def getBetaBatchGradient(train_x, train_y, alpha):
    beta = np.random.rand(train_x.shape[1])
    ########## Please Fill Missing Lines Here ##########
    converge = False
    err = train_y - np.dot(train_x, beta)
    while not converge:
        temp_err = np.array(err )

        beta =  beta + alpha * np.dot(np.transpose(train_x), err)
        err = train_y - np.dot(train_x, beta)
        if(abs(np.sum(temp_err - err) ) <  .1):
            converge = True
    return beta


def getBetaStochasticGradient(train_x, train_y, alpha):
    beta = np.random.rand(train_x.shape[1])
    ########## Please Fill Missing Lines Here ##########
    err = train_y - np.dot(train_x, beta)
    for i in range(train_x.shape[0]):
        beta = beta + alpha * err[i] * train_x.iloc[i,:]
    return beta


path = '/Users/aguil/Desktop/school/cs145/CS145_HW1/HW1_Programming/LinearRegression/linear-regression-train.csv'
df, y = getDataframe(path)

#print(getBeta(df,y))
#print(getBetaBatchGradient(applyZScore(df),y,.000001))

print(getBetaStochasticGradient(df,y,.0001))
