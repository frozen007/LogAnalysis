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

LOGANALYSIS_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
fi

JVM_EXECUTABLE="$JAVA_HOME"/bin/java
if [ ! -r "$JVM_EXECUTABLE" ]; then
  echo "$JVM_EXECUTABLE not exists"
fi

if [ -z "$PERL_HOME" ]; then
  echo "PERL_HOME not exists"
fi
PERL_PATH=/home/webadmin/perl-5.16/bin/perl

#SET CLASSPATH
CLASSPATH=.
LS_LIBS=`ls $LOGANALYSIS_HOME/libs`
for jar in $LS_LIBS; do
  CLASSPATH=$CLASSPATH:"$LOGANALYSIS_HOME/libs/"$jar
done
