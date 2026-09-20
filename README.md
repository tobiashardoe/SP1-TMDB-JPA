# SP1-TMDB-JPA

Importen skal kun køres én gang. Efter importen hentes alle data direkte fra
vores egen database.

## Sådan køres projektet

1. Opret en PostgreSQL-database.
2. Indsæt `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` og `TMDB_API_KEY` i
   `src/main/resources/config.properties`.
3. Sæt `importMovies` til `true` i `Main`, og kør programmet én gang for at
   hente film fra TMDb.
4. Sæt derefter `importMovies` tilbage til `false` ved normale kørsler.


## Data i databasen

Databasen indeholder i øjeblikket:

- 1.524 film
- 4.417 actors
- 1.067 directors
- 19 genres

## Tests

Start Docker Desktop, og kør:

Seneste testresultat:

```text
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0
```
