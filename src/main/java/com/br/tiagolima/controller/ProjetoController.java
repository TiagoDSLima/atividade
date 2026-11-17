package com.br.tiagolima.controller;

import com.br.tiagolima.dto.ProjetoRequest;
import com.br.tiagolima.dto.ProjetoResponse;
import com.br.tiagolima.dto.TarefaResponse;
import com.br.tiagolima.service.ProjetoService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService service;
    private final TarefaService tarefaService;

    public ProjetoController(ProjetoService service, TarefaService tarefaService) {
        this.service = service;
        this.tarefaService = tarefaService;
    }

    @GetMapping
    public List<ProjetoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ProjetoResponse buscar(@PathVariable long id) {
        return service.buscar(id);
    }

    @GetMapping("/{id}/tarefas")
    public List<TarefaResponse> listarTarefas(@PathVariable long id) {
        service.encontrar(id);
        return tarefaService.listar(null, null, id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjetoResponse> criar(@RequestBody ProjetoRequest request) {
        ProjetoResponse projeto = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projeto.id())
                .toUri();
        return ResponseEntity.created(location).body(projeto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProjetoResponse substituir(@PathVariable long id, @RequestBody ProjetoRequest request) {
        return service.substituir(id, request);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProjetoResponse atualizar(@PathVariable long id, @RequestBody ProjetoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) {
        service.excluir(id);
    }
}
