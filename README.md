# iot-demo-device-integration
integration tests for the project iot-demo-device

1 - Start a local instance of Active_MQ Artemis:
    podman run -it --rm -p 8161:8161 -p 61616:61616 -e ARTEMIS_USERNAME=quarkus -e ARTEMIS_PASSWORD=quarkus vromero/activemq-artemis:2.9.0-alpine

2 - run the integration tests app