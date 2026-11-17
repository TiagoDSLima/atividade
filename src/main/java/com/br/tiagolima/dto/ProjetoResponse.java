package com.br.tiagolima.dto;

import com.br.tiagolima.model.Projeto;

public record ProjetoResponse(Long id, String nome, String descricao) {

    public static ProjetoResponse from(Projeto projeto) {
        return new ProjetoResponse(projeto.getId(), projeto.getNome(), projeto.getDescricao());
    }
}
