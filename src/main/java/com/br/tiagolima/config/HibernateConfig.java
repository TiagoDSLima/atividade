package com.br.tiagolima.config;

import com.br.tiagolima.model.Etiqueta;
import com.br.tiagolima.model.Projeto;
import com.br.tiagolima.model.Tarefa;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory(DataSource dataSource,
                                         @Value("${hibernate.hbm2ddl.auto}") String ddlAuto,
                                         @Value("${hibernate.show_sql}") boolean showSql) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DATASOURCE, dataSource)
                .applySetting(AvailableSettings.HBM2DDL_AUTO, ddlAuto)
                .applySetting(AvailableSettings.SHOW_SQL, showSql)
                .applySetting(AvailableSettings.FORMAT_SQL, true)
                .build();
        try {
            return new MetadataSources(registry)
                    .addAnnotatedClass(Tarefa.class)
                    .addAnnotatedClass(Projeto.class)
                    .addAnnotatedClass(Etiqueta.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
}
