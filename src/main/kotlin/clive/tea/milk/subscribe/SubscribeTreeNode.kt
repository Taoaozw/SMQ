package mil.clive.mqtt.commons.topic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 *@author CLIVE
 *@date 2022/2/11 11:48
 *@description
 *@since 0.0.1
 */
open class SubscribeTreeNode(
    private val token: Token? = null
) {

    val subscribers: MutableMap<String, Subscriber> = ConcurrentHashMap()

    val children: MutableMap<String, SubscribeTreeNode> = ConcurrentHashMap()

    fun anyChildMatch(matchToken: Token, isLast: Boolean): Pair<List<SubscribeTreeNode>, List<Subscriber>>? {
        if (children.isEmpty()) return null
        val subNodes = mutableListOf<SubscribeTreeNode>()
        val composeSubscriber = mutableListOf<Subscriber>()
        children[Token.MUlTI.path]?.let {
            //todo 如果是第一个 TOKEN 是$系统开头的 不能用 # 进行匹配
            it.composeSubscriberWhenMatch(isLast)?.let { subs -> composeSubscriber.addAll(subs) }
        }
        children[matchToken.path]?.let {
            subNodes.add(it)
            it.composeSubscriberWhenMatch(isLast)?.let { subs -> composeSubscriber.addAll(subs) }
        }
        children[Token.SINGLE.path]?.let {
            subNodes.add(it)
            it.composeSubscriberWhenMatch(isLast)?.let { subs -> composeSubscriber.addAll(subs) }
        }
        return Pair(subNodes, composeSubscriber)
    }

    private fun composeSubscriberWhenMatch(isLast: Boolean): MutableList<Subscriber>? =
        when (token) {
            Token.MUlTI -> subscribers.values.toMutableList()
            else -> if (isLast) subscribers.values.toMutableList() else null
        }

    override fun toString(): String {
        return "SubscribeTreeNode(token=$token, subscribers=$subscribers, children=$children)"
    }


}

class AtomicNode(subNode: SubscribeTreeNode) {

    private val mainNode: AtomicReference<SubscribeTreeNode> = when (subNode) {
        is TNode -> throw IllegalArgumentException("TNode is not supported to set as main node")
        else -> AtomicReference(subNode)
    }

    fun get(): SubscribeTreeNode = mainNode.get()

    fun compareAndSet(expected: SubscribeTreeNode, update: SubscribeTreeNode): Boolean =
        mainNode.compareAndSet(expected, update)

    fun compareAndSet(expected: SubscribeTreeNode, update: TNode): Boolean = mainNode.compareAndSet(expected, update)

}

class TNode(children: MutableMap<Char, SubscribeTreeNode>) : SubscribeTreeNode() {

}

enum class MatchResult {
    MATCH,
    NO_MATCH,
    INCOMPLETE
}

