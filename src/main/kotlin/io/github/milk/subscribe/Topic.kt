package io.github.milk.subscribe

import java.text.ParseException


/**
 *@author CLIVE
 *@date 2022/2/11 09:55
 *@description
 * <a>https://www.zybuluo.com/khan-lau/note/1325300</a>
 *@since 0.0.1
 */
data class Topic(
    val name: String,
) {
    val tokens: List<Token> = parseTopic()

    /**
     *  非标准: 不允许 单独订阅 / 字符串
     *  注意：删除第一个 / 字符  减少第一层的一个 空节点
     */
    private fun parseTopic(): List<Token> {
        if (name.isEmpty()) {
            throw ParseException(
                "Bad format of topic, topic must be at least 1 character [MQTT-4.7.3-1] and this was empty", 0
            )
        }
        val topicList = name.split("/")
        return topicList.mapIndexed { id, path ->
            when {
                path == "#" -> {
                    if (id == topicList.size - 1) {
                        Token.MUlTI
                    } else {
                        throw ParseException(
                            "Bad format of topic:[$path], # must in the end of  the topic ", 0
                        )
                    }
                }

                path == "+" -> Token.SINGLE

                path.contains("+") -> throw ParseException(
                    "Bad format of topic:[$path], + is not allowed in topic [MQTT-4.7.3-2] ", 0
                )
                path.contains("#") -> throw ParseException(
                    "Bad format of topic:[$path], # is not allowed in topic [MQTT-4.7.3-3] ", 0
                )
                path.isEmpty() -> Token.EMPTY
                else -> Token(path)
            }
        }
    }

    /**
     * subscriberTopic 是订阅者的主题 拥有 通配符
     */
    fun match(subscriberTopic: Topic): Boolean {
        val isTail = tokens.size - 1
        for ((i, token) in subscriberTopic.tokens.withIndex()) {
            if (isTail < i) return false
            return when (token) {
                Token.MUlTI -> true
                Token.SINGLE -> continue
                tokens[i] -> continue
                else -> false
            }
        }
        return subscriberTopic.tokens.size == tokens.size
    }

    override fun toString(): String {
        return "Topic(topic='$name', tokens=$tokens)"
    }

}

/***
 * @author CLIVE
 * ‘#’ -> 是用于匹配主题中任意层级的通配符. 多层通配符表示它的父级和任意数量的子层级.
 * 多层通配符必须单独指定, 或者跟在主题层级分隔符后面. 不管哪种情况, 它都必须是主题过滤器的最后一个字符
 *
 * ‘+’ -> 加号 U+002B)是只能用于单个主题层级匹配的通配符.
 * 在主题过滤器的任意层级都可以使用单层通配符, 包括第一个和最后一个层级.
 * 在使用它时, 它必须占据过滤器的整个层级 [MQTT-4.7.1-2]. 可以在主题过滤器中的多个层级中使用它, 也可以和多层通配符一起使用
 *
 *  以$开头的主题
 * 服务端不能将$字符开头的主题名匹配通配符(#或+)开头的主题过滤器 [MQTT-4.7.2-1].
 * 服务端应该阻止客户端使用这种主题名与其他客户端交换消息. 服务端实现可以将$开头的主题名用作其他目的.
 *
 *   • $SYS/被广泛用作包含服务端特定信息或控制接口的主题的前缀.
 *   • 应用不能使用$字符开头的主题.
 *   • 订阅“#”的客户端不会收到任何发布到以$开头主题的消息.
 *   • 订阅“+/monitor/Clients”的客户端不会收到任何发布到“$SYS/monitor/Clients”的消息.
 *   • 订阅“$SYS/#”的客户端会收到发布到以“$SYS/”开头主题的消息.
 *   • 订阅“$SYS/monitor/+”的客户端会收到发布到“$SYS/monitor/Clients”主题的消息.
 *   • 如果客户端想同时接受以“$SYS/”开头主题的消息和不以$开头主题的消息, 它需要同时订阅“#”和“$SYS/#”.
 */

@JvmInline
value class Token(val path: String) {

    companion object {
        val EMPTY: Token = Token("")
        val MUlTI: Token = Token("#")
        val SINGLE: Token = Token("+")

        val ROOT: Token = Token("root")
    }

    /**
     * 是否是系统的主题
     *
     */
    fun onSysTemTopic(): Boolean {
        if (path.isEmpty()) return false
        return path.first() == '$'
    }

}

fun main() {
//    val topic = "/s/"
//    println(topic.split("/"))
//    println(topic.split("/").size)
//    topic.split("/").let {
//        it.forEach { s ->
//            println(s.isBlank())
//        }
//    }
    val topic = Topic("/s/a")
    println(topic)
    val p = "\$SYS"
    println(p.first() == '$')
    val subscriber = Topic("/a/#")
    val topic2 = Topic("/a/b/c")
    println(topic2.match(subscriber))

}