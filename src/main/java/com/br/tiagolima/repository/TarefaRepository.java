package com.br.tiagolima.repository;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class TarefaRepository {

    private static final String SELECT_COM_RELACIONAMENTOS =
            "select distinct t from Tarefa t left join fetch t.projeto left join fetch t.etiquetas";

    private final SessionFactory sessionFactory;

    public TarefaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Tarefa> listar(StatusTarefa status, Prioridade prioridade, Long projetoId) {
        StringBuilder hql = new StringBuilder(SELECT_COM_RELACIONAMENTOS).append(" where 1 = 1");
        if (status != null) {
            hql.append(" and t.status = :status");
        }
        if (prioridade != null) {
            hql.append(" and t.prioridade = :prioridade");
        }
        if (projetoId != null) {
            hql.append(" and t.projeto.id = :projetoId");
        }
        hql.append(" order by t.id");

        return sessionFactory.fromSession(session -> {
            SelectionQuery<Tarefa> query = session.createSelectionQuery(hql.toString(), Tarefa.class);
            if (status != null) {
                query.setParameter("status", status);
            }
            if (prioridade != null) {
                query.setParameter("prioridade", prioridade);
            }
            if (projetoId != null) {
                query.setParameter("projetoId", projetoId);
            }
            return query.getResultList();
        });
    }

    public Optional<Tarefa> buscar(long id) {
        return sessionFactory.fromSession(session -> session
                .createSelectionQuery(SELECT_COM_RELACIONAMENTOS + " where t.id = :id", Tarefa.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    public Tarefa inserir(Tarefa tarefa) {
        sessionFactory.inTransaction(session -> session.persist(tarefa));
        return tarefa;
    }

    public Optional<Tarefa> atualizar(long id, Consumer<Tarefa> alteracao) {
        return sessionFactory.fromTransaction(session -> {
            Tarefa tarefa = session.find(Tarefa.class, id);
            if (tarefa == null) {
                return Optional.empty();
            }
            alteracao.accept(tarefa);
            session.flush();
            Hibernate.initialize(tarefa.getProjeto());
            Hibernate.initialize(tarefa.getEtiquetas());
            return Optional.of(tarefa);
        });
    }

    public boolean remover(long id) {
        return sessionFactory.fromTransaction(session -> {
            Tarefa tarefa = session.find(Tarefa.class, id);
            if (tarefa == null) {
                return false;
            }
            session.remove(tarefa);
            return true;
        });
    }

    public boolean existePorProjeto(long projetoId) {
        return sessionFactory.fromSession(session -> session
                .createSelectionQuery("select count(t) from Tarefa t where t.projeto.id = :projetoId", Long.class)
                .setParameter("projetoId", projetoId)
                .getSingleResult() > 0);
    }
}
