
package com.fornecedor.service;

import com.fornecedor.domain.dto.FornecedorResponse;
import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.repository.FornecedorRepository;
import com.messaging.FornecedorProducer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    private final FornecedorProducer producer;

    @Autowired
    public FornecedorService(FornecedorRepository fornecedorRepository,FornecedorProducer producer) {
        this.repository = fornecedorRepository;
        this.producer = producer;
    }

    public List<FornecedorResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(fornecedor -> new FornecedorResponse(
                        fornecedor.getId(),
                        fornecedor.getNome(),
                        fornecedor.getCnpj(),
                        fornecedor.getTelefone(),
                        fornecedor.getEmail(),
                        fornecedor.getEndereco()
                ))
                .toList();
    }

    public FornecedorEntity getFornecedorById(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Fornecedor não encontrado para o id " + id));
    }


    public String salvarArquivoTemporariamente(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo CSV não pode estar vazio.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null || !nomeOriginal.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("O arquivo deve possuir extensão .csv.");
        }

        try {
            Path diretorio = Paths.get(
                    System.getProperty("user.dir"),
                    "src",
                    "main",
                    "resources",
                    "temp",
                    "fornecedores"
            );

            Files.createDirectories(diretorio);

            String nomeArquivo = UUID.randomUUID() + ".csv";

            Path arquivoTemporario = diretorio.resolve(nomeArquivo);

            arquivo.transferTo(arquivoTemporario.toFile());


            producer.enviar(nomeArquivo);
            return nomeArquivo;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível salvar o arquivo CSV temporariamente.",
                    e
            );
        }
    }

}
