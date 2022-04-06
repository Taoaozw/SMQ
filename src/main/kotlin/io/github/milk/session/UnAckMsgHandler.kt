package io.github.milk.session

import io.github.milk.commons.*
import io.github.milk.message.*
import io.github.milk.repository.*
import io.vertx.kotlin.coroutines.*
import io.vertx.mqtt.MqttEndpoint
import mu.*

/**
 *@author CLIVE
 *@date 2022/4/5 19:43
 *@description
 * Server need process two unAck msg
 * 1 publish msg
 * 2 pubRel msg
 *@since 1.4.1
 */
class UnAckMsgHandler(
    private val endpoint: MqttEndpoint,
    private val unAckStorage: UnAckMsgMemoryStorage,
) : CoroutineVerticle() {

    private var isContinue: Boolean = true

    override suspend fun start() {
        log.info("UnAckMsgHandler started....")
        vertx.setPeriodic(1000) { id ->
            if (isContinue) {
                unAckStorage.listUnAckMsg().map {
                    when (it) {
                        is LocalUnAckPublishMsg -> endpoint.publish(it.msg)
                        is LocalUnAckPubRelMsg -> endpoint.publishRelease(it.msg.messageId())
                        else -> {
                            log.error("UnAckMsgHandler error msg:$it")
                        }
                    }
                }
            } else {
                vertx.cancelTimer(id)
            }
        }
    }

    override suspend fun stop() {
        log.info("UnAckMsgHandler stopping....")
        isContinue = false
    }


    companion object {
        private val log = KotlinLogging.logger {}
    }


}