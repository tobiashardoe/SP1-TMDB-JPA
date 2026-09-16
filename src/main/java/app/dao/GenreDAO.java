package app.dao;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GenreDAO {

    private final EntityManagerFactory emf;

    // CREATE
    public Genre create(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(genre);

            em.getTransaction().commit();

            return genre;
        }
    }

    // READ
    public Genre findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Genre.class, id);
        }
    }

    // READ ALL
    public List<Genre> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT g FROM Genre g",
                    Genre.class
            ).getResultList();
        }
    }

    // UPDATE
    public Genre update(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre updatedGenre = em.merge(genre);

            em.getTransaction().commit();

            return updatedGenre;
        }
    }

    // DELETE
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre genre = em.find(Genre.class, id);

            if (genre != null) {
                em.remove(genre);
            }

            em.getTransaction().commit();
        }
    }

    // FIND BY NAME
    public Genre findByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT g FROM Genre g WHERE g.name = :name",
                            Genre.class
                    )
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            //////
        }
    }
}