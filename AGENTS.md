# Repository Guidelines

## Project Structure & Module Organization
Library code lives under `src/main/java/com/xmb/llm/requester`. `core/` carries the OpenAI-style request pipeline, `http/` manages Apache HttpClient reuse, `utils/` holds shared helpers, and `constants/` lists cross-cutting enums. Configuration defaults belong in `src/main/resources/application.properties`. Mirror the production package tree under `src/test/java` for automated tests.

## LLM Client Configuration
Define endpoints in consuming apps with `LLMEndpoint.builder()` and group them via `LLMClientConfiguration`. Instantiate an `LLMClient`, register it with `LLMClientRegistry.setDefault(...)`, and wire custom clients into requesters when you need isolation. Pull secrets from environment managers rather than source and feed them through `apiKeySupplier`; avoid hard-coding keys. Adjust behaviour per call using `LLMRequestOptions`—for example set `temperature`, `maxTokens`, or streaming flags to match your workload.

## Build, Test, and Development Commands
Use `./mvnw clean verify` to compile and run tests; during quick edits `./mvnw test` is enough. Inspect dependencies with `./mvnw dependency:tree` when proposing new libraries, and always rely on the Maven wrapper to avoid version drift.

## Coding Style & Naming Conventions
Keep Java sources tab-indented with K&R braces. Document public entry points briefly and comment only when logic is non-obvious. Follow `PascalCase` for types, `camelCase` for methods, and `UPPER_SNAKE` for constants.

## Testing Guidelines
Write JUnit 5 tests under mirrored packages in `src/test/java`, naming classes `${ClassName}Test` and methods `should...`. Mock HTTP seams with Mockito or light fakes, and ensure failing tests reproduce defects before applying fixes.

## Commit & Pull Request Guidelines
Keep commit messages concise, present-tense, and under 60 characters. Run `./mvnw clean` before staging to avoid `target/` artifacts. Pull requests should explain the change, link tracking issues, and capture evidence (screenshots or curl logs) for new endpoints or behaviours.

## Configuration Notes
Keep environment secrets out of version control; `application.properties` should expose only safe defaults. Document required keys in your PR description and share sample `.env` snippets out-of-band.
