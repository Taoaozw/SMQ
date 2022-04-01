package clive.tea.milk.commons

import clive.tea.milk.session.*
import io.vertx.core.*

/**
 *@author CLIVE
 *@date 2022/4/2 00:46
 *@description
 *@since 1.4.1
 */

fun SmqConnection.undeploy(): Future<Void> = vertx.undeploy(actorId)
