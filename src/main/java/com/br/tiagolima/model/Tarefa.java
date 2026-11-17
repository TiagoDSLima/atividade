package com.br.tiagolima.model;

public record Tarefa(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        Prioridade prioridade
) {
}
