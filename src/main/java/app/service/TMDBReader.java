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

import static app.utils.Utils.getPropertyValue;

public class TMDBReader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey = getPropertyValue("TMDB_API_KEY", "config.properties");
    private final String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?language=en-US&api_key=" + apiKey;

    public static void main(String[] args) {
        TMDBReader reader = new TMDBReader();
        String json = reader.readAPI(reader.genreUrl);
        GenreListDTO genreListDTO = reader.convertFromJson(json);
        System.out.println(genreListDTO);
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

    public GenreListDTO convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GenreListDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GenreDTO {
        @JsonProperty("id")
        Integer id;
        @JsonProperty("name")
        String name;
    }
}