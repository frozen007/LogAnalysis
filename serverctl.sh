#!/bin/sh
source env.sh
cd $LOGANALYSIS_HOME

SERVER_PID=server.pid

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
                rm $SERVER_PID
            fi
        fi;
        nohup $JAVA_HOME/bin/java -Dsend.mail.script=sendmail.xml -Dperl.path=${PERL_PATH} -Dloganalysis.home=$LOGANALYSIS_HOME -cp $CLASSPATH com.changyou.loganalysis.LogAnalysisMain -daemon > server.out &
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