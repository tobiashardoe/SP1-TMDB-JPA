package app.dao;

import app.config.TestHibernateConfig;
import app.entities.Movie;
import app.entities.Genre;
import app.service.MovieService;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers(disabledWithoutDocker = true)
class MovieDAOTest {
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    static EntityManagerFactory emf;
    MovieDAO movieDAO;
    MovieService movieService;

    @BeforeAll
    static void setUpDatabase() {
        emf = TestHibernateConfig.create(postgres);
    }

    @BeforeEach
    void setUp() {
        movieDAO = new MovieDAO(emf);
        movieService = new MovieService(movieDAO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("delete from Movie").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void closeDatabase() {
        emf.close();
    }

    @Test
    void getAllShouldReturnAllMovies() {
        movieDAO.create(createMovie(101, "Movie 1", 5, 10));
        movieDAO.create(createMovie(102, "Movie 2", 7, 20));

        List<Movie> results = movieService.getAllMovies();

        assertEquals(2, results.size());
    }

    @Test
    void searchByTitleShouldIgnoreUpperAndLowerCase() {
        movieDAO.create(createMovie(201, "Danish Film", 5, 10));
        movieDAO.create(createMovie(202, "Another Movie", 7, 20));

        List<Movie> results = movieService.searchByTitle("FILM");

        assertEquals(1, results.size());
        assertEquals("Danish Film", results.get(0).getTitle());
    }

    @Test
    void getAverageRatingShouldReturnAverage() {
        movieDAO.create(createMovie(301, "Movie 1", 4, 10));
        movieDAO.create(createMovie(302, "Movie 2", 8, 10));

        double result = movieService.getAverageRating();

        assertEquals(6.0, result);
    }

    @Test
    void getMoviesByGenreShouldReturnMoviesInGenre() {
        Genre genre = new Genre();
        genre.setTmdbId(18L);
        genre.setName("Drama");
        new GenreDAO(emf).create(genre);

        Movie movie = createMovie(401, "Drama movie", 7, 10);
        movie.getGenres().add(genre);
        movieDAO.create(movie);

        List<Movie> results = movieService.getMoviesByGenre(genre.getId());

        assertEquals(1, results.size());
        assertEquals("Drama movie", results.get(0).getTitle());
    }

    @Test
    void getTop10HighestRatedShouldReturnHighestRated() {
        for (int i = 1; i <= 12; i++) {
            movieDAO.create(createMovie(600 + i, "Movie " + i, i, i));
        }

        List<Movie> results = movieService.getTop10HighestRated();

        assertEquals(10, results.size());
        assertEquals(12.0, results.get(0).getRating());
    }

    @Test
    void getTop10LowestRatedShouldReturnLowestRated() {
        for (int i = 1; i <= 12; i++) {
            movieDAO.create(createMovie(700 + i, "Movie " + i, i, i));
        }

        List<Movie> results = movieService.getTop10LowestRated();

        assertEquals(10, results.size());
        assertEquals(1.0, results.get(0).getRating());
    }

    @Test
    void getTop10MostPopularShouldReturnMostPopular() {
        for (int i = 1; i <= 12; i++) {
            movieDAO.create(createMovie(800 + i, "Movie " + i, i, i));
        }

        List<Movie> results = movieService.getTop10MostPopular();

        assertEquals(10, results.size());
        assertEquals(12.0, results.get(0).getPopularity());
    }

    @Test
    void updateShouldChangeTitleAndReleaseDate() {
        Movie movie = movieDAO.create(createMovie(901, "Old title", 5, 10));
        LocalDate newDate = LocalDate.of(2026, 1, 1);

        movieService.updateTitleAndReleaseDate(movie.getId(), "New title", newDate);
        Movie updated = movieDAO.findById(movie.getId());

        assertEquals("New title", updated.getTitle());
        assertEquals(newDate, updated.getReleaseDate());
    }

    @Test
    void deleteShouldRemoveMovie() {
        Movie movie = movieDAO.create(createMovie(902, "Movie", 5, 10));

        movieDAO.delete(movie.getId());

        assertNull(movieDAO.findById(movie.getId()));
    }

    private Movie createMovie(long tmdbId, String title, double rating, double popularity) {
        return Movie.builder()
                .tmdbId(tmdbId)
                .title(title)
                .releaseDate(LocalDate.of(2025, 1, 1))
                .rating(rating)
                .popularity(popularity)
                .build();
    }
}
