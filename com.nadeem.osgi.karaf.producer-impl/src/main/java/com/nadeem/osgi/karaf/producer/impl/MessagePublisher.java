package com.nadeem.osgi.karaf.producer.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nadeem.osgi.karaf.producer.api.MessageListener;
import com.nadeem.osgi.karaf.producer.api.MessageService;

public class MessagePublisher implements MessageService {

	private static Logger LOGGER = LoggerFactory.getLogger(MessagePublisher.class);

	private List<MessageListener> listeners;

	public MessagePublisher() {
		this.listeners = new ArrayList<MessageListener>();
	}

	public void addListener(MessageListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(MessageListener listener) {
		this.listeners.remove(listener);
	}

	public void publish(String message) {
		LOGGER.info("Publishing message {} ", message);
		for (MessageListener listener : listeners) {
			listener.onMessage(message);
		}
	}
}
