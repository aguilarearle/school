library(stringr)
library(ggplot2)
library(alr3)
library(caTools)
library(lattice)
library(car)
set.seed(101) 
setwd("~/Downloads")
df <- read.csv("heal_analytics_challenge/usage.csv")

date_time <- str_split_fixed(df$time_date, " ", 2)
date_time <- as.data.frame(date_time)
colnames(date_time) <- c("date", "time")
hour <- date_time$time
month <- months(as.Date(date_time$date, '%Y-%m-%d'))
day <- weekdays(as.Date(date_time$date, '%Y-%m-%d'))
split_date <- str_split_fixed(date_time$date, "-", 2)
year <- data.frame(year=split_date[,1])
df <- cbind(df, year, month, day, hour)
df <- df[complete.cases(df),]
ix <- which(df$temp_c == -200)
df <- df[-ix,] 
df$day <- factor(df$day, levels= c( "Monday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday") )
df$month <- factor(df$month, levels=c("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December") )
df$vacation <- factor(df$vacation)
df$quarter <- factor(df$quarter)
df$workday <- factor(df$workday)
df$weather <- factor(df$weather)

plot(df$hour,df$weather, xlab= "Time", ylab="Weather")

plot(df$hour, df$temp_c, xlab= "Time", ylab="Temperatyre (Celsius)")

plot(df$hour, df$humidity, xlab= "Time", ylab="Humidity")

plot(df$weather, df$temp_c, xlab= "Weather", ylab="Temperature (Celsius)")

plot(df$weather, df$humidity, xlab= "Weather", ylab="Humidity")

plot(df$humidity, df$temp_c, xlab= "Humidity", ylab="Temperature (Celsius)")

pl <- ggplot(data=df,aes(x = humidity,y=temp_c)) 
pl + geom_point(aes( color=weather))  

sample <- sample.split(df$count, SplitRatio = 0.70) 
train = subset(df, sample == TRUE)
test = subset(df, sample == FALSE)

model1 <- lm(count~ (hour + day + month + humidity +
                       weather + temp_c +workday + vacation+ quarter + year)^2, train)
anova(model1)


model2 <-lm(count ~ year*(temp_c+hour+day+month) +
hour*(humidity+weather+temp_c+workday+day+month) +
month*(humidity + temp_c) + humidity*weather+day*month, train)
anova(model2)

model2 <-lm(count ~ year*(temp_c) +
              hour*(humidity+temp_c+workday) +
              month*(humidity + temp_c) + day, train)
summary(model2)

plot(model2, 1:2)
mmps(model2)

powerTransform(model2)
model3 <-lm(count^(.25) ~ year*(temp_c) +
              hour*(humidity+temp_c+workday) +
              month*(humidity + temp_c)  + day , train)

plot(model3,1:2)
mmps(model3)

count.predictions <- predict(model3,test)
results <- cbind(count.predictions,(test$count)^.25) 
colnames(results) <- c('pred','real')
results <- as.data.frame(results)
mse <- mean((results$real-results$pred)^2)
mse^2

xyplot(count~hour|day,data = df,scales=list(x=list(rot=45)))

xyplot(count~day,data=df)

xyplot(count~month,data=df, scales=list(x=list(rot=45)))

xyplot(count~vacation,data=df)

xyplot(count~ temp_c| hour,data=df)

xyplot(count~ temp_c| month,data=df)
