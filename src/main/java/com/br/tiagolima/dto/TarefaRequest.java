package com.br.tiagolima.dto;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;

public record TarefaRequest(
        String titulo,
        String descricao,
        StatusTarefa status,
        Prioridade prioridade
) {
}
