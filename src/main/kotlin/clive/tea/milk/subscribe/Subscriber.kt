package clive.tea.milk.subscribe

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.*

/**
 *@author CLIVE
 *@date 2022/2/11 13:32
 *@description
 *@since 0.0.1
 */
class Subscriber(
    val clientId: String,
    val topic: Topic,
    val qos: MqttQoS,
    private val connectActorId: String? = null
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
}