# Tomato Focus Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a coherent tomato-only app using the existing model with honest, tested prediction handling.

**Architecture:** A pure Kotlin catalog and prediction decoder sit between the unchanged 38-class model and Compose UI. A scan ViewModel owns asynchronous decoding/inference; shared Material 3 components and theme unify the existing app.

**Tech Stack:** Existing Kotlin / Jetpack Compose / TensorFlow Lite model binding / Android Activity Result APIs / Firebase / Room / JUnit.

**Spec:** `docs/superpowers/specs/2026-10-19-tomato-focus-design.md`

## Global Constraints
- Bundled `1.tflite`, application ID, Room schemas and user data stay unchanged.
- Compare all 38 original probabilities; tomato indices are 28–37; confidence cutoff is 0.70, a heuristic, not measured accuracy.
- Preserve RGB / 255 preprocessing; no unsupported accuracy claims or forced diagnoses.
- Preserve unrelated/staged user work; leave implementation uncommitted for review.
- No new UI framework or runtime dependency.

## Review Focus
- Non-leaf/other-crop input: no claim that the closed-set classifier reliably validates tomato identity.
- Very large/corrupt/camera-cancelled images: bounded decoding, no stale diagnosis or silent failure.
- Rapid image changes or screen recreation: latest image wins; loading/inference do not block the UI.
- Small screens, large fonts and dark mode: scrollable content, readable theme-aware text and adequate controls.
- Missing assistant API key or network errors: safe error state; no failed messages retained as model history.

### Task 1: Tomato catalog and prediction contract
**Files:** create `models/TomatoDisease.kt`, `models/TomatoClassifier.kt`, and corresponding JVM tests under `app/src/test/java/com/example/smartpest/`.
**Interfaces:** `TomatoDisease.entries`, `TomatoDisease.search(query)`, `TomatoClassifier.classify(scores: FloatArray): TomatoPrediction`; detected results contain a `TomatoMatch(disease, confidence)`.
- [ ] Write mapping, global winner, original score, cutoff, invalid distribution, healthy/pest distinction and searchable-care tests.
- [ ] Run `./gradlew :app:testDebugUnitTest` and observe missing implementation failures.
- [ ] Implement the catalog and decoder; retain all original scores and validate output shape/range/finiteness/sum.
- [ ] Re-run JVM tests; expect all passing.

### Task 2: Safe image inference and scan UI
**Files:** replace `models/TensorFlowHelper.kt` and `view/PestDiseaseAI.kt`; create `models/ScanImageLoader.kt`, `viewmodels/ScanViewModel.kt`, `view/TomatoScanContent.kt`, `res/xml/scan_paths.xml`; modify manifest; add Android tests.
**Interfaces:** `TensorFlowHelper.classifyTomato(context, image): TomatoPrediction` (suspend), `ScanUiState`, `ScanViewModel.selectImage(uri)`, `TomatoScanContent(state, onPickImage, onTakePhoto, onRetry, onGuide)`.
- [ ] Add Android preprocessing, actual-model smoke and scan-state tests before implementation.
- [ ] Run test compilation to expose missing implementation.
- [ ] Implement bounded decode/software bitmaps, original normalization, model close in finally, serialized off-main inference, cancellation/stale-result protection and URI restoration.
- [ ] Implement camera/picker and accessible scan empty/loading/error/result states with safe guidance and confidence explanation.
- [ ] Run JVM tests and Android test compilation; device tests if available.

### Task 3: Consistent tomato-focused experience
**Files:** update theme files, MainActivity, HomePage, FarmGuide, auth/onboarding/profile, ChatPage/ChatViewModel, strings and README; create shared UI components and navigation constants; add library Android tests and navigation JVM tests.
**Interfaces:** shared theme and `AppRoutes`; existing public screen signatures remain compatible.
- [ ] Add search/library UI and navigation contract regression tests first.
- [ ] Apply the shared light/dark theme, tomato branding and scan-first navigation/home; preserve supporting tools.
- [ ] Replace generic guide with searchable shared catalog and conservative linked care guidance.
- [ ] Correct route case and back stack behavior, scrolling/auth errors and tomato assistant/loading behavior.
- [ ] Run `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:compileDebugAndroidTestKotlin :app:lintDebug`.

### Task 4: Review and verification
- [ ] Review the diff separately against the spec, especially model output handling, image lifecycle and unsupported claims.
- [ ] Resolve important findings with regression tests and re-run the full verification command.
- [ ] Record test results, device limitations and remaining accuracy constraints in README and final response.

## Execution record
- Baseline: `:app:testDebugUnitTest` passed (2026-10-19), before product changes.
- Workspace: feature branch `feat/tomato-disease-focus` in the supplied checkout. Existing user modifications/staged artifacts are preserved. No worktree created without consent, no commits planned.
- Executor: inline; no subagent tool is available. Final review will be explicitly identified as self-review.
- Shared interfaces: Tasks 1→2/3 consume the same catalog and `TomatoPrediction`; Task 2→3 consumes `AppRoutes` only at screen navigation boundaries. No conflict found.
