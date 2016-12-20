#!/bin/sh
start(){
	nohup java -Xms800m -Xms2048m -Xss100m -jar netty5server-0.0.1-SNAPSHOT.jar 5 > log.log &
	tail -f log.log
}

stop(){
	ps -ef|grep netty5server|awk '{print $2}'|while read pid
	do
		kill -9 $pid
	done
}

case "$1" in
start)
	start
	;;
stop)
	stop
	;;
restart)
	stop
	start
	;;
*)
	printf 'Choose: $s {start|stop|restart}\n' "$prog"
	exit 1
	;;
esac
