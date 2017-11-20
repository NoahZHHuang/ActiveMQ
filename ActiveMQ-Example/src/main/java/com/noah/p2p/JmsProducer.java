package com.noah.p2p;

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

public class JmsProducer implements Runnable {

	private String name;

	public JmsProducer(String name) {
		this.name = name;
	}

	public void run() {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD, Constant.CONNECTION_URL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("P2P_QUEUE");
			MessageProducer producer = session.createProducer(destination);
			while (true) {
				int secs = RandomTimeGenerator.getRandomSeconds();
				TextMessage message;
				try {
					message = session.createTextMessage("a message from Noah " + secs);
					producer.send(message);
					session.commit();
					System.out.println(this.name + " produced " + message.getText());
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

		new Thread(new JmsProducer("Producer_1")).start();
		new Thread(new JmsProducer("Producer_2")).start();

	}

}
