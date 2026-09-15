package app.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

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