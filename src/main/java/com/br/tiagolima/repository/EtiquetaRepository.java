package com.br.tiagolima.repository;

import com.br.tiagolima.model.Etiqueta;
import com.br.tiagolima.model.Tarefa;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class EtiquetaRepository {

    private final SessionFactory sessionFactory;

    public EtiquetaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Etiqueta> listar() {
        return sessionFactory.fromSession(session -> session
                .createSelectionQuery("from Etiqueta e order by e.id", Etiqueta.class)
                .getResultList());
    }

    public List<Etiqueta> buscarPorIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return sessionFactory.fromSession(session -> session
                .createSelectionQuery("from Etiqueta e where e.id in :ids", Etiqueta.class)
                .setParameterList("ids", ids)
                .getResultList());
    }

    public Optional<Etiqueta> buscar(long id) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(session.find(Etiqueta.class, id)));
    }

    public boolean existeComNome(String nome, Long ignorarId) {
        String hql = "select count(e) from Etiqueta e where lower(e.nome) = lower(:nome)"
                + (ignorarId != null ? " and e.id <> :id" : "");
        return sessionFactory.fromSession(session -> {
            SelectionQuery<Long> query = session.createSelectionQuery(hql, Long.class)
                    .setParameter("nome", nome);
            if (ignorarId != null) {
                query.setParameter("id", ignorarId);
            }
            return query.getSingleResult() > 0;
        });
    }

    public Etiqueta inserir(Etiqueta etiqueta) {
        sessionFactory.inTransaction(session -> session.persist(etiqueta));
        return etiqueta;
    }

    public Optional<Etiqueta> atualizar(long id, Consumer<Etiqueta> alteracao) {
        return sessionFactory.fromTransaction(session -> {
            Etiqueta etiqueta = session.find(Etiqueta.class, id);
            if (etiqueta == null) {
                return Optional.empty();
            }
            alteracao.accept(etiqueta);
            return Optional.of(etiqueta);
        });
    }

    public boolean remover(long id) {
        return sessionFactory.fromTransaction(session -> {
            Etiqueta etiqueta = session.find(Etiqueta.class, id);
            if (etiqueta == null) {
                return false;
            }
            for (Tarefa tarefa : etiqueta.getTarefas()) {
                tarefa.getEtiquetas().remove(etiqueta);
            }
            session.remove(etiqueta);
            return true;
        });
    }
}
