package clive.tea.milk.server

import clive.tea.milk.commons.*
import clive.tea.milk.config.*
import clive.tea.milk.repository.*
import clive.tea.milk.session.*
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
    val clientStorage: ClientStorage

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
    override val clientStorage: ClientStorage = ClientStorage()
) : SmqServer, CoroutineVerticle() {

    override suspend fun start() {
        log.info("SingleMqttServer starting ...")
        MqttServer.create(vertx)
            .endpointHandler(::deployConnectionActor)
            .listen(port, host)
            .await()
    }


    override fun deployConnectionActor(endpoint: MqttEndpoint) {
        val clientId = endpoint.clientIdentifier()
        clientStorage.getClient(clientId)?.undeploy()?.onComplete {
            if (it.succeeded()) {
                clientStorage.removeClient(clientId)
                clientStorage.addClient(clientId, SmqConnection(endpoint))
            } else {
                // notify client connect fail  because can't remove old connection actor
                endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE)
            }

        } ?: clientStorage.addClient(clientId, SmqConnection(endpoint))
    }


    override suspend fun stop() {
        log.info("SingleMqttServer ending ...")
    }


    companion object {
        private val log = KotlinLogging.logger {}
    }
}