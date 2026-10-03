package com.estoque.repository;
import com.estoque.domain.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {}
