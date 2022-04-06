package io.github.milk.commons

import io.github.milk.session.*
import io.vertx.core.*
import io.vertx.mqtt.MqttEndpoint
import io.vertx.mqtt.messages.*

/**
 *@author CLIVE
 *@date 2022/4/2 00:46
 *@description
 *@since 1.4.1
 */

fun SmqConnection.undeploy(): Future<Void> = vertx.undeploy(actorId)

fun MqttEndpoint.publish(msg: MqttPublishMessage): Future<Int> = publish(
    msg.topicName(),
    msg.payload(),
    msg.qosLevel(),
    msg.isDup,
    msg.isRetain
)
