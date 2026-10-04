package com.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FornecedorProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.solicitar-processamento}")
    private String filaSolicitarProcessamento;

    public FornecedorProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(String mensagem) {
        System.out.println("Enviando mensagem: " + mensagem);
        rabbitTemplate.convertAndSend(
                filaSolicitarProcessamento,
                mensagem
        );
    }
}
