package io.github.milk.message

/**
 *@author CLIVE
 *@date 2022/4/2 09:55
 *@description
 * this is a class to process different type message
 *@since 1.4.1
 */
sealed interface SmqMessage


class SmqQos0Message(val message: String) : SmqMessage


class SmqQos1Message(val message: String) : SmqMessage


class SmqQos2Message(val message: String) : SmqMessage