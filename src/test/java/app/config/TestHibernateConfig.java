package app.config;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class TestHibernateConfig {
    private TestHibernateConfig() {}

    public static EntityManagerFactory create(PostgreSQLContainer postgres) {
        Configuration config = new Configuration()
                .addAnnotatedClass(Movie.class)
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Director.class)
                .addAnnotatedClass(Genre.class);

        config.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        config.setProperty("hibernate.connection.username", postgres.getUsername());
        config.setProperty("hibernate.connection.password", postgres.getPassword());
        config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        return config.buildSessionFactory();
    }
}
