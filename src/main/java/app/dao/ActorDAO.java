package app.dao;

import app.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ActorDAO {

    private final EntityManagerFactory emf;

    // CREATE
    public Actor create(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(actor);

            em.getTransaction().commit();

            return actor;
        }
    }

    // READ
    public Actor findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Actor.class, id);
        }
    }
    public Actor findByName(String fullName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Actor a WHERE a.fullName = :name",
                            Actor.class
                    )
                    .setParameter("name", fullName)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    // READ ALL
    public List<Actor> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT a FROM Actor a",
                    Actor.class
            ).getResultList();
        }
    }

    // UPDATE
    public Actor update(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Actor updatedActor = em.merge(actor);

            em.getTransaction().commit();

            return updatedActor;
        }
    }

    // DELETE
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Actor actor = em.find(Actor.class, id);

            if (actor != null) {
                em.remove(actor);
            }

            em.getTransaction().commit();
        }
    }

}