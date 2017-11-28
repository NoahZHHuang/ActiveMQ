package com.noah.cluster.withzookeeper;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.noah.Constant;
import com.noah.RandomTimeGenerator;

public class Producer implements Runnable {

	public void run() {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD, Constant.CONNECTION_URL_QUEUE_ALL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("P2P_QUEUE_CLUSTER");
			MessageProducer producer = session.createProducer(destination);
			while (true) {
				int secs = RandomTimeGenerator.getRandomSeconds();
				TextMessage message;
				try {
					message = session.createTextMessage("a message from Noah " + secs);
					producer.send(message);
					session.commit();
					System.out.println("Producer produced " + message.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(secs);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} catch (JMSException ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) throws JMSException {
		new Thread(new Producer()).start();
	}

}

