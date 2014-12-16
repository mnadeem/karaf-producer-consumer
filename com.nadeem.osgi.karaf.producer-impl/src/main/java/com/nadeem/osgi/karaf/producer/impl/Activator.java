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
package com.nadeem.osgi.karaf.producer.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nadeem.osgi.karaf.producer.api.MessageService;

public class Activator implements BundleActivator, ManagedService {
	private static Logger LOGGER = LoggerFactory.getLogger(Activator.class);

	// OSGi "Persistent ID" of this bundle
	private static String CONFIG_PID = "com.nadeem.osgi.karaf.producer";
	// Default delay, in case none is specified
	private static int DEFAULT_DELAY = 5000;
	// Time to delay between publish
	private int delay = DEFAULT_DELAY;

	private ServiceRegistration<?> managedServiceRegistration;
	private ServiceRegistration<?> messageServiceRegistration;

	private Timer timer = new Timer();
	private MessagePublisher publisher = new MessagePublisher();
	private PublishTask task = new PublishTask(publisher);

	public void start(BundleContext context) {
		LOGGER.info("Starting the bundle");

		registerMessageService(context);
		registerManageService(context);

		timer.schedule(task, 0, delay);
	}

	private void registerManageService(BundleContext context) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, CONFIG_PID);
		managedServiceRegistration = context.registerService(ManagedService.class.getName(), this, properties);
	}

	private void registerMessageService(BundleContext context) {
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("description", "Producer Consumer service Config update");
		messageServiceRegistration = context.registerService(MessageService.class.getName(), publisher, props);
	}

	public void stop(BundleContext context) {
		LOGGER.info("Stoping the bundle");

		managedServiceRegistration.unregister();
		messageServiceRegistration.unregister();

		this.timer.cancel();
	}

	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		LOGGER.info("Config Update");
		if (properties != null) {
			String newDelay = (String) properties.get("delay");
			if (newDelay != null) {
				LOGGER.info("Delay Changed: {} msec", newDelay);
				delay = Integer.parseInt(newDelay);

				task.cancel();
				timer.purge();
				task = new PublishTask(publisher);
				timer.schedule(task, 0, delay);
			}
		}
	}
}
