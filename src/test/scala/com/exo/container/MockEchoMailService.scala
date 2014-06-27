package com.exo.container

import com.exo.email.EmailMessage
import com.exo.email.service.EchoMailService

class MockEchoMailService extends EchoMailService[EmailMessage]{
    def connect() {}

	def disconnect() {} 

	def isConnected() = true

	def send(message:EmailMessage) = true 
}