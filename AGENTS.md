# Repository Guidelines

## Project Structure & Module Organization
Application code lives under `src/main/java/com/hoocta/llm/requester`, with `core/` handling the request pipeline, `http/` managing reusable Apache HttpClient assets, `utils/` housing shared helpers, and `constants/` keeping enums. Store safe defaults in `src/main/resources/application.properties`. Mirror the production package tree under `src/test/java` so tests line up with the code they verify, and keep repository documentation at the root.

## Build, Test, and Development Commands
- `./mvnw clean verify` — full compile, unit tests, and integration checks; run before pushing.
- `./mvnw test` — fast unit-test cycle during development.
- `./mvnw dependency:tree` — inspect dependency impact before proposing new libraries.
Use the Maven wrapper consistently to avoid local version drift.

## Coding Style & Naming Conventions
Write Java with tabs and K&R braces. Favor single-purpose methods, and add brief Javadoc only when behavior is non-obvious. Stick to PascalCase types, camelCase methods and variables, and UPPER_SNAKE constants; keep package names lowercase and descriptive. Ensure files end with a newline and avoid trailing whitespace.

## Testing Guidelines
Tests use JUnit 5 and Mockito. Name classes `${ClassName}Test` and methods `should...` to capture expected behavior. Place fixtures under `src/test/resources` and fake HTTP seams rather than hitting real services. Reproduce defects with failing tests before fixes, and run `./mvnw test` prior to any commit.

## Commit & Pull Request Guidelines
Keep commit messages present-tense, ≤60 characters, and scoped to one change (e.g., `Add retry policy to LLM client`). Run `./mvnw clean` before staging to prevent `target/` artifacts. Pull requests should explain the change, link issues, document required configuration, and attach evidence (curl logs or screenshots) for new endpoints or behaviors.

## Security & Configuration Tips
Never hard-code secrets; supply them via environment-backed `apiKeySupplier` hooks. Limit `application.properties` to non-sensitive defaults and document required environment keys in your PR description. Share sample `.env` guidance out of band when onboarding new contributors.
