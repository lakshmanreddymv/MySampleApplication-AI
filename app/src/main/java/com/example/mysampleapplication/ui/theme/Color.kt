package com.example.mysampleapplication.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Full Material 3 colour palette for MySampleApplication.
 *
 * Organised into semantic families:
 * - **Primary**: deep blue-indigo (#1A237E family) — brand identity
 * - **Secondary**: vibrant teal (#00897B family) — accent actions
 * - **Neutrals**: warm grey scale — text, backgrounds, surfaces
 * - **Error**: accessible red — error states
 * - **Surface variants**: blue-grey — card/chip backgrounds
 *
 * No colours are used directly in composables; reference them only via
 * [androidx.compose.material3.MaterialTheme.colorScheme] tokens.
 */

// ── Primary: Deep Blue-Indigo family ─────────────────────────────────────────

/** Darkest blue-indigo; used for [onPrimaryContainer] in light theme and [onPrimary] in dark. */
val Blue900 = Color(0xFF0D1B6E)

/** Brand primary for the light theme — the deep indigo anchor colour. */
val Blue800 = Color(0xFF1A237E)

/** Mid blue-indigo; used for [tertiary] in light theme. */
val Blue700 = Color(0xFF283593)

/** Light blue-indigo for the dark-theme primary role. */
val Blue200 = Color(0xFF9FA8DA)

/** Pale blue-indigo for [primaryContainer] in the light theme. */
val Blue100 = Color(0xFFC5CAE9)

// ── Secondary: Teal accent family ────────────────────────────────────────────

/** Dark teal; [onSecondaryContainer] in light theme and [onSecondary] / [secondaryContainer] in dark. */
val Teal700 = Color(0xFF00796B)

/** Brand secondary for the light theme — vibrant teal accent. */
val Teal600 = Color(0xFF00897B)

/** Light teal for the dark-theme secondary role. */
val Teal200 = Color(0xFF80CBC4)

/** Pale teal for [secondaryContainer] in the light theme. */
val Teal100 = Color(0xFFB2DFDB)

// ── Neutrals ──────────────────────────────────────────────────────────────────

/** Near-black; primary text and icons on light surfaces. */
val Grey900 = Color(0xFF212121)

/** Dark grey; secondary text, outlines, and [onSurfaceVariant] in light theme. */
val Grey800 = Color(0xFF424242)

/** Very light grey; text and icons on dark surfaces. */
val Grey100 = Color(0xFFF5F5F5)

/** Off-white; [background] and ambient light-theme surface. */
val Grey50 = Color(0xFFFAFAFA)

// ── Error palette ─────────────────────────────────────────────────────────────

/** Error colour for the light theme — accessible on white. */
val Red700 = Color(0xFFB00020)

/** Error colour for the dark theme — accessible on dark surfaces. */
val Red200 = Color(0xFFCF6679)

// ── Surface variants ──────────────────────────────────────────────────────────

/** Tinted surface for cards and chips in the light theme ([surfaceVariant]). */
val BlueGrey50 = Color(0xFFECEFF1)

/** Tinted surface for cards and chips in the dark theme ([surfaceVariant]). */
val BlueGrey800 = Color(0xFF37474F)

// ── Fixed ─────────────────────────────────────────────────────────────────────

/** Pure white; [onPrimary] / [onSecondary] in the light theme. */
val White = Color(0xFFFFFFFF)

/** Pure black; [onError] in the dark theme. */
val Black = Color(0xFF000000)
