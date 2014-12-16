package com.nadeem.osgi.karaf.producer.api;

public interface MessageService {

	void addListener (MessageListener listener);
	void removeListener (MessageListener listener);
}
