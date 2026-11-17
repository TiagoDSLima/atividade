package com.br.tiagolima.dto;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;

import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        Prioridade prioridade,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm
) {

    public static TarefaResponse from(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrioridade(),
                tarefa.getCriadaEm(),
                tarefa.getAtualizadaEm()
        );
    }
}
