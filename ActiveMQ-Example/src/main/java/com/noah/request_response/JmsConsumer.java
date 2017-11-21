package com.noah.request_response;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.noah.Constant;

public class JmsConsumer implements Runnable {

	private String name;

	public JmsConsumer(String name) {
		this.name = name;
	}

	public void run() {
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD, Constant.CONNECTION_URL);
			Connection connection = factory.createConnection();
			connection.start();
			//Notice, if want to send a receipt for a message, the session must be "transacted"
			//namely the first param of connection.createSession() must be true
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination messageDestination = session.createQueue("RequestResponse_QUEUE");
			MessageConsumer consumer = session.createConsumer(messageDestination);
			consumer.setMessageListener((message) -> {
				try {
					message.acknowledge();
					TextMessage receivedMessage = (TextMessage) message;
					System.out.println(this.name + " received " + "\""+receivedMessage.getText()+"\"");
					// once the message is proceeded, send a receipt back
					TextMessage receipteMessage = session.createTextMessage(receivedMessage.getText() + " is successfully proceeded by " + this.name);
					//Notice, DONT use receivedMessage.getJMSReplyTo(), it is null. Please use message.getJMSReplyTo().
					//I think it is because the Convert "TextMessage receivedMessage = (TextMessage) message"
					//Make the JSMReplyTo lost in receivedMessage
					MessageProducer producer = session.createProducer(message.getJMSReplyTo());
					producer.send(receipteMessage);
					session.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			while (true) {

			}
		} catch (JMSException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws JMSException {
		new Thread(new JmsConsumer("Consumer_1")).start();
		new Thread(new JmsConsumer("Consumer_2")).start();
	}

}
