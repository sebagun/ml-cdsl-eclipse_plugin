export PID=`ps -ef | grep $1 | grep -v grep`
export CLASSPATH=`./classpath.ksh`
if [ "$PID" != "" ]; then
echo "El proceso ya esta levantado!"
else
echo "Levantando el proceso..."
nohup java -server -Xms256m -Xmx300m -Duser.name=VM84 -Xdebug $* 1>> /data1/resin/log/$2.log 2>> /data1/resin/log/$2.err&
#java -server -Xms768m -Xmx768m $*
fi
