package io.github.milk.session

import io.github.milk.repository.*
import io.github.milk.subscribe.*
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.kotlin.coroutines.*
import io.vertx.mqtt.*
import io.vertx.mqtt.messages.codes.*
import mu.*

/**
 *@author CLIVE
 *@date 2022/4/2 00:05
 *@description
 *@since 1.4.1
 */
class SmqConnection(
    private val endpoint: MqttEndpoint,
    private val smqMsgStorage: UnAckMsgMemoryStorage = UnAckMsgMemoryStorage(),
    private val subscribeStorage: SubscribeStorage = SubscribeStorage()
) : CoroutineVerticle(), MqttEndpoint by endpoint {

    val actorId get() = deploymentID

    override suspend fun start() {
        //auto ack publish and subscribe
        publishAutoAck(true)
        subscriptionAutoAck(true)

        subscribeHandler()
        unsubscribeHandler()
        publishHandler()
        pubBackMsg()

        disConnection()
        closeConnection()
        exceptionProcess()
        accept(true)
    }

    override suspend fun stop() {
        disconnect(MqttDisconnectReasonCode.NORMAL, null)
        log.info("Client <${endpoint.clientIdentifier()}> left the MQTT server !")
    }

    private fun subscribeHandler() = subscribeHandler {
        it.topicSubscriptions().forEach { sub ->
            log.info("Client <${clientIdentifier()}> add new subscription <${sub.topicName()}>")
            subscribeStorage.add(Subscriber.of(sub, clientIdentifier(), actorId))
        }
    }

    private fun unsubscribeHandler() = unsubscribeHandler { msg ->
        msg.topics().forEach { topic ->
            log.info(" client <${clientIdentifier()}> unsubscribe topic: <$topic>")
            subscribeStorage.remove(topic, clientIdentifier())
        }
    }

    private fun publishHandler() = publishHandler { msg ->
        subscribeStorage.subscribeTree().matchAllSubscriber(Topic(msg.topicName())) {
            it.map { subscriber ->
                if (subscriber.connectActorId == actorId) {
                    publish(subscriber.topic.name, msg.payload(), subscriber.qos, false, false) { ar ->
                        if (ar.succeeded()) {
                            log.info("publish to <${subscriber.topic.name}> succeed")
                            when (msg.qosLevel()) {
                                MqttQoS.AT_LEAST_ONCE -> {

                                }
                                MqttQoS.EXACTLY_ONCE -> {}
                                else -> {
                                    //ignore
                                }
                            }
                        } else {
                            log.error("publish to <${subscriber.topic.name}> failed")
                        }
                    }
                } else {
                    vertx.eventBus().publish(subscriber.connectActorId, msg.payload())
                }
            }
        }
    }

    private fun receiveMessageHandler() = publishReceivedMessageHandler {

    }

    private fun pubBackMsg() = publishAcknowledgeHandler {

    }

    private fun exceptionProcess() = exceptionHandler {
        log.error("Connection actor: ${clientIdentifier()}  receive a exception msg \n { ${it.message} : ${it.printStackTrace()}}")
    }

    private fun disConnection() = disconnectHandler {
        log.error("Client <${clientIdentifier()}> disconnect from the MQTT server !")
        vertx.undeploy(actorId).onComplete {

        }
    }

    private fun closeConnection() = closeHandler {
        log.info("Client <${clientIdentifier()}> close the MQTT connection !")
        vertx.undeploy(actorId)
    }


    companion object {
        private val log = KotlinLogging.logger {}
    }
}

fun main() {
}