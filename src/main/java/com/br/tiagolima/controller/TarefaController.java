package com.br.tiagolima.controller;

import com.br.tiagolima.dto.TarefaRequest;
import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;
import com.br.tiagolima.service.TarefaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public List<Tarefa> listar(@RequestParam(required = false) StatusTarefa status,
                               @RequestParam(required = false) Prioridade prioridade) {
        return service.listar(status, prioridade);
    }

    @GetMapping("/{id}")
    public Tarefa buscar(@PathVariable long id) {
        return service.buscar(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tarefa> criar(@RequestBody TarefaRequest request) {
        Tarefa tarefa = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarefa.id())
                .toUri();
        return ResponseEntity.created(location).body(tarefa);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Tarefa atualizar(@PathVariable long id, @RequestBody TarefaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) {
        service.excluir(id);
    }
}
