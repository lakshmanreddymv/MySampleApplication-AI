# MySampleApplication — AI Natural Language Search

[![CI](https://github.com/lakshmanreddymv-bot/MySampleApplication-AI/actions/workflows/ci.yml/badge.svg)](https://github.com/lakshmanreddymv-bot/MySampleApplication-AI/actions/workflows/ci.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)
![Gemini API](https://img.shields.io/badge/Gemini-3.5%20Flash-4285F4?logo=google&logoColor=white)
![Tests](https://img.shields.io/badge/Tests-38%20passing-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

> Search 90 catalogue items using plain English. Powered by Gemini 3.5 Flash — no keyword matching, just intent. Material 3 UI with semantic category badges, shimmer skeleton loading, animated pill search bar, and full light/dark theme support.

---

## Screenshots

<p align="center">
  <img src="Screenshot_20260504_161357.png"
       width="270" alt="List Screen"/>
  &nbsp;&nbsp;
  <img src="Screenshot_20260504_161446.png"
       width="270" alt="Search Results"/>
  &nbsp;&nbsp;
  <img src="Screenshot_20260504_161607.png"
       width="270" alt="Detail Screen"/>
</p>

---

## Problem Statement

Traditional list screens force users to scroll or rely on exact keyword matches. A user searching for *"something to eat in Italy"* gets nothing from a `contains()` filter on "Pizza Margherita" or "Pasta Carbonara" — even though those are exact answers.

This project solves that by routing every search through Gemini 3.5 Flash, which understands intent and returns semantically matching results. It is **Project 1 of 4** in an AI-powered Android portfolio, establishing the foundational architecture pattern used across all four projects.

---

## Features

| Feature | Description |
|---------|-------------|
| Natural language search | Type anything — "Japanese street food", "something for cardio", "European capital" — Gemini 3.5 Flash returns matching results |
| AI-generated item detail | Tap any item for a 2–3 sentence description written by the model on demand |
| Semantic category badges | Each card shows a keyword-derived label: Food, Tech, Biology, AI, Space, Health, Wellness, Fitness, Science |
| Material 3 design | Blue-indigo primary, teal secondary accent bar, full light and dark theme via `MaterialTheme.colorScheme` tokens |
| Shimmer skeleton loading | 5 pulsing placeholder cards during API calls — no third-party library, pure Compose animation |
| Animated pill search bar | Shadow elevation animates 2 dp → 6 dp on focus; disabled during in-flight requests |
| Empty and error states | Distinct UI for zero results and network failures, each with a one-tap retry |
| 28 Compose `@Preview` functions | Light + dark variants for every composable — no device needed to review UI changes |
| 38 unit tests | 6 test classes covering use cases, ViewModels, repository, and HTTP layer via MockWebServer |
| Clean Architecture + MVVM + UDF | Domain/data/UI layers with sealed state classes and StateFlow; domain layer has zero Android imports |

---

## How It Works

```mermaid
flowchart TD
    A[User types search query] --> B[ListViewModel.search]
    B --> C[SearchItemsUseCase.invoke]
    C --> D[ItemRepositoryImpl.searchItems]
    D --> E[Build prompt with full 90-item catalogue]
    E --> F[GeminiApiImpl — POST v1beta/gemini-3.5-flash]
    F --> G{API Response}
    G -->|Success| H[Parse JSON array of matching IDs]
    G -->|Error| I[Throw exception with message]
    H --> J[Filter getAllItems by matching IDs]
    J --> K[Emit ListUiState.Success via StateFlow]
    I --> L[Emit ListUiState.Error via StateFlow]
    K --> M[ListScreen recomposes with results]
    L --> N[ListScreen shows error + Retry button]
```

---

## Architecture

```mermaid
graph TD
    subgraph UI Layer
        LS[ListScreen\nComposable]
        DS[DetailScreen\nComposable]
        LVM[ListViewModel\nStateFlow + UDF]
        DVM[DetailViewModel\nStateFlow + UDF]
    end

    subgraph Domain Layer
        SIU[SearchItemsUseCase]
        GIDU[GetItemDetailUseCase]
        IR[ItemRepository\ninterface]
        MLI[MyListItem\ndata class]
    end

    subgraph Data Layer
        IRI[ItemRepositoryImpl]
        GAI[GeminiApiImpl\nOkHttp REST]
        GA[GeminiApi\ninterface]
    end

    LS --> LVM
    DS --> DVM
    LVM --> SIU
    DVM --> GIDU
    SIU --> IR
    GIDU --> IR
    IRI --> IR
    IRI --> GA
    GAI --> GA
```

**Layer rules:**
- **Domain** — pure Kotlin, zero Android or framework imports. Defines `MyListItem`, `ItemRepository` interface, and both use cases.
- **Data** — implements domain interfaces. `ItemRepositoryImpl` holds the catalogue and bridges to `GeminiApiImpl`.
- **UI** — Jetpack Compose screens observe ViewModels via `StateFlow`. All user events call ViewModel functions (UDF).

**SOLID principles applied:**
- **S** — each class has one job (e.g., `SearchItemsUseCase` only handles search logic)
- **O** — new item categories can be added to `getAllItems()` without touching search logic
- **D** — all cross-layer dependencies point at interfaces (`ItemRepository`, `GeminiApi`), never concrete classes

**UDF pattern (every ViewModel):**
- Events flow **UP** from UI → ViewModel public functions
- State flows **DOWN** from ViewModel → UI via `StateFlow`
- UI never mutates state directly

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material 3 | BOM 2024.09 |
| Architecture | Clean Architecture · MVVM · UDF · StateFlow | — |
| AI | Gemini 3.5 Flash REST API | v1beta |
| Networking | OkHttp | 4.12.0 |
| Async | Kotlin Coroutines + StateFlow | — |
| Navigation | Navigation Compose | 2.8.9 |
| Testing | JUnit 4 · MockK · MockWebServer · Coroutines Test · JaCoCo | — |
| CI | GitHub Actions | — |
| Min SDK | Android 7.0 (API 24) | — |
| Target SDK | Android 15 (API 36) | — |

---

## Setup

### Prerequisites
- Android Studio Ladybug or newer
- A [Google AI Studio](https://aistudio.google.com/) API key (free tier is sufficient)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/lakshmanreddymv-bot/MySampleApplication-AI.git
cd MySampleApplication-AI

# 2. Add your Gemini API key to local.properties (NOT committed to git)
echo "gemini.api.key=YOUR_API_KEY_HERE" >> local.properties

# 3. Build and run
./gradlew assembleDebug
# Or open in Android Studio and press Run
```

> `local.properties` is in `.gitignore`. Your API key will never be committed.

---

## Unit Tests

38 tests across 6 test classes. Run with:

```bash
./gradlew :app:testDebugUnitTest
```

| Test Class | Tests | What Is Verified |
|------------|-------|-----------------|
| `SearchItemsUseCaseTest` | 5 | Delegates to repository, returns items, empty results, error propagation, query passthrough |
| `GetItemDetailUseCaseTest` | 3 | Delegates to repository, returns description, propagates exceptions |
| `ListViewModelTest` | 9 | Initial state, query updates, blank query clears, Loading→Success, Loading→Error, clear reset |
| `DetailViewModelTest` | 5 | Initial state, invalid ID→Error, valid ID→Success, correct item name, error with item |
| `ItemRepositoryImplTest` | 10 | 90-item count, first/last item, sequential IDs, JSON parsing, empty array, markdown stripping, prompt construction |
| `GeminiApiImplTest` | 5 | Valid response parsed, error object throws, missing candidates throws, empty body throws, POST request body |
| **Total** | **38** | |

---

## Known Issues & Bugs Fixed

| Issue | Root Cause | Fix |
|-------|-----------|-----|
| `android.util.Log` not available in unit tests | Android stub throws "not mocked" | Removed `Log.d` from `GeminiApiImpl` — debug logging has no place in a data class |
| `org.json` stubs throw in unit tests | Android SDK unit test stubs for `JSONObject`/`JSONArray` | Added `org.json:json:20231013` as `testImplementation` |
| URL construction broke MockWebServer tests | Missing `/` after `trimEnd('/')` gave `"65301models"` as port | Fixed to `"${base.trimEnd('/')}/models/..."` |

---

## Portfolio

| # | Project | AI Technology | Tests | Highlights |
|---|---------|--------------|-------|------------|
| 1 | [MySampleApplication-AI](https://github.com/lakshmanreddymv-bot/MySampleApplication-AI) | Gemini 3.5 Flash (REST) | 38 | Natural language search, Material 3 UI, Clean Architecture foundation |
| 2 | [FakeProductDetector](https://github.com/lakshmanreddymv-bot/FakeProductDetector) | Gemini Vision + Claude API | 46 | Image analysis, multi-model AI, barcode scanning |
| 3 | [EnterpriseDocumentRedactor](https://github.com/lakshmanreddymv-bot/EnterpriseDocumentRedactor) | ML Kit on-device | 0 API cost | Zero-cost PII redaction, fully on-device inference |
| 4 | [MedicalSymptomPreScreener](https://github.com/lakshmanreddymv-bot/MedicalSymptomPreScreener) | Gemini + safety layers | 67 | 3-layer safety, medical disclaimer, CI badge |

Each project builds on the same Clean Architecture + MVVM + UDF foundation established here.

---

## Tech Stack & AI Integration

**AI Layer:**
- **Gemini API** — Google's multimodal LLM used for natural language 
  understanding and intelligent response generation
- **LLM Integration** — REST API calls to large language models 
  using Retrofit + OkHttp, handling streaming responses, 
  error states and rate limiting
- **AI-powered Android features** — on-device intelligence 
  via ML Kit + cloud inference via Gemini API

**Developer Tooling:**
- **MCP (Model Context Protocol)** — open standard that connects 
  AI assistants directly to IDEs, enabling Claude Desktop to 
  read, write and refactor code inside Android Studio in real time

## Author

**Lakshmana Reddy**
Android Tech Lead · 12 years experience · Pleasanton, CA

[![GitHub](https://img.shields.io/badge/GitHub-lakshmanreddymv--bot-181717?logo=github)](https://github.com/lakshmanreddymv-bot)
[![Email](https://img.shields.io/badge/Email-lakshmanreddymv%40gmail.com-EA4335?logo=gmail&logoColor=white)](mailto:lakshmanreddymv@gmail.com)
