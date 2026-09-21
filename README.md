# 👩‍⚕️ Nursera

**Nursera** is an Android app for nurses in Spain who work across multiple hospitals and need a clearer view of their monthly income. The product helps users define hospitals, configure shift rates, log worked shifts, and understand how those shifts translate into projected gross and net earnings.

This repository is intentionally being shaped as a **portfolio-grade Android project**: product-driven, modular, and built with architecture that scales.

## ✨ Why this project exists

Many nurses combine shifts across different hospitals, each with its own rates, tax settings, and schedules. That makes it difficult to answer a simple but important question:

**"How much money will I actually make this month?"**

Nursera turns that uncertainty into something visible:

- 🏥 Manage multiple hospitals
- 💶 Configure pay rates and IRPF per hospital
- 🗓️ Register shifts from the calendar
- 📈 Track monthly projected earnings
- 🎯 Move toward a future target-income experience

## 🚧 Current status

Nursera is **work in progress**, but it already shows the direction of the product and the engineering approach behind it.

Today, the codebase demonstrates:

- ✅ Modular Android architecture
- ✅ Clean Architecture + MVI
- ✅ Jetpack Compose UI
- ✅ Type-safe Navigation Compose
- ✅ Hilt dependency injection
- ✅ Coroutines and Flow
- ✅ Unit tests around presentation logic
- ✅ English and Spanish user-facing strings

## 🧱 Architecture

The project follows a **modular Clean Architecture + MVI** setup.

### Modules

```text
app/                    Android entry point
core/common/            Shared utilities
core/ui/                Shared Compose design system
feature/hospital/       Hospital management
feature/schedule/       Calendar and shift planning
feature/profile/        Monthly overview and income breakdown
```

### Feature layering

Each feature is split into:

- `data/` → repository implementations and data sources
- `domain/` → business models, contracts, and use cases
- `presentation/` → screens, state, intents, transforms, and UI models

### UI pattern

The presentation layer follows **MVI**:

- `State` for persistent screen state
- `Intent` for user actions
- `SideEffect` for one-off events
- `Transform` for controlled state mutation

The project conventions are documented in [`CLAUDE.md`](./CLAUDE.md).

## 🛠️ Tech stack

| Area | Stack |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVI |
| DI | Hilt |
| Async | Coroutines + Flow |
| Navigation | Navigation Compose with type-safe routes |
| Testing | JUnit + Turbine |
| Build | Gradle Kotlin DSL |

## 📱 Product areas

### 🏥 Hospital
- Create and edit hospitals
- Configure visual identity and IRPF
- Define shift types and pay rates

### 🗓️ Schedule
- Browse the month in calendar mode
- Inspect a focused week view
- Add shifts directly from a calendar day

### 📊 Profile
- Review monthly totals
- See gross vs net earnings
- Inspect earnings breakdown by hospital

## 🧪 Engineering goals

This repository is meant to showcase more than screens. It is also designed to communicate engineering discipline:

- Atomic features split by module and layer
- Explicit presentation state
- Reusable design-system components
- Testable business and presentation logic
- Clear separation between domain and UI models

## 🎯 Portfolio focus

Nursera is the kind of project I want to use to demonstrate:

- Android product thinking
- Scalable architecture decisions
- Strong UI state modeling
- Maintainable Kotlin code
- Real-world feature decomposition

## 🗺️ Roadmap

Planned improvements for the next iterations:

1. Add screenshots and short demo media
2. Finish a polished end-to-end vertical slice for the core user journey
3. Expand test coverage for ViewModels and feature flows
4. Add persistence for production-ready data handling
5. Implement the monthly income target notification flow
6. Prepare shared business logic for a future Kotlin Multiplatform evolution

## ▶️ Running the project

### Requirements

- Android Studio
- JDK 17
- Android SDK configured locally

### Run

```bash
./gradlew assembleDebug
```

Then run the `app` module from Android Studio or install the generated APK on an emulator/device.

## 🧹 Repository standards

To keep this repository portfolio-ready, the target standard is:

- Clear commit history
- Small, meaningful changes
- Consistent naming
- Architecture-first decisions
- Documentation that explains both the product and the code

## 📌 Notes

- The target audience and product context are currently centered on **nurses in Spain**
- Some domain terminology intentionally reflects that context, such as **IRPF**
- The project is **Android-first** today, with room to evolve shared logic further over time
