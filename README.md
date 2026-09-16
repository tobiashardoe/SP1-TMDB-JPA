# SP1-TMDB-JPA

This is a small JPA backend for recent Danish movies from TMDb. `TMDBReader` reads
TMDb JSON into DTOs, `TmdbImporter` converts DTOs to entities, and the DAOs read
and write the PostgreSQL database. Importing is a one-time step; later database
queries do not call TMDb.

## Run it

1. Create a PostgreSQL database and set `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
   and `TMDB_API_KEY` in `src/main/resources/config.properties`.
2. In `Main`, set `importMovies` to `true` and run it once. Set it back to `false`
   for normal runs. Hibernate keeps the tables between runs.
3. Use `MovieDAO`, `ActorDAO`, `DirectorDAO`, and `GenreDAO` to work with the stored data.

`MovieDAO` provides movie CRUD, case-insensitive title search, movies by genre,
average rating, and the top 10 highest rated, lowest rated, and most popular movies.
The people and genre DAOs provide lists of their entities. Movies have many-to-many
relationships with actors, directors, and genres because each can belong to more
than one movie. TMDb IDs let the import reuse the same person or genre across movies.

`MovieDAOTest` contains a PostgreSQL Testcontainers test for the main database
operations.
