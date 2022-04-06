package io.github.milk.config

import com.akuleshov7.ktoml.file.*
import kotlinx.serialization.*

/**
 *@author CLIVE
 *@date 2022/4/1 22:24
 *@description
 *@since 0.0.1
 */
object ConfigManager {

    private val parser = TomlFileReader.Default

    private const val configFileName: String = "src/main/resources/config.toml"

    val property: MqProperty by lazy { parser.decodeFromFile(serializer(), configFileName) }

}

@Serializable
data class MqProperty(

    val address: Address,

    val clusterModel: Boolean = false
)

@Serializable
data class Address(

    val port: Long = 1883,

    val host: String = "127.0.0.1"
)

@Serializable
data class MqttMsgProperty(
    // Timed processing time for messages without ACK
    val unAckMsgProcessPeriod: Long = 1000
)

internal val host = ConfigManager.property.address.host

internal val port = ConfigManager.property.address.port.toInt()


fun main() {
    println(ConfigManager.property)
}


