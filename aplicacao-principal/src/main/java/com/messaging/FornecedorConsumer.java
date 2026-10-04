package com.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FornecedorConsumer {

    @Value("${rabbitmq.queue.solicitar-processamento}")
    private String filaSolicitarProcessamento;

    @RabbitListener(queues = "${rabbitmq.queue.solicitar-processamento}")
    public void receber(String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);
    }
}