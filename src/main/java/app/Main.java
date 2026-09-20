package app;

import app.config.HibernateConfig;
import app.dao.ActorDAO;
import app.dao.DirectorDAO;
import app.dao.GenreDAO;
import app.dao.MovieDAO;
import app.service.TmdbImporter;

public class Main {
    public static void main(String[] args) {
        try (var emf = HibernateConfig.getEntityManagerFactory()) {
            MovieDAO movieDAO = new MovieDAO(emf);

            // Sæt det til true en gang for at fylde databasen, og sæt det derefter tilbage til false.
            boolean importMovies = false;
            if (importMovies) {
                int imported = new TmdbImporter(movieDAO).importRecentDanishMovies();
                System.out.println("Imported movies: " + imported);
            }

            System.out.println("Movies: " + movieDAO.getAll().size());
            System.out.println("Actors: " + new ActorDAO(emf).getAll().size());
            System.out.println("Directors: " + new DirectorDAO(emf).getAll().size());
            System.out.println("Genres: " + new GenreDAO(emf).getAll().size());
            System.out.println("Average rating: " + movieDAO.averageRating());

            movieDAO.highestRated().forEach(System.out::println);
        }
    }
}
