package app.service;

import app.DTOs.ActorDTO;
import app.DTOs.DirectorDTO;
import app.DTOs.GenreDTO;
import app.DTOs.TmdbMovieDTO;
import app.entities.Movie;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TmdbImporterTest {
    @Test
    void shouldConvertDTOsToMovieEntity() {
        TmdbMovieDTO movieDTO = new TmdbMovieDTO();
        movieDTO.setId(100);
        movieDTO.setTitle("Danish movie");
        movieDTO.setReleaseDate(LocalDate.of(2025, 1, 1));
        movieDTO.setRating(7.5);
        movieDTO.setGenres(List.of(new GenreDTO(18, "Drama")));

        TMDBReader.CreditsDTO credits = new TMDBReader.CreditsDTO();
        credits.setCast(List.of(new ActorDTO(200, "Actor")));
        credits.setCrew(List.of(
                new DirectorDTO(300, "Director", "Director"),
                new DirectorDTO(301, "Writer", "Writer")
        ));

        Movie movie = TmdbImporter.convertToEntity(movieDTO, credits);

        assertEquals(100L, movie.getTmdbId());
        assertEquals("Danish movie", movie.getTitle());
        assertEquals(1, movie.getGenres().size());
        assertEquals(1, movie.getActors().size());
        assertEquals(1, movie.getDirectors().size());
    }
}
