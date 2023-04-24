package com.example.proxtalk.createMessage

class CreateMessageModel(
    var controller: CreateMessageController,
    var messageText: String = ""
) {

    fun validateMessageText(): Boolean {
        return messageText != "" && messageText.length <= 200
    }

}