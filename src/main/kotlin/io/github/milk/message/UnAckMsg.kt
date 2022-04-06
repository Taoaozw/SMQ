package io.github.milk.message

import io.vertx.mqtt.messages.*
import io.vertx.mqtt.messages.impl.*
import java.time.Instant
import java.util.concurrent.*

/**
 *@author CLIVE
 *@date 2022/4/5 20:32
 *@description
 *@since 1.4.1
 */

sealed interface UnAckMsg {
    val executeTime: Long
}

abstract class LocalDelayAckMsg : UnAckMsg, Delayed {

    override fun compareTo(other: Delayed): Int {
        val rs = this.executeTime - (other as UnAckMsg).executeTime
        return if (rs == 0L) {
            0
        } else if (rs > 0L) {
            1
        } else {
            -1
        }
    }

    override fun getDelay(unit: TimeUnit): Long {
        return unit.convert(executeTime - Instant.now().toEpochMilli(), TimeUnit.MILLISECONDS)
    }
}

class LocalUnAckPublishMsg(
    val msg: MqttPublishMessage,
    override val executeTime: Long,
) : LocalDelayAckMsg() {

}

class LocalUnAckPubRelMsg(
    val msg: MqttPubRelMessage,
    override val executeTime: Long,
) : LocalDelayAckMsg()