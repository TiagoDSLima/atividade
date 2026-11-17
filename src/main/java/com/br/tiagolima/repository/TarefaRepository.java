package com.br.tiagolima.repository;

import com.br.tiagolima.model.Tarefa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

@Repository
public class TarefaRepository {

    private final Map<Long, Tarefa> tarefas = new ConcurrentSkipListMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    public List<Tarefa> listar() {
        return List.copyOf(tarefas.values());
    }

    public Optional<Tarefa> buscar(long id) {
        return Optional.ofNullable(tarefas.get(id));
    }

    public Tarefa inserir(Tarefa tarefa) {
        Tarefa nova = new Tarefa(
                sequencia.incrementAndGet(),
                tarefa.titulo(),
                tarefa.descricao(),
                tarefa.status(),
                tarefa.prioridade()
        );
        tarefas.put(nova.id(), nova);
        return nova;
    }

    public Optional<Tarefa> atualizar(long id, UnaryOperator<Tarefa> alteracao) {
        return Optional.ofNullable(tarefas.computeIfPresent(id, (chave, atual) -> alteracao.apply(atual)));
    }

    public boolean remover(long id) {
        return tarefas.remove(id) != null;
    }
}
