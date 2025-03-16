# Spring Boot Assignment

This is a sample Spring Boot application that includes REST endpoints.

## How to run locally
Make sure you have [Java21](https://www.oracle.com/de/java/technologies/downloads/#java21) and [Docker (preferably desktop)](https://docs.docker.com/desktop/setup/install/windows-install/) installed

Also, make sure, 5432 and 8080 ports are available.

Execute the below commands in order:
1. `./gradlew clean test build`
   This command runs PostgreSQL using Docker Compose and executes tests within the container. It also generates JOOQ classes automatically and applies Flyway migration scripts for testing.
2. `docker compose -f docker-compose-run.yml up -d`
   This command bootstraps a fresh PostgreSQL instance (not from the test) and runs the application on port 8080.

That's all, now you have the app running. 

You can refer to [http-commands.http](http-commands.http) to execute some API endpoints. If you haven't used http files, here [how you can do it](https://timdeschryver.dev/bits/http-files)
Please note, that excessive execution of `/api/members/search` may trigger the rate limit.

DB scripts are inside, `src/main/resources/db.migration` and wwill be executed automatically as part of Flyway migrations and you will have data available to test.

## Notes
- There are more tests and coverage than actually we have in [http-commands.http](http-commands.http) file. Find more integration tests inside **src/main/test/java/assignment** and **src/main/test/java/assignment/controller**.
- Integration tests cover most scenarios, including CORS, rate limiting, authentication, and all cases of member loading expect some regex validation errors.
- When issuing a new access token using a refresh token, we could also return a new refresh token - can be discussed
- JWT token revocation functionality has not been implemented.
- There were challenges in generating JOOQ classes. See: https://github.com/jOOQ/jOOQ/issues/11873
- Audit logs can only be accessed directly from the PostgreSQL database
- For production, obviously secrets and other secure data won't be hardcoded in *yml* files
- We could have added table and proper hashing of passwords for authentication
- To stop running locally execute the command: `docker compose -f docker-compose-run.yml down -v`
