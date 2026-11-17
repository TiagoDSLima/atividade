package com.br.tiagolima.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "etiquetas")
public class Etiqueta {

    public static final int TAMANHO_NOME = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = TAMANHO_NOME)
    private String nome;

    /** Lado inverso do relacionamento N:N com Tarefa. */
    @ManyToMany(mappedBy = "etiquetas")
    private Set<Tarefa> tarefas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Tarefa> getTarefas() {
        return tarefas;
    }
}
