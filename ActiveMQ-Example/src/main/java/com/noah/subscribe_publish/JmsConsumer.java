package com.noah.subscribe_publish;

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
			Destination destination = session.createTopic("SubPub_Topic");
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new JmsListener(this.name));
			while(true){
				
			}
		}
		catch(JMSException ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws JMSException{
		new Thread(new JmsConsumer("Subscriber_1")).start();
		new Thread(new JmsConsumer("Subscriber_2")).start();
	}
	
}
