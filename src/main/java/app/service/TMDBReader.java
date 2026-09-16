package app.service;

import app.DTOs.ActorDTO;
import app.DTOs.DirectorDTO;
import app.DTOs.GenreDTO;
import app.DTOs.TmdbMovieDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static app.utils.Utils.getPropertyValue;

public class TMDBReader {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private final String apiKey = getPropertyValue("TMDB_API_KEY", "config.properties");
    private final String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?language=en-US&api_key=" + apiKey;

    public static void main(String[] args) {
        TMDBReader reader = new TMDBReader();
        String json = reader.readAPI(reader.genreUrl);


        GenreListDTO genreListDTO = reader.convertFromJson(json);
       // System.out.println(genreListDTO);




        //Ændre movieID fra 550 til noget andet, hvis du gerne vil have oplysningerne på en anden film
        TmdbMovieDTO movie = reader.getMovieById(550);
        System.out.println(movie);


        //Test vores Actors og Directors
        CreditsDTO credits = reader.getCredits(550);
        System.out.println(credits);
    }

    public String readAPI(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("GET request failed. Status code: " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public TmdbMovieDTO getMovieById(int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        String json = readAPI(url);
        try {
            return objectMapper.readValue(json, TmdbMovieDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DiscoverDTO getDanishMovies(int page, LocalDate start, LocalDate end) {
        String url = "https://api.themoviedb.org/3/discover/movie?with_origin_country=DK"
                + "&primary_release_date.gte=" + start
                + "&primary_release_date.lte=" + end
                + "&sort_by=primary_release_date.asc&page=" + page
                + "&api_key=" + apiKey;
        try {
            return objectMapper.readValue(readAPI(url), DiscoverDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TmdbMovieDTO> getRecentDanishMovies(LocalDate start, LocalDate end) {
        DiscoverDTO firstPage = getDanishMovies(1, start, end);
        List<TmdbMovieDTO> movies = new ArrayList<>(firstPage.getResults());
        System.out.printf("Reading TMDb pages: 1/%d%n", firstPage.getTotalPages());

        for (int page = 2; page <= firstPage.getTotalPages(); page++) {
            movies.addAll(getDanishMovies(page, start, end).getResults());
            System.out.printf("Reading TMDb pages: %d/%d%n", page, firstPage.getTotalPages());
        }
        return movies;
    }

    public GenreListDTO convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GenreListDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CreditsDTO getCredits(int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;
        String json = readAPI(url);
        try {
            return objectMapper.readValue(json, CreditsDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditsDTO {
        @JsonProperty("cast")
        List<ActorDTO> cast;
        @JsonProperty("crew")
        List<DirectorDTO> crew;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscoverDTO {
        private List<TmdbMovieDTO> results;
        @JsonProperty("total_pages")
        private int totalPages;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GenreListDTO {
        @JsonProperty("genres")
        List<GenreDTO> genres;
    }



}
