package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.Fornecedor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um fornecedor")
public class FornecedorRequest {

    @Schema(description = "ID do fornecedor", example = "1")
    private Long id;

    @Schema(description = "Nome do fornecedor", example = "Distribuidora São Paulo", required = true)
    private String nome;

    @Schema(description = "CNPJ do fornecedor", example = "12.345.678/0001-90")
    private String cnpj;

    @Schema(description = "Telefone", example = "(11) 99999-8888")
    private String telefone;

    @Schema(description = "Email", example = "contato@distribuidora.com")
    private String email;

    @Schema(description = "Endereço", example = "Rua das Flores, 123 - São Paulo/SP")
    private String endereco;

    public FornecedorRequest() {}

    public FornecedorRequest(Fornecedor fornecedor) {
        this.id = fornecedor.getId();
        this.nome = fornecedor.getNome();
        this.cnpj = fornecedor.getCnpj();
        this.telefone = fornecedor.getTelefone();
        this.email = fornecedor.getEmail();
        this.endereco = fornecedor.getEndereco();
    }

    public Fornecedor toEntity() {
        Fornecedor fornecedor = new Fornecedor(this.nome, this.cnpj, this.telefone, this.email, this.endereco);
        fornecedor.setId(this.id);
        return fornecedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}