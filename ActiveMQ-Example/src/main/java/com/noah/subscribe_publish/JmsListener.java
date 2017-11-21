package com.noah.subscribe_publish;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class JmsListener implements MessageListener {

	private String name;
	
	public JmsListener(String name){
		this.name = name;
	}
	
	public void onMessage(Message message) {
		try{
			System.out.println(name + " Received "+((TextMessage)message).getText());
		}catch (JMSException ex){
			ex.printStackTrace();
		}
	}

}
