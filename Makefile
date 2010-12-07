# makefile for SSM, Simple Stack Machine

default: ssm

DIR_SRC			:= src
DIR_HELP		:= Help
DIR_IMGS		:= Images
DIR_BLD			:= build
DIR_BLD_JAR		:= build/ssm-classes

SSM_JAR			:= $(DIR_BLD)/ssm.jar
SSM_SRC			:= $(wildcard $(DIR_SRC)/nl/uu/cs/ssm/*.java) $(wildcard $(DIR_SRC)/nl/uu/cs/ssmui/*.java)
SSM_SRC_HELP	:= $(wildcard $(DIR_SRC)/$(DIR_HELP)/*.prop)
SSM_SRC_IMGS	:= $(wildcard $(DIR_SRC)/$(DIR_IMGS)/*.gif) $(wildcard $(DIR_SRC)/$(DIR_IMGS)/*.GIF)
SSM_MKF			:= Makefile

JAVAC_OPTS		:= -Xlint

ssm: $(SSM_JAR)

$(SSM_JAR): $(SSM_SRC) $(SSM_SRC_HELP) $(SSM_SRC_IMGS) $(SSM_MKF)
	mkdir -p $(DIR_BLD_JAR) $(DIR_BLD_JAR)/$(DIR_HELP) $(DIR_BLD_JAR)/$(DIR_IMGS)
	cp $(SSM_SRC_HELP) $(DIR_BLD_JAR)/$(DIR_HELP)
	cp $(SSM_SRC_IMGS) $(DIR_BLD_JAR)/$(DIR_IMGS)
	( echo "Manifest-Version: 1.0" ; \
	  echo "Main-Class: nl.uu.cs.ssmui.Runner" ; \
	) > $(DIR_BLD)/manifest
	javac $(JAVAC_OPTS) -d $(DIR_BLD_JAR) $(SSM_SRC) && \
	cd $(DIR_BLD_JAR) && \
	jar cmf ../manifest ../$(@F) .

clean:
	rm -rf $(DIR_BLD)

