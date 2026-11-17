package com.br.tiagolima.dto;

import com.br.tiagolima.model.Etiqueta;

public record EtiquetaResponse(Long id, String nome) {

    public static EtiquetaResponse from(Etiqueta etiqueta) {
        return new EtiquetaResponse(etiqueta.getId(), etiqueta.getNome());
    }
}
