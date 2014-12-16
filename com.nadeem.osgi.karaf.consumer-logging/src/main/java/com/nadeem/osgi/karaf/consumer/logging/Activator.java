/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nadeem.osgi.karaf.consumer.logging;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nadeem.osgi.karaf.producer.api.MessageListener;
import com.nadeem.osgi.karaf.producer.api.MessageService;

public class Activator implements BundleActivator , MessageListener {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Activator.class.getName());
	
	public void start(BundleContext context) {
		LOGGER.info("Starting the bundle");
		
		// Get a service tracker for the Tick service, using its
		// class name
		ServiceTracker<MessageService,MessageService> tracker = new ServiceTracker<MessageService, MessageService>(context, MessageService.class.getName(), null);
		// Start the tracker
		tracker.open();
		// Find the service's API from the tracker. If the tick service
		// is not running, we get a null here
		MessageService producerService =  tracker.getService();
		// Stop the tracker, since we're done with it
		tracker.close();
		// If we got a reference to the tick service, then call its
		// addListener method to register this class as a receiver of
		// tick events. Otherwise, throw an exception
		if (producerService != null)
			producerService.addListener(this);
		else
			throw new UnsupportedOperationException("Can't start consumer bundle, as producer service is not running");
	}

	public void stop(BundleContext context) {
		LOGGER.info("Stopping the bundle");
	}

	public void onMessage(String message) {
		LOGGER.info("Consumer Received Message: {}", message);		
	}
}