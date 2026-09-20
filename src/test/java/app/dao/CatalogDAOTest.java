package app.dao;

import app.config.TestHibernateConfig;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers(disabledWithoutDocker = true)
class CatalogDAOTest {
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    static EntityManagerFactory emf;

    @BeforeAll
    static void setUpDatabase() {
        emf = TestHibernateConfig.create(postgres);
    }

    @BeforeEach
    void clearDatabase() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("delete from Actor").executeUpdate();
            em.createQuery("delete from Director").executeUpdate();
            em.createQuery("delete from Genre").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void closeDatabase() {
        emf.close();
    }

    @Test
    void actorCRUDShouldWork() {
        ActorDAO actorDAO = new ActorDAO(emf);
        Actor actor = new Actor();
        actor.setTmdbId(101L);
        actor.setFullName("Old name");

        actorDAO.create(actor);
        actor.setFullName("New name");
        actorDAO.update(actor);

        assertEquals(1, actorDAO.getAll().size());
        assertEquals("New name", actorDAO.findById(actor.getId()).getFullName());

        actorDAO.delete(actor.getId());
        assertNull(actorDAO.findById(actor.getId()));
    }

    @Test
    void directorCRUDShouldWork() {
        DirectorDAO directorDAO = new DirectorDAO(emf);
        Director director = new Director();
        director.setTmdbId(201L);
        director.setFullName("Old name");

        directorDAO.create(director);
        director.setFullName("New name");
        directorDAO.update(director);

        assertEquals(1, directorDAO.getAll().size());
        assertEquals("New name", directorDAO.findById(director.getId()).getFullName());

        directorDAO.delete(director.getId());
        assertNull(directorDAO.findById(director.getId()));
    }

    @Test
    void genreCRUDShouldWork() {
        GenreDAO genreDAO = new GenreDAO(emf);
        Genre genre = new Genre();
        genre.setTmdbId(301L);
        genre.setName("Old name");

        genreDAO.create(genre);
        genre.setName("New name");
        genreDAO.update(genre);

        assertEquals(1, genreDAO.getAll().size());
        assertEquals("New name", genreDAO.findById(genre.getId()).getName());

        genreDAO.delete(genre.getId());
        assertNull(genreDAO.findById(genre.getId()));
    }
}
