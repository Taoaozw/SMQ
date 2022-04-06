package io.github.milk.subscribe

import io.netty.handler.codec.mqtt.*

/**
 *@author CLIVE
 *@date 2022/4/3 21:35
 *@description
 *@since 1.4.1
 */
data class SubscribeOption(
    val qos: MqttQoS,
)
