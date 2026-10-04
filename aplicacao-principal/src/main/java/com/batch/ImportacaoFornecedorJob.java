package com.batch;

import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.repository.FornecedorRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ImportacaoFornecedorJob {

    private final FornecedorRepository fornecedorRepository;

    private static final int CHUNK_SIZE = 50;

    public ImportacaoFornecedorJob(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }


    @Bean
    public Job importarFornecedoresJob(
            JobRepository jobRepository,
            Step importarFornecedoresStep,
            Step excluirArquivoTemporarioStep) {

        return new JobBuilder("importarFornecedoresJob", jobRepository)
                .start(importarFornecedoresStep)
                .on("*").to(excluirArquivoTemporarioStep)
                .end()
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<String> fornecedorReader(
            @Value("#{jobParameters['arquivo']}") String nomeArquivo) {

        Path caminhoArquivo = Paths.get(
                System.getProperty("user.dir"),
                "src",
                "main",
                "resources",
                "temp",
                "fornecedores",
                nomeArquivo
        );

        return new FlatFileItemReaderBuilder<String>()
                .name("fornecedorReader")
                .resource(new FileSystemResource(caminhoArquivo))
                .linesToSkip(1)
                .lineMapper((line, lineNumber) -> line)
                .build();
    }

    @Bean
    public ItemProcessor<String, FornecedorEntity> fornecedorProcessor() {
        return linha -> {

            String[] campos = linha.split(",", -1);

            String nome = campos.length > 0 ? campos[0].trim() : null;
            String cnpj = campos.length > 1 ? campos[1].trim() : null;
            String telefone = campos.length > 2 ? campos[2].trim() : null;
            String email = campos.length > 3 ? campos[3].trim() : null;
            String endereco = campos.length > 4 ? campos[4].trim() : null;

            return new FornecedorEntity(
                    nome,
                    cnpj,
                    telefone,
                    email,
                    endereco
            );
        };
    }

    @Bean
    public ItemWriter<FornecedorEntity> fornecedorWriter() {
        return fornecedorRepository::saveAll;
    }

    @Bean
    public Step importarFornecedoresStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<String> fornecedorReader,
            ItemProcessor<String, FornecedorEntity> fornecedorProcessor,
            ItemWriter<FornecedorEntity> fornecedorWriter) {

        return new StepBuilder("importarFornecedoresStep", jobRepository)
                .<String, FornecedorEntity>chunk(CHUNK_SIZE, transactionManager)
                .reader(fornecedorReader)
                .processor(fornecedorProcessor)
                .writer(fornecedorWriter)
                .build();
    }

    @Bean
    public Step excluirArquivoTemporarioStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {

        return new StepBuilder("excluirArquivoTemporarioStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    JobParameters jobParameters =
                            chunkContext.getStepContext()
                                    .getStepExecution()
                                    .getJobParameters();

                    String nomeArquivo = jobParameters.getString("arquivo");

                    Path caminhoArquivo = Paths.get(
                            System.getProperty("user.dir"),
                            "src",
                            "main",
                            "resources",
                            "temp",
                            "fornecedores",
                            nomeArquivo
                    );

                    Files.deleteIfExists(caminhoArquivo);

                    System.out.println(
                            "Arquivo temporário excluído: " + caminhoArquivo
                    );

                    return org.springframework.batch.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}