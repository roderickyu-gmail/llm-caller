# llm-requester

Reusable Java library that wraps OpenAI-compatible chat-completion APIs behind a configurable client. The code in this repository focuses on request construction, retry/failover orchestration, and response parsing so that consuming projects can supply their own LLM endpoints, secrets, and tuning parameters without copying logic.

## Architecture In A Nutshell
- `com.hoocta.llm.requester.core.config`: builders for `LLMEndpoint`, `LLMRequestOptions`, and `LLMClientConfiguration`. They describe **where** to call and which defaults to apply.
- `com.hoocta.llm.requester.core.LLMClient`: high-level orchestrator. Handles retries, failover between endpoint tiers, streaming flags, and token usage aggregation.
- `com.hoocta.llm.requester.core.common.*`: family of typed requesters (plain text, booleans, list parsers, JSON, etc.) that reuse `LLMClient` and parse tagged responses into domain-friendly shapes.
- `com.hoocta.llm.requester.http.*`: Apache HttpClient wrappers that execute the actual HTTPS requests.
- `com.hoocta.llm.requester.utils.*`: shared helpers (JSON, crypto, threading, logging) reused throughout the codebase.

Everything is built around OpenAI’s chat-completion schema: messages (`Message`), parameters (`OpenAIParams`), responses (`OpenAIResponseSuccessResult` / `OpenAIErrorResult`).

## Requirements & Build
- Java 21 or newer
- Maven 3.9+

```bash
./mvnw clean install
```

The library publishes a standard JAR (`llm-requester-<version>.jar`) that other projects can consume.

## Quick Start (Standalone Example)
```java
LLMEndpoint standard = LLMEndpoint.builder()
    .name("standard")
    .baseUrl(System.getenv("LLM_BASE_URL"))
    .model(System.getenv("LLM_MODEL"))
    .apiKeySupplier(() -> System.getenv("LLM_API_KEY"))
    .defaultOptions(LLMRequestOptions.builder().temperature(0.2f).build())
    .build();

LLMEndpoint smart = LLMEndpoint.builder()
    .name("smart")
    .baseUrl(System.getenv("LLM_SMART_URL"))
    .model(System.getenv("LLM_SMART_MODEL"))
    .apiKeySupplier(() -> System.getenv("LLM_SMART_KEY"))
    .build();

LLMClientConfiguration cfg = LLMClientConfiguration.builder()
    .standardEndpoint(standard)
    .smartEndpoint(smart)
    .defaultOptions(LLMRequestOptions.builder().maxTokens(512).build())
    .build();

LLMClient client = LLMClient.builder(cfg)
    .maxAttemptsPerEndpoint(5)
    .trackUsage(true)
    .build();

// Option A: set as global default for built-in singleton requesters
LLMClientRegistry.setDefault(client);

String answer = FixedPlainTextAIRequester.getInstance()
    .complete("You are a concise assistant.", "用一句中文介绍这段库。", true,
        LLMRequestOptions.builder().temperature(0.6f).stream(false).build());
System.out.println(answer);
```

If you prefer dependency injection, create `new FixedPlainTextAIRequester(client)` instead of using the registry-backed singleton.

## Using From Another Project
1. **Add the dependency** (after running `mvn install` locally or publishing to your artifact repository):
   ```xml
   <dependency>
     <groupId>io.github.roderickyu</groupId>
     <artifactId>llm-requester</artifactId>
     <version>0.0.4</version>
   </dependency>
   ```
2. **Provide runtime configuration**: read endpoints, models, and API keys from environment variables, your secrets manager, or Spring configuration properties.
3. **Build the `LLMClientConfiguration`** with the endpoints you want to try in order. The `SMART` tier is optional; if absent it falls back to `STANDARD`.
4. **Construct an `LLMClient`** and register it (either via `LLMClientRegistry.setDefault(...)` or by wiring it into the requesters you instantiate).
5. **Issue requests** using the typed requesters (`FixedPlainTextAIRequester`, `FixedBooleanAIRequester`, `FixedStrListAIRequester`, etc.) or call `LLMClient.complete(...)` directly if you need full control.

## Request Options & Behaviour
- `LLMRequestOptions` is immutable; build one per invocation or reuse a cached instance. Fields include `temperature`, `maxTokens`, `topP`, `presencePenalty`, `frequencyPenalty`, and `stream`.
- Options are merged in order: configuration defaults → endpoint defaults → per-call overrides.
- Retries: each endpoint is attempted up to `maxAttemptsPerEndpoint` (default 5) when network errors, invalid finish reasons, or parsing failures occur.
- Failover: if all retries on an endpoint fail, the client advances to the next endpoint in the tier. When using the `SMART` tier, it will fall back to the standard tier if no smart endpoints succeed.
- Usage tracking: when `trackUsage(true)` the client keeps token counts per model (`LLMClient.usageSnapshot()` returns a defensive copy).

## Operational Notes
- **Secrets**: never hard-code API keys. Use env vars, Vault/Secrets Manager, or OS keychains. The endpoint builders accept a `Supplier<String>` so you can refresh keys lazily.
- **Logging**: `LogUtils` writes detailed request/response info; ensure log files or stdout are handled appropriately in production.
- **Streaming**: setting `stream(true)` toggles SSE-style responses. The current implementation collects streamed chunks into memory; adjust if you need incremental processing.
- **Threading**: request execution relies on Apache HttpClient pooling. For high-throughput scenarios, review connection pool sizing in `HttpClientMgr`.
- **Testing**: mock out `LLMClient` or provide fake endpoints that return canned JSON; the design avoids static singletons when you pass clients explicitly.

## Attention & Known Gaps
- No automated tests yet—add unit/integration tests before large refactors.
- Error classification is coarse; consider introducing resilience libraries (Resilience4j, Spring Retry) for backoff, circuit breaking, and metrics.
- `utils/` and `constants/` house many unrelated helpers; long term, split them into smaller modules or migrate to well-known libraries.
- Usage reporting is in-memory only; hook it up to your observability stack if you need shared analytics.
- Some legacy classes (Ernie-specific requesters, CV parsers) may be unused; audit before shipping to consumers.

## Getting Help
- Read `AGENTS.md` for contributor guidelines.
- See `com.hoocta.llm.requester.core.common` for additional requester flavors and parsing examples.
- Issues and PRs should document the endpoint configuration you used and include screenshots or logs of new behaviours.
