class_d=bin
source_d=src

JAVAC=javac
FLAGS=-d $(class_d) -sourcepath $(source_d) -Xlint:all


S_UTIL= $(shell find $(source_d)/tweetoradio/util -name *.java)
C_UTIL= $(patsubst $(source_d)/%.java, $(class_d)/%.class, $(S_UTIL))

S_DIFFUSEUR= $(shell find $(source_d)/tweetoradio/diffuseur -name *.java)
C_DIFFUSEUR= $(patsubst $(source_d)/%.java, $(class_d)/%.class, $(S_DIFFUSEUR))

S_CLIENT= $(shell find $(source_d)/tweetoradio/client -name *.java)
C_CLIENT= $(patsubst $(source_d)/%.java, $(class_d)/%.class, $(S_CLIENT))

S_GESTIONNAIRE= $(shell find $(source_d)/tweetoradio/gestionnaire -name *.java)
C_GESTIONNAIRE= $(patsubst $(source_d)/%.java, $(class_d)/%.class, $(S_GESTIONNAIRE))


.SUFFIXES: .java .class

default: classes

$(class_d)/%.class: $(source_d)/%.java
	@$(JAVAC) $(FLAGS) $<;

$(class_d)/%.png: $(source_d)/%.png
	@mkdir -p $(@D)
	@cp $< $@

util: $(C_UTIL)

diffuseur: $(C_DIFFUSEUR)

client: $(C_CLIENT)

gestionnaire: $(C_GESTIONNAIRE)

classes: $(class_d) util diffuseur client gestionnaire

all: classes

$(class_d):
	@mkdir $(class_d)

javadoc:
	javadoc -d doc -encoding "ISO-8859-1" -docencoding "ISO-8859-1" -charset "ISO-8859-1" -classpath src -subpackages tweetoradio:tweetoradio.util:tweetoradio.diffuseur

clean:
	rm -rf $(class_d)
