package com.br.tiagolima.controller;

import com.br.tiagolima.dto.EtiquetaRequest;
import com.br.tiagolima.dto.EtiquetaResponse;
import com.br.tiagolima.service.EtiquetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/etiquetas")
public class EtiquetaController {

    private final EtiquetaService service;

    public EtiquetaController(EtiquetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EtiquetaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EtiquetaResponse buscar(@PathVariable long id) {
        return service.buscar(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EtiquetaResponse> criar(@RequestBody EtiquetaRequest request) {
        EtiquetaResponse etiqueta = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(etiqueta.id())
                .toUri();
        return ResponseEntity.created(location).body(etiqueta);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EtiquetaResponse substituir(@PathVariable long id, @RequestBody EtiquetaRequest request) {
        return service.substituir(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable long id) {
        service.excluir(id);
    }
}
