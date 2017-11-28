package com.noah.cluster.withzookeeper;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.noah.Constant;

public class Consumer implements Runnable {

	public void run(){
		try{
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD,Constant.CONNECTION_URL_QUEUE_ALL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("P2P_QUEUE_CLUSTER");
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener(){
				@Override
				public void onMessage(Message message) {
					try {
						System.out.println("Consumer Received "+((TextMessage)message).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
				
			});
			while(true){
				
			}
		}
		catch(JMSException ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws JMSException{
		new Thread(new Consumer()).start();
	}

}
