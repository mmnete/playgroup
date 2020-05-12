package model

import scala.collection.mutable.ListBuffer

class messageObject (sender: String, receiver: String, sms: String) {
    val from: String = sender
    val to: String = receiver
    val message: String = sms
}

class messageBox() {


    var userMessageListStore = ListBuffer[messageObject]()


    def sendMessage(from: String, to: String, message: String)
    {
      userMessageListStore += new messageObject(from, to, message)
    }



    def getUserMessages (): ListBuffer[messageObject] =
    {
        return userMessageListStore
    }
}
