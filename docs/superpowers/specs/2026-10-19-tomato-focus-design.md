# Tomato-focused disease detection

## Intent and agreed scope
The user wants the existing Android plant-disease app to focus exclusively on tomatoes, with a substantially improved UI and careful diagnosis handling. The user explicitly chose the bundled model, not a replacement or retraining. The chat proposal covers tomato scanning, confidence and symptoms, practical next steps, uncertain/non-tomato handling, tomato guidance and assistant instructions.

## Constraints
- Keep Kotlin, Jetpack Compose, Firebase, Room, the application ID and the existing `app/src/main/ml/1.tflite` unchanged.
- Preserve unrelated local/staged changes and existing user data. Do not commit, publish or overwrite release APKs.
- The bundled model has a float32 `[1,224,224,3]` input and float32 `[1,38]` softmax output. It contains no label/preprocessing metadata. Retain the existing RGB / 255 preprocessing and inherited class order; do not claim these were independently validated against training artifacts.
- Tomato labels occupy indices 28–37. Keep the complete 38-output comparison. Never renormalize tomato scores or force an unsupported/non-tomato top prediction into a tomato disease.
- Preserve the previous 0.70 confidence cutoff as a heuristic, not a calibrated clinical/agronomic threshold. Weak predictions, invalid output and non-tomato winners must not display a disease diagnosis or disease-specific treatment.
- Confidence is a model score, not measured accuracy. A high-confidence photo of an unsupported object can still be wrong; a closed-set model cannot reliably validate tomato identity.

## Implementation decisions (assistant defaults)
- Consolidate the duplicate themes into a Material 3 design: warm neutral surfaces, leaf-green primary, tomato-red accents used sparingly, clear typography, comfortable touch targets and dark-mode equivalents.
- Make the home screen scan-first, introduce a direct Scan navigation tab, and replace general farming advice with a searchable tomato condition library. Preserve weather, alerts, profile and authentication access as supporting features.
- Scan from the system photo picker or a full-resolution external camera using a narrowly scoped FileProvider. Decode bounded software bitmaps, run inference off the main thread, clear old results immediately and provide distinct empty/loading/error/result states. Keep the selected URI across recreation and re-run work safely.
- Store condition-specific symptoms and conservative care guidance in one shared catalog, including the existing spider-mite output accurately described as pest damage, not an infectious disease. Healthy means no supported symptoms detected in the photo, not a guarantee of plant health.
- Keep the optional assistant tomato-focused through a system instruction; make unavailable-service and loading states honest. The assistant is not the image classifier and must not fabricate diagnoses, pesticide dosages or accuracy claims.
- Fix navigation route case mismatch and duplicate destinations so users can actually reach Home after authentication. Make onboarding, login and profile use the shared theme and responsive scrolling.

## Validation and non-goals
Unit-test all tomato mappings, non-tomato rejection without score renormalization, cutoff behavior, malformed output, and catalog/search behavior. Add Android tests for real bitmap preprocessing/model smoke inference and scan/library UI states. Run JVM tests, compile Android tests, assemble debug and Android lint. Run device tests/screenshots if an emulator/device is available; explicitly report if not.

No new trained model, promised field accuracy, synthetic evaluation metrics, fabricated scan history, automatic disease alerts, backend rewrite or unsupported disease classes. Real accuracy evaluation requires an independently labelled held-out field dataset.
