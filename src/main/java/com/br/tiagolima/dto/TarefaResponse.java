package com.br.tiagolima.dto;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.Projeto;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        Prioridade prioridade,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm,
        ProjetoResumo projeto,
        List<EtiquetaResponse> etiquetas
) {

    public record ProjetoResumo(Long id, String nome) {
    }

    public static TarefaResponse from(Tarefa tarefa) {
        Projeto projeto = tarefa.getProjeto();
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrioridade(),
                tarefa.getCriadaEm(),
                tarefa.getAtualizadaEm(),
                projeto == null ? null : new ProjetoResumo(projeto.getId(), projeto.getNome()),
                tarefa.getEtiquetas().stream()
                        .map(EtiquetaResponse::from)
                        .sorted(Comparator.comparing(EtiquetaResponse::id))
                        .toList()
        );
    }
}
