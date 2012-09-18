#!/bin/sh
source env.sh

cd $LOGANALYSIS_HOME

$JAVA_HOME/bin/java -Dsend.mail.script=sendmail.xml -Dperl.path=/usr/bin/perl -Dloganalysis.home=$LOGANALYSIS_HOME -cp $CLASSPATH com.changyou.loganalysis.LogAnalysisMain $1 $2 $3 $4

