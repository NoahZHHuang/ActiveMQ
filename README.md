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



# ActiveMQ Cluster Config With Zookeeper
Refer links:
https://www.cnblogs.com/ywjy/articles/5434415.html
http://www.importnew.com/23142.html

Steps
1:
	Config the zookeeper instance cluster like "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"
2:
	Config the activemq instance cluster like "failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61617,tcp://127.0.0.1:61618)"
    Need to copy 3 activemq instances and change the config files "jetty.xml" & "activemq.xml"
	In "jetty.xml", admin port set to 8161, 8162, 8163 for different instances
	In "activemq.xml" broker url port set to 61616, 61617, 61618 for different instances
	In "activemq.xml" we also need to reset the <persistenceAdapter> node, make the activemq instance work with zookeeper
	Change it from 
		<persistenceAdapter>
            <kahaDB directory="${activemq.data}/kahadb"/>
	    </persistenceAdapter>
	To
	    <persistenceAdapter>
			<replicatedLevelDB 
				directory="${activemq.data}/leveldb"
				replicas="3"
				bind="tcp://0.0.0.0:0"
				zkAddress="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"
				hostname="127.0.0.1"
				zkPath="/activemq/leveldb-stores"
			/>
        </persistenceAdapter>    
3: Start up the zookeeper instance one by one
4: Start up the activemq instance one by one (when only one instance start up, it will says "Not enough cluster members connected to elect a master")
5: When all up, zookeeper will pick up only one activemq instance to run as master, other two will be warm standby. 
   It means at a time, only one activemq instance can provide service. 
   For the admin web console, also only one of http://localhost:8161/admin/, http://localhost:8162/admin/, http://localhost:8163/admin/  will work.
   For example, if 8162 is working as master, "failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61617,tcp://127.0.0.1:61618)" will redirect the request to 61617
   Once 8162 is down, then zookeeper will raise a election process to generate a master between 8161 & 8163, usually a few seconds later, the queue will back to work.
   But based on the 2n+1 principle, 3 zk nodes can only be tolerant 1 node down, if 2 nodes down then the whole cluster structur will fail.
   

    


