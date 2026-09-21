# Nursera

**Nursera** is an Android app for nurses in Spain who split their work across multiple hospitals and want a clearer view of their monthly income.

The app lets users define hospitals, configure shift rates, register worked shifts, and track estimated gross and net earnings across the month.

## Status

**Work in progress.** Nursera is actively being built and is not feature-complete yet.

This repository is also part of my Android portfolio, so the codebase is intentionally structured to show architecture, modularization, and product thinking as the app evolves.

## Roadmap

- Complete the core Android experience with hospital, schedule, and profile flows
- Improve persistence and income-target tracking
- Add more tests around feature behavior and state handling
- Migrate the shared business layers toward **Kotlin Multiplatform (KMP)** once the Android foundation is stable

## Tech stack

- Kotlin
- Jetpack Compose
- Clean Architecture
- MVI
- Hilt
- Coroutines and Flow
- Navigation Compose

## Architecture

The project follows a modular **Clean Architecture + MVI** setup:

- `app/` as the Android entry point
- `core/` for shared utilities and UI components
- `feature/*` modules split by domain and layer (`data`, `domain`, `presentation`)

The current architectural conventions and development rules live in [`CLAUDE.md`](./CLAUDE.md).

## Why this project exists

Many nurses in Spain work in more than one hospital at the same time, which makes it hard to predict how much they will earn each month. Nursera aims to make that easier by turning worked shifts into a clear income projection.

## Repository notes

- The project is currently Android-first
- Some product decisions, copy, and sample data still reflect the app's Spanish target audience
- The long-term direction is to evolve shared logic so it can be reused through **KMP**
