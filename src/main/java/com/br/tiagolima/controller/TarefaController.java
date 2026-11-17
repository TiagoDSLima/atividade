package com.br.tiagolima.controller;

import com.br.tiagolima.dto.TarefaRequest;
import com.br.tiagolima.dto.TarefaResponse;
import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.service.TarefaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService service;

    public TarefaController(TarefaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TarefaResponse> listar(@RequestParam(required = false) StatusTarefa status,
                                       @RequestParam(required = false) Prioridade prioridade,
                                       @RequestParam(required = false) Long projetoId) {
        return service.listar(status, prioridade, projetoId);
    }

    @GetMapping("/{id}")
    public TarefaResponse buscar(@PathVariable long id) {
        return service.buscar(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TarefaResponse> criar(@RequestBody TarefaRequest request) {
        TarefaResponse tarefa = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarefa.id())
                .toUri();
        return ResponseEntity.created(location).body(tarefa);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TarefaResponse substituir(@PathVariable long id, @RequestBody TarefaRequest request) {
        return service.substituir(id, request);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TarefaResponse atualizar(@PathVariable long id, @RequestBody TarefaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) {
        service.excluir(id);
    }
}
