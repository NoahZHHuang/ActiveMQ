package com.noah.request_response;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
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
			ConnectionFactory factory = new ActiveMQConnectionFactory(Constant.USER_NAME, Constant.PASSWORD,
					Constant.CONNECTION_URL);
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination messageDestination = session.createQueue("RequestResponse_QUEUE");
			MessageProducer producer = session.createProducer(messageDestination);
			while (true) {
				int secs = RandomTimeGenerator.getRandomSeconds();
				TextMessage sendMessage;
				try {
					sendMessage = session.createTextMessage("a message from Noah " + secs);
					// Create a temporary queue to store the receipt. Set it to the JMSReplyTo before the message is sent
					// So that the consumer knows where to reply after that message is received and processed
					Destination tempReceiptDestination = session.createTemporaryQueue();
					sendMessage.setJMSReplyTo(tempReceiptDestination);
					producer.send(sendMessage);
					session.commit();
					System.out.println(this.name + " produced " + "\""+sendMessage.getText()+"\"");
					MessageConsumer consumer = session.createConsumer(tempReceiptDestination);
					consumer.setMessageListener((receiptMessage) -> {
						try {
							System.out.println(this.name + " received the receipte " + "\""+((TextMessage) receiptMessage).getText()+"\"");
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					//Expect the receipt of the message that Producer_1 produce will be received by Producer_1
					//Same apply to Producer_2, like:
					/*
						Producer_1 produced "a message from Noah 2754"
						Producer_2 produced "a message from Noah 7880"
						Producer_1 received the receipte "a message from Noah 2754 is successfully proceeded by Consumer_1"
						Producer_2 received the receipte "a message from Noah 7880 is successfully proceeded by Consumer_2"
						Producer_1 produced "a message from Noah 1030"
						Producer_1 received the receipte "a message from Noah 1030 is successfully proceeded by Consumer_1"
						Producer_1 produced "a message from Noah 5454"
						Producer_1 received the receipte "a message from Noah 5454 is successfully proceeded by Consumer_2"
						Producer_2 produced "a message from Noah 4557"
						Producer_2 received the receipte "a message from Noah 4557 is successfully proceeded by Consumer_1"
					 */
					
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
