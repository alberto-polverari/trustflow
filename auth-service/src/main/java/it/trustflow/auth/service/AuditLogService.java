package it.trustflow.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.trustflow.auth.dto.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class AuditLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);
    public static final String AUDIT_QUEUE = "audit.logs";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendAuditLog(AuditLog log, HttpServletRequest request) {
        log.setIp(getClientIp(request));
        LOGGER.info("Sending audit log: {}", log.getEventType());
        try {
            String json = objectMapper.writeValueAsString(log);
            Message message = MessageBuilder
                .withBody(json.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
            LOGGER.info("Queue message built successfully");
            rabbitTemplate.send(AUDIT_QUEUE, message);
            LOGGER.info("Audit log sent successfully");
        } catch (JsonProcessingException e) {
            LOGGER.error("Error sending message to queue", e);
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // se ci sono più IP, prendi il primo (l’originale)
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr(); // fallback
    }

}
