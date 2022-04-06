package io.github.milk.server

import io.github.milk.config.*
import io.github.milk.repository.*
import io.github.milk.session.*
import io.netty.handler.codec.mqtt.*
import io.vertx.kotlin.coroutines.*
import io.vertx.mqtt.*
import mu.*

/**
 *@author CLIVE
 *@date 2022/4/1 23:54
 *@description
 *@since 1.4.1
 */
interface SmqServer {
    val clientStorage: ConnectActorStorage

    /**
     * There are two situations here:
     * 1. when a client connects, we create a new connection actor and pass it the client
     * 2. when there is  old connection actor, we close old connect then create a new one
     *    - if in a cluster mode , maybe old connection actor is in a different node
     *    - if in a single mode, remove old connection actor in local memory
     */
    fun deployConnectionActor(endpoint: MqttEndpoint)


}

class SingleSmqServer(
    override val clientStorage: ConnectActorStorage = ConnectActorStorage()
) : SmqServer, CoroutineVerticle() {

    override suspend fun start() {
        log.info("SingleMqttServer starting ...")
        MqttServer.create(vertx)
            .endpointHandler(::deployConnectionActor)
            .listen(port, host)
            .await()
    }


    override fun deployConnectionActor(endpoint: MqttEndpoint) {
        clientStorage[endpoint.clientIdentifier()]?.let {
            removeClient(it) {
                // notify client connect fail,because can't remove old connection actor
                endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE)
            }
        }
        createNewClient(endpoint)
    }


    private fun createNewClient(endpoint: MqttEndpoint) {
        vertx.deployVerticle(SmqConnection(endpoint)).onComplete {
            if (it.succeeded()) {
                log.info("create new client <${endpoint.clientIdentifier()}>")
                clientStorage[endpoint.clientIdentifier()] = it.result()
                //todo process if clean session logic
            } else {
                log.error("create new client <${endpoint.clientIdentifier()}> failed", it.cause())
                endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE)
            }
        }
    }

    private fun removeClient(connectionActorId: String, onError: () -> Unit = {}) {
        vertx.undeploy(connectionActorId).onComplete {
            if (it.succeeded()) {
                log.info("ConnectActor <$connectionActorId> remove from SmqServer")
                clientStorage.remove(connectionActorId)
            } else {
                log.error("Client <$connectionActorId> remove from SmqServer failed", it.cause())
                onError.invoke()
            }
        }

    }

    override suspend fun stop() {
        log.info("SingleMqttServer ending ...")
    }


    companion object {
        private val log = KotlinLogging.logger {}
    }
}
