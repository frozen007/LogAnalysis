#!/bin/sh
source env.sh
cd $LOGANALYSIS_HOME
$JAVA_HOME/bin/java -Dsend.mail.script=testsendmail.xml -Dperl.path=${PERL_PATH} -Dloganalysis.home=$LOGANALYSIS_HOME -cp $CLASSPATH com.changyou.loganalysis.LogAnalysisMain $1 $2 $3 $4
