package app.service;

import app.dao.MovieDAO;
import app.entities.Movie;

import java.time.LocalDate;
import java.util.List;

public class MovieService {
    private final MovieDAO movieDAO;

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public List<Movie> getAllMovies() {
        return movieDAO.getAll();
    }

    public Movie create(Movie movie) {
        return movieDAO.create(movie);
    }

    public Movie findById(Long id) {
        return movieDAO.findById(id);
    }

    public void delete(Long id) {
        movieDAO.delete(id);
    }

    public List<Movie> searchByTitle(String title) {
        return movieDAO.searchByTitle(title);
    }

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieDAO.findByGenre(genreId);
    }

    public void updateTitleAndReleaseDate(Long id, String title, LocalDate releaseDate) {
        movieDAO.updateTitleAndReleaseDate(id, title, releaseDate);
    }

    public double getAverageRating() {
        return movieDAO.averageRating();
    }

    public List<Movie> getTop10HighestRated() {
        return movieDAO.highestRated();
    }

    public List<Movie> getTop10LowestRated() {
        return movieDAO.lowestRated();
    }

    public List<Movie> getTop10MostPopular() {
        return movieDAO.mostPopular();
    }
}
