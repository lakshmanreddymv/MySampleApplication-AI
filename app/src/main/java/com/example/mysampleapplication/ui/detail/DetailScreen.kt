package com.example.mysampleapplication.ui.detail

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.ui.theme.MySampleApplicationTheme

// ── Stateful entry point ──────────────────────────────────────────────────────

/**
 * Root composable for the item detail screen.
 *
 * Observes [DetailViewModel.uiState] and delegates all rendering to the stateless
 * [DetailScreenContent] composable so that every sub-composable is independently
 * previewable without a real [DetailViewModel].
 *
 * @param vm     The [DetailViewModel] driving this screen.
 * @param onBack Callback invoked when the user taps the back navigation button.
 */
@Composable
fun DetailScreen(vm: DetailViewModel, onBack: () -> Unit) {
    val uiState by vm.uiState.collectAsState()

    DetailScreenContent(
        uiState  = uiState,
        onBack   = onBack,
        onRetry  = { (uiState as? DetailUiState.Error)?.item?.let { vm.loadDetail(it.id) } },
    )
}

// ── Stateless content composable ──────────────────────────────────────────────

/**
 * Stateless rendering composable for the detail screen.
 *
 * Handles all three [DetailUiState] variants exhaustively:
 * - [DetailUiState.Loading] → [DetailLoadingContent] centred on screen
 * - [DetailUiState.Success] → [DetailSuccessContent] with the AI description
 * - [DetailUiState.Error]   → [DetailErrorContent] with retry action
 *
 * The TopAppBar title is resolved from [uiState] so the item name appears
 * immediately on navigation, before the AI description loads.
 *
 * @param uiState  Current screen state.
 * @param onBack   Called when the user taps the back arrow.
 * @param onRetry  Called when the user taps "Retry" in the error state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailScreenContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val title = when (val state = uiState) {
        is DetailUiState.Success -> state.item.text
        is DetailUiState.Error   -> state.item?.text ?: "Detail"
        is DetailUiState.Loading -> state.item?.text ?: "Loading…"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor          = MaterialTheme.colorScheme.primary,
                    titleContentColor       = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier        = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> DetailLoadingContent()
                is DetailUiState.Success -> DetailSuccessContent(description = state.description)
                is DetailUiState.Error   -> DetailErrorContent(
                    message = state.message,
                    onRetry = onRetry,
                )
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

/**
 * Full-screen loading overlay shown while the AI summary is being generated.
 *
 * Uses [MaterialTheme.colorScheme.surface] at 85 % opacity so any content
 * already on screen is subtly visible beneath the spinner.
 *
 * @param modifier Optional modifier applied to the backing [Surface].
 */
@Composable
internal fun DetailLoadingContent(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text  = "Generating AI summary…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Renders the AI-generated [description] for the selected item.
 *
 * Typography token: [MaterialTheme.typography.bodyLarge].
 * Colour token: [MaterialTheme.colorScheme.onSurface].
 *
 * @param description AI-generated text to display.
 * @param modifier    Optional modifier.
 */
@Composable
internal fun DetailSuccessContent(
    description: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text     = description,
        style    = MaterialTheme.typography.bodyLarge,
        color    = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

/**
 * Centred error state with a [message] and a "Retry" action button.
 *
 * Error text colour: [MaterialTheme.colorScheme.error].
 * Retry label typography: [MaterialTheme.typography.labelMedium].
 *
 * @param message  Human-readable description of the failure.
 * @param onRetry  Called when the user taps "Retry".
 * @param modifier Optional modifier.
 */
@Composable
internal fun DetailErrorContent(
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

/** Sample item used exclusively in preview functions — never in production code. */
private val previewItem = MyListItem(id = 1, text = "Organic Green Tea")

private const val PREVIEW_DESCRIPTION =
    "Organic green tea is a traditional beverage rich in antioxidants, celebrated for its " +
        "delicate vegetal flavour and numerous wellness benefits. Our premium selection is sourced " +
        "from high-altitude tea gardens in Darjeeling, where cooler temperatures slow leaf growth " +
        "and concentrate flavour. Brew at 70–80 °C for 2–3 minutes for best results."

// ── DetailScreenContent previews ──────────────────────────────────────────────

/** Light-mode preview of [DetailScreenContent] in the [DetailUiState.Success] state. */
@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Success(item = previewItem, description = PREVIEW_DESCRIPTION),
            onBack  = {},
            onRetry = {},
        )
    }
}

/** Dark-mode preview of [DetailScreenContent] in the [DetailUiState.Success] state. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailScreenPreviewDark() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Success(item = previewItem, description = PREVIEW_DESCRIPTION),
            onBack  = {},
            onRetry = {},
        )
    }
}

/** Light-mode preview of [DetailScreenContent] in the [DetailUiState.Loading] state. */
@Preview(showBackground = true)
@Composable
private fun DetailScreenLoadingPreview() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Loading(item = previewItem),
            onBack  = {},
            onRetry = {},
        )
    }
}

/** Dark-mode preview of [DetailScreenContent] in the [DetailUiState.Loading] state. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailScreenLoadingPreviewDark() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Loading(item = previewItem),
            onBack  = {},
            onRetry = {},
        )
    }
}

/** Light-mode preview of [DetailScreenContent] in the [DetailUiState.Error] state. */
@Preview(showBackground = true)
@Composable
private fun DetailScreenErrorPreview() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Error(item = previewItem, message = "Network unavailable"),
            onBack  = {},
            onRetry = {},
        )
    }
}

/** Dark-mode preview of [DetailScreenContent] in the [DetailUiState.Error] state. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailScreenErrorPreviewDark() {
    MySampleApplicationTheme {
        DetailScreenContent(
            uiState = DetailUiState.Error(item = previewItem, message = "Network unavailable"),
            onBack  = {},
            onRetry = {},
        )
    }
}

// ── DetailLoadingContent previews ─────────────────────────────────────────────

/** Light-mode preview of [DetailLoadingContent]. */
@Preview(showBackground = true)
@Composable
private fun DetailLoadingContentPreview() {
    MySampleApplicationTheme {
        DetailLoadingContent()
    }
}

/** Dark-mode preview of [DetailLoadingContent]. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailLoadingContentPreviewDark() {
    MySampleApplicationTheme {
        DetailLoadingContent()
    }
}

// ── DetailSuccessContent previews ─────────────────────────────────────────────

/** Light-mode preview of [DetailSuccessContent]. */
@Preview(showBackground = true)
@Composable
private fun DetailSuccessContentPreview() {
    MySampleApplicationTheme {
        DetailSuccessContent(description = PREVIEW_DESCRIPTION)
    }
}

/** Dark-mode preview of [DetailSuccessContent]. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailSuccessContentPreviewDark() {
    MySampleApplicationTheme {
        DetailSuccessContent(description = PREVIEW_DESCRIPTION)
    }
}

// ── DetailErrorContent previews ───────────────────────────────────────────────

/** Light-mode preview of [DetailErrorContent]. */
@Preview(showBackground = true)
@Composable
private fun DetailErrorContentPreview() {
    MySampleApplicationTheme {
        DetailErrorContent(message = "Network unavailable", onRetry = {})
    }
}

/** Dark-mode preview of [DetailErrorContent]. */
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DetailErrorContentPreviewDark() {
    MySampleApplicationTheme {
        DetailErrorContent(message = "Network unavailable", onRetry = {})
    }
}
