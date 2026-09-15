package app.service;

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
import java.util.List;

public class TMDBReader {

    private static final String GENRE_URL = "https://api.themoviedb.org/3/genre/movie/list?language=en-US";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey = app.utils.Utils.getPropertyValue("TMDB_API_KEY", "config.properties");

    public static void main(String[] args) {
        TMDBReader reader = new TMDBReader();
        String json = reader.readAPI(GENRE_URL);
        GenreListDTO genreListDTO = reader.convertFromJson(json);
        System.out.println(genreListDTO);
    }

    public String readAPI(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("accept", "application/json")
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

    public GenreListDTO convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GenreListDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }