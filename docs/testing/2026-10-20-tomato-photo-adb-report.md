# TomatoCare: multi-photo ADB verification

**Date:** 2026-10-20

**Functional outcome:** all 22 distinct images completed the real picker-to-result flow without an observed app crash.

**Model outcome:** limitations found; do not describe this as an accuracy certification.

## Environment and method

- Visible `Medium_Phone_API_35` emulator, serial `emulator-5554`, Android 15 / API 35, x86_64, 16 KB memory pages.
- Installed debug app: `com.example.smartpest`, activity `.view.MainActivity`.
- Controlled the app using ADB taps, swipes, UIAutomator hierarchy dumps, and screenshots. Predictions came from the installed app's existing bundled model, not mocked responses or host-side classification.
- Selected each file through **Scan → Choose a photo / Choose another photo → Android DocumentsUI**. The emulator's native photo picker is unavailable, so the app used its image-document fallback.
- Verified that the on-screen image preview matched the selected source image, by comparing the fitted preview against all 22 fixtures. This guards against reading a stale result for the previous image.
- Recorded the actual displayed outcome and, when shown, the rounded **model confidence**. Confidence is not measured accuracy.
- Checked process continuity and the Android crash buffer from the start of the run. All 22 recorded results had app PID `8708`; the crash buffer was empty for the test window.
- Also opened and cancelled the browser after a photo had already been analyzed: the previous photo remained selected. Finished by scanning a healthy tomato sample again; the app was left on Scan showing **Healthy-looking leaf — 94% model confidence**.
- No model, confidence threshold, app code, app data reset, or reinstall was used to improve these test outcomes.

## Samples and provenance

Used two deterministically sampled images from each of the ten inherited tomato categories, plus a healthy apple leaf and a late-blight potato leaf as non-tomato controls. All images were 256 × 256 pixels. Sampling seed: `20261020`.

Source: [spMohanty/PlantVillage-Dataset](https://github.com/spMohanty/PlantVillage-Dataset/tree/7f7ecc7e1eaca78107e3affe7cb5abd9427e139a/raw/color), pinned revision:

```text
7f7ecc7e1eaca78107e3affe7cb5abd9427e139a
```

The [machine-readable results CSV](2026-10-20-tomato-photo-results.csv) includes each source URL, dataset label, file SHA-256, and displayed result.

Bundled model `app/src/main/ml/1.tflite` SHA-256, unchanged during testing:

```text
ef97f1c425b68c37ec676a5555a4d2da3c36895be86e166e4ab558c65169c04f
```

Photos remain on the emulator in:

```text
Downloads/TomatoCare-Tests-20261020
```

If Recent is empty in the image browser, navigate to **Downloads → TomatoCare-Tests-20261020**.

## Summary

| Check | Observed outcome |
| --- | --- |
| Picker → correct preview → completed screening result | 22 / 22 images |
| Tomato result agrees with dataset label | 16 / 20 tomato images |
| Tomato result is uncertain, without a diagnosis | 3 / 20 tomato images |
| Accepted tomato result disagrees with dataset label | 1 / 20 tomato images |
| Non-tomato control declined | 2 / 2 controls |
| App crash observed during this run | None |
| Cancel browser without losing previous photo | Passed |

A functional pass means the selection, preview, and screening-result flow completed. It does **not** mean the predicted disease was correct.

## Individual observations

Confidence below is the rounded value shown by the app. A dash means the state did not show a diagnostic confidence value.

| File | Dataset expectation | Displayed result | Confidence | Interpretation |
| --- | --- | --- | --- | --- |
| `t01_bacterial_spot_1.jpg` | Bacterial spot | Bacterial spot | 99% | Label agreement |
| `t02_bacterial_spot_2.jpg` | Bacterial spot | Bacterial spot | 99% | Label agreement |
| `t03_early_blight_1.jpg` | Early blight | No reliable match yet | — | Uncertain |
| `t04_early_blight_2.jpg` | Early blight | Early blight | 90% | Label agreement |
| `t05_late_blight_1.jpg` | Late blight | Late blight | 94% | Label agreement |
| `t06_late_blight_2.jpg` | Late blight | Late blight | 100% | Label agreement |
| `t07_leaf_mold_1.jpg` | Leaf mold | Leaf mold | 99% | Label agreement |
| `t08_leaf_mold_2.jpg` | Leaf mold | Leaf mold | 100% | Label agreement |
| `t09_septoria_1.jpg` | Septoria leaf spot | Septoria leaf spot | 98% | Label agreement |
| `t10_septoria_2.jpg` | Septoria leaf spot | Tomato mosaic virus | 80% | **Incorrect accepted result** |
| `t11_mites_1.jpg` | Two-spotted spider mite damage | Two-spotted spider mite damage | 89% | Label agreement |
| `t12_mites_2.jpg` | Two-spotted spider mite damage | Two-spotted spider mite damage | 98% | Label agreement |
| `t13_target_spot_1.jpg` | Target spot | No reliable match yet | — | Uncertain |
| `t14_target_spot_2.jpg` | Target spot | No reliable match yet | — | Uncertain |
| `t15_yellow_leaf_curl_1.jpg` | Tomato yellow leaf curl virus | Tomato yellow leaf curl virus | 100% | Label agreement |
| `t16_yellow_leaf_curl_2.jpg` | Tomato yellow leaf curl virus | Tomato yellow leaf curl virus | 100% | Label agreement |
| `t17_mosaic_1.jpg` | Tomato mosaic virus | Tomato mosaic virus | 100% | Label agreement |
| `t18_mosaic_2.jpg` | Tomato mosaic virus | Tomato mosaic virus | 97% | Label agreement |
| `t19_healthy_1.jpg` | Healthy-looking leaf | Healthy-looking leaf | 99% | Label agreement |
| `t20_healthy_2.jpg` | Healthy-looking leaf | Healthy-looking leaf | 94% | Label agreement |
| `t21_control_apple_1.jpg` | Non-tomato control | Tomato leaf not recognized | — | Correctly declined this control |
| `t22_control_potato_1.jpg` | Non-tomato control | Tomato leaf not recognized | — | Correctly declined this control |

## Important findings and limitations

1. **A confident result can still be wrong.** `t10_septoria_2.jpg` produced Tomato mosaic virus at 80% confidence despite its Septoria dataset label. Repeating the scan produced the same label. The symptoms and care shown were consistent with the model's predicted category, but that does not make the prediction correct.
2. **Some supported tomato conditions remain inconclusive.** One early-blight photo and both target-spot photos produced the uncertain state. They were not counted as correct diagnoses.
3. **The two non-tomato controls were declined, but this does not establish reliable species detection or rejection of arbitrary images.**
4. **This is a small functional spot check, not independent model validation.** These public PlantVillage images may overlap the model's training data. The model's training provenance, label-order metadata, and preprocessing provenance are not bundled. The inherited label mapping and preprocessing were retained, not independently verified by this exercise.
5. The set does not establish performance on independent field photos, different lighting/backgrounds, several leaves in one frame, other stresses, or diseases outside the model's categories. Independent labeled field data and per-class evaluation are needed before making accuracy claims or tuning thresholds.
6. The QA automation initially over-scrolled one long result and checked one external-activity transition too early. Slow scrolling and bounded transition waits corrected the harness. Those observations were repeated; no app fix was needed. The first ten images were also repeated to improve screenshot framing, with all ten displayed labels unchanged. Original and retry evidence remains in the local artifacts.

## Local evidence

Generated artifacts live under `build/tomato-photo-validation/` and may be removed by build cleanup:

- [Input manifest](../../build/tomato-photo-validation/manifest.json): pinned source URLs and file hashes.
- [Recorded results](../../build/tomato-photo-validation/results.json): actual states, confidence text, preview checks, process IDs, and screenshot paths.
- [Crash buffer](../../build/tomato-photo-validation/crash-log.txt): empty during the verification window.
- [Photo contact sheet](../../build/tomato-photo-validation/sample-contact-sheet.jpg).
- [Incorrect Septoria result screenshot](../../build/tomato-photo-validation/screenshots/t10_septoria_2.jpg-result.png).
- [Early-blight uncertain screenshot](../../build/tomato-photo-validation/screenshots/t03_early_blight_1.jpg-result.png).
- [Healthy tomato screenshot](../../build/tomato-photo-validation/screenshots/t20_healthy_2.jpg-result.png).
- [Potato control screenshot](../../build/tomato-photo-validation/screenshots/t22_control_potato_1.jpg-result.png).
- `screenshots/` and `ui/`: selected-photo screenshots, result screenshots, and UI hierarchy captures for all 22 samples.
- `results-first-completed-pass.json`, `results-before-scroll-retry.json`, `attempts.jsonl`: retained first-pass/retry evidence.
- `final-smoke-check.json`: cancellation preservation and the final healthy-leaf recheck.
- `prepare.py`, `run_adb.py`: local fixture-download and ADB automation scripts. Their elapsed times include file-browser navigation and UIAutomator overhead; they are not inference-time benchmarks.
