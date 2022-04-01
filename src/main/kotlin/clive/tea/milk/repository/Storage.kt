package clive.tea.milk.repository

import clive.tea.milk.session.*

/**
 *@author CLIVE
 *@date 2022/4/1 23:56
 *@description
 *@since 1.4.1
 */
interface Storage {}

interface MemoryStorage : Storage {

}

class ClientStorage(
    private val clientMap: MutableMap<String, SmqConnection> = mutableMapOf()
) : MemoryStorage {

    fun isClientExist(clientId: String): Boolean {
        return clientMap.containsKey(clientId)
    }

    fun getClient(clientId: String): SmqConnection? = clientMap[clientId]


    fun addClient(clientId: String, client: SmqConnection) {
        clientMap[clientId] = client
    }

    fun removeClient(clientId: String) = clientMap.remove(clientId)

}

