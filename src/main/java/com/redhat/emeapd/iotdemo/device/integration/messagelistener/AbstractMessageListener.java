/**
 * 
 */
package com.redhat.emeapd.iotdemo.device.integration.messagelistener;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * @author abattagl
 *
 */

public abstract class AbstractMessageListener implements MessageListener {

    @ConfigProperty(name = "quarkus.artemis.url")
    String artemisUrl;
    @ConfigProperty(name = "quarkus.artemis.username")
    String artemisUsername;
    @ConfigProperty(name = "quarkus.artemis.password")
    String artemisPassword;

    @Inject
    ConnectionFactory connectionFactory;

    private JMSContext context = null;
    private JMSProducer replyProducer;
//    private MessageConsumer requestConsumer;

    void init() {
	context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
	replyProducer = context.createProducer();
    }

    @PreDestroy
    void preDestroy() {
	context.close();
    }

    abstract protected Logger getLogger();

    @Override
    public void onMessage(Message message) {

	String messagePayload = null;
	Destination replyDestination = null;
	String replyMessagePayload = null;
	TextMessage replyMessage = null;

	try {
	    messagePayload = ((TextMessage) message).getText();
	    getLogger().info("Received request message: " + messagePayload);

	    // Extract the ReplyTo destination
	    replyDestination = message.getJMSReplyTo();

//	    getLogger().info("Reply to queue: " + replyDestination);

	    replyMessagePayload = evaluateMessagePayload(messagePayload);

	    getLogger().info("Reply message payload: " + replyMessagePayload);

	    if (context == null)
		context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);

	    // Create the reply message
	    replyMessage = context.createTextMessage(replyMessagePayload);

	    // Set the CorrelationID, using message id.
	    replyMessage.setJMSCorrelationID(message.getJMSMessageID());

	    // Send out the reply message
	    replyProducer.send(replyDestination, replyMessage);

	    getLogger().info("Reply sent");
	} catch (JMSException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    protected abstract String evaluateMessagePayload(String messagePayload);
}
