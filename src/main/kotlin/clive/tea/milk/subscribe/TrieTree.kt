package clive.tea.milk.subscribe

import io.netty.handler.codec.mqtt.MqttQoS


/**
 *@author CLIVE
 *@date 2022/2/11 15:01
 *@description
 *@since 0.0.1
 */
object TrieTree {

    private val root = SubscribeTreeNode(Token.ROOT)

    fun addSubscriber(subscriber: Subscriber) {
        var pointer = root
        subscriber.tokens().forEach {
            //不存在这个path
            pointer = pointer.children[it.path] ?: createNewSubNode(it, pointer)
        }
        //指针指向最后一个节点 添加订阅
        pointer.subscribers[subscriber.clientId] = subscriber
    }

    fun removeSubscribe(subscriber: Subscriber) {
        visitTopicTailNode(subscriber.topic)?.subscribers?.remove(subscriber.clientId)
    }

    /**
     * 访问 trie tree下 topic路径的最后一个节点
     */
    private fun visitTopicTailNode(topic: Topic): SubscribeTreeNode? {
        var pointer = root
        for (token in topic.tokens) {
            pointer = pointer.children[token.path] ?: return null
        }
        return pointer
    }

    private fun createNewSubNode(token: Token, pointer: SubscribeTreeNode): SubscribeTreeNode {
        val subNode = SubscribeTreeNode(token)
        pointer.children[token.path] = subNode
        return subNode
    }


    fun matchAllSubscriber(topic: Topic, callback: (MutableList<Subscriber>) -> Unit){
        val subscriberCompose = mutableListOf<Subscriber>()
        var maybeNodeLists = listOf(root)
        val tail = topic.tokens.size - 1
        topic.tokens.forEachIndexed { index, token ->
            maybeNodeLists.map {
                it.anyChildMatch(token, index == tail)?.apply {
                    maybeNodeLists = this.first
                    subscriberCompose.addAll(this.second)
                }
            }
        }
        callback(subscriberCompose)
    }

    override fun toString(): String {
        return "TrieTree(root=$root)"
    }


}

fun main() {
    TrieTree.addSubscriber(Subscriber("taozij", Topic("/a/b/c"), MqttQoS.AT_LEAST_ONCE))
    println(TrieTree.matchAllSubscriber(Topic("/a/b/c")){
        it.forEach { su ->
            println(su)
        }
    })
    println(TrieTree.toString())
    TrieTree.removeSubscribe(Subscriber("taozij", Topic("/a/b/c"), MqttQoS.AT_LEAST_ONCE))
    println(TrieTree.matchAllSubscriber(Topic("/a/b/c")){
        it.forEach { su ->
            println(su)
        }
    })
}

