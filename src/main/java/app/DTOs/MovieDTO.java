package app.DTOs;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private int tmdbId;
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private String originalLanguage;
    private double rating;
    private int voteCount;
    private double popularity;

    private List<GenreDTO> genres;
    private List<ActorDTO> actors;
    private DirectorDTO director;
}