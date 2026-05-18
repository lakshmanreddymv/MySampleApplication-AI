package com.example.mysampleapplication.ui.list

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.ui.theme.MySampleApplicationTheme

// ── Stateful entry point ──────────────────────────────────────────────────────

/**
 * Root composable for the list/search screen.
 *
 * Observes [ListViewModel.uiState] and [ListViewModel.query], then delegates all
 * rendering to the stateless [ListScreenContent] composable.
 *
 * @param vm          The [ListViewModel] driving this screen.
 * @param onItemClick Callback invoked when the user taps an item.
 */
@Composable
fun ListScreen(vm: ListViewModel, onItemClick: (MyListItem) -> Unit) {
    val query by vm.query.collectAsState()
    val uiState by vm.uiState.collectAsState()

    ListScreenContent(
        query         = query,
        uiState       = uiState,
        allItems      = vm.allItems,
        onQueryChange = vm::onQueryChange,
        onSearch      = { vm.search(query) },
        onItemClick   = onItemClick,
    )
}

// ── Stateless content composable ─────────────────────────────────────────────

/**
 * Stateless rendering composable for the list/search screen.
 *
 * Layout zones:
 * 1. [TopAppBar] — primary-coloured app bar with title "AI Catalogue"
 * 2. **Search zone** — [MaterialTheme.colorScheme.surfaceVariant] background with 16 dp padding
 *    containing the full-width pill-shaped [AiSearchBar]
 * 3. **List zone** — [MaterialTheme.colorScheme.background] showing one of:
 *    - [ListUiState.Idle]    → [ItemList] with the full catalogue
 *    - [ListUiState.Loading] → [ShimmerItemList] (5 skeleton cards)
 *    - [ListUiState.Success] → filtered [ItemList] or [EmptyState]
 *    - [ListUiState.Error]   → [SearchErrorState] with retry action
 *
 * @param query         Current search query text.
 * @param uiState       Current screen state.
 * @param allItems      Full unfiltered catalogue (shown in Idle state).
 * @param onQueryChange Called on every keystroke in the search field.
 * @param onSearch      Called when the user submits the search.
 * @param onItemClick   Called when the user taps a catalogue item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListScreenContent(
    query: String,
    uiState: ListUiState,
    allItems: List<MyListItem>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onItemClick: (MyListItem) -> Unit,
) {
    val isLoading = uiState is ListUiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "AI Catalogue",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Search Zone ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                AiSearchBar(
                    query         = query,
                    isLoading     = isLoading,
                    onQueryChange = onQueryChange,
                    onSearch      = onSearch,
                )
            }

            // ── List Zone ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                when (val state = uiState) {
                    is ListUiState.Idle    -> ItemList(items = allItems, onItemClick = onItemClick)
                    is ListUiState.Loading -> ShimmerItemList()
                    is ListUiState.Success -> {
                        if (state.items.isEmpty()) {
                            EmptyState(modifier = Modifier.align(Alignment.Center))
                        } else {
                            ItemList(items = state.items, onItemClick = onItemClick)
                        }
                    }
                    is ListUiState.Error   -> {
                        SearchErrorState(
                            message  = state.message,
                            onRetry  = onSearch,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

// ── Search bar ────────────────────────────────────────────────────────────────

/**
 * Full-width pill-shaped AI search bar.
 *
 * Visual anatomy:
 * - Background: [MaterialTheme.colorScheme.surfaceVariant] — matches the search zone behind it
 * - Leading icon: [Icons.Default.Search] tinted [MaterialTheme.colorScheme.secondary]
 * - Trailing icon: [Icons.Default.Mic] (muted, decorative — not wired to mic functionality)
 * - Shadow elevation animates from 2 dp (idle) to 6 dp (focused or active query)
 * - Indicator (underline) is hidden — pill border is the only affordance
 *
 * @param query         Current field value.
 * @param isLoading     When `true` the field is disabled (AI search in progress).
 * @param onQueryChange Called on every character change.
 * @param onSearch      Called when the user submits via the IME Search action.
 * @param modifier      Optional modifier applied to the outermost element.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AiSearchBar(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isFocused || query.isNotEmpty()) 6.dp else 2.dp,
        label       = "search_bar_elevation",
    )

    val pillShape = RoundedCornerShape(28.dp)

    TextField(
        value             = query,
        onValueChange     = onQueryChange,
        modifier          = modifier
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = pillShape, clip = false),
        shape             = pillShape,
        placeholder       = {
            Text(
                text  = "Search with AI...",
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        leadingIcon       = {
            Icon(
                imageVector        = Icons.Default.Search,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.secondary,
            )
        },
        trailingIcon      = {
            // Mic icon is a non-functional visual placeholder — mic feature not implemented.
            Icon(
                imageVector        = Icons.Default.Mic,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        },
        singleLine        = true,
        enabled           = !isLoading,
        colors            = TextFieldDefaults.colors(
            focusedContainerColor    = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor    = Color.Transparent,
            unfocusedIndicatorColor  = Color.Transparent,
            disabledIndicatorColor   = Color.Transparent,
        ),
        keyboardOptions   = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions   = KeyboardActions(onSearch = { onSearch() }),
        interactionSource = interactionSource,
    )
}

// ── Item list ─────────────────────────────────────────────────────────────────

/**
 * Scrollable list of [MyListItem] entries rendered as elevated [ItemCard] rows.
 *
 * Uses 16 dp horizontal / 8 dp vertical content padding and 6 dp spacing between cards
 * so cards float above the [MaterialTheme.colorScheme.background] zone.
 *
 * @param items       Items to display.
 * @param onItemClick Callback invoked when the user taps a card.
 * @param modifier    Optional modifier applied to the [LazyColumn].
 */
@Composable
internal fun ItemList(
    items: List<MyListItem>,
    onItemClick: (MyListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier            = modifier,
    ) {
        items(items) { item ->
            ItemCard(item = item, onItemClick = onItemClick)
        }
    }
}

// ── Item card ─────────────────────────────────────────────────────────────────

/**
 * Maps an item title to a display category string by scanning for known keywords.
 *
 * Priority order matters: first match wins.
 * Returns `"Science"` when no keyword matches.
 *
 * @param title The [MyListItem.text] to classify.
 */
internal fun categoryLabel(title: String): String {
    val t = title.lowercase()
    return when {
        "dna" in t || "gene" in t         -> "Biology"
        "ai" in t || "neural" in t        -> "AI"
        "space" in t || "mars" in t       -> "Space"
        "diet" in t || "health" in t      -> "Health"
        "meditation" in t || "sleep" in t -> "Wellness"
        "computing" in t || "code" in t   -> "Tech"
        else                               -> "Science"
    }
}

/**
 * Elevated Material 3 [Card] representing a single catalogue entry.
 *
 * Visual anatomy (left → right):
 * - 4 dp [MaterialTheme.colorScheme.secondary] accent bar spanning the full card height
 * - Content area (12 dp all-sides padding):
 *   - Item title in [MaterialTheme.typography.titleMedium] with [FontWeight.SemiBold]
 *   - Category badge (top-end corner): primary background, onPrimary text, 4 dp corners.
 *     Label is derived from [categoryLabel].
 *
 * Elevation: 2 dp. Container colour: [MaterialTheme.colorScheme.surface].
 *
 * @param item        The catalogue item to display.
 * @param onItemClick Callback invoked when the user taps this card.
 */
@Composable
internal fun ItemCard(
    item: MyListItem,
    onItemClick: (MyListItem) -> Unit,
) {
    Card(
        onClick   = { onItemClick(item) },
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // ── Left secondary accent border ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(MaterialTheme.colorScheme.secondary),
            )

            // ── Content ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text     = item.text,
                    style    = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color    = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 44.dp), // guard space for the badge
                )

                // ── Item number badge (top-end) ───────────────────────────────
                Box(
                    modifier         = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color  = MaterialTheme.colorScheme.primary,
                            shape  = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = categoryLabel(item.text),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

// ── Shimmer skeleton loading ──────────────────────────────────────────────────

/**
 * Displays 5 placeholder skeleton cards with a pulsing shimmer animation.
 *
 * Uses a single [rememberInfiniteTransition] so all 5 cards pulse in lockstep,
 * driven by `animateFloat` from 0.3f → 1f (LinearEasing, 800 ms, Reverse).
 * No third-party library — pure Compose animation only.
 *
 * @param modifier Optional modifier applied to the [LazyColumn].
 */
@Composable
internal fun ShimmerItemList(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label         = "shimmer_alpha",
    )

    LazyColumn(
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier            = modifier,
    ) {
        items(5) {
            ShimmerCard(alpha = shimmerAlpha)
        }
    }
}

/**
 * Single skeleton card that mirrors the dimensions and structure of [ItemCard].
 *
 * All coloured boxes use [MaterialTheme.colorScheme.onSurface] (or [MaterialTheme.colorScheme.primary])
 * at varying `alpha` multiples of the animated [alpha] value — no hardcoded colours.
 *
 * @param alpha Animated opacity multiplier in the range [0.3, 1.0].
 */
@Composable
private fun ShimmerCard(alpha: Float) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // Left accent bar skeleton
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = alpha * 0.5f),
                    ),
            )

            // Content skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(40.dp),
            ) {
                // Title skeleton — 70 % width, 16 dp tall
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.15f),
                            shape = RoundedCornerShape(4.dp),
                        ),
                )

                // Badge skeleton — top-end corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(width = 32.dp, height = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.15f),
                            shape = RoundedCornerShape(4.dp),
                        ),
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

/**
 * Illustrated empty state shown when a search returns zero results.
 *
 * Visual hierarchy (top → bottom, 24 dp spacing):
 * - [Icons.Default.SearchOff] at 64 dp, tinted [MaterialTheme.colorScheme.secondary]
 * - "No results found" in [MaterialTheme.typography.headlineSmall]
 * - "Try a different search term" in [MaterialTheme.typography.bodyMedium] +
 *   [MaterialTheme.colorScheme.onSurfaceVariant]
 *
 * @param modifier Optional modifier (typically [Alignment] inside a parent [Box]).
 */
@Composable
internal fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Icon(
            imageVector        = Icons.Default.SearchOff,
            contentDescription = null,
            modifier           = Modifier.size(64.dp),
            tint               = MaterialTheme.colorScheme.secondary,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text  = "No results found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text  = "Try a different search term",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Error state ───────────────────────────────────────────────────────────────

/**
 * Centred error state with a human-readable [message] and a "Retry" action.
 *
 * - Error text: [MaterialTheme.colorScheme.error] + [MaterialTheme.typography.bodyMedium]
 * - Retry label: [MaterialTheme.typography.labelMedium]
 *
 * @param message  Human-readable description of the failure.
 * @param onRetry  Called when the user taps "Retry".
 * @param modifier Optional modifier (typically [Alignment] inside a parent [Box]).
 */
@Composable
internal fun SearchErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text  = "Error: $message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        TextButton(onClick = onRetry) {
            Text(
                text  = "Retry",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

// ── Preview data ──────────────────────────────────────────────────────────────

/** Sample items used exclusively in Compose preview functions — never in production code. */
private val previewItems = listOf(
    MyListItem(1,  "Organic Green Tea"),
    MyListItem(2,  "Wireless Noise-Cancelling Headphones"),
    MyListItem(3,  "Yoga Mat Premium"),
    MyListItem(4,  "Stainless Steel Water Bottle"),
    MyListItem(5,  "Bamboo Cutting Board"),
)

// ── ListScreenContent previews ────────────────────────────────────────────────

/** Light-mode preview: [ListScreenContent] in [ListUiState.Idle] with 5 catalogue items. */
@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "",
            uiState       = ListUiState.Idle,
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Dark-mode preview: [ListScreenContent] in [ListUiState.Idle] with 5 catalogue items. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenPreviewDark() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "",
            uiState       = ListUiState.Idle,
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Light-mode preview: [ListScreenContent] in the [ListUiState.Loading] shimmer state. */
@Preview(showBackground = true)
@Composable
private fun ListScreenLoadingPreview() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "green tea",
            uiState       = ListUiState.Loading,
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Dark-mode preview: [ListScreenContent] in the [ListUiState.Loading] shimmer state. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenLoadingPreviewDark() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "green tea",
            uiState       = ListUiState.Loading,
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Light-mode preview: [ListScreenContent] in [ListUiState.Success] with empty result list. */
@Preview(showBackground = true)
@Composable
private fun ListScreenEmptyStatePreview() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "quantum physics",
            uiState       = ListUiState.Success(emptyList()),
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Dark-mode preview: [ListScreenContent] in [ListUiState.Success] with empty result list. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenEmptyStatePreviewDark() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "quantum physics",
            uiState       = ListUiState.Success(emptyList()),
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Light-mode preview: [ListScreenContent] in the [ListUiState.Error] state. */
@Preview(showBackground = true)
@Composable
private fun ListScreenErrorPreview() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "green tea",
            uiState       = ListUiState.Error("Network unavailable"),
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

/** Dark-mode preview: [ListScreenContent] in the [ListUiState.Error] state. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenErrorPreviewDark() {
    MySampleApplicationTheme {
        ListScreenContent(
            query         = "green tea",
            uiState       = ListUiState.Error("Network unavailable"),
            allItems      = previewItems,
            onQueryChange = {},
            onSearch      = {},
            onItemClick   = {},
        )
    }
}

// ── AiSearchBar previews ──────────────────────────────────────────────────────

/** Light-mode preview: [AiSearchBar] with active query text showing search elevation. */
@Preview(showBackground = true)
@Composable
private fun AiSearchBarPreview() {
    MySampleApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
        ) {
            AiSearchBar(
                query         = "green tea",
                isLoading     = false,
                onQueryChange = {},
                onSearch      = {},
            )
        }
    }
}

/** Dark-mode preview: [AiSearchBar] with active query text showing search elevation. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AiSearchBarPreviewDark() {
    MySampleApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
        ) {
            AiSearchBar(
                query         = "green tea",
                isLoading     = false,
                onQueryChange = {},
                onSearch      = {},
            )
        }
    }
}

// ── ItemCard previews ─────────────────────────────────────────────────────────

/**
 * [PreviewParameterProvider] supplying three representative [MyListItem] values
 * for [ItemCard] previews.
 */
class MyListItemPreviewProvider : PreviewParameterProvider<MyListItem> {
    override val values = sequenceOf(
        MyListItem(1, "Organic Green Tea"),
        MyListItem(2, "Wireless Noise-Cancelling Headphones"),
        MyListItem(3, "Yoga Mat Premium"),
    )
}

/** Light-mode preview: [ItemCard] — elevated, left accent border, number badge. */
@Preview(showBackground = true)
@Composable
private fun ItemCardPreview(
    @PreviewParameter(MyListItemPreviewProvider::class) item: MyListItem,
) {
    MySampleApplicationTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ItemCard(item = item, onItemClick = {})
        }
    }
}

/** Dark-mode preview: [ItemCard] — elevated, left accent border, number badge. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ItemCardPreviewDark(
    @PreviewParameter(MyListItemPreviewProvider::class) item: MyListItem,
) {
    MySampleApplicationTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ItemCard(item = item, onItemClick = {})
        }
    }
}

// ── EmptyState previews ───────────────────────────────────────────────────────

/** Light-mode preview: [EmptyState] with SearchOff icon, headline, and subtitle. */
@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    MySampleApplicationTheme {
        Box(
            modifier        = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState()
        }
    }
}

/** Dark-mode preview: [EmptyState] with SearchOff icon, headline, and subtitle. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EmptyStatePreviewDark() {
    MySampleApplicationTheme {
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState()
        }
    }
}

// ── ShimmerItemList previews ──────────────────────────────────────────────────

/** Light-mode preview: [ShimmerItemList] — 5 pulsing skeleton cards. */
@Preview(showBackground = true)
@Composable
private fun ShimmerItemListPreview() {
    MySampleApplicationTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ShimmerItemList()
        }
    }
}

/** Dark-mode preview: [ShimmerItemList] — 5 pulsing skeleton cards. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ShimmerItemListPreviewDark() {
    MySampleApplicationTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ShimmerItemList()
        }
    }
}

// ── SearchErrorState previews ─────────────────────────────────────────────────

/** Light-mode preview: [SearchErrorState]. */
@Preview(showBackground = true)
@Composable
private fun SearchErrorStatePreview() {
    MySampleApplicationTheme {
        SearchErrorState(message = "Network unavailable", onRetry = {})
    }
}

/** Dark-mode preview: [SearchErrorState]. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SearchErrorStatePreviewDark() {
    MySampleApplicationTheme {
        SearchErrorState(message = "Network unavailable", onRetry = {})
    }
}
