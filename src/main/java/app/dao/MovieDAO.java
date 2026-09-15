package app.dao;

import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MovieDAO {

    private final EntityManagerFactory emf;

    public Movie create(Movie movie) {

        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();

            return movie;
        }
    }
    public Movie findById(Long id) {

        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Movie.class, id);
        }
    }
    public List<Movie> getAll() {

        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery(
                    "SELECT m FROM Movie m",
                    Movie.class
            ).getResultList();
        }
    }
    public Movie update(Movie movie) {

        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            Movie updatedMovie = em.merge(movie);

            em.getTransaction().commit();

            return updatedMovie;
        }
    }
    public void delete(Long id) {

        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            Movie movie = em.find(Movie.class, id);

            if (movie != null) {
                em.remove(movie);
            }

            em.getTransaction().commit();
        }
    }
}