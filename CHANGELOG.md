# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.3.1] — 2026-05-17

### Changed
- `ItemCard` accent bar color: `colorScheme.primary` → `colorScheme.secondary`
- `ItemCard` badge: replaced static `#N` id label with `categoryLabel()` — maps title
  keywords (DNA/Gene→Biology, AI/Neural→AI, Space/Mars→Space, Diet/Health→Health,
  Meditation/Sleep→Wellness, Computing/Code→Tech, else→Science)

---

## [1.3.0] — 2026-05-17

### Added

#### Change 1 — Pill-shaped AI Search Bar (`AiSearchBar`)
- Replaced the `OutlinedTextField`-in-TopAppBar approach with a dedicated full-width pill-shaped `AiSearchBar` composable
- Leading icon: `Icons.Default.Search` tinted `secondary` (teal accent)
- Trailing icon: `Icons.Default.Mic` — muted/decorative, not wired to mic functionality
- Placeholder: "Search with AI…"
- Shadow elevation animates 2 dp → 6 dp when the field is focused or has text (`animateDpAsState`)
- Background: `surfaceVariant` token; indicator (underline) removed for pill aesthetic
- Keyboard IME action: `ImeAction.Search` → triggers VM search

#### Change 2 — Elevated `ItemCard`
- Elevation raised to 2 dp; container colour changed from `surfaceVariant` → `surface`
- **Left accent border**: 4 dp wide `Box` spanning full card height, filled with `primary`
- **Item title**: `typography.titleMedium` + `FontWeight.SemiBold` + `onSurface` colour
- **Item number badge** (top-end corner): `primary` background, `onPrimary` text, `labelSmall`, 4 dp rounded corners, `#N` format
- Content padding: 12 dp all sides; badge space guarded with 44 dp end padding on title

#### Change 3 — Two-Zone Background Layout
- Search zone: full-width `Box` with `surfaceVariant` background + 16 dp all-sides padding containing `AiSearchBar`
- List zone: `Box` with `background` colour containing the state-driven list content
- TopAppBar retains `primary` container — creates a clean three-layer visual progression: primary → surfaceVariant → background

#### Change 4 — Illustrated `EmptyState`
- Replaced single-text `EmptyStateMessage` with the new `EmptyState` composable
- `Icons.Default.SearchOff` at 64 dp, tinted `secondary`
- "No results found" in `typography.headlineSmall`
- "Try a different search term" in `typography.bodyMedium` + `onSurfaceVariant`
- 24 dp spacing between elements via `Arrangement.spacedBy`

#### Change 5 — Shimmer Skeleton Loading (`ShimmerItemList` / `ShimmerCard`)
- Replaced the spinner overlay with 5 skeleton cards using pure Compose animation — zero third-party libraries
- `rememberInfiniteTransition` + `animateFloat` (0.3f → 1.0f, `LinearEasing`, 800 ms, `RepeatMode.Reverse`)
- Skeleton cards mirror `ItemCard` structure: left accent bar + title skeleton (70 % width, 16 dp) + badge skeleton
- All skeleton colours are `onSurface.copy(alpha = shimmerAlpha × 0.15f)` — no hardcoded hex

#### Change 6 — List Zone Polish
- `LazyColumn` now uses `contentPadding = PaddingValues(horizontal=16.dp, vertical=8.dp)` and `verticalArrangement = Arrangement.spacedBy(6.dp)` for cards that float above the background

### Changed
- `ListScreenContent` signature: `onClear` parameter removed (no clear button in new design; ViewModel clears internally when `search("")` is called)
- `ListScreen` updated to match — no longer passes `onClear`
- `TopAppBar` title changed from embedded search field to "AI Catalogue" text label
- `material-icons-extended` added as explicit compile dependency (`libs.androidx.material.icons.extended`)
- `libs.versions.toml`: `androidx-material-icons-extended` library alias added

### Removed
- `SearchField` composable (replaced by `AiSearchBar`)
- `LoadingOverlay` composable (replaced by `ShimmerItemList`)
- `EmptyStateMessage` composable (replaced by `EmptyState`)

### Added — Previews
- `AiSearchBarPreview` / `AiSearchBarPreviewDark`
- `EmptyStatePreview` / `EmptyStatePreviewDark`
- `ShimmerItemListPreview` / `ShimmerItemListPreviewDark`
- All existing screen-level and component-level previews updated to match new composable signatures and new design

---

## [1.2.0] — 2026-05-17

### Added

#### Part 1 — Material 3 Color Design System
- **`Color.kt` — full M3 palette** with two semantic families:
  - *Primary*: deep blue-indigo (`#1A237E` / `Blue800`) with light (`Blue100`), dark (`Blue200`), and container (`Blue900`) variants
  - *Secondary*: vibrant teal (`#00897B` / `Teal600`) with light (`Teal200`), container (`Teal100`), and dark-on (`Teal700`) variants
  - Neutral scale: `Grey900` → `Grey50`, error reds (`Red700` light / `Red200` dark), surface variants (`BlueGrey50` / `BlueGrey800`), and fixed `White` / `Black`
  - Every token is documented with KDoc explaining its role in the scheme
- **`Theme.kt` — full `lightColorScheme` + `darkColorScheme`** covering all 24 M3 colour roles:
  `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`,
  `secondary`, `onSecondary`, `secondaryContainer`, `onSecondaryContainer`,
  `tertiary`, `onTertiary`, `background`, `onBackground`,
  `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`,
  `error`, `onError`, `errorContainer`, `onErrorContainer`,
  `outline`, `outlineVariant`, `scrim`
  - `dynamicColor` parameter removed — consistent branding on all devices and API levels
- **`Type.kt` — full M3 typography scale** (12 tokens): `headlineLarge`, `headlineMedium`, `headlineSmall`, `titleLarge`, `titleMedium`, `titleSmall`, `bodyLarge`, `bodyMedium`, `bodySmall`, `labelLarge`, `labelMedium`, `labelSmall`

#### Part 1 — M3 tokens applied to all composables
- **`ListScreen`** refactored into stateless `ListScreenContent` + focused sub-composables:
  - `SearchField` — `OutlinedTextField` styled with `onPrimary`-derived colours (text, border, cursor, placeholder) against the primary TopAppBar background; no hardcoded hex
  - `ItemList` — delegates to `ItemCard`
  - `ItemCard` — Material 3 `Card()` with `containerColor = surfaceVariant` and `1.dp` elevation; text uses `bodyLarge` + `onSurface`
  - `LoadingOverlay` — `surface.copy(alpha = 0.85f)` background; spinner tinted with `primary`; label uses `bodyMedium`
  - `EmptyStateMessage` — `bodyLarge` + `onSurfaceVariant`
  - `SearchErrorState` — error text in `error` colour, retry label in `labelMedium`
  - `TopAppBar` — `containerColor = primary`, `titleContentColor = onPrimary`, `actionIconContentColor = onPrimary`
- **`DetailScreen`** refactored into stateless `DetailScreenContent` + sub-composables:
  - `DetailLoadingContent` — identical overlay pattern to `LoadingOverlay` above
  - `DetailSuccessContent` — AI description in `bodyLarge` + `onSurface`
  - `DetailErrorContent` — error/retry pattern matching `SearchErrorState`
  - `TopAppBar` — same primary colour tokens; title in `titleLarge`

#### Part 2 — Compose Previews
- **38 `@Preview` functions** added across `ListScreen.kt` and `DetailScreen.kt` — every composable has both a light and a dark variant
- **`ListScreen.kt`** — 16 preview functions (8 composables × 2 modes):
  - `ListScreenPreview` / `ListScreenPreviewDark` — Idle state with 5 catalogue items
  - `ListScreenEmptyStatePreview` / `ListScreenEmptyStatePreviewDark` — Success with empty result list
  - `ListScreenLoadingPreview` / `ListScreenLoadingPreviewDark` — Loading overlay
  - `ListScreenErrorPreview` / `ListScreenErrorPreviewDark` — Error state with retry
  - `ItemCardPreview` / `ItemCardPreviewDark` — via `MyListItemPreviewProvider` (3 items each)
  - `SearchFieldPreview` / `SearchFieldPreviewDark`
  - `LoadingOverlayPreview` / `LoadingOverlayPreviewDark`
  - `EmptyStateMessagePreview` / `EmptyStateMessagePreviewDark`
  - `SearchErrorStatePreview` / `SearchErrorStatePreviewDark`
- **`DetailScreen.kt`** — 12 preview functions (6 composables × 2 modes):
  - `DetailScreenPreview` / `DetailScreenPreviewDark` — Success state
  - `DetailScreenLoadingPreview` / `DetailScreenLoadingPreviewDark`
  - `DetailScreenErrorPreview` / `DetailScreenErrorPreviewDark`
  - `DetailLoadingContentPreview` / `DetailLoadingContentPreviewDark`
  - `DetailSuccessContentPreview` / `DetailSuccessContentPreviewDark`
  - `DetailErrorContentPreview` / `DetailErrorContentPreviewDark`
- `MyListItemPreviewProvider` (`PreviewParameterProvider<MyListItem>`) eliminates duplication across `ItemCard` previews
- All previews use `@Preview(showBackground = true)` and `uiMode = UI_MODE_NIGHT_YES`; all wrap content in `MySampleApplicationTheme`; all use mock data — zero real API calls

### Changed
- `MySampleApplicationTheme` signature simplified: `dynamicColor` parameter removed (was `Boolean = true`); now always uses static brand palette
- `ListScreen` → thin stateful wrapper; stateless `ListScreenContent` now hosts all rendering logic (enables preview + improves testability)
- `DetailScreen` → thin stateful wrapper; stateless `DetailScreenContent` now hosts all rendering logic

### Fixed
- `dynamicColor` was `true` by default — on Android 12+ devices this overrode the brand palette with wallpaper colours; now permanently disabled

---

## [1.1.0] — 2026-04-29

### Added
- **38 unit tests** across 6 test classes (was 0 real tests)
  - `SearchItemsUseCaseTest` (5 tests)
  - `GetItemDetailUseCaseTest` (3 tests)
  - `ListViewModelTest` (9 tests)
  - `DetailViewModelTest` (5 tests)
  - `ItemRepositoryImplTest` (10 tests)
  - `GeminiApiImplTest` (5 tests — via MockWebServer)
- **KDoc** on all 14 Kotlin source files — every class, interface, function, and property
- **SOLID + UDF documentation** in KDoc and README for all ViewModels and use cases
- **GitHub Actions CI** (`.github/workflows/ci.yml`) — runs unit tests and debug build on every push and PR to `main`
- **README rewritten** to portfolio standard: problem statement, Mermaid flowchart, Mermaid architecture diagram, tech stack table, setup instructions, unit tests table, bugs-fixed table, portfolio table (all 4 projects), author section
- **CHANGELOG.md** (this file)
- Test dependencies: `mockk 1.13.10`, `kotlinx-coroutines-test 1.9.0`, `mockwebserver 4.12.0`, `org.json 20231013`

### Changed
- `GeminiApiImpl`: `OkHttpClient` is now injectable (default parameter) to enable unit testing
- `GeminiApiImpl`: Added `baseUrl` parameter (default: Gemini production URL) so tests can redirect to MockWebServer
- `GeminiApiImpl`: Removed `android.util.Log.d` — debug logging in a data class violates single responsibility and breaks unit tests
- URL construction fixed: missing `/` between base URL and path segment

### Fixed
- `org.json.JSONArray`/`JSONObject` stubs throwing in unit tests (added real `org.json` test dependency)
- URL malformed as `"65301models"` port in MockWebServer tests

---

## [1.0.0] — 2026-03-15

### Added
- AI-powered natural language search using **Gemini 2.0 Flash** REST API
- Clean Architecture with three layers: `domain/`, `data/`, `ui/`
- `SearchItemsUseCase` and `GetItemDetailUseCase` for domain-layer business logic
- `ItemRepositoryImpl` with a 90-item catalogue spanning food, tech, sports, nature, cities, art, science, and wellness
- `GeminiApiImpl` — OkHttp-based REST client with JSON request/response parsing and error handling
- `ListViewModel` and `DetailViewModel` following MVVM + Unidirectional Data Flow with sealed `UiState` classes and `StateFlow`
- `ListScreen` — searchable catalogue with AI loading overlay and error/retry states
- `DetailScreen` — AI-generated item description with loading overlay and error/retry
- Navigation Compose — `list` → `detail/{itemId}` route
- PR #1: Loading spinner overlay with semi-transparent background and AI status labels
