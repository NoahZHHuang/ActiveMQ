package com.noah.p2p;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.noah.Constant;

public class JmsConsumer implements Runnable {

	private String name;

	public JmsConsumer(String name) {
		this.name = name;
	}

	public void run(){
		try{
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD,Constant.CONNECTION_URL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("P2P_QUEUE");
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new JmsListener(this.name));
			// consumer.receive();
			// ####### Notice ##########
			// In JMS Specification, there are 2 ways to receive a message for Consumer
			// .receive()/.receive(timeout) is a sync way, it means when it is waiting for the message, it is blocked
			// .setMessageListener is a asyn way, it means it just registers a listener to that queue, any new message comes in, the onMessage() method will be called, not blocked.
			// but sync and asyn are not compatible, set both will throw "Cannot synchronously receive a message when a MessageListener is set"
			while(true){
				
			}
		}
		catch(JMSException ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws JMSException{
		new Thread(new JmsConsumer("Consumer_1")).start();
		new Thread(new JmsConsumer("Consumer_2")).start();
	}

}
