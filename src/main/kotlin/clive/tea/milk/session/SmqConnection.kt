package clive.tea.milk.session

import io.vertx.kotlin.coroutines.*
import io.vertx.mqtt.*

/**
 *@author CLIVE
 *@date 2022/4/2 00:05
 *@description
 *@since 1.4.1
 */
class SmqConnection(private val endpoint: MqttEndpoint) : CoroutineVerticle(), MqttEndpoint by endpoint {

    val actorId = deploymentID


}