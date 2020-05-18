/**
 * 
 */
package com.redhat.emeapd.iotdemo.device.integration.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.redhat.emeapd.iotdemo.device.integration.messagelistener.CoolingMessageListener;
import com.redhat.emeapd.iotdemo.device.integration.messagelistener.ProductionMessageListener;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;

/**
 * @author abattagl
 * 
 * @see https://stackoverflow.com/questions/47617617/camel-in-out-messages-and-correlation-id-confusion
 *
 */
@ApplicationScoped
public class IntegrationService {
    @ConfigProperty(name = "production.queue.name", defaultValue = "production")
    String productionQueueName;
    @ConfigProperty(name = "cooling.queue.name", defaultValue = "cooling")
    String coolingQueueName;

    @Inject
    ConnectionFactory connectionFactory;

    private JMSContext context = null;
    private Queue productionQueue = null;
    private Queue coolingQueue = null;
    private JMSConsumer productionConsumer;
    private JMSConsumer coolingConsumer;
    final Map<String, TextMessage> requestMap = new HashMap<>();

    @Inject
    ProductionMessageListener productionMessageListener;
    @Inject
    CoolingMessageListener coolingMessageListener;

    @Inject
    Logger LOGGER;

    void onStart(@Observes StartupEvent ev) {
	LOGGER.info("The application is starting...{}");
    }

    void onStop(@Observes ShutdownEvent ev) {
	LOGGER.info("The application is stopping... {}");
    }

    @PostConstruct
    void postConstruct() {
	LOGGER.info("PostConstruct method");
	
//	try {
//	    context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
//	    productionQueue = context.createQueue(productionQueueName);
//	    coolingQueue = context.createQueue(coolingQueueName);
//
//	    productionConsumer = context.createConsumer(productionQueue);
//	    productionConsumer.setMessageListener(productionMessageListener);
//
//	    coolingConsumer = context.createConsumer(coolingQueue);
//	    coolingConsumer.setMessageListener(coolingMessageListener);
//	} catch (Exception e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
    }

    private final AtomicBoolean initialize = new AtomicBoolean(true);

    @Scheduled(every = "1s")
    void init() {
	if (initialize.getAndSet(false))
	    try {
		context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
		productionQueue = context.createQueue(productionQueueName);
		coolingQueue = context.createQueue(coolingQueueName);

		productionConsumer = context.createConsumer(productionQueue);
		productionConsumer.setMessageListener(productionMessageListener);

		coolingConsumer = context.createConsumer(coolingQueue);
		coolingConsumer.setMessageListener(coolingMessageListener);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
    }

    @PreDestroy
    void preDestroy() {
	context.close();
    }

}
