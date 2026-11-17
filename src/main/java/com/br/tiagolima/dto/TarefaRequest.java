package com.br.tiagolima.dto;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;

import java.util.Set;

public record TarefaRequest(
        String titulo,
        String descricao,
        StatusTarefa status,
        Prioridade prioridade,
        Long projetoId,
        Set<Long> etiquetaIds
) {
}
