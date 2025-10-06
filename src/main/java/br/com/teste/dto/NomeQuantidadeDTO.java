package br.com.teste.dto;

import java.io.Serializable;

/**
 * DTO simples para transportar um par Nome/Quantidade em rankings.
 */
public class NomeQuantidadeDTO implements Serializable {

    private final String nome;
    private final Long quantidade;

    public NomeQuantidadeDTO(String nome, Long quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public Long getQuantidade() {
        return quantidade;
    }
}
