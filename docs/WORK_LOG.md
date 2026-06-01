# Work Log

이 문서는 프로젝트 작업 기록을 남기는 공간입니다. 각 작업자는 자기 task의 변경 요약과 검증 결과를 간단히 기록합니다.

## 기록 형식

```markdown
## YYYY-MM-DD / T-XXX-task-id / owner

- Branch:
- Status:
- 변경 파일:
- 작업 요약:
- 테스트/빌드:
- 수동 확인:
- 남은 이슈:
- PR:
```

## 초기 기록

### 2026-05-27 / T-010-agents-and-docs-setup / Codex

- Branch: 없음
- Status: 문서 bootstrap 작성
- 변경 파일: `AGENTS.md`, `docs/*.md`, `docs/tasks/*.md`, `.github/pull_request_template.md`
- 작업 요약: 새 레포 기준의 협업 문서와 task plan 작성
- 테스트/빌드: 문서 작업만 수행
- 수동 확인: 문서 파일 생성 여부 확인 예정
- 남은 이슈: 실제 Android 코드 감사는 `T-000-current-code-audit`에서 진행
- PR: 아직 없음

### 2026-05-27 / T-000-current-code-audit / Codex

- Branch: 없음
- Status: 코드 감사 완료, 앱 코드 수정 없음
- 변경 파일: `docs/ARCHITECTURE.md`, `docs/WORK_LOG.md`, `docs/tasks/T-000-current-code-audit.md`
- 작업 요약: 로컬 작업공간이 Git repository가 아니며 Android 앱 코드가 아직 없음을 확인했습니다. 참고 GitHub 초안의 README, Gradle, Manifest, Room, Repository, Share Target, Quick Tile, MediaStore, OCR/OG/Gemini, Main UI, Handoff 관련 파일을 읽고 유지/수정/폐기 대상을 `docs/ARCHITECTURE.md`에 정리했습니다.
- 테스트/빌드: 현재 로컬에 Android 앱 코드가 없어 빌드 대상 없음
- 수동 확인: `rg --files`, `Get-ChildItem -Force`, 참고 GitHub file tree, 주요 파일 fetch로 구조 확인
- 남은 이슈: `T-020-architecture-baseline`에서 새 로컬 Android 프로젝트 baseline을 생성하고 Gradle/패키지/Hilt/Room/Compose 기준을 잡아야 함
- PR: 아직 없음

### 2026-05-27 / T-020-architecture-baseline / Codex

- Branch: 없음
- Status: Android baseline 생성 완료
- 변경 파일: `.gitignore`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `local.properties`, `gradle/wrapper/*`, `gradlew`, `gradlew.bat`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/proguard-rules.pro`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/smartclipboard/ai/**`, `app/src/main/res/values/**`, `app/src/test/**`, `app/src/androidTest/**`, `docs/ARCHITECTURE.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-020-architecture-baseline.md`, `docs/tasks/T-030-data-model-audit.md`
- 작업 요약: 새 로컬 Android 앱 baseline을 만들고 `com.smartclipboard.ai` package, Compose, Hilt, Room, Coroutines/Flow, BuildConfig 기반 Gemini key 주입 기준을 구성했습니다. Manifest는 launcher Activity만 포함한 최소 상태로 두었고 권한/Share/Tile/Samsung queries는 `T-050`에서 순차 처리하도록 남겼습니다.
- 테스트/빌드: `.\gradlew.bat assembleDebug` 성공, `.\gradlew.bat test` 성공
- 수동 확인: Gradle wrapper 생성, AndroidX 버전 호환성 조정, build/test 로그 확인
- 남은 이슈: 다음 작업은 `T-030-data-model-audit`입니다. DataItem/Topic/TopicAnalysis/TopicAction, Room Entity/DAO/Repository 계약을 먼저 확정해야 합니다.
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-27 / T-030-data-model-audit / Codex

- Branch: 없음
- Status: 모델/Room/Repository 계약 정리 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/domain/model/**`, `app/src/main/java/com/smartclipboard/ai/domain/repository/DataRepository.kt`, `app/src/main/java/com/smartclipboard/ai/data/source/local/**`, `app/src/main/java/com/smartclipboard/ai/data/repository/DataRepositoryImpl.kt`, `app/src/test/java/com/smartclipboard/ai/**`, `docs/ARCHITECTURE.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-030-data-model-audit.md`, `docs/tasks/T-040-navigation-baseline.md`, `docs/tasks/T-050-permission-and-manifest-baseline.md`, `docs/tasks/README.md`
- 작업 요약: `DataItem`, `Topic`, `TopicAnalysis`, `TopicAction` domain model과 Room Entity/DAO/Database, `DataRepository` 계약 및 기본 구현을 추가했습니다. Entity는 enum 값을 문자열로 저장하고 mapper에서 domain 모델로 복원합니다.
- 테스트/빌드: TDD RED로 새 테스트의 컴파일 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 모델 관계와 task dependency 문서 정리. 다음 Ready task를 `T-050-permission-and-manifest-baseline` 하나로 제한
- 남은 이슈: `SmartClipboardDatabase`는 version `1`, `exportSchema = false`로 시작합니다. schema export/migration 운영은 Git/CI 기준이 생긴 뒤 별도 승인 필요
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-28 / T-050-permission-and-manifest-baseline / Codex

- Branch: 없음
- Status: 권한/Manifest/component baseline 정리 완료
- 변경 파일: `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/smartclipboard/ai/collection/permissions/MediaPermissionPolicy.kt`, `app/src/main/java/com/smartclipboard/ai/collection/clipboard/ClipboardCaptureTileService.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/share/ShareReceiverActivity.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/clipboard/ClipboardCaptureActivity.kt`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values/themes.xml`, `app/src/main/res/drawable/ic_qs_smartclipboard.xml`, `app/src/test/java/com/smartclipboard/ai/collection/permissions/MediaPermissionPolicyTest.kt`, `docs/DATA_COLLECTION_STRATEGY.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-040-navigation-baseline.md`, `docs/tasks/T-050-permission-and-manifest-baseline.md`, `docs/tasks/T-100-share-target-flow.md`, `docs/tasks/T-110-quick-tile-flow.md`, `docs/tasks/T-120-media-store-batch-query.md`, `docs/tasks/README.md`
- 작업 요약: Share Target, Quick Settings Tile, clipboard focus용 투명 Activity, Samsung app queries, 이미지 권한 baseline을 Manifest에 추가했습니다. 수집 저장 로직은 구현하지 않고 component skeleton만 추가했습니다.
- 테스트/빌드: TDD RED로 `MediaPermissionPolicy` 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 정책상 위험한 접근성/화면감시/전체 package query/calendar DB 권한을 추가하지 않았습니다.
- 남은 이슈: 다음 작업은 `T-040-navigation-baseline`입니다. 수집 기능 구현 task는 Navigation baseline 이후 Ready 전환합니다.
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-28 / T-040-navigation-baseline / Codex

- Branch: 없음
- Status: Navigation baseline 정리 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/main/MainActivity.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/navigation/**`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/**`, `app/src/test/java/com/smartclipboard/ai/presentation/navigation/NavigationContractTest.kt`, `docs/ARCHITECTURE.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-040-navigation-baseline.md`, `docs/tasks/T-100-share-target-flow.md`, `docs/tasks/T-110-quick-tile-flow.md`, `docs/tasks/T-120-media-store-batch-query.md`, `docs/tasks/T-130-storage-access-framework-picker.md`, `docs/tasks/README.md`
- 작업 요약: `AppRoute`, `TopLevelDestination`, `SmartClipboardRoot`를 추가하고 Home/Inbox/Logs/Settings shell을 MainActivity에 연결했습니다. Topic create/data selection/analysis route 문자열도 후속 task 기준으로 고정했습니다.
- 테스트/빌드: TDD RED로 navigation 계약 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: Gradle, Manifest, Repository, Data model은 수정하지 않았습니다. 화면은 placeholder shell 수준으로만 유지했습니다.
- 남은 이슈: 다음 작업은 `T-100-share-target-flow`입니다. Share Target 저장 결과 패턴이 잡힌 뒤 Quick Tile/MediaStore/SAF 흐름을 순차 전환합니다.
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-30 / T-100-share-target-flow / Codex

- Branch: 없음
- Status: Share Target 저장 흐름 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/collection/share/**`, `app/src/main/java/com/smartclipboard/ai/presentation/share/ShareReceiverActivity.kt`, `app/src/main/java/com/smartclipboard/ai/di/**`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/smartclipboard/ai/collection/share/ShareContentHandlerTest.kt`, `docs/ARCHITECTURE.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-100-share-target-flow.md`, `docs/tasks/T-110-quick-tile-flow.md`, `docs/tasks/T-120-media-store-batch-query.md`, `docs/tasks/T-130-storage-access-framework-picker.md`, `docs/tasks/README.md`, `docs/WORK_LOG.md`
- 작업 요약: Android Share Sheet에서 들어온 텍스트, URL, 이미지 URI, 파일 URI를 `DataItem`으로 저장하는 흐름을 추가했습니다. `SharePayload`/`SharedUri`/`ShareSaveResult`로 공유 입력과 저장 결과를 분리하고, 중복 payload는 부분 성공으로 처리합니다. 실제 앱 저장을 위해 Hilt Database/Repository module을 최소 추가했습니다.
- 테스트/빌드: TDD RED로 `ShareContentHandler` 부재 컴파일 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 실제 기기 Share Sheet 수동 테스트는 아직 하지 않았습니다. 현재 검증은 fake repository 단위 테스트와 debug/release unit test, debug APK 빌드 기준입니다.
- 남은 이슈: 다음 작업은 `T-110-quick-tile-flow`입니다. Tile 클릭 시 투명 Activity로 최신 Primary Clip 1개를 저장하는 흐름을 같은 저장 결과 패턴으로 연결해야 합니다.
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-30 / T-110-quick-tile-flow / Codex

- Branch: 없음
- Status: Quick Settings Tile + TransparentActivity 클립보드 수집 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/collection/clipboard/**`, `app/src/main/java/com/smartclipboard/ai/presentation/clipboard/ClipboardCaptureActivity.kt`, `app/src/main/res/values/strings.xml`, `app/src/test/java/com/smartclipboard/ai/collection/clipboard/ClipboardCaptureHandlerTest.kt`, `docs/ARCHITECTURE.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-110-quick-tile-flow.md`, `docs/tasks/T-120-media-store-batch-query.md`, `docs/tasks/README.md`, `docs/WORK_LOG.md`
- 작업 요약: Quick Settings Tile 클릭 시 투명 `ClipboardCaptureActivity`를 열고, window focus를 얻은 뒤 Primary Clip 1개만 읽어 텍스트/링크를 `DataItemSource.CLIPBOARD_TILE`로 저장하도록 구현했습니다. 빈 클립보드, 미지원 clip, 저장 실패를 별도 결과로 처리하고 Toast 문구를 추가했습니다.
- 테스트/빌드: TDD RED로 `ClipboardCaptureHandler` 부재 실패와 미지원 clip 처리 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 실제 기기 Quick Settings Tile 추가/클릭 수동 테스트는 아직 하지 않았습니다. 현재 검증은 fake repository 단위 테스트와 debug/release unit test, debug APK 빌드 기준입니다.
- 남은 이슈: 다음 작업은 `T-120-media-store-batch-query`입니다. 앱 실행 시 Last Sync Time 이후 새 이미지를 MediaStore에서 batch query해 저장해야 합니다.
- PR: 아직 없음. 사용자 승인 전 commit/push/PR 생성 금지

### 2026-05-30 / T-120-media-store-batch-query / Codex

- Branch: `feat/T-120-media-store-batch-query`
- Status: 앱 실행 시 Last Sync Time 기준 이미지 자동 수집 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/collection/media/**`, `app/src/main/java/com/smartclipboard/ai/presentation/main/MainActivity.kt`, `app/src/test/java/com/smartclipboard/ai/collection/media/**`, `docs/ARCHITECTURE.md`, `docs/DATA_COLLECTION_STRATEGY.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-120-media-store-batch-query.md`, `docs/tasks/T-130-storage-access-framework-picker.md`, `docs/tasks/T-140-enrichment-ocr-og-pipeline.md`, `docs/tasks/T-160-storage-quota-cleanup.md`, `docs/tasks/T-220-save-feedback-bottom-sheet.md`, `docs/tasks/README.md`, `docs/WORK_LOG.md`
- 작업 요약: MediaStore에서 마지막 sync 이후 추가된 이미지를 조회하고 `DataItemSource.MEDIASTORE`로 저장하는 흐름을 추가했습니다. 스크린샷/다운로드 이미지 분류, batch/DB 중복 방지, 저장 실패 count, checkpoint 안전 갱신을 구현했습니다. 앱 실행 시 권한 확인 후 sync를 시작하는 hook을 `MainActivity`에 추가했습니다.
- 테스트/빌드: TDD RED로 MediaStore 수집 클래스 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 실제 기기에서 권한 허용 후 스크린샷/갤러리 이미지 추가 시 자동 수집되는지는 아직 확인하지 않았습니다. 현재 검증은 fake datasource/repository 단위 테스트와 debug/release unit test, debug APK 빌드 기준입니다.
- 남은 이슈: 다음 작업은 `T-130-storage-access-framework-picker`입니다. 이후 실제 기기에서 MediaStore 권한/부분 접근/삼성 갤러리 동작을 QA해야 합니다.
- PR: 아직 없음. remote가 연결되지 않아 push/PR은 만들지 않았습니다.

### 2026-05-30 / T-130-storage-access-framework-picker / Codex

- Branch: `feat/T-130-storage-access-framework-picker`
- Status: SAF 파일 직접 선택 저장 흐름 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/collection/filepicker/**`, `app/src/main/java/com/smartclipboard/ai/presentation/main/MainActivity.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/navigation/SmartClipboardRoot.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `app/src/test/java/com/smartclipboard/ai/collection/filepicker/SafImportHandlerTest.kt`, `docs/ARCHITECTURE.md`, `docs/DATA_COLLECTION_STRATEGY.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/T-130-storage-access-framework-picker.md`, `docs/tasks/T-140-enrichment-ocr-og-pipeline.md`, `docs/tasks/T-160-storage-quota-cleanup.md`, `docs/tasks/T-210-data-list-filter-selection.md`, `docs/tasks/T-220-save-feedback-bottom-sheet.md`, `docs/tasks/README.md`, `docs/WORK_LOG.md`
- 작업 요약: Inbox shell에 `파일 추가` 진입점을 만들고, SAF picker 결과를 `SafPickedFileReader`로 metadata화한 뒤 `SafImportHandler`가 `DataItemSource.SAF`로 저장하게 했습니다. 이미지 MIME type은 `IMAGE`, 그 외 파일은 `FILE`로 분류하고, 기존 URI와 batch 내 중복 URI는 건너뜁니다.
- 테스트/빌드: TDD RED로 SAF import 클래스 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공
- 수동 확인: 실제 기기에서 system picker로 이미지/PDF 선택 후 DB 저장되는 수동 테스트는 아직 하지 않았습니다.
- 남은 이슈: 다음 작업은 `T-140-enrichment-ocr-og-pipeline`입니다. SAF/Share/MediaStore로 들어온 링크/이미지에 OCR/OG 전처리 상태를 연결해야 합니다.
- PR: 아직 없음. remote가 연결되지 않아 push/PR은 만들지 않았습니다.

### 2026-05-30 / T-140-enrichment-ocr-og-pipeline / Codex

- Branch: `feat/T-140-enrichment-ocr-og-pipeline`
- Status: OCR/OG 전처리 파이프라인 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/processing/**`, `app/src/main/java/com/smartclipboard/ai/collection/**`, `app/src/main/java/com/smartclipboard/ai/data/source/local/dao/DataItemDao.kt`, `app/src/main/java/com/smartclipboard/ai/di/ProcessingModule.kt`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, 관련 테스트, 관련 문서
- 작업 요약: pending DataItem을 대상으로 링크 OG와 이미지 OCR을 수행하는 `DataItemEnrichmentManager`를 추가했습니다. Jsoup OG 추출은 `Dispatchers.IO`, OCR은 ML Kit Korean Text Recognition으로 처리합니다. Share/Clipboard/MediaStore/SAF 저장 성공 후 `DataItemEnrichmentTrigger`가 최대 3개 항목을 2초 timeout 안에서 시도하고, 실패는 최대 3회 retry 후 `FAILED`로 남깁니다. timeout/cancellation은 retry 실패로 세지 않습니다.
- 테스트/빌드: TDD RED로 전처리 클래스 부재 실패, 저장 직후 trigger 부재 실패, cancellation 처리 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공.
- 수동 확인: 실제 기기에서 ML Kit OCR, 실제 URL OG, Share Sheet/Tile transparent flow 수동 확인은 아직 하지 않았습니다.
- 남은 이슈: 다음 작업은 `T-150-gemini-topic-recommendation`입니다. Gemini API key는 `local.properties -> BuildConfig.GEMINI_API_KEY` 경로를 유지해야 합니다.
- PR: 브랜치 push 후 PR 작성 예정

### 2026-05-31 / T-150-gemini-topic-recommendation / Codex

- Branch: `feat/T-150-gemini-topic-recommendation`
- Status: Gemini 기반 이번 실행 추천 세션 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/processing/gemini/recommendation/**`, `app/src/main/java/com/smartclipboard/ai/di/ProcessingModule.kt`, 관련 테스트, 관련 문서
- 작업 요약: Gemini API key를 `BuildConfig.GEMINI_API_KEY`로 주입하고, `gemini-2.5-flash:generateContent` REST 호출 구조를 추가했습니다. 추천 후보는 `InMemoryRecommendationSessionStore`의 현재 세션으로만 보관하며, 새로 refresh하면 이전 추천을 교체합니다. key가 없거나 네트워크 실패가 있어도 예외를 UI로 던지지 않고 `SKIPPED`/`FAILED` 세션으로 처리합니다.
- 테스트/빌드: TDD RED로 추천 parser/generator/manager/client parser 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공.
- 수동 확인: 현재 `local.properties`의 `gemini.api.key`가 비어 있어 실제 Gemini 네트워크 smoke test는 수행하지 않았습니다. key 없음 fallback은 단위 테스트로 확인했습니다.
- 남은 이슈: 다음 작업은 `T-160-storage-quota-cleanup`입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-160-storage-quota-cleanup / Codex

- Branch: `feat/T-160-storage-quota-cleanup`
- Status: 저장 용량 계산과 안전한 자동 삭제 정책 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/storage/**`, `app/src/main/java/com/smartclipboard/ai/di/StorageModule.kt`, `DataItemDao`, `TopicDao`, 관련 테스트, 관련 문서
- 작업 요약: active DataItem 사용량과 quota 초과량을 계산하고, 중요 표시/보존/Topic 연결 항목을 제외한 cleanup 후보를 선택하는 정책을 추가했습니다. 자동 정리는 MediaStore 원본을 삭제하지 않고 앱 DB의 DataItem만 soft-delete합니다. 내부 복사본이 있는 항목을 먼저 정리 후보로 보고, 이후 오래된 미연결 DataItem을 선택합니다.
- 테스트/빌드: TDD RED로 storage 정책/manager/store 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공.
- 수동 확인: 실제 Settings UI가 아직 없어 화면 수동 확인은 하지 않았습니다.
- 남은 이슈: 다음 작업은 `T-170-repository-integration`입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-170-repository-integration / Codex

- Branch: `chore/T-170-repository-integration`
- Status: ViewModel용 Repository facade 통합 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/domain/repository/**`, `app/src/main/java/com/smartclipboard/ai/data/repository/DataRepositoryImpl.kt`, `RecommendationDataSource`, 관련 테스트, 관련 문서
- 작업 요약: `HomeRepositoryState`, `InboxFilter`와 Home/Inbox/추천/저장 사용량/cleanup API를 `DataRepository`에 추가했습니다. `DataRepositoryImpl`은 추천 세션과 저장 cleanup manager를 facade로 노출합니다. 추천 data source가 `DataRepository`를 다시 주입받던 구조를 `DataItemDao` 기반으로 바꿔 순환 의존성을 피했습니다.
- 테스트/빌드: TDD RED로 repository facade API 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest` 성공, `.\gradlew.bat assembleDebug test` 성공.
- 수동 확인: 화면 UI가 아직 없어 수동 UX 확인은 하지 않았습니다.
- 남은 이슈: 다음 작업은 사용자-facing 첫 화면인 `T-200-home-ux-redesign`을 우선 추천합니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-200-home-ux-redesign / Codex

- Branch: `feat/T-200-home-ux-redesign`
- Status: ChatGPT/Codex 스타일 Home UX 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/home/**`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `app/src/test/java/com/smartclipboard/ai/presentation/home/HomeUiStateMapperTest.kt`, 관련 문서
- 작업 요약: Home 전용 UI 상태/매퍼/ViewModel/Compose 화면을 추가했습니다. 첫 화면은 새 작업 입력, 자동 수집/분석 상태, 이번 실행 AI 추천, 기존 작업, 접이식 최근 자료 요약을 단순한 행/카드 구조로 보여줍니다. 기존 `HomeShellScreen`은 route 변경 없이 새 `HomeRoute`로 연결했습니다.
- 테스트/빌드: TDD RED로 `HomeUiStateMapper` 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.home.HomeUiStateMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 에뮬레이터/실기기 화면 확인은 아직 하지 않았습니다. Compose preview와 실제 화면 육안 확인은 Android Studio에서 추가 QA가 필요합니다.
- 남은 이슈: Home 입력을 실제 Topic 생성으로 연결하는 작업은 `T-300-topic-create-flow` 범위입니다. 다음 작업은 자료 모음/필터 UX인 `T-210-data-list-filter-selection`을 우선 추천합니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-210-data-list-filter-selection / Codex

- Branch: `feat/T-210-data-list-filter-selection`
- Status: Inbox 자료 모음/필터/선택 UX 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/inbox/**`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `app/src/test/java/com/smartclipboard/ai/presentation/inbox/InboxUiStateMapperTest.kt`, 관련 문서
- 작업 요약: Inbox 전용 UI 상태/매퍼/ViewModel/Compose 화면을 추가했습니다. 카테고리는 최근, 이미지, 링크, 텍스트, 파일, 중요, 미분석으로 고정했고, 세부 자료는 리스트/그리드로 전환할 수 있습니다. 중요 표시는 기존 `saveDataItem` 경로로 토글하고, 삭제는 `deletedAtMillis`를 기록하는 soft-delete로 처리합니다.
- 테스트/빌드: TDD RED로 `InboxUiStateMapper` 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.inbox.InboxUiStateMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 에뮬레이터/실기기 화면 확인은 아직 하지 않았습니다. 실제 삭제/중요 표시 UX는 Android Studio 또는 기기에서 추가 확인이 필요합니다.
- 남은 이슈: Topic 추가 버튼은 진입점만 있으며 실제 Topic 연결은 `T-300`, `T-310` 범위입니다. 다음 작업은 용량/권한 UX인 `T-240-settings-ux-storage-permission`을 우선 추천합니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-240-settings-ux-storage-permission / Codex

- Branch: `feat/T-240-settings-ux-storage-permission`
- Status: Settings 수집 기간/용량/권한 UX 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/settings/**`, `app/src/main/java/com/smartclipboard/ai/presentation/main/MainActivity.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/navigation/SmartClipboardRoot.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `app/src/test/java/com/smartclipboard/ai/presentation/settings/SettingsUiStateMapperTest.kt`, 관련 문서
- 작업 요약: Settings 전용 UI 상태/SharedPreferences store/ViewModel/Compose 화면을 추가했습니다. 수집 기간 preset/custom은 설정 저장 후 MediaStore checkpoint를 조정해 다음 수집 범위에 반영합니다. 저장 용량은 `DataRepository.getStorageUsage()`로 표시하고, 정리 버튼은 `cleanupStorage()`를 호출합니다. 권한 버튼은 MainActivity의 이미지 권한 launcher와 연결했습니다.
- 테스트/빌드: TDD RED로 Settings 상태/정책 클래스 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.settings.SettingsUiStateMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 에뮬레이터/실기기 화면 확인은 아직 하지 않았습니다. 실제 권한 dialog와 SharedPreferences 재시작 유지 여부는 Android Studio 또는 기기에서 추가 확인이 필요합니다.
- 남은 이슈: 자동 저장 직후 quota cleanup trigger는 아직 수집 파이프라인에 직접 연결하지 않았습니다. 현재는 Settings의 정리 버튼과 T-160 cleanup manager로 수행합니다. 다음 작업은 `T-220-save-feedback-bottom-sheet`입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-220-save-feedback-bottom-sheet / Codex

- Branch: `feat/T-220-save-feedback-bottom-sheet`
- Status: 저장 피드백 문구와 Toast hook 정리 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/feedback/**`, `ShareReceiverActivity`, `ClipboardCaptureActivity`, `strings.xml`, `app/src/test/java/com/smartclipboard/ai/presentation/feedback/SaveFeedbackMessageMapperTest.kt`, 관련 문서
- 작업 요약: Share/Clipboard 저장 결과를 `SaveFeedbackMessageMapper`로 모아 성공/부분 실패/빈 클립보드/저장 실패 문구를 일관화했습니다. Activity는 저장 로직을 유지하고 feedback hook만 공통 Toast로 교체했습니다.
- 테스트/빌드: TDD RED로 feedback mapper 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.feedback.SaveFeedbackMessageMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 실제 Share Sheet/Quick Tile Toast 표시 수동 테스트는 아직 하지 않았습니다.
- 남은 이슈: 작은 BottomSheet 피드백은 아직 구현하지 않았고, 이번 task는 Toast hook 기준으로 닫았습니다. 다음 작업은 `T-230-logs-tab-flow`입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-05-31 / T-230-logs-tab-flow / Codex

- Branch: `feat/T-230-logs-tab-flow`
- Status: Topic 기반 Logs 탭 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/logs/**`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `app/src/test/java/com/smartclipboard/ai/presentation/logs/LogsUiStateMapperTest.kt`, 관련 문서
- 작업 요약: 별도 Log Entity가 없는 현재 구조에서는 `Topic`을 사용자-visible 작업 기록으로 사용합니다. Logs 탭은 전체, 사용자 요청, AI 추천, 진행 중, 완료, 미완료 필터와 badge를 제공합니다. 내부 OCR/OG/Gemini 자동 이벤트는 기록에 표시하지 않습니다.
- 테스트/빌드: TDD RED로 Logs mapper 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.logs.LogsUiStateMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 실제 화면 필터링 수동 테스트는 아직 하지 않았습니다.
- 남은 이슈: TopicAction 기반 외부 앱 전송 기록은 `T-410` 이후 확장 범위입니다. 다음 Ready 작업은 `T-300-topic-create-flow`입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-06-01 / T-300-topic-create-flow / Codex

- Branch: `feat/T-300-topic-create-flow`
- Status: 사용자 입력/AI 추천 기반 Topic 생성 흐름 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/topic/**`, `app/src/main/java/com/smartclipboard/ai/presentation/home/**`, `app/src/test/java/com/smartclipboard/ai/presentation/topic/TopicCreateUseCaseTest.kt`, 관련 문서
- 작업 요약: `TopicCreateUseCase`를 추가해 Home 입력은 `USER_REQUEST`, AI 추천 수락은 `AI_RECOMMENDATION` Topic으로 저장합니다. 추천 수락 시 연결 자료 id는 `TopicItemSelectedBy.AI`로 저장합니다. HomeViewModel은 입력 제출과 추천 카드 클릭을 use case에 연결하고, 후속 화면 이동에 사용할 `topicCreatedEvents`를 노출합니다.
- 테스트/빌드: TDD RED로 Topic create use case 부재 실패를 확인한 뒤 구현했습니다. `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.topic.TopicCreateUseCaseTest --tests com.smartclipboard.ai.presentation.home.HomeUiStateMapperTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 실제 Home 입력과 추천 카드 클릭 수동 테스트는 아직 하지 않았습니다.
- 남은 이슈: Topic 생성 후 자료 선택 화면으로 실제 이동하는 UI는 `T-310-topic-data-selection-flow`에서 연결합니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-06-01 / T-310-topic-data-selection-flow / Codex

- Branch: `feat/T-310-topic-data-selection-flow`
- Status: Topic 자료 선택 요약/수정/저장 흐름 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/topic/selection/**`, `app/src/main/java/com/smartclipboard/ai/presentation/home/HomeScreen.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/navigation/SmartClipboardRoot.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/screens/ShellScreens.kt`, `DataRepository`, `DataRepositoryImpl`, `TopicDao`, 관련 테스트, 관련 문서
- 작업 요약: Topic에 연결된 자료를 `사용된 자료 N개`로 요약하고, 자료 선택 화면에서 체크박스로 추가/제거 후 저장할 수 있게 했습니다. Home에서 새 Topic 생성 또는 기존 Topic 클릭 시 자료 선택 화면으로 이동합니다. 선택 저장은 기존 Topic-DataItem cross-ref를 교체하고 사용자 수정 선택은 `TopicItemSelectedBy.USER`로 기록합니다.
- 테스트/빌드: TDD RED로 selection use case와 UI state mapper 부재 실패를 확인한 뒤 구현했습니다. `TopicDataSelectionUseCaseTest`, `TopicDataSelectionUiStateMapperTest`, `DataRepositoryImplIntegrationTest` 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 실제 기기에서 Home 입력/추천 수락 후 자료 선택 화면 이동과 저장 후 닫힘은 추가 QA가 필요합니다.
- 남은 이슈: TopicAnalysis 생성과 Gemini 분석 입력 구성은 `T-400-topic-analysis-draft` 범위입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-06-01 / T-400-topic-analysis-draft / Codex

- Branch: `feat/T-400-topic-analysis-draft`
- Status: TopicAnalysis 생성 및 분석 화면 흐름 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/processing/gemini/analysis/**`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/**`, `ProcessingModule`, `SmartClipboardRoot`, `ShellScreens`, `TopicDataSelectionScreen`, 관련 테스트, 관련 문서
- 작업 요약: 선택된 Topic/DataItem을 Gemini 분석 입력으로 구성하고 `TopicAnalysis`를 `RUNNING/DONE/FAILED` 상태로 저장합니다. Gemini 추천과 같은 API key/client를 재사용하되 분석 전용 prompt/parser/generator로 분리했습니다. 자료 선택 저장 후 분석 화면으로 이동하고, 화면 진입 시 분석을 자동 시작하며 실패 시 `다시 분석`을 제공합니다.
- 테스트/빌드: TDD RED로 `TopicAnalysisUseCase`, Gemini parser/generator, UI state mapper 부재 실패를 확인한 뒤 구현했습니다. 관련 단위 테스트 성공. PR 전 전체 검증으로 `.\gradlew.bat assembleDebug test`를 실행합니다.
- 수동 확인: 실제 Gemini key 네트워크 호출과 기기 화면 이동은 추가 QA가 필요합니다. 단위 테스트는 fake Gemini client로 검증했습니다.
- 남은 이슈: Notes/Calendar/Reminder 카드 초안과 수정/완료 UX는 `T-410-topic-action-draft` 범위입니다.
- PR: 브랜치 push 후 PR URL 제공 예정

### 2026-06-01 / T-410-topic-action-draft / Codex

- Branch: `feat/T-410-topic-action-draft`
- Status: TopicAction 초안 생성 및 검토 UX 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/presentation/analysis/action/**`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/TopicAnalysisScreen.kt`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/TopicAnalysisUiState.kt`, `app/src/test/java/com/smartclipboard/ai/presentation/analysis/action/**`, 관련 문서
- 작업 요약: DONE 상태의 TopicAnalysis를 기준으로 삼성 노트/캘린더/리마인더용 `TopicAction` 초안을 생성하고, 분석 화면에서 수정/완료/즉시 완료/뒤로가기 완료 처리 UX를 제공했습니다. 완료된 카드는 세로 높이를 줄이고 내용 영역을 숨겨 사용자가 완료 상태를 눈으로 확인할 수 있게 했습니다.
- 테스트/빌드: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 수동 확인: 실제 기기에서 분석 완료 후 초안 카드, 수정 dialog, 완료 접힘 애니메이션, 뒤로가기 dialog는 추가 QA가 필요합니다.
- 남은 이슈: 실제 Samsung Notes/Calendar/Reminder 인텐트 전송은 `T-500`, `T-510`, `T-520` 범위입니다.
- PR: `https://github.com/swisdom784/SmartClipboardAI/compare/feat/T-400-topic-analysis-draft...feat/T-410-topic-action-draft?expand=1`

### 2026-06-01 / T-510-notes-share-draft / Codex

- Branch: `feat/T-510-notes-share-draft`
- Status: Samsung Notes share 전송 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/export/notes/**`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/**`, `app/src/test/java/com/smartclipboard/ai/export/notes/**`, `app/src/test/java/com/smartclipboard/ai/presentation/analysis/action/**`, 관련 문서
- 작업 요약: NOTE 카드에 `노트로 보내기` 액션을 추가하고, `ACTION_SEND` + `text/plain` + `Intent.EXTRA_TEXT` + Samsung Notes package 지정으로 검토한 노트 초안을 전송합니다. Samsung Notes 실행이 시작되면 해당 action을 `EXPORTED`로 표시하고 완료 카드처럼 접습니다.
- 테스트/빌드: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 수동 확인: 실제 Samsung Notes 설치 기기에서 공유 수신 화면과 미설치 fallback Toast는 추가 QA가 필요합니다.
- 남은 이슈: Calendar/Reminder export는 `T-500`, `T-520` 범위입니다.
- PR: `https://github.com/swisdom784/SmartClipboardAI/compare/feat/T-410-topic-action-draft...feat/T-510-notes-share-draft?expand=1`

### 2026-06-01 / T-500-calendar-intent-draft / Codex

- Branch: `feat/T-500-calendar-intent-draft`
- Status: Samsung Calendar insert intent 전송 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/export/calendar/**`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/**`, `app/src/test/java/com/smartclipboard/ai/export/calendar/**`, `app/src/test/java/com/smartclipboard/ai/presentation/analysis/action/**`, 관련 문서
- 작업 요약: Calendar 카드에 `캘린더로 보내기` 액션을 추가하고, `ACTION_INSERT` + Calendar Events URI + title/description/location/begin/end/all-day extra로 검토한 일정 초안을 전송합니다. Samsung Calendar package를 우선 사용하고 없으면 기본 Calendar insert intent로 fallback합니다. 시간이 없는 payload는 임의 시간을 넣지 않고 안내만 합니다.
- 테스트/빌드: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 수동 확인: 실제 Samsung Calendar 설치 기기와 기본 Calendar fallback 환경에서 insert 화면은 추가 QA가 필요합니다.
- 남은 이슈: Reminder export는 `T-520` 범위입니다.
- PR: `https://github.com/swisdom784/SmartClipboardAI/compare/feat/T-510-notes-share-draft...feat/T-500-calendar-intent-draft?expand=1`

### 2026-06-01 / T-520-reminder-intent-draft / Codex

- Branch: `feat/T-520-reminder-intent-draft`
- Status: Samsung Reminder share 전송 구현 완료
- 변경 파일: `app/src/main/java/com/smartclipboard/ai/export/reminder/**`, `app/src/main/java/com/smartclipboard/ai/presentation/analysis/**`, `app/src/test/java/com/smartclipboard/ai/export/reminder/**`, `app/src/test/java/com/smartclipboard/ai/presentation/analysis/action/**`, 관련 문서
- 작업 요약: Reminder 카드에 `리마인더로 보내기` 액션을 추가하고, `ACTION_SEND` + `text/plain` + `Intent.EXTRA_TEXT` + Samsung Reminder package 지정으로 검토한 할 일 초안을 전송합니다. Samsung Reminder 실행이 시작되면 해당 action을 `EXPORTED`로 표시하고 완료 카드처럼 접습니다.
- 테스트/빌드: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 수동 확인: 실제 Samsung Reminder 설치 기기에서 공유 수신 화면과 미설치 fallback Toast는 추가 QA가 필요합니다.
- 남은 이슈: Phase 7 export 구현은 완료됐고, 다음 작업은 `T-900` 전체 QA입니다.
- PR: `https://github.com/swisdom784/SmartClipboardAI/compare/feat/T-500-calendar-intent-draft...feat/T-520-reminder-intent-draft?expand=1`
