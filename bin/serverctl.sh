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

SERVER_PID=$LOGANALYSIS_HOME/server.pid

usage()
{
    echo "Usage: $0 {start|stop}"
    exit 1
}

if [ $# -lt 0 ] ; then
    usage
fi

ACTION=$1

case "$ACTION" in
    start)
        if [ -f $SERVER_PID ] ; then
            if [ "$(ps -p `cat $SERVER_PID` | wc -l)" -gt 1 ]; then
                # process is still running
                echo "Already Running!"
                exit 1
            else
                # process not running, but PID file was not deleted
                echo "Server was not stopped correctly. Removing old pid file"
                rm -f $SERVER_PID
            fi
        fi;
        nohup $JVM_EXECUTABLE -Dsend.mail.script=$LOGANALYSIS_HOME/etc/sendmail.xml -Djava.io.tmpdir=$LOGANALYSIS_HOME/tmp \
            -Dperl.path=${PERL_PATH} -Dloganalysis.home=$LOGANALYSIS_HOME \
            -cp $CLASSPATH com.myz.loganalysis.LogAnalysisMain -daemon > $LOGANALYSIS_HOME/server.out &
        PID=$!
        echo $PID > $SERVER_PID
        echo "PID=$PID"
        ;;
    stop)
        if [ -f $SERVER_PID ] ; then
            PID=`cat $SERVER_PID 2>/dev/null`
            echo "Shutting down server:$PID"
            kill $PID 2>/dev/null
            rm -f $SERVER_PID
            echo "Server stopped.";
        else
            echo "Server is not running"
        fi
        ;;
        
    *)
        usage
        ;;
        
esac

exit 0