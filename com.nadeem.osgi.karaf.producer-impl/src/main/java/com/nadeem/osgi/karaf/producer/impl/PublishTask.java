package com.nadeem.osgi.karaf.producer.impl;


public class PublishTask extends java.util.TimerTask {

	private MessagePublisher publisher;

	public PublishTask(MessagePublisher publisher) {
		this.publisher =  publisher;
	}

	@Override
	public void run() {
		publisher.publish("Hello");		
	}
}
