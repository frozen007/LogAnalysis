#!/bin/sh
PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`

exec $PRGDIR/env.sh

$JVM_EXECUTABLE \
-Dsend.mail.script=$LOGANALYSIS_HOME/etc/sendmail.xml \
-Dperl.path=${PERL_PATH} \
-Dloganalysis.home=$LOGANALYSIS_HOME \
-cp $CLASSPATH com.myz.loganalysis.LogAnalysisMain $1 $2 $3 $4
