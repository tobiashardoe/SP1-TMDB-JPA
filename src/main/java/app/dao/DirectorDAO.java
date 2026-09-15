package app.dao;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DirectorDAO {

    private final EntityManagerFactory emf;

    // CREATE
    public Director create(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(director);

            em.getTransaction().commit();

            return director;
        }
    }

    // READ
    public Director findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Director.class, id);
        }
    }

    // READ ALL
    public List<Director> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT d FROM Director d",
                    Director.class
            ).getResultList();
        }
    }

    // UPDATE
    public Director update(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director updatedDirector = em.merge(director);

            em.getTransaction().commit();

            return updatedDirector;
        }
    }

    // DELETE
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director director = em.find(Director.class, id);

            if (director != null) {
                em.remove(director);
            }

            em.getTransaction().commit();
        }
    }

    // FIND BY NAME
    public Director findByName(String fullName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT d FROM Director d WHERE d.fullName = :name",
                            Director.class
                    )
                    .setParameter("name", fullName)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}