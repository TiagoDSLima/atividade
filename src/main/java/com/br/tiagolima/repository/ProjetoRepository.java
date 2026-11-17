package com.br.tiagolima.repository;

import com.br.tiagolima.model.Projeto;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class ProjetoRepository {

    private final SessionFactory sessionFactory;

    public ProjetoRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Projeto> listar() {
        return sessionFactory.fromSession(session -> session
                .createSelectionQuery("from Projeto p order by p.id", Projeto.class)
                .getResultList());
    }

    public Optional<Projeto> buscar(long id) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(session.find(Projeto.class, id)));
    }

    public boolean existeComNome(String nome, Long ignorarId) {
        String hql = "select count(p) from Projeto p where lower(p.nome) = lower(:nome)"
                + (ignorarId != null ? " and p.id <> :id" : "");
        return sessionFactory.fromSession(session -> {
            SelectionQuery<Long> query = session.createSelectionQuery(hql, Long.class)
                    .setParameter("nome", nome);
            if (ignorarId != null) {
                query.setParameter("id", ignorarId);
            }
            return query.getSingleResult() > 0;
        });
    }

    public Projeto inserir(Projeto projeto) {
        sessionFactory.inTransaction(session -> session.persist(projeto));
        return projeto;
    }

    public Optional<Projeto> atualizar(long id, Consumer<Projeto> alteracao) {
        return sessionFactory.fromTransaction(session -> {
            Projeto projeto = session.find(Projeto.class, id);
            if (projeto == null) {
                return Optional.empty();
            }
            alteracao.accept(projeto);
            return Optional.of(projeto);
        });
    }

    public boolean remover(long id) {
        return sessionFactory.fromTransaction(session -> {
            Projeto projeto = session.find(Projeto.class, id);
            if (projeto == null) {
                return false;
            }
            session.remove(projeto);
            return true;
        });
    }
}
