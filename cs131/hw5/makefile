SHELL:=/bin/bash

# Image sets ------
acts =	act/florence.jpg act/florenceBlur.jpg act/florenceGrey.jpg\
	act/florenceMirror.jpg act/florenceMirror2.jpg act/florenceNeg.jpg
difs =  dif/florence.jpg dif/florenceBlur.jpg dif/florenceGrey.jpg\
	dif/florenceMirror.jpg dif/florenceMirror2.jpg dif/florenceNeg.jpg
cmps =  cmp/florence.jpg cmp/florenceBlur.jpg cmp/florenceGrey.jpg\
	cmp/florenceMirror.jpg cmp/florenceMirror2.jpg cmp/florenceNeg.jpg

# ALL -------------
all : clean run append
	eog --single-window all.jpg &
	feh --force-aliasing --zoom max ./min/* &

# RUN -------------
build:
	javac hw5.java
	javac Test.java

run: build
	java Test

# APPEND ----------
append: cmp
	convert $(cmps) -append all.jpg

# ACT -------------
act : $(acts)

act/%.jpg : act/%.ppm
	convert $< -quality 75% $@

# DIF -------------
dif : act $(difs)

dif/%.jpg : exp/%.jpg act/%.jpg
	compare $? -fuzz 6.67% -compose src $@ | true

# CMP -------------
cmp : act dif $(cmps)

cmp/%.jpg : exp/%.jpg act/%.jpg dif/%.jpg
	convert $? +append $@

# CLEAN -----------
clean:
	rm -f act/florence?*.ppm act/*.jpg act/testImage.ppm
	rm -f dif/*.jpg cmp/*.jpg min/*.ppm all.jpg ./*.class

