package com.gerenciador.estoque.domain.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, length = 18)
    private String cnpj;

    private String telefone;
    private String email;
    private String endereco;

    @OneToMany(mappedBy = "fornecedor")
    private List<Produto> produtos = new ArrayList<>();

    public Fornecedor() {}

    public Fornecedor(String nome, String cnpj, String telefone, String email, String endereco) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
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

    @Override
    public String toString() {
        return "Fornecedor{\n" +
                "  id=" + id + ",\n" +
                "  nome='" + nome + "',\n" +
                "  cnpj='" + cnpj + "',\n" +
                "  telefone='" + telefone + "',\n" +
                "  email='" + email + "',\n" +
                "  endereco='" + endereco + "'\n" +
                '}';
    }
}