package com.br.tiagolima.service;

import com.br.tiagolima.dto.EtiquetaRequest;
import com.br.tiagolima.dto.EtiquetaResponse;
import com.br.tiagolima.exception.exceptions.ConflitoException;
import com.br.tiagolima.exception.exceptions.RecursoNaoEncontradoException;
import com.br.tiagolima.exception.exceptions.RequisicaoInvalidaException;
import com.br.tiagolima.model.Etiqueta;
import com.br.tiagolima.repository.EtiquetaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtiquetaService {

    private final EtiquetaRepository repository;

    public EtiquetaService(EtiquetaRepository repository) {
        this.repository = repository;
    }

    public List<EtiquetaResponse> listar() {
        return repository.listar().stream()
                .map(EtiquetaResponse::from)
                .toList();
    }

    public EtiquetaResponse buscar(long id) {
        return repository.buscar(id)
                .map(EtiquetaResponse::from)
                .orElseThrow(() -> naoEncontrada(id));
    }

    public EtiquetaResponse criar(EtiquetaRequest request) {
        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNome(validarNome(request.nome(), null));
        return EtiquetaResponse.from(repository.inserir(etiqueta));
    }

    /** PUT: a etiqueta só possui o campo nome, que é substituído. */
    public EtiquetaResponse substituir(long id, EtiquetaRequest request) {
        if (repository.buscar(id).isEmpty()) {
            throw naoEncontrada(id);
        }
        String nome = validarNome(request.nome(), id);
        return repository.atualizar(id, etiqueta -> etiqueta.setNome(nome))
                .map(EtiquetaResponse::from)
                .orElseThrow(() -> naoEncontrada(id));
    }

    public void excluir(long id) {
        if (!repository.remover(id)) {
            throw naoEncontrada(id);
        }
    }

    private String validarNome(String nome, Long idAtual) {
        if (nome == null || nome.isBlank()) {
            throw new RequisicaoInvalidaException("O campo nome é obrigatório");
        }
        String valor = nome.trim();
        if (valor.length() > Etiqueta.TAMANHO_NOME) {
            throw new RequisicaoInvalidaException(
                    "O campo nome deve ter no máximo " + Etiqueta.TAMANHO_NOME + " caracteres");
        }
        if (repository.existeComNome(valor, idAtual)) {
            throw new ConflitoException("Já existe uma etiqueta com o nome " + valor);
        }
        return valor;
    }

    private RecursoNaoEncontradoException naoEncontrada(long id) {
        return new RecursoNaoEncontradoException("Etiqueta " + id + " não encontrada");
    }
}
