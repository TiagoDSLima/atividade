package com.br.tiagolima.service;

import com.br.tiagolima.dto.TarefaRequest;
import com.br.tiagolima.dto.TarefaResponse;
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

    public List<TarefaResponse> listar(StatusTarefa status, Prioridade prioridade) {
        return repository.listar(status, prioridade).stream()
                .map(TarefaResponse::from)
                .toList();
    }

    public TarefaResponse buscar(long id) {
        return repository.buscar(id)
                .map(TarefaResponse::from)
                .orElseThrow(() -> naoEncontrada(id));
    }

    public TarefaResponse criar(TarefaRequest request) {
        validarCompleto(request);
        Tarefa tarefa = new Tarefa();
        preencher(tarefa, request);
        return TarefaResponse.from(repository.inserir(tarefa));
    }

    /** PUT: substitui todos os campos da tarefa. */
    public TarefaResponse substituir(long id, TarefaRequest request) {
        validarCompleto(request);
        return repository.atualizar(id, tarefa -> preencher(tarefa, request))
                .map(TarefaResponse::from)
                .orElseThrow(() -> naoEncontrada(id));
    }

    /** PATCH: altera apenas os campos informados. */
    public TarefaResponse atualizar(long id, TarefaRequest request) {
        if (request.titulo() != null) {
            validarTitulo(request.titulo());
        }
        validarDescricao(request.descricao());
        return repository.atualizar(id, tarefa -> {
            if (request.titulo() != null) {
                tarefa.setTitulo(request.titulo().trim());
            }
            if (request.descricao() != null) {
                tarefa.setDescricao(request.descricao());
            }
            if (request.status() != null) {
                tarefa.setStatus(request.status());
            }
            if (request.prioridade() != null) {
                tarefa.setPrioridade(request.prioridade());
            }
        }).map(TarefaResponse::from).orElseThrow(() -> naoEncontrada(id));
    }

    public void excluir(long id) {
        if (!repository.remover(id)) {
            throw naoEncontrada(id);
        }
    }

    private void validarCompleto(TarefaRequest request) {
        if (request.titulo() == null) {
            throw new RequisicaoInvalidaException("O campo titulo é obrigatório");
        }
        validarTitulo(request.titulo());
        validarDescricao(request.descricao());
    }

    private void preencher(Tarefa tarefa, TarefaRequest request) {
        tarefa.setTitulo(request.titulo().trim());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(Objects.requireNonNullElse(request.status(), StatusTarefa.PENDENTE));
        tarefa.setPrioridade(Objects.requireNonNullElse(request.prioridade(), Prioridade.MEDIA));
    }

    private void validarTitulo(String titulo) {
        String valor = titulo.trim();
        if (valor.isEmpty()) {
            throw new RequisicaoInvalidaException("O campo titulo não pode ser vazio");
        }
        if (valor.length() > Tarefa.TAMANHO_TITULO) {
            throw new RequisicaoInvalidaException(
                    "O campo titulo deve ter no máximo " + Tarefa.TAMANHO_TITULO + " caracteres");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > Tarefa.TAMANHO_DESCRICAO) {
            throw new RequisicaoInvalidaException(
                    "O campo descricao deve ter no máximo " + Tarefa.TAMANHO_DESCRICAO + " caracteres");
        }
    }

    private RecursoNaoEncontradoException naoEncontrada(long id) {
        return new RecursoNaoEncontradoException("Tarefa " + id + " não encontrada");
    }
}
