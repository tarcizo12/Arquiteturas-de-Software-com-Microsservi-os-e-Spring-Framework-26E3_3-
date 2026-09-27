package com.gerenciador.estoque.repository;

import com.gerenciador.estoque.domain.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}