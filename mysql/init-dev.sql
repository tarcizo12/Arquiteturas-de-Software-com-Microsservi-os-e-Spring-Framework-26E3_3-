-- ============================================================
-- Inicializacao do ambiente dev
-- Idempotente: pode ser executado varias vezes sem duplicar nada
-- ============================================================

-- ------------------------------------------------------------
-- Bancos
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS estoque_principal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS movimentacao_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Usuarios e permissoes
-- ------------------------------------------------------------
CREATE USER IF NOT EXISTS 'estoque_dev'@'%' IDENTIFIED BY 'estoque_dev';
GRANT ALL PRIVILEGES ON estoque_principal.* TO 'estoque_dev'@'%';

CREATE USER IF NOT EXISTS 'movimentacao_dev'@'%' IDENTIFIED BY 'movimentacao_dev';
GRANT ALL PRIVILEGES ON movimentacao_db.* TO 'movimentacao_dev'@'%';

FLUSH PRIVILEGES;

-- ============================================================
-- BANCO: estoque_principal  (aplicacao-principal)
-- ============================================================
USE estoque_principal;

CREATE TABLE IF NOT EXISTS categoria (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    descricao  VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categoria_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fornecedor (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(150) NOT NULL,
    cnpj      VARCHAR(18)  NOT NULL,
    telefone  VARCHAR(20)  NULL,
    email     VARCHAR(150) NULL,
    endereco  VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_fornecedor_cnpj (cnpj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nome    VARCHAR(150) NOT NULL,
    login   VARCHAR(50)  NOT NULL,
    senha   VARCHAR(255) NOT NULL,
    perfil  VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Produto (abstract) com heranca SINGLE_TABLE:
--   tipo_produto = 'PERECIVEL'     -> usa data_validade e lote
--   tipo_produto = 'NAO_PERECIVEL' -> usa garantia_meses
CREATE TABLE IF NOT EXISTS produto (
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    tipo_produto        VARCHAR(20)    NOT NULL,
    nome                VARCHAR(150)   NOT NULL,
    descricao           VARCHAR(255)   NULL,
    preco               DECIMAL(12,2)  NOT NULL,
    quantidade_estoque  INT            NOT NULL DEFAULT 0,
    categoria_id        BIGINT         NOT NULL,
    fornecedor_id       BIGINT         NOT NULL,
    -- ProdutoPerecivel
    data_validade       DATE           NULL,
    lote                VARCHAR(50)    NULL,
    -- ProdutoNaoPerecivel
    garantia_meses      INT            NULL,
    PRIMARY KEY (id),
    KEY idx_produto_categoria  (categoria_id),
    KEY idx_produto_fornecedor (fornecedor_id),
    CONSTRAINT fk_produto_categoria  FOREIGN KEY (categoria_id)  REFERENCES categoria  (id),
    CONSTRAINT fk_produto_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor (id),
    CONSTRAINT ck_produto_tipo CHECK (tipo_produto IN ('PERECIVEL', 'NAO_PERECIVEL')),
    CONSTRAINT ck_produto_quantidade CHECK (quantidade_estoque >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- BANCO: movimentacao_db  (servico-movimentacao)
-- Obs: usuario_id e produto_id apontam para o banco estoque_principal.
-- Nao existe FK entre bancos diferentes: a integridade e validada
-- pela aplicacao (chamada ao servico principal).
-- ============================================================
USE movimentacao_db;

CREATE TABLE IF NOT EXISTS movimentacao (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    data_hora   DATETIME(6)  NOT NULL,
    tipo        VARCHAR(10)  NOT NULL,
    observacao  VARCHAR(255) NULL,
    usuario_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    KEY idx_movimentacao_usuario  (usuario_id),
    KEY idx_movimentacao_data     (data_hora),
    CONSTRAINT ck_movimentacao_tipo CHECK (tipo IN ('ENTRADA', 'SAIDA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS item_movimentacao (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    quantidade      INT    NOT NULL,
    movimentacao_id BIGINT NOT NULL,
    produto_id      BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_item_movimentacao (movimentacao_id),
    KEY idx_item_produto      (produto_id),
    CONSTRAINT fk_item_movimentacao FOREIGN KEY (movimentacao_id) REFERENCES movimentacao (id),
    CONSTRAINT ck_item_quantidade CHECK (quantidade > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;