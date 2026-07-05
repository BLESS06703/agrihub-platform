package com.agrihub.platform.notifications.sms

import org.slf4j.LoggerFactory

class SmsService {
    private val logger = LoggerFactory.getLogger(SmsService::class.java)
    
    suspend fun send(phoneNumber: String, message: String): Boolean {
        logger.info("Sending SMS to={}, message={}", phoneNumber, message.take(50))
        // TODO: SMS gateway integration (Airtel, TNM)
        return true
    }
}
