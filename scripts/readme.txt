first,you must modify you c3p0-config.xml and server-config.xml config,include ip port and database config.
If you want to replace you jar inner file ,just like xml file ,you should use this command.


jar -uvf netty5server-0.0.1-SNAPSHOT.jar server-config.xml
jar -uvf netty5server-0.0.1-SNAPSHOT.jar c3p0-config.xml


1.run

sh server-netty.sh start


2.stop

sh server-netty.sh stop


3.restart

sh server-netty.sh restart

4.is setup ok ?

ps aux|grep netty5





#auther luanx
#2015-08-21

