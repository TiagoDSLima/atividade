package com.br.tiagolima.service;

import com.br.tiagolima.dto.TarefaRequest;
import com.br.tiagolima.dto.TarefaResponse;
import com.br.tiagolima.exception.exceptions.RecursoNaoEncontradoException;
import com.br.tiagolima.exception.exceptions.RequisicaoInvalidaException;
import com.br.tiagolima.model.Etiqueta;
import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.Projeto;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;
import com.br.tiagolima.repository.EtiquetaRepository;
import com.br.tiagolima.repository.ProjetoRepository;
import com.br.tiagolima.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Service
public class TarefaService {

    private final TarefaRepository repository;
    private final ProjetoRepository projetoRepository;
    private final EtiquetaRepository etiquetaRepository;

    public TarefaService(TarefaRepository repository,
                         ProjetoRepository projetoRepository,
                         EtiquetaRepository etiquetaRepository) {
        this.repository = repository;
        this.projetoRepository = projetoRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    public List<TarefaResponse> listar(StatusTarefa status, Prioridade prioridade, Long projetoId) {
        return repository.listar(status, prioridade, projetoId).stream()
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
        Projeto projeto = resolverProjeto(request.projetoId());
        List<Etiqueta> etiquetas = resolverEtiquetas(request.etiquetaIds());

        Tarefa tarefa = new Tarefa();
        preencher(tarefa, request, projeto, etiquetas);
        return TarefaResponse.from(repository.inserir(tarefa));
    }

    public TarefaResponse substituir(long id, TarefaRequest request) {
        validarCompleto(request);
        Projeto projeto = resolverProjeto(request.projetoId());
        List<Etiqueta> etiquetas = resolverEtiquetas(request.etiquetaIds());

        return repository.atualizar(id, tarefa -> preencher(tarefa, request, projeto, etiquetas))
                .map(TarefaResponse::from)
                .orElseThrow(() -> naoEncontrada(id));
    }

    public TarefaResponse atualizar(long id, TarefaRequest request) {
        if (request.titulo() != null) {
            validarTitulo(request.titulo());
        }
        validarDescricao(request.descricao());
        Projeto projeto = resolverProjeto(request.projetoId());
        List<Etiqueta> etiquetas = request.etiquetaIds() != null
                ? resolverEtiquetas(request.etiquetaIds())
                : null;

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
            if (projeto != null) {
                tarefa.setProjeto(projeto);
            }
            if (etiquetas != null) {
                tarefa.definirEtiquetas(etiquetas);
            }
        }).map(TarefaResponse::from).orElseThrow(() -> naoEncontrada(id));
    }

    public void excluir(long id) {
        if (!repository.remover(id)) {
            throw naoEncontrada(id);
        }
    }

    private void preencher(Tarefa tarefa, TarefaRequest request, Projeto projeto, List<Etiqueta> etiquetas) {
        tarefa.setTitulo(request.titulo().trim());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(Objects.requireNonNullElse(request.status(), StatusTarefa.PENDENTE));
        tarefa.setPrioridade(Objects.requireNonNullElse(request.prioridade(), Prioridade.MEDIA));
        tarefa.setProjeto(projeto);
        tarefa.definirEtiquetas(etiquetas);
    }

    private Projeto resolverProjeto(Long projetoId) {
        if (projetoId == null) {
            return null;
        }
        return projetoRepository.buscar(projetoId)
                .orElseThrow(() -> new RequisicaoInvalidaException("Projeto " + projetoId + " não encontrado"));
    }

    private List<Etiqueta> resolverEtiquetas(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        if (ids.contains(null)) {
            throw new RequisicaoInvalidaException("O campo etiquetaIds não pode conter valores nulos");
        }
        List<Etiqueta> etiquetas = etiquetaRepository.buscarPorIds(ids);
        if (etiquetas.size() != ids.size()) {
            Set<Long> faltando = new TreeSet<>(ids);
            etiquetas.forEach(etiqueta -> faltando.remove(etiqueta.getId()));
            throw new RequisicaoInvalidaException("Etiqueta(s) não encontrada(s): " + faltando);
        }
        return etiquetas;
    }

    private void validarCompleto(TarefaRequest request) {
        if (request.titulo() == null) {
            throw new RequisicaoInvalidaException("O campo titulo é obrigatório");
        }
        validarTitulo(request.titulo());
        validarDescricao(request.descricao());
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
