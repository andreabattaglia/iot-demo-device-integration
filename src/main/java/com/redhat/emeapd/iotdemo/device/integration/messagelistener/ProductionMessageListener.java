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
public class ProductionMessageListener extends AbstractMessageListener {

    @Inject
    Logger LOGGER;

    @ConfigProperty(name = "production.temperature.avg.default")
    int temperatureAvg;
    @ConfigProperty(name = "production.temperature.delta.default")
    int temperatureDelta;
    @ConfigProperty(name = "production.rpm.avg.default")
    int rpmAvg;
    @ConfigProperty(name = "production.rpm.delta.default")
    int rpmDelta;
    int temperatureMin, temperatureMax, rpmMin, rpmMax;

//    void onStart(@Observes StartupEvent ev) {} // make sure the bean is created during startup

    @PostConstruct
    @Override
    void init() {
	if (this.LOGGER.isInfoEnabled()) {
	    this.LOGGER.info(
		    "init() - temperatureAvg={}, temperatureDelta={}, rpmAvg={}, rpmDelta={}, temperatureMin={}, temperatureMax={}, rpmMin={}, rpmMax={}",
		    temperatureAvg, temperatureDelta, rpmAvg, rpmDelta, temperatureMin, temperatureMax, rpmMin, rpmMax);
	}

	super.init();
	this.LOGGER.info("PostConstruct method");

	temperatureMin = temperatureAvg - temperatureDelta - 1;
	temperatureMax = temperatureAvg + temperatureDelta + 1;
	rpmMin = rpmAvg - rpmDelta - 1;
	rpmMax = rpmAvg + rpmDelta + 1;
    }

//    @PreDestroy
//    private void preDestroy() {
//    }

    @Override
    protected Logger getLogger() {
	return LOGGER;
    }

    @Override
    protected String evaluateMessagePayload(String messagePayload) {
	LOGGER.info("Evaluating production info: \n\t{}", messagePayload);
	int temperature, rpm;
	try (JsonReader jsonReader = Json.createReader(new StringReader(messagePayload))) {
	    JsonObject jsonObject = jsonReader.readObject();
	    temperature = jsonObject.getInt("temperature");
	    rpm = jsonObject.getInt("rpm");
	}
	LOGGER.info("Detail for values: \n\t temperature = {}\n\trpm = {}", temperature, rpm);
	LOGGER.info(
		"Eveluating:\n\ttemperature value ({}) is between {} and {} (exclusive)"
			+ "\n\trpm value ({}) is between {} and {} (exclusive)",
		temperature, temperatureMin, temperatureMax, rpm, rpmMin, rpmMax);
	return Boolean.toString(
		(temperature > temperatureMin && temperature < temperatureMax) && (rpm > rpmMin && rpm < rpmMax));

    }

}
