package com.br.tiagolima.service;

import com.br.tiagolima.dto.TarefaRequest;
import com.br.tiagolima.exception.exceptions.RecursoNaoEncontradoException;
import com.br.tiagolima.exception.exceptions.RequisicaoInvalidaException;
import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;
import com.br.tiagolima.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    public List<Tarefa> listar(StatusTarefa status, Prioridade prioridade) {
        return repository.listar().stream()
                .filter(tarefa -> status == null || tarefa.status() == status)
                .filter(tarefa -> prioridade == null || tarefa.prioridade() == prioridade)
                .toList();
    }

    public Tarefa buscar(long id) {
        return repository.buscar(id).orElseThrow(() -> naoEncontrada(id));
    }

    public Tarefa criar(TarefaRequest request) {
        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new RequisicaoInvalidaException("O campo titulo é obrigatório");
        }
        return repository.inserir(new Tarefa(
                null,
                request.titulo().trim(),
                request.descricao(),
                Objects.requireNonNullElse(request.status(), StatusTarefa.PENDENTE),
                Objects.requireNonNullElse(request.prioridade(), Prioridade.MEDIA)
        ));
    }

    public Tarefa atualizar(long id, TarefaRequest request) {
        if (request.titulo() != null && request.titulo().isBlank()) {
            throw new RequisicaoInvalidaException("O campo titulo não pode ser vazio");
        }
        return repository.atualizar(id, atual -> new Tarefa(
                atual.id(),
                request.titulo() != null ? request.titulo().trim() : atual.titulo(),
                request.descricao() != null ? request.descricao() : atual.descricao(),
                Objects.requireNonNullElse(request.status(), atual.status()),
                Objects.requireNonNullElse(request.prioridade(), atual.prioridade())
        )).orElseThrow(() -> naoEncontrada(id));
    }

    public void excluir(long id) {
        if (!repository.remover(id)) {
            throw naoEncontrada(id);
        }
    }

    private RecursoNaoEncontradoException naoEncontrada(long id) {
        return new RecursoNaoEncontradoException("Tarefa " + id + " não encontrada");
    }
}
