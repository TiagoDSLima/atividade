package com.br.tiagolima.service;

import com.br.tiagolima.dto.ProjetoRequest;
import com.br.tiagolima.dto.ProjetoResponse;
import com.br.tiagolima.exception.exceptions.ConflitoException;
import com.br.tiagolima.exception.exceptions.RecursoNaoEncontradoException;
import com.br.tiagolima.exception.exceptions.RequisicaoInvalidaException;
import com.br.tiagolima.model.Projeto;
import com.br.tiagolima.repository.ProjetoRepository;
import com.br.tiagolima.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository repository;
    private final TarefaRepository tarefaRepository;

    public ProjetoService(ProjetoRepository repository, TarefaRepository tarefaRepository) {
        this.repository = repository;
        this.tarefaRepository = tarefaRepository;
    }

    public List<ProjetoResponse> listar() {
        return repository.listar().stream()
                .map(ProjetoResponse::from)
                .toList();
    }

    public ProjetoResponse buscar(long id) {
        return ProjetoResponse.from(encontrar(id));
    }

    public ProjetoResponse criar(ProjetoRequest request) {
        String nome = validarNome(request.nome(), null);
        validarDescricao(request.descricao());
        Projeto projeto = new Projeto();
        projeto.setNome(nome);
        projeto.setDescricao(request.descricao());
        return ProjetoResponse.from(repository.inserir(projeto));
    }

    /** PUT: substitui todos os campos do projeto. */
    public ProjetoResponse substituir(long id, ProjetoRequest request) {
        encontrar(id);
        String nome = validarNome(request.nome(), id);
        validarDescricao(request.descricao());
        return repository.atualizar(id, projeto -> {
            projeto.setNome(nome);
            projeto.setDescricao(request.descricao());
        }).map(ProjetoResponse::from).orElseThrow(() -> naoEncontrado(id));
    }

    /** PATCH: altera apenas os campos informados. */
    public ProjetoResponse atualizar(long id, ProjetoRequest request) {
        encontrar(id);
        String nome = request.nome() != null ? validarNome(request.nome(), id) : null;
        validarDescricao(request.descricao());
        return repository.atualizar(id, projeto -> {
            if (nome != null) {
                projeto.setNome(nome);
            }
            if (request.descricao() != null) {
                projeto.setDescricao(request.descricao());
            }
        }).map(ProjetoResponse::from).orElseThrow(() -> naoEncontrado(id));
    }

    public void excluir(long id) {
        encontrar(id);
        if (tarefaRepository.existePorProjeto(id)) {
            throw new ConflitoException(
                    "O projeto " + id + " possui tarefas vinculadas e não pode ser excluído");
        }
        if (!repository.remover(id)) {
            throw naoEncontrado(id);
        }
    }

    /** Garante que o projeto existe (usado também pela listagem de tarefas do projeto). */
    public Projeto encontrar(long id) {
        return repository.buscar(id).orElseThrow(() -> naoEncontrado(id));
    }

    private String validarNome(String nome, Long idAtual) {
        if (nome == null || nome.isBlank()) {
            throw new RequisicaoInvalidaException("O campo nome é obrigatório");
        }
        String valor = nome.trim();
        if (valor.length() > Projeto.TAMANHO_NOME) {
            throw new RequisicaoInvalidaException(
                    "O campo nome deve ter no máximo " + Projeto.TAMANHO_NOME + " caracteres");
        }
        if (repository.existeComNome(valor, idAtual)) {
            throw new ConflitoException("Já existe um projeto com o nome " + valor);
        }
        return valor;
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > Projeto.TAMANHO_DESCRICAO) {
            throw new RequisicaoInvalidaException(
                    "O campo descricao deve ter no máximo " + Projeto.TAMANHO_DESCRICAO + " caracteres");
        }
    }

    private RecursoNaoEncontradoException naoEncontrado(long id) {
        return new RecursoNaoEncontradoException("Projeto " + id + " não encontrado");
    }
}
