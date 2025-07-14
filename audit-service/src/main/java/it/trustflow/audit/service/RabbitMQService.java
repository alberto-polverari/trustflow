package it.trustflow.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.trustflow.audit.dto.Audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class RabbitMQService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQService.class);

    public static final String AUDIT_QUEUE = "audit.logs";

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = AUDIT_QUEUE)
    public void handleAuditLog(Message message) {
        try {
            LOGGER.info("Received message: {}", message.getBody());
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            Audit log = objectMapper.readValue(json, Audit.class);
            LOGGER.info("Saving audit log: {}", log.getEventType());
            auditService.log(log, null);
            LOGGER.info("Audit log saved successfully");
        } catch (IOException e) {
            LOGGER.error("Error processing audit log message", e);
        }
    }
}
