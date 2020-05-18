//package com.redhat.emeapd.iotdemo.device.integration.service;
//
//import java.nio.file.Paths;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.event.Observes;
//import javax.inject.Inject;
//
//import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
//import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//
//import io.quarkus.runtime.ShutdownEvent;
//import io.quarkus.runtime.Startup;
//import io.quarkus.runtime.StartupEvent;
//
////@ApplicationScoped
////@Startup(value = 1)
//public class ArtemisEmbeddedService {
//
//    private EmbeddedActiveMQ embedded;
//
//    @Inject
//    Logger LOGGER;
//
//    void onStart(@Observes StartupEvent ev) {
//	LOGGER.info("The application is starting...{}");
//	try {
//	    FileUtils.deleteDirectory(Paths.get("./target/artemis").toFile());
//	    embedded = new EmbeddedActiveMQ();
//	    embedded.start();
//	} catch (Exception e) {
//	    throw new RuntimeException("Could not start embedded ActiveMQ server", e);
//	}
//    }
//
//    void onStop(@Observes ShutdownEvent ev) {
//	LOGGER.info("The application is stopping... {}");
//	try {
//	    embedded.stop();
//	} catch (Exception e) {
//	    throw new RuntimeException("Could not stop embedded ActiveMQ server", e);
//	}
//    }
//}
