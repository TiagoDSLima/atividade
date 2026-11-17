package com.br.tiagolima.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projetos")
public class Projeto {

    public static final int TAMANHO_NOME = 100;
    public static final int TAMANHO_DESCRICAO = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = TAMANHO_NOME)
    private String nome;

    @Column(length = TAMANHO_DESCRICAO)
    private String descricao;

    /** 1 projeto possui N tarefas (lado inverso; a chave estrangeira fica em tarefas.projeto_id). */
    @OneToMany(mappedBy = "projeto")
    private List<Tarefa> tarefas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }
}
