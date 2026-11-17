package com.br.tiagolima.repository;

import com.br.tiagolima.model.Prioridade;
import com.br.tiagolima.model.StatusTarefa;
import com.br.tiagolima.model.Tarefa;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class TarefaRepository {

    private final SessionFactory sessionFactory;

    public TarefaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Tarefa> listar(StatusTarefa status, Prioridade prioridade) {
        StringBuilder hql = new StringBuilder("from Tarefa t where 1 = 1");
        if (status != null) {
            hql.append(" and t.status = :status");
        }
        if (prioridade != null) {
            hql.append(" and t.prioridade = :prioridade");
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
            return query.getResultList();
        });
    }

    public Optional<Tarefa> buscar(long id) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(session.find(Tarefa.class, id)));
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
}
