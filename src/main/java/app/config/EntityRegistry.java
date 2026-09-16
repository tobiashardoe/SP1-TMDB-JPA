package app.config;


import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Director.class);

    }
}