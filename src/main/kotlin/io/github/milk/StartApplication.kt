package io.github.milk

import io.github.milk.config.*
import io.github.milk.server.*
import io.vertx.core.*


/**
 *@author CLIVE
 *@date 2022/3/30 22:10
 *@description
 *@since 1.4.1
 */

class StartApplication {

}

fun main() {
    if (ConfigManager.property.clusterModel) {

    } else {
        Vertx.vertx().deployVerticle(SingleSmqServer())
    }
}