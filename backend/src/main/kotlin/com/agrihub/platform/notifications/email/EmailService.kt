package com.agrihub.platform.notifications.email

import org.slf4j.LoggerFactory

class EmailService {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    
    suspend fun send(to: String, subject: String, bodyHtml: String, bodyText: String? = null): Boolean {
        logger.info("Sending email to={}, subject={}", to, subject)
        // TODO: SMTP integration
        return true
    }
    
    suspend fun sendTemplate(to: String, templateCode: String, variables: Map<String, String>): Boolean {
        logger.info("Sending template email: code={}, to={}", templateCode, to)
        return true
    }
}
