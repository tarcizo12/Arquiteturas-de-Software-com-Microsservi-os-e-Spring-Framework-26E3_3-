package com.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class FornecedorConsumer {

    @Value("${rabbitmq.queue.solicitar-processamento}")
    private String filaSolicitarProcessamento;

    private final JobLauncher jobLauncher;
    private final Job importarFornecedoresJob;

    public FornecedorConsumer(
            JobLauncher jobLauncher,
            Job importarFornecedoresJob) {

        this.jobLauncher = jobLauncher;
        this.importarFornecedoresJob = importarFornecedoresJob;
    }

    @RabbitListener(queues = "${rabbitmq.queue.solicitar-processamento}")
    public void receber(String mensagem) {

        System.out.println("Mensagem recebida: " + mensagem);

        try {

            JobParameters parametros = new JobParametersBuilder()
                    .addString("arquivo", mensagem)
                    .toJobParameters();

            jobLauncher.run(
                    importarFornecedoresJob,
                    parametros
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao iniciar o Job de importação de fornecedores.",
                    e
            );
        }
    }
}