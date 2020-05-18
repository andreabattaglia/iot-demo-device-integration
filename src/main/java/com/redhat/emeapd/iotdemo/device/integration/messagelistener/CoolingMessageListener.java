/**
 * 
 */
package com.redhat.emeapd.iotdemo.device.integration.messagelistener;

import java.io.StringReader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;

/**
 * @author abattagl
 *
 */
@ApplicationScoped
public class CoolingMessageListener extends AbstractMessageListener {

    @Inject
    Logger LOGGER;
    @ConfigProperty(name = "cooling.decrement.avg.default")
    int coolingAvg;
    @ConfigProperty(name = "cooling.decrement.delta.default")
    int coolingDelta;
    int coolingMin, coolingMax;

//    void onStart(@Observes StartupEvent ev) {} // make sure the bean is created during startup

    @PostConstruct
    @Override
    void init() {
	if (this.LOGGER.isInfoEnabled()) {
	    this.LOGGER.info("init() - coolingAvg={}, coolingDelta={}, coolingMin={}, coolingMax={}", coolingAvg,
		    coolingDelta, coolingMin, coolingMax);
	}

	super.init();
	this.LOGGER.info("PostConstruct method");

	coolingMin = coolingAvg - coolingDelta - 1;
	coolingMax = coolingAvg + coolingDelta + 1;
    }

    @Override
    protected Logger getLogger() {
	return LOGGER;
    }

    @Override
    protected String evaluateMessagePayload(String messagePayload) {
	LOGGER.info("Received cooling info: \n\t{}", messagePayload);
	int cooling=0;
	try (JsonReader jsonReader = Json.createReader(new StringReader(messagePayload))) {
	    JsonObject jsonObject = jsonReader.readObject();
	    cooling = jsonObject.getInt("cooling");
	}

	String valid = Boolean.toString(cooling > coolingMin && cooling < coolingMax);
	LOGGER.info("Eveluating if cooling value ({}) is between {} and {} (exclusive): {}", cooling, coolingMin, coolingMax, valid);
	return valid;

    }
}
