package app.service;

import app.DTOs.ActorDTO;
import app.DTOs.DirectorDTO;
import app.DTOs.GenreDTO;
import app.DTOs.TmdbMovieDTO;
import app.dao.MovieDAO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;

import java.time.LocalDate;

// Konverterer TMDb-DTO'er til entiteter og beder derefter MovieDAO om at gemme dem. //
public class TmdbImporter {
    private final TMDBReader reader = new TMDBReader();
    private final MovieDAO movieDAO;

    public TmdbImporter(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public int importRecentDanishMovies() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusYears(5);
        int imported = 0;
        int processed = 0;

        for (TmdbMovieDTO summary : reader.getRecentDanishMovies(start, end)) {
            processed++;
            if (processed % 50 == 0) System.out.println("Processed movies: " + processed);
            if (movieDAO.findByTmdbId((long) summary.getId()) != null) continue;

            TmdbMovieDTO details = reader.getMovieById(summary.getId());
            if (details.getReleaseDate() == null || details.getReleaseDate().isBefore(start)
                    || details.getReleaseDate().isAfter(end)) continue;

            TMDBReader.CreditsDTO credits = reader.getCredits(summary.getId());
            Movie movie = convertToEntity(details, credits);
            if (movieDAO.createImported(movie)) imported++;
        }
        return imported;
    }

    public static Movie convertToEntity(TmdbMovieDTO dto, TMDBReader.CreditsDTO credits) {
        Movie movie = new Movie();
        movie.setTmdbId((long) dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setOverview(dto.getOverview());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setRating(dto.getRating());
        movie.setVoteCount(dto.getVoteCount());
        movie.setPopularity(dto.getPopularity());

        if (dto.getGenres() != null) for (GenreDTO item : dto.getGenres()) {
            Genre genre = new Genre();
            genre.setTmdbId((long) item.getId());
            genre.setName(item.getName());
            movie.getGenres().add(genre);
        }
        if (credits.getCast() != null) for (ActorDTO item : credits.getCast()) {
            Actor actor = new Actor();
            actor.setTmdbId((long) item.getId());
            actor.setFullName(item.getName());
            movie.getActors().add(actor);
        }
        if (credits.getCrew() != null) for (DirectorDTO item : credits.getCrew()) {
            if (!"Director".equals(item.getJob())) continue;
            Director director = new Director();
            director.setTmdbId((long) item.getId());
            director.setFullName(item.getName());
            movie.getDirectors().add(director);
        }
        return movie;
    }
}
