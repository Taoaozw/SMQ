package io.github.milk.subscribe

import io.netty.handler.codec.mqtt.*
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.*
import io.vertx.mqtt.MqttTopicSubscription

/**
 *@author CLIVE
 *@date 2022/2/11 13:32
 *@description
 *@since 0.0.1
 */
class Subscriber(
    val qos: MqttQoS,
    val topic: Topic,
    val clientId: String,
    val connectActorId: String,
    val option: SubscribeOption? = null,
) {

    var isPersistMsg: Boolean? = null

    private val keepSessionMsgList: MutableList<JsonObject> by lazy { mutableListOf() }

    fun tokens() = topic.tokens

    fun json(): JsonObject = json {
        obj {
            put("clientId", clientId)
            put("topic", topic.name)
            put("qos", qos.value())
            put("connectActorId", connectActorId)
        }
    }

    companion object {
        fun of(subscription: MqttTopicSubscription, clientId: String, connectActorId: String) =
            Subscriber(subscription.qualityOfService(), Topic(subscription.topicName()), clientId, connectActorId)

    }
}