# CLAUDE.md — MySampleApplication-AI

## Project

AI-powered natural language search over 90 catalogue items. Gemini 2.5 Flash via OkHttp REST. Clean Architecture + MVVM + UDF. Portfolio Project 1 of 4.

## Architecture Contracts

- **Domain layer** (`domain/`) — pure Kotlin only. No `android.*` imports. No OkHttp. No Compose. Unit tests run without Robolectric.
- **Data layer** (`data/`) — implements domain interfaces. `GeminiApiImpl` must keep `client` and `baseUrl` as injectable default params (MockWebServer tests depend on this).
- **UI layer** (`ui/`) — Jetpack Compose + ViewModel. Events flow UP via ViewModel public functions. State flows DOWN via `StateFlow`. UI never mutates state.

## Critical Rules

- Never add `android.util.Log` to `data/` or `domain/` classes — it throws "Method not mocked" in unit tests.
- Never remove `testImplementation(libs.org.json)` from `app/build.gradle.kts` — `org.json` stubs throw without it.
- Never commit a real `gemini.api.key` — it lives in `local.properties` which is gitignored. CI uses `PLACEHOLDER`.
- URL construction in `GeminiApiImpl`: `"${baseUrl.trimEnd('/')}/models/..."` — the leading `/` before `models` is required.

## Skill Routing

When the user's request matches an available skill, invoke it via the Skill tool.

Key routing rules:
- Product ideas/brainstorming → invoke /office-hours
- Strategy/scope → invoke /plan-ceo-review
- Architecture → invoke /plan-eng-review
- Design system/plan review → invoke /design-consultation or /plan-design-review
- Full review pipeline → invoke /autoplan
- Bugs/errors → invoke /investigate
- QA/testing site behavior → invoke /qa or /qa-only
- Code review/diff check → invoke /review
- Visual polish → invoke /design-review
- Ship/deploy/PR → invoke /ship or /land-and-deploy
- Save progress → invoke /context-save
- Resume context → invoke /context-restore
- Retrospective → invoke /retro
