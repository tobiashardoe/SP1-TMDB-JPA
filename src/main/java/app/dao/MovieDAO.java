package app.dao;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Movie findByTmdbId(Long tmdbId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM Movie m WHERE m.tmdbId = :id",
                            Movie.class
                    )
                    .setParameter("id", tmdbId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    public boolean createImported(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                if (em.createQuery(
                                "SELECT COUNT(m) FROM Movie m WHERE m.tmdbId = :id",
                                Long.class
                        )
                        .setParameter("id", movie.getTmdbId())
                        .getSingleResult() > 0) {

                    em.getTransaction().commit();
                    return false;
                }

                Set<Actor> actors = movie.getActors().stream()
                        .map(actor -> {
                            Actor existing = em.createQuery(
                                            "SELECT a FROM Actor a WHERE a.tmdbId = :id",
                                            Actor.class
                                    )
                                    .setParameter("id", actor.getTmdbId())
                                    .getResultStream()
                                    .findFirst()
                                    .orElse(null);

                            if (existing != null) {
                                return existing;
                            }

                            em.persist(actor);
                            return actor;
                        })
                        .collect(Collectors.toSet());

                Set<Director> directors = movie.getDirectors().stream()
                        .map(director -> {
                            Director existing = em.createQuery(
                                            "SELECT d FROM Director d WHERE d.tmdbId = :id",
                                            Director.class
                                    )
                                    .setParameter("id", director.getTmdbId())
                                    .getResultStream()
                                    .findFirst()
                                    .orElse(null);

                            if (existing != null) {
                                return existing;
                            }

                            em.persist(director);
                            return director;
                        })
                        .collect(Collectors.toSet());

                Set<Genre> genres = movie.getGenres().stream()
                        .map(genre -> {
                            Genre existing = em.createQuery(
                                            "SELECT g FROM Genre g WHERE g.tmdbId = :id",
                                            Genre.class
                                    )
                                    .setParameter("id", genre.getTmdbId())
                                    .getResultStream()
                                    .findFirst()
                                    .orElse(null);

                            if (existing != null) {
                                return existing;
                            }

                            em.persist(genre);
                            return genre;
                        })
                        .collect(Collectors.toSet());

                movie.setActors(actors);
                movie.setDirectors(directors);
                movie.setGenres(genres);

                em.persist(movie);
                em.getTransaction().commit();

                return true;

            } catch (RuntimeException ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw ex;
            }
        }
    }

    // Opgave 5 - Search movie by title
    public List<Movie> searchByTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {

            String pattern = "%" + title.toLowerCase(Locale.ROOT)
                    .replace("!", "!!")
                    .replace("%", "!%")
                    .replace("_", "!_") + "%";

            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "WHERE LOWER(m.title) LIKE :title ESCAPE '!' " +
                                    "ORDER BY m.title",
                            Movie.class
                    )
                    .setParameter("title", pattern)
                    .getResultList();
        }
    }

    public List<Movie> findByGenre(Long genreId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM Movie m " +
                                    "JOIN m.genres g " +
                                    "WHERE g.id = :id " +
                                    "ORDER BY m.title",
                            Movie.class
                    )
                    .setParameter("id", genreId)
                    .getResultList();
        }
    }

    public void updateTitleAndReleaseDate(
            Long id,
            String title,
            LocalDate releaseDate
    ) {

        if (title == null || title.isBlank() || releaseDate == null) {
            throw new IllegalArgumentException(
                    "Title and release date are required"
            );
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Movie movie = em.find(Movie.class, id);

                if (movie == null) {
                    throw new IllegalArgumentException(
                            "Movie not found: " + id
                    );
                }

                movie.setTitle(title.trim());
                movie.setReleaseDate(releaseDate);

                em.getTransaction().commit();

            } catch (RuntimeException ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                throw ex;
            }
        }
    }

    // Opgave 6 - Average rating
    public double averageRating() {
        try (EntityManager em = emf.createEntityManager()) {

            Double average = em.createQuery(
                    "SELECT AVG(m.rating) FROM Movie m",
                    Double.class
            ).getSingleResult();

            return average == null ? 0 : average;
        }
    }

    // Opgave 6 - Top 10 highest rated
    public List<Movie> highestRated() {
        return top("rating", true);
    }

    // Opgave 6 - Top 10 lowest rated
    public List<Movie> lowestRated() {
        return top("rating", false);
    }

    // Opgave 6 - Top 10 most popular
    public List<Movie> mostPopular() {
        return top("popularity", true);
    }

    private List<Movie> top(String field, boolean descending) {
        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m." +
                                    field +
                                    (descending ? " DESC" : " ASC") +
                                    ", m.id",
                            Movie.class
                    )
                    .setMaxResults(10)
                    .getResultList();
        }
    }
}