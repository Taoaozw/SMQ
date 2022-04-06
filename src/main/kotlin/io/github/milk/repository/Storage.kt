package io.github.milk.repository

import io.github.milk.message.*
import io.github.milk.subscribe.*
import java.util.concurrent.*


/**
 *@author CLIVE
 *@date 2022/4/1 23:56
 *@description
 *@since 1.4.1
 */
interface Storage {}

interface MemoryStorage : Storage {

}

class UnAckMsgMemoryStorage(
    private val unAckMsgQueue: DelayQueue<LocalDelayAckMsg> = DelayQueue()
) : MemoryStorage {

    fun listUnAckMsg(): List<LocalDelayAckMsg> {
        val list = ArrayList<LocalDelayAckMsg>()
        unAckMsgQueue.drainTo(list)
        return list
    }

}

class SubscribeStorage(
    private val subscriberMap: MutableMap<String, MutableMap<String, Subscriber>> = mutableMapOf()
) : MemoryStorage {

    fun add(subscriber: Subscriber) {
        subscriberMap.computeIfAbsent(subscriber.clientId) { mutableMapOf() }[subscriber.topic.name] = subscriber
        TrieTree.addSubscriber(subscriber)
    }

    fun remove(topic: String, clientId: String) {
        subscriberMap[clientId]?.remove(topic)
        TrieTree.removeSubscribe(Topic(topic), clientId)
    }

    fun removeClient(subscriber: Subscriber) {
        subscriberMap.remove(subscriber.clientId)?.forEach {
            TrieTree.removeSubscribe(it.value.topic, it.value.clientId)
        }
    }

    fun subscribeTree() = TrieTree

}

class ConnectActorStorage(
    private val connectActorMap: MutableMap<String, String> = mutableMapOf()
) : MemoryStorage {

    fun isClientExist(clientId: String): Boolean {
        return connectActorMap.containsKey(clientId)
    }

    operator fun get(clientId: String): String? = connectActorMap[clientId]


    operator fun set(clientId: String, actorId: String) {
        connectActorMap[clientId] = actorId
    }

    fun remove(clientId: String) = connectActorMap.remove(clientId)

}

