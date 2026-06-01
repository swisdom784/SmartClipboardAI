# Gemini and Device QA Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Gemini failures diagnosable, complete Gemini end-to-end QA with a valid key, and stabilize large-library topic data selection before final manual device QA.

**Architecture:** Keep the existing MVVM, Repository, and Compose structure. First add a narrow Gemini diagnostics layer and user-visible status, then verify real Gemini recommendation/analysis, then improve Topic selection UX without changing DB schema.

**Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Coroutines, Gemini REST API, Android ADB device QA.

---

## File Structure

- `app/src/main/java/com/smartclipboard/ai/processing/gemini/**`: Gemini error classification, key diagnostics, client failure mapping.
- `app/src/main/java/com/smartclipboard/ai/presentation/home/**`: Home recommendation failed/skipped state display.
- `app/src/main/java/com/smartclipboard/ai/presentation/settings/**`: Gemini status/diagnostic display.
- `app/src/main/java/com/smartclipboard/ai/presentation/topic/selection/**`: Large-library selection UX, sticky save action, filter/search state.
- `app/src/test/java/com/smartclipboard/ai/processing/gemini/**`: Gemini diagnostics and parser tests.
- `app/src/test/java/com/smartclipboard/ai/presentation/home/**`: Home Gemini state mapper tests.
- `app/src/test/java/com/smartclipboard/ai/presentation/settings/**`: Settings Gemini state mapper tests.
- `app/src/test/java/com/smartclipboard/ai/presentation/topic/selection/**`: Large-library selection UX state tests.
- `docs/QA_REPORT.md`: QA results and remaining manual checks.
- `docs/WORK_LOG.md`: Work log for each task.

---

### Task 1: T-910 Gemini Diagnostics

**Files:**
- Modify: `app/src/main/java/com/smartclipboard/ai/processing/gemini/recommendation/HttpGeminiTextClient.kt`
- Create or modify: `app/src/main/java/com/smartclipboard/ai/processing/gemini/recommendation/GeminiFailure.kt`
- Modify: `app/src/main/java/com/smartclipboard/ai/processing/gemini/recommendation/GeminiTopicRecommendationManager.kt`
- Modify: `app/src/main/java/com/smartclipboard/ai/presentation/home/HomeUiStateMapper.kt`
- Modify: `app/src/main/java/com/smartclipboard/ai/presentation/settings/SettingsScreen.kt`
- Test: `app/src/test/java/com/smartclipboard/ai/processing/gemini/recommendation/GeminiFailureClassifierTest.kt`
- Test: `app/src/test/java/com/smartclipboard/ai/presentation/home/HomeUiStateMapperTest.kt`

- [ ] **Step 1: Write failing tests for error classification**

```kotlin
@Test
fun classifiesInvalidApiKeyErrorBody() {
    val errorBody = """
        {
          "error": {
            "code": 400,
            "message": "API key not valid. Please pass a valid API key.",
            "status": "INVALID_ARGUMENT",
            "details": [
              {
                "reason": "API_KEY_INVALID",
                "domain": "googleapis.com"
              }
            ]
          }
        }
    """.trimIndent()

    val failure = GeminiFailureClassifier.classify(
        httpCode = 400,
        responseBody = errorBody
    )

    assertEquals(GeminiFailure.InvalidApiKey, failure)
}
```

- [ ] **Step 2: Run RED**

Run: `.\gradlew.bat testDebugUnitTest --console=plain`

Expected: FAIL because `GeminiFailureClassifier` or `GeminiFailure.InvalidApiKey` does not exist.

- [ ] **Step 3: Add minimal failure model and classifier**

```kotlin
sealed interface GeminiFailure {
    data object MissingApiKey : GeminiFailure
    data object InvalidApiKey : GeminiFailure
    data object NetworkFailure : GeminiFailure
    data class ApiFailure(val httpCode: Int, val message: String) : GeminiFailure
    data class ParseFailure(val message: String) : GeminiFailure
}

object GeminiFailureClassifier {
    fun classify(httpCode: Int, responseBody: String): GeminiFailure {
        return when {
            responseBody.contains("API_KEY_INVALID", ignoreCase = true) -> GeminiFailure.InvalidApiKey
            responseBody.contains("API key not valid", ignoreCase = true) -> GeminiFailure.InvalidApiKey
            else -> GeminiFailure.ApiFailure(httpCode = httpCode, message = "Gemini 요청에 실패했습니다.")
        }
    }
}
```

- [ ] **Step 4: Run GREEN**

Run: `.\gradlew.bat testDebugUnitTest --console=plain`

Expected: PASS.

- [ ] **Step 5: Surface recommendation failed/skipped state in Home**

Add mapper tests showing `RecommendationSessionStatus.FAILED` produces a quiet status item or status message instead of disappearing.

- [ ] **Step 6: Add Settings Gemini status**

Add state and UI text for:
- key not set
- key invalid
- network unavailable
- last check successful

- [ ] **Step 7: Verify**

Run:
- `.\gradlew.bat testDebugUnitTest --console=plain`
- `.\gradlew.bat assembleDebug --console=plain`

- [ ] **Step 8: Commit**

```powershell
git add app/src/main/java/com/smartclipboard/ai/processing/gemini app/src/main/java/com/smartclipboard/ai/presentation/home app/src/main/java/com/smartclipboard/ai/presentation/settings app/src/test/java/com/smartclipboard/ai docs/QA_REPORT.md docs/WORK_LOG.md
git commit -m "feat: add Gemini diagnostics"
```

---

### Task 2: T-930 Large-Library Topic Selection UX

**Files:**
- Modify: `app/src/main/java/com/smartclipboard/ai/presentation/topic/selection/TopicDataSelectionScreen.kt`
- Modify: `app/src/main/java/com/smartclipboard/ai/presentation/topic/selection/TopicDataSelectionUiState.kt`
- Test: `app/src/test/java/com/smartclipboard/ai/presentation/topic/selection/TopicDataSelectionUiStateMapperTest.kt`

- [ ] **Step 1: Write failing test for display range messaging**

```kotlin
@Test
fun exposesDisplayLimitSummaryForLargeLibraries() {
    val state = TopicDataSelectionUiStateMapper.map(
        topicId = 1L,
        allItems = (1L..250L).map { id ->
            dataItem(id = id, type = DataItemType.IMAGE, capturedAtMillis = id)
        },
        selectedDataItemIds = emptySet()
    )

    assertEquals("최근 자료 200개 표시", state.displayLimitText)
}
```

- [ ] **Step 2: Run RED**

Run: `.\gradlew.bat testDebugUnitTest --console=plain`

Expected: FAIL because `displayLimitText` does not exist.

- [ ] **Step 3: Add UI state for display limit**

Add `displayLimitText: String?` to `TopicDataSelectionUiState` and set it when total item count exceeds the visible cap.

- [ ] **Step 4: Make save action accessible**

Move `선택 저장` into a bottom sticky action area or duplicate it near the top. The button must remain reachable without scrolling through 200 rows.

- [ ] **Step 5: Add filter state**

Support at least these filters in UI state:
- 전체
- 이미지
- 링크
- 텍스트
- 파일

- [ ] **Step 6: Verify on large-library device**

Run:
- `.\gradlew.bat testDebugUnitTest --console=plain`
- `.\gradlew.bat assembleDebug --console=plain`
- Install and open Topic selection on a device with many MediaStore rows.

- [ ] **Step 7: Commit**

```powershell
git add app/src/main/java/com/smartclipboard/ai/presentation/topic/selection app/src/test/java/com/smartclipboard/ai/presentation/topic/selection docs/QA_REPORT.md docs/WORK_LOG.md
git commit -m "feat: stabilize large topic selection"
```

---

### Task 3: T-920 Gemini Recommendation and Analysis E2E

**Files:**
- Modify: `docs/QA_REPORT.md`
- Modify: `docs/WORK_LOG.md`
- Optional test helper only if approved: `app/src/debug/java/com/smartclipboard/ai/qa/**`

- [ ] **Step 1: Verify preconditions**

Run:
- Direct Gemini smoke test with key redacted from output
- `adb devices -l`

Expected:
- direct smoke test returns success
- one device is listed as `device`

- [ ] **Step 2: Build and install**

Run:
- `.\gradlew.bat assembleDebug test --console=plain`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`

- [ ] **Step 3: Verify Home recommendation**

Launch app and wait for Home recommendation. Confirm UI shows a reviewable AI recommendation or Settings/Home shows a clear Gemini status.

- [ ] **Step 4: Verify analysis**

Create or accept a Topic, select at least one data item, save, and confirm analysis reaches DONE.

- [ ] **Step 5: Verify action draft**

Confirm Notes/Calendar/Reminder draft cards appear after analysis DONE.

- [ ] **Step 6: Commit QA docs**

```powershell
git add docs/QA_REPORT.md docs/WORK_LOG.md
git commit -m "test: verify Gemini e2e"
```

---

### Task 4: T-940 Remaining Manual Device QA

**Files:**
- Modify: `docs/QA_REPORT.md`
- Modify: `docs/WORK_LOG.md`
- Optional small bug fix files only when approved.

- [ ] **Step 1: SAF picker QA**

Verify image, PDF, cancel, and duplicate URI behavior.

- [ ] **Step 2: Permission QA**

Verify full allow, deny, and partial photo access states.

- [ ] **Step 3: Quick Settings Tile QA**

Manually copy text and link, then tap the tile. Confirm saved rows in DB and Toast feedback.

- [ ] **Step 4: Samsung app handoff QA**

Verify Notes, Calendar, and Reminder handoff on Galaxy device.

- [ ] **Step 5: Commit QA docs**

```powershell
git add docs/QA_REPORT.md docs/WORK_LOG.md
git commit -m "test: complete manual device qa"
```

---

## Self-Review

- Spec coverage: Covers invalid Gemini key, Gemini E2E, large-library topic selection, and remaining manual device QA.
- Placeholder scan: No task uses TBD/TODO placeholders.
- Type consistency: Task ids match `docs/IMPLEMENTATION_PLAN.md` and `docs/tasks/T-*.md`.

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-06-01-gemini-and-device-qa-hardening.md`.

Recommended execution order:

1. `T-910-gemini-key-diagnostics`
2. `T-930-topic-selection-large-library-ux`
3. `T-920-gemini-recommendation-analysis-e2e`
4. `T-940-device-manual-qa-suite`
