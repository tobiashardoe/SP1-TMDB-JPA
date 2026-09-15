package app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate releaseDate;

    @Column(length = 5000)
    private String overview;

    private double rating;

    private int voteCount;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private Set<Director> directors = new HashSet<>();

    public void addActor(Actor actor) {
        if (actor != null) {
            actors.add(actor);
            actor.getMovies().add(this);
        }
    }

    public void removeActor(Actor actor) {
        if (actor != null) {
            actors.remove(actor);
            actor.getMovies().remove(this);
        }
    }

    public void addGenre(Genre genre) {
        if (genre != null) {
            genres.add(genre);
            genre.getMovies().add(this);
        }
    }

    public void removeGenre(Genre genre) {
        if (genre != null) {
            genres.remove(genre);
            genre.getMovies().remove(this);
        }
    }

    public void addDirector(Director director) {
        if (director != null) {
            directors.add(director);
            director.getMovies().add(this);
        }
    }

    public void removeDirector(Director director) {
        if (director != null) {
            directors.remove(director);
            director.getMovies().remove(this);
        }
    }
}