# ActiveMQ

How to run ActiveMQ
{$AmqHome}/bin/win64/activemq.bat

Web admin console is http://localhost:8161/admin/ 
default admin port 8161 is configured in {$AmqHome}/conf/jetty.xml

Broker_URL tcp://127.0.0.1:61616
default broker url port 61616 is configured in {$AmqHome}/conf/activemq.xml <transportConnectors/> node

two default username & password & rule
admin: admin, admin
user: user, user
they are configured in {$AmqHome}/conf/jetty-realm.properties
