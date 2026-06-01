# SmartClipboard MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: 구현 단계에서는 `superpowers:executing-plans` 또는 `superpowers:subagent-driven-development`를 사용한다. 이 문서는 작업 추적용이며, 체크 상태 변경은 `AGENTS.md`와 각 task 문서의 권한 규칙을 따른다.

**Goal:** Android Studio + Kotlin + Jetpack Compose 기반으로 실제 사용 가능한 SmartClipboard MVP를 완성한다.

**Architecture:** `DataRepository`를 중심으로 Share Target, Quick Settings Tile, MediaStore, SAF 입력을 `DataItem`으로 통합한다. Topic과 AI Agent 흐름은 사용자가 검토 가능한 `TopicAnalysis`와 `TopicAction` 초안으로 이어지며, 최종 실행은 사용자가 Notes/Calendar/Reminder 전송 버튼을 누를 때만 발생한다.

**Tech Stack:** Kotlin, Jetpack Compose, MVVM, Room, Hilt, Coroutines/Flow, AndroidX Activity Result, MediaStore, Storage Access Framework, Jsoup, ML Kit 또는 기존 OCRProcessor, Gemini API.

---

## 문서 사용 규칙

- 이 문서는 전체 task 인덱스입니다. 전체 완료 체크는 프로젝트 오너가 관리합니다.
- 각 작업자는 담당 task 문서(`docs/tasks/T-*.md`)의 체크리스트만 수정합니다.
- `Status: Ready`인 task만 시작할 수 있습니다.
- `Not Ready`, `Blocked`, `In Progress`, `Done` task는 시작하지 않습니다.
- 선행 task가 `Done`이 아닌 경우, 작업을 시작하지 않고 blocker를 보고합니다.
- `main` 브랜치 직접 작업, 승인 없는 commit/push, 범위 밖 리팩토링은 금지합니다.

## 파일 구조 계획

### 문서

- `AGENTS.md`: Codex와 팀원이 지켜야 할 최상위 작업 규칙
- `docs/PROJECT_SPEC.md`: 제품 목적, MVP 범위, 하지 않을 것
- `docs/ARCHITECTURE.md`: MVVM/Repository/model/component 구조
- `docs/DATA_COLLECTION_STRATEGY.md`: Android 제약과 가능한 수집 경로
- `docs/UX_FLOW.md`: 사용자 흐름과 화면 방향
- `docs/BRANCH_RULES.md`: 브랜치/PR 운영 규칙
- `docs/TASK_STATUS_GUIDE.md`: task 상태 변경 기준
- `docs/WORK_LOG.md`: 프로젝트 단위 작업 로그
- `docs/tasks/*.md`: 개별 task 범위와 완료 기준
- `.github/pull_request_template.md`: PR 작성 기준

### 앱 코드 영역

- `app/build.gradle.kts`, `gradle/libs.versions.toml`: 의존성, BuildConfig, Hilt/Room/KSP 설정
- `app/src/main/AndroidManifest.xml`: Share Target, TileService, Activity, 권한, queries 선언
- `app/src/main/java/.../domain/model/`: `DataItem`, `Topic`, `TopicAnalysis`, `TopicAction`
- `app/src/main/java/.../data/source/local/`: Room Entity, DAO, Database
- `app/src/main/java/.../domain/repository/`, `app/src/main/java/.../data/repository/`: `DataRepository` 계약/구현
- `app/src/main/java/.../collection/`: Share/Clipboard/MediaStore/SAF 수집 흐름
- `app/src/main/java/.../processing/`: OCR, OG 추출, Gemini, clustering
- `app/src/main/java/.../presentation/navigation/`: Compose navigation
- `app/src/main/java/.../presentation/home/`: Home UX
- `app/src/main/java/.../presentation/inbox/`: Inbox 자료 모음 UX
- `app/src/main/java/.../presentation/topic/`: Topic 생성/자료 선택 UX
- `app/src/main/java/.../presentation/analysis/`: AI 초안/전송 카드 UX
- `app/src/main/java/.../presentation/logs/`: 작업 로그 UX
- `app/src/main/java/.../presentation/settings/`: 설정/용량/권한 UX
- `app/src/main/java/.../export/`: Samsung Notes/Calendar/Reminder export
- `app/src/test/`, `app/src/androidTest/`: 단위/계측 테스트

## Phase 0: 현재 코드 감사 및 문서 기반 정리

### T-000-current-code-audit

- 작업명: 현재 코드 감사 및 재사용 후보 정리
- Status: Done
- Owner: 미배정
- 목적: 현재 로컬 코드와 참고 GitHub 초안에서 유지/수정/폐기할 구현을 구분합니다.
- 담당 브랜치명: `docs/T-000-current-code-audit`
- 예상 수정 파일: `docs/ARCHITECTURE.md`, `docs/WORK_LOG.md`, `docs/tasks/T-000-current-code-audit.md`
- 선행 task: 없음
- Blocked by: 없음
- Ready criteria: 현재 문서 세트가 존재하고, 코드 구현을 시작하지 않는다는 조건이 확인됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: 공통 모델/DB/Repository/Navigation/Manifest/Gradle 작업 전체
- 충돌 가능성이 있는 파일: `docs/ARCHITECTURE.md`, `docs/WORK_LOG.md`
- 완료 기준: 재사용 후보 클래스, 위험한 구현, UI 폐기 범위, missing 파일 구조가 문서화됨
- 검증 방법: `rg --files`로 프로젝트 구조 확인, 주요 Kotlin/Gradle/Manifest 파일 읽기, 결과를 문서에 반영
- 작업자가 수정해도 되는 파일 범위: 위 예상 수정 파일만
- 수정하면 안 되는 파일 범위: 앱 코드, Gradle, Manifest, README 외 전체
- 관련 task 문서 경로: `docs/tasks/T-000-current-code-audit.md`

### T-010-agents-and-docs-setup

- 작업명: 협업 기준 문서와 task 문서 생성
- Status: Done
- Owner: Codex
- 목적: 병렬 협업을 위한 기준 문서, branch 규칙, task 상태 규칙, PR 템플릿을 만듭니다.
- 담당 브랜치명: `docs/T-010-agents-and-docs-setup`
- 예상 수정 파일: `AGENTS.md`, `docs/*.md`, `docs/tasks/*.md`, `.github/pull_request_template.md`
- 선행 task: 없음
- Blocked by: 없음
- Ready criteria: 사용자 승인 완료
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: 전체 문서 구조를 동시에 바꾸는 작업
- 충돌 가능성이 있는 파일: `AGENTS.md`, `docs/IMPLEMENTATION_PLAN.md`, `docs/tasks/*.md`
- 완료 기준: 필수 문서와 task 문서가 생성되고, 첫 Ready task가 명확함
- 검증 방법: 필수 파일 존재 확인, task status scan, 금지 구현/의존성 규칙 포함 확인
- 작업자가 수정해도 되는 파일 범위: 협업 문서 전체
- 수정하면 안 되는 파일 범위: 앱 코드, Gradle, Manifest
- 관련 task 문서 경로: `docs/tasks/T-010-agents-and-docs-setup.md`

## Phase 1: 공통 기반 정리

### T-020-architecture-baseline

- 작업명: 앱 패키지 구조와 의존성 기준 정리
- Status: Done
- Owner: Codex
- 목적: 실제 코드 구조를 MVP 아키텍처에 맞게 정리할 최소 변경 범위를 확정합니다.
- 담당 브랜치명: `chore/T-020-architecture-baseline`
- 예상 수정 파일: `app/build.gradle.kts`, `gradle/libs.versions.toml`, 패키지 디렉터리 골격, `docs/ARCHITECTURE.md`
- 선행 task: `T-000-current-code-audit`
- Blocked by: 없음
- Ready criteria: 현재 코드 감사 결과와 재사용/폐기 목록이 문서화됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-030`, `T-040`, `T-050`, 모든 앱 구현 task
- 충돌 가능성이 있는 파일: Gradle, 패키지 구조, DI entrypoint
- 완료 기준: 앱이 빌드 가능한 공통 구조로 정리되고 문서와 일치함
- 검증 방법: Android Studio sync, `.\gradlew.bat assembleDebug`, `.\gradlew.bat test`
- 작업자가 수정해도 되는 파일 범위: task 문서에 승인된 공통 구조 파일
- 수정하면 안 되는 파일 범위: 화면 구현, 수집 기능 구현, AI 기능 구현
- 관련 task 문서 경로: `docs/tasks/T-020-architecture-baseline.md`

### T-030-data-model-audit

- 작업명: DataItem/Topic/Analysis/Action 모델 및 Room 기준 정리
- Status: Done
- Owner: Codex
- 목적: MVP 데이터 모델과 Entity/DAO/Repository 계약을 충돌 없이 확정합니다.
- 담당 브랜치명: `chore/T-030-data-model-audit`
- 예상 수정 파일: `domain/model/`, `data/source/local/`, `domain/repository/`, `data/repository/`, `docs/ARCHITECTURE.md`
- 선행 task: `T-020-architecture-baseline`
- Blocked by: 없음
- Ready criteria: 패키지 구조와 의존성 기준이 확정됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-040`, `T-050`, `T-100`, `T-120`, `T-140`, `T-170`
- 충돌 가능성이 있는 파일: model, entity, DAO, repository
- 완료 기준: 모든 수집/분석/UX task가 참조할 데이터 계약이 문서와 코드에 존재함
- 검증 방법: Room compile, mapper 단위 테스트, `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: 모델, Entity, DAO, Repository 계약/기본 구현
- 수정하면 안 되는 파일 범위: 화면 세부 UI, 삼성 앱 export 세부 구현
- 관련 task 문서 경로: `docs/tasks/T-030-data-model-audit.md`

### T-040-navigation-baseline

- 작업명: Compose Navigation 기준 정리
- Status: Done
- Owner: Codex
- 목적: Home, Inbox, Logs, Settings, Topic, Analysis 화면 이동 규칙을 확정합니다.
- 담당 브랜치명: `chore/T-040-navigation-baseline`
- 예상 수정 파일: `presentation/navigation/`, `MainActivity.kt`, 화면 shell 파일
- 선행 task: `T-020-architecture-baseline`, `T-030-data-model-audit`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 패키지 구조, 핵심 모델, Manifest/component 기준이 확정됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: 화면 UI task 전체
- 충돌 가능성이 있는 파일: `MainActivity.kt`, navigation graph, root scaffold
- 완료 기준: 빈 화면 또는 shell 화면 기준으로 주요 route가 빌드 가능함
- 검증 방법: 앱 실행 후 탭/route 이동 수동 확인
- 작업자가 수정해도 되는 파일 범위: navigation/root shell
- 수정하면 안 되는 파일 범위: 개별 화면의 완성 UI, 수집/AI 로직
- 관련 task 문서 경로: `docs/tasks/T-040-navigation-baseline.md`

### T-050-permission-and-manifest-baseline

- 작업명: 권한, Manifest, Android component 기준 정리
- Status: Done
- Owner: Codex
- 목적: ShareReceiverActivity, TileService, TransparentActivity, SAF, Samsung app queries를 Manifest에 안전하게 반영합니다.
- 담당 브랜치명: `chore/T-050-permission-and-manifest-baseline`
- 예상 수정 파일: `app/src/main/AndroidManifest.xml`, 권한 helper, `docs/DATA_COLLECTION_STRATEGY.md`
- 선행 task: `T-020-architecture-baseline`, `T-030-data-model-audit`
- Blocked by: 없음
- Ready criteria: component 패키지 위치와 데이터 저장 계약이 확정됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-100`, `T-110`, `T-120`, `T-130`, `T-500`, `T-510`, `T-520`
- 충돌 가능성이 있는 파일: Manifest, permission helper
- 완료 기준: 필요한 component/queries/권한이 선언되고 금지 구현이 포함되지 않음
- 검증 방법: manifest merge 확인, 설치/실행, Share target/Tile 등록 가능성 확인
- 작업자가 수정해도 되는 파일 범위: Manifest와 권한 helper
- 수정하면 안 되는 파일 범위: 수집 구현 세부, UI 완성 화면
- 관련 task 문서 경로: `docs/tasks/T-050-permission-and-manifest-baseline.md`

## Phase 2: 데이터 수집 흐름 구현/정리

### T-100-share-target-flow

- 작업명: Share Target 기반 링크/텍스트/이미지/파일 수신
- Status: Done
- Owner: Codex
- 목적: Android Share Sheet에서 SmartClipboard로 들어온 자료를 `DataItem`으로 저장합니다.
- 담당 브랜치명: `feat/T-100-share-target-flow`
- 예상 수정 파일: `collection/share/`, `ShareReceiverActivity`, repository 호출부, Hilt DI module, 관련 테스트
- 선행 task: `T-050-permission-and-manifest-baseline`, `T-040-navigation-baseline`
- Blocked by: 없음
- Ready criteria: Manifest share target, DataRepository 계약, 앱 root navigation 기준이 확정됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-050`, `T-170`
- 충돌 가능성이 있는 파일: Manifest, DataRepository, DataItem mapper
- 완료 기준: 텍스트/URL/이미지 URI/file URI 수신 후 저장 피드백까지 동작
- 검증 방법: Android share sheet 수동 테스트, repository fake 테스트
- 작업자가 수정해도 되는 파일 범위: share collection package, share activity, Hilt DI module, share result strings, share tests
- 수정하면 안 되는 파일 범위: model/entity/repository 계약 변경, navigation root
- 관련 task 문서 경로: `docs/tasks/T-100-share-target-flow.md`

### T-110-quick-tile-flow

- 작업명: Quick Settings Tile + TransparentActivity 최근 클립보드 수집
- Status: Done
- Owner: Codex
- 목적: Tile 클릭 시 투명 Activity가 focus를 얻고 Primary Clip 1개를 저장합니다.
- 담당 브랜치명: `feat/T-110-quick-tile-flow`
- 예상 수정 파일: `collection/clipboard/`, `TileService`, `TransparentActivity`, 관련 테스트
- 선행 task: `T-050-permission-and-manifest-baseline`, `T-040-navigation-baseline`, `T-100-share-target-flow`
- Blocked by: 없음
- Ready criteria: TileService/TransparentActivity Manifest, repository 계약, 앱 root navigation 기준, 공통 저장 결과 처리 패턴이 확정됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-050`, `T-170`
- 충돌 가능성이 있는 파일: Manifest, clipboard handler, repository mapper
- 완료 기준: Tile 클릭으로 최신 텍스트/링크 클립 저장과 toast 피드백이 동작
- 검증 방법: 실제 기기 또는 에뮬레이터 Tile 수동 테스트
- 작업자가 수정해도 되는 파일 범위: clipboard collection package, Tile/Activity, tests
- 수정하면 안 되는 파일 범위: 백그라운드 클립보드 감시 구현, model 계약 변경
- 관련 task 문서 경로: `docs/tasks/T-110-quick-tile-flow.md`

### T-120-media-store-batch-query

- 작업명: 앱 실행 시 Last Sync Time 기준 이미지 자동 수집
- Status: Done
- Owner: Codex
- 목적: 앱 재진입 시 마지막 종료/동기화 시각 이후 추가된 모든 이미지를 `DataItem`으로 저장합니다.
- 담당 브랜치명: `feat/T-120-media-store-batch-query`
- 예상 수정 파일: `collection/media/`, sync preference, repository 호출부, tests
- 선행 task: `T-050-permission-and-manifest-baseline`, `T-030-data-model-audit`, `T-040-navigation-baseline`, `T-100-share-target-flow`, `T-110-quick-tile-flow`
- Blocked by: 없음
- Ready criteria: READ_MEDIA_IMAGES 또는 버전별 권한 전략과 DataItem image 계약 확정
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-030`, `T-050`, `T-170`
- 충돌 가능성이 있는 파일: repository, sync preference, permission helper
- 완료 기준: 새 이미지 batch query, 중복 방지, last sync 갱신, 실패 로그가 동작
- 검증 방법: 테스트 이미지 추가 후 앱 실행, DB 저장 확인, 권한 거부 케이스 확인
- 작업자가 수정해도 되는 파일 범위: media collection package, sync settings, tests
- 수정하면 안 되는 파일 범위: 전체 저장 정책 변경, UI 대규모 변경
- 관련 task 문서 경로: `docs/tasks/T-120-media-store-batch-query.md`

### T-130-storage-access-framework-picker

- 작업명: Storage Access Framework 파일 직접 선택
- Status: Done
- Owner: Codex
- 목적: 사용자가 직접 이미지/파일/PDF를 선택해 SmartClipboard에 담을 수 있게 합니다.
- 담당 브랜치명: `feat/T-130-storage-access-framework-picker`
- 예상 수정 파일: SAF launcher, Inbox/Home entry point, repository 호출부, tests
- 선행 task: `T-040-navigation-baseline`, `T-050-permission-and-manifest-baseline`, `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`
- Blocked by: 없음
- Ready criteria: 파일 선택 진입점과 DataItem file 계약 확정
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: `T-040`
- 충돌 가능성이 있는 파일: launcher owner composable, repository mapper
- 완료 기준: 사용자가 선택한 파일 URI가 저장되고 Inbox에서 확인 가능
- 검증 방법: `ActivityResultContracts.GetContent/GetMultipleContents` 수동 테스트
- 작업자가 수정해도 되는 파일 범위: SAF picker package, 해당 entry UI, tests
- 수정하면 안 되는 파일 범위: navigation route 변경, model 계약 변경
- 관련 task 문서 경로: `docs/tasks/T-130-storage-access-framework-picker.md`

## Phase 3: 데이터 저장/전처리/클러스터링 연결

### T-140-enrichment-ocr-og-pipeline

- 작업명: OCR/OG 추출 전처리 파이프라인
- Status: Done
- Owner: Codex
- 목적: 이미지 OCR과 링크 OG 추출을 입력 직후 시도하고, 실패 시 retry 상태로 남깁니다.
- 담당 브랜치명: `feat/T-140-enrichment-ocr-og-pipeline`
- 예상 수정 파일: `processing/ocr/`, `processing/web/`, `processing/enrichment/`, enrichment DAO, collection handler trigger, tests
- 선행 task: `T-030-data-model-audit`, `T-100-share-target-flow`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-150`은 interface 합의 후 가능
- Cannot run with: `T-030`, `T-170`
- 충돌 가능성이 있는 파일: DataItem DAO, collection handlers, Gradle dependency catalog
- 완료 기준: OCR/OG 성공 저장, 3회 retry 후 failed 처리, cancellation은 retry로 세지 않음, IO dispatcher 사용
- 검증 방법: fake OCR/Web extractor 단위 테스트, parser 단위 테스트, `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: OCR/Web/enrichment package, collection handler trigger, tests
- 수정하면 안 되는 파일 범위: 수집 Activity lifecycle, UI 화면
- 관련 task 문서 경로: `docs/tasks/T-140-enrichment-ocr-og-pipeline.md`

### T-150-gemini-topic-recommendation

- 작업명: Gemini 기반 이번 실행 Topic 추천
- Status: Done
- Owner: Codex
- 목적: 새로 수집/분석된 자료에서 이번 실행의 추천 후보만 생성합니다.
- 담당 브랜치명: `feat/T-150-gemini-topic-recommendation`
- 예상 수정 파일: `processing/gemini/`, recommendation repository, settings key access, tests
- 선행 task: `T-140-enrichment-ocr-og-pipeline`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-160` 중 파일 충돌이 없는 경우
- Cannot run with: `T-030`, `T-170`
- 충돌 가능성이 있는 파일: Gemini recommendation package, DI module
- 완료 기준: 하드코딩 key 없이 BuildConfig/local.properties 기반 호출 구조와 fake 테스트가 존재, 추천 후보는 사용자 수락 전 영구 저장하지 않음
- 검증 방법: fake Gemini 테스트, key 없음 fallback 테스트, parser 테스트, 실제 key가 있는 로컬에서 수동 smoke test
- 작업자가 수정해도 되는 파일 범위: Gemini/recommendation package, tests
- 수정하면 안 되는 파일 범위: UI 완성 화면, 외부 앱 export
- 관련 task 문서 경로: `docs/tasks/T-150-gemini-topic-recommendation.md`

### T-160-storage-quota-cleanup

- 작업명: 저장 용량 한도와 자동 삭제 정책
- Status: Done
- Owner: Codex
- 목적: 자동 수집 자료가 용량을 넘으면 안전한 순서로 오래된 데이터를 정리합니다.
- 담당 브랜치명: `feat/T-160-storage-quota-cleanup`
- 예상 수정 파일: storage policy, cleanup worker/use case, settings repository, tests
- 선행 task: `T-030-data-model-audit`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`, `T-150-gemini-topic-recommendation`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: 없음
- Cannot run with: `T-030`, `T-170`, `T-240`
- 충돌 가능성이 있는 파일: settings repository, DataItem flags, cleanup DAO
- 완료 기준: 사용량/초과량 계산, 내부 복사본 우선, 중요/보존/Topic 연결 제외, 오래된 DataItem soft-delete 순서가 테스트로 고정됨
- 검증 방법: quota 계산 단위 테스트, 삭제 제외 대상 테스트, manager soft-delete 테스트, `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: storage policy package, cleanup tests, cleanup DAO query
- 수정하면 안 되는 파일 범위: Inbox UI, Topic UI
- 관련 task 문서 경로: `docs/tasks/T-160-storage-quota-cleanup.md`

### T-170-repository-integration

- 작업명: DataRepository 통합 정리
- Status: Done
- Owner: Codex
- 목적: 수집, enrichment, 추천, 저장 정책을 ViewModel이 쓰기 쉬운 API로 통합합니다.
- 담당 브랜치명: `chore/T-170-repository-integration`
- 예상 수정 파일: `domain/repository/`, `data/repository/`, integration tests
- 선행 task: `T-100`, `T-110`, `T-120`, `T-130`, `T-140`, `T-150`, `T-160`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 아니오
- Can run in parallel with: 없음
- Cannot run with: repository를 만지는 모든 task
- 충돌 가능성이 있는 파일: DataRepository 전체
- 완료 기준: Home/Inbox/Settings ViewModel이 사용할 Repository API가 안정화되고 순환 의존성이 제거됨
- 검증 방법: repository integration test, fake datasource test, `.\gradlew.bat testDebugUnitTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: repository 계약/구현/테스트
- 수정하면 안 되는 파일 범위: 화면 UI 완성 작업
- 관련 task 문서 경로: `docs/tasks/T-170-repository-integration.md`

## Phase 4: 홈 화면 및 데이터 리스트 UX 재설계

### T-200-home-ux-redesign

- 작업명: ChatGPT/Codex 스타일 Home UX 재설계
- Status: Done
- Owner: Codex
- 목적: 새 작업 입력, 이전 작업, 이번 AI 추천, 자동 분석 상태를 단순하게 보여줍니다.
- 담당 브랜치명: `feat/T-200-home-ux-redesign`
- 예상 수정 파일: `presentation/home/`, Home ViewModel tests, UI tests
- 선행 task: `T-040-navigation-baseline`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-210`, `T-230`, `T-240`
- Cannot run with: `T-040`
- 충돌 가능성이 있는 파일: root scaffold, theme token
- 완료 기준: Home이 작업 중심으로 동작하고 과거 AI 추천을 영구 노출하지 않음
- 검증 방법: `HomeUiStateMapperTest`, Compose compile, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Home package와 Home 전용 tests
- 수정하면 안 되는 파일 범위: navigation root 변경, repository 계약 변경
- 관련 task 문서 경로: `docs/tasks/T-200-home-ux-redesign.md`

### T-210-data-list-filter-selection

- 작업명: Inbox 자료 모음, 필터, 선택 UX
- Status: Done
- Owner: Codex
- 목적: 이미지/링크/텍스트/파일 카테고리와 리스트/그리드 전환을 제공합니다.
- 담당 브랜치명: `feat/T-210-data-list-filter-selection`
- 예상 수정 파일: `presentation/inbox/`, Inbox ViewModel tests, UI tests
- 선행 task: `T-040-navigation-baseline`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-200`, `T-230`, `T-240`
- Cannot run with: `T-040`, repository query 변경 작업
- 충돌 가능성이 있는 파일: Inbox route, selection state
- 완료 기준: 카테고리 카드, list/grid 전환, 삭제/중요/Topic 추가 entry가 동작
- 검증 방법: `InboxUiStateMapperTest`, Compose compile, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Inbox package와 tests
- 수정하면 안 되는 파일 범위: DataItem model/DAO 계약 변경
- 관련 task 문서 경로: `docs/tasks/T-210-data-list-filter-selection.md`

### T-220-save-feedback-bottom-sheet

- 작업명: 저장 피드백 Toast/BottomSheet UX
- Status: Done
- Owner: Codex
- 목적: Share/Tile 저장 시 앱 전체가 켜진 듯 보이지 않는 짧은 피드백을 제공합니다.
- 담당 브랜치명: `feat/T-220-save-feedback-bottom-sheet`
- 예상 수정 파일: `presentation/feedback/`, Share/Clipboard feedback hook, tests
- 선행 task: `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-200`, `T-210`
- Cannot run with: `T-100`, `T-110`의 Activity lifecycle 수정
- 충돌 가능성이 있는 파일: ShareReceiverActivity, TransparentActivity
- 완료 기준: 성공/부분 실패/오류 문구가 UX 톤과 일치하고 자동 종료 흐름이 안정적임
- 검증 방법: `SaveFeedbackMessageMapperTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: feedback UI package와 연동 hook
- 수정하면 안 되는 파일 범위: 수집 저장 로직 변경
- 관련 task 문서 경로: `docs/tasks/T-220-save-feedback-bottom-sheet.md`

### T-230-logs-tab-flow

- 작업명: 사용자 확인 기록 Logs 탭
- Status: Done
- Owner: Codex
- 목적: 사용자가 확인한 작업, 추천 수락, 전송, 완료/미완료 기록을 badge로 필터링합니다.
- 담당 브랜치명: `feat/T-230-logs-tab-flow`
- 예상 수정 파일: `presentation/logs/`, log model/query, tests
- 선행 task: `T-040-navigation-baseline`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-200`, `T-210`, `T-240`
- Cannot run with: repository log 계약 변경 작업
- 충돌 가능성이 있는 파일: log entity/query, navigation tab
- 완료 기준: 내부 자동 이벤트가 아닌 사용자 확인 기록만 표시하고 badge 필터가 동작
- 검증 방법: `LogsUiStateMapperTest`, Compose compile, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Logs package와 log query tests
- 수정하면 안 되는 파일 범위: Home/Inbox UI 세부 변경
- 관련 task 문서 경로: `docs/tasks/T-230-logs-tab-flow.md`

### T-240-settings-ux-storage-permission

- 작업명: Settings 수집 기간, 용량, 권한 UX
- Status: Done
- Owner: Codex
- 목적: 자동 수집 기간과 저장 용량/자동 삭제 정책을 사용자가 조정할 수 있게 합니다.
- 담당 브랜치명: `feat/T-240-settings-ux-storage-permission`
- 예상 수정 파일: `presentation/settings/`, settings repository, tests
- 선행 task: `T-040-navigation-baseline`, `T-160-storage-quota-cleanup`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-200`, `T-210`, `T-230`
- Cannot run with: `T-160` policy 변경 작업
- 충돌 가능성이 있는 파일: settings repository, Settings route
- 완료 기준: 수집 기간 preset/custom, 용량 표시, 자동 삭제 설명, 권한 상태가 표시됨
- 검증 방법: `SettingsUiStateMapperTest`, Compose compile, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Settings package와 tests
- 수정하면 안 되는 파일 범위: storage policy 핵심 알고리즘 변경
- 관련 task 문서 경로: `docs/tasks/T-240-settings-ux-storage-permission.md`

## Phase 5: Topic 생성 및 데이터 선택 UX

### T-300-topic-create-flow

- 작업명: 사용자 입력 기반 Topic 생성
- Status: Done
- Owner: Codex
- 목적: Home 입력 또는 추천 수락으로 Topic을 생성합니다.
- 담당 브랜치명: `feat/T-300-topic-create-flow`
- 예상 수정 파일: `presentation/topic/`, Topic ViewModel/use case, tests
- 선행 task: `T-200-home-ux-redesign`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-310`은 UI 계약 합의 후 가능
- Cannot run with: Topic model/repository 변경 작업
- 충돌 가능성이 있는 파일: Topic route, Home navigation event
- 완료 기준: 사용자 요청/AI 추천 source badge를 가진 Topic이 생성됨
- 검증 방법: `TopicCreateUseCaseTest`, Home compile, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Topic create package와 tests
- 수정하면 안 되는 파일 범위: DataItem model/DAO 변경
- 관련 task 문서 경로: `docs/tasks/T-300-topic-create-flow.md`

### T-310-topic-data-selection-flow

- 작업명: Topic 자료 자동 선택 및 수정 UX
- Status: Done
- Owner: Codex
- 목적: AI가 고른 자료를 한 줄 요약으로 보여주고, 사용자가 필요할 때만 수정합니다.
- 담당 브랜치명: `feat/T-310-topic-data-selection-flow`
- 예상 수정 파일: `presentation/topic/selection/`, selection use case, tests
- 선행 task: `T-210-data-list-filter-selection`, `T-300-topic-create-flow`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-400`은 selection 결과 계약을 사용해 진행 가능
- Cannot run with: `T-210` selection state 변경 작업
- 충돌 가능성이 있는 파일: selection component, Topic ViewModel, Topic cross-ref repository 계약
- 완료 기준: `사용된 자료 N개` 요약, tap-to-edit, 선택 저장이 동작
- 검증 방법: `TopicDataSelectionUseCaseTest`, `TopicDataSelectionUiStateMapperTest`, `DataRepositoryImplIntegrationTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: Topic selection package와 tests, 선택 저장에 필요한 최소 repository/DAO 계약
- 수정하면 안 되는 파일 범위: Inbox category 구조 변경
- 관련 task 문서 경로: `docs/tasks/T-310-topic-data-selection-flow.md`

## Phase 6: AI Agent 분석/초안 생성 흐름

### T-400-topic-analysis-draft

- 작업명: TopicAnalysis 생성 흐름
- Status: Done
- Owner: Codex
- 목적: Topic과 연결 DataItem을 Gemini에 보내 분석 요약과 근거를 생성합니다.
- 담당 브랜치명: `feat/T-400-topic-analysis-draft`
- 예상 수정 파일: `processing/gemini/analysis/`, `presentation/analysis/`, tests
- 선행 task: `T-150-gemini-topic-recommendation`, `T-310-topic-data-selection-flow`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-410`은 DTO 계약 합의 후 가능
- Cannot run with: Gemini manager 계약 변경 작업
- 충돌 가능성이 있는 파일: TopicAnalysis model, Gemini prompt builder
- 완료 기준: 분석 draft가 저장되고, 실패/재시도 상태가 기록됨
- 검증 방법: `TopicAnalysisUseCaseTest`, `GeminiTopicAnalysisParserTest`, `GeminiTopicAnalysisGeneratorTest`, `TopicAnalysisUiStateMapperTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: analysis generation package와 tests
- 수정하면 안 되는 파일 범위: 외부 앱 export 구현
- 관련 task 문서 경로: `docs/tasks/T-400-topic-analysis-draft.md`

### T-410-topic-action-draft

- 작업명: TopicAction 초안 생성 및 검토 상태
- Status: Done
- Owner: Codex
- 목적: Notes/Calendar/Reminder로 보낼 초안을 사용자가 검토 가능한 카드 데이터로 만듭니다.
- 담당 브랜치명: `feat/T-410-topic-action-draft`
- 예상 수정 파일: `domain/model/TopicAction`, `presentation/analysis/action/`, tests
- 선행 task: `T-400-topic-analysis-draft`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: `T-500`, `T-510`, `T-520`은 payload contract freeze 후 가능
- Cannot run with: TopicAction model 변경 작업
- 충돌 가능성이 있는 파일: TopicAction model, analysis UI
- 완료 기준: 카드 preview, 작은 수정 버튼, 완료/미완료 상태, 뒤로가기 처리 기준이 동작
- 검증 방법: `TopicActionDraftUseCaseTest`, `TopicActionDraftUiStateMapperTest`, `.\gradlew.bat assembleDebug test`
- 작업자가 수정해도 되는 파일 범위: action draft package와 tests
- 수정하면 안 되는 파일 범위: Samsung intent launcher 세부 구현
- 관련 task 문서 경로: `docs/tasks/T-410-topic-action-draft.md`

## Phase 7: 외부 앱 연동 초안

### T-500-calendar-intent-draft

- 작업명: Samsung Calendar intent 전송
- Status: Done
- Owner: Codex
- 목적: 사용자가 검토한 일정 초안을 Samsung Calendar 또는 기본 Calendar insert intent로 보냅니다.
- 담당 브랜치명: `feat/T-500-calendar-intent-draft`
- 예상 수정 파일: `export/calendar/`, action card hook, tests
- 선행 task: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-510`, `T-520`
- Cannot run with: `T-050` Manifest 변경 작업
- 충돌 가능성이 있는 파일: action card hook, export manager
- 완료 기준: 제목/설명/장소/시간/all-day를 확인 후 Calendar insert가 열림
- 검증 방법: Samsung 기기 또는 캘린더 앱 설치 환경 수동 테스트
- 작업자가 수정해도 되는 파일 범위: calendar export package와 tests
- 수정하면 안 되는 파일 범위: TopicAction model 변경
- 관련 task 문서 경로: `docs/tasks/T-500-calendar-intent-draft.md`

### T-510-notes-share-draft

- 작업명: Samsung Notes share 전송
- Status: Done
- Owner: Codex
- 목적: 사용자가 검토한 요약 노트 초안을 Samsung Notes로 보냅니다.
- 담당 브랜치명: `feat/T-510-notes-share-draft`
- 예상 수정 파일: `export/notes/`, action card hook, tests
- 선행 task: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-500`, `T-520`
- Cannot run with: `T-050` Manifest 변경 작업
- 충돌 가능성이 있는 파일: action card hook, export manager
- 완료 기준: `ACTION_SEND` + `text/plain` + Samsung Notes package 전송이 동작
- 검증 방법: Samsung Notes 설치 환경 수동 테스트, 미설치 fallback toast 확인
- 작업자가 수정해도 되는 파일 범위: notes export package와 tests
- 수정하면 안 되는 파일 범위: TopicAction model 변경
- 관련 task 문서 경로: `docs/tasks/T-510-notes-share-draft.md`

### T-520-reminder-intent-draft

- 작업명: Samsung Reminder share 전송
- Status: Done
- Owner: Codex
- 목적: 사용자가 검토한 할 일 초안을 Samsung Reminder로 보냅니다.
- 담당 브랜치명: `feat/T-520-reminder-intent-draft`
- 예상 수정 파일: `export/reminder/`, action card hook, tests
- 선행 task: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 완료됨
- 병렬 진행 가능 여부: 예
- Can run in parallel with: `T-500`, `T-510`
- Cannot run with: `T-050` Manifest 변경 작업
- 충돌 가능성이 있는 파일: action card hook, export manager
- 완료 기준: `ACTION_SEND` + `text/plain` + Samsung Reminder package 전송이 동작
- 검증 방법: Samsung Reminder 설치 환경 수동 테스트, 미설치 fallback toast 확인
- 작업자가 수정해도 되는 파일 범위: reminder export package와 tests
- 수정하면 안 되는 파일 범위: TopicAction model 변경
- 관련 task 문서 경로: `docs/tasks/T-520-reminder-intent-draft.md`

## Phase 8: QA, 오류 처리, 문서 정리

### T-900-qa-build-test

- 작업명: 전체 QA, 빌드, 오류 처리, 문서 정리
- Status: Blocked
- Owner: Codex
- 목적: MVP end-to-end 흐름을 실제 기기 기준으로 점검하고 문서를 최신화합니다.
- 담당 브랜치명: `test/T-900-qa-build-test`
- 예상 수정 파일: QA docs, test fixes, small bug fix files approved in PR
- 선행 task: `T-200`, `T-210`, `T-220`, `T-230`, `T-240`, `T-300`, `T-310`, `T-400`, `T-410`, `T-500`, `T-510`, `T-520`
- Blocked by: 연결된 Android device/emulator 없음, 실제 Gemini key 없음
- Ready criteria: Android device/emulator 연결, 필요 시 `local.properties`의 Gemini key 설정
- 병렬 진행 가능 여부: 제한적 가능
- Can run in parallel with: 작은 문서 보완 task
- Cannot run with: 대규모 기능 구현 task
- 충돌 가능성이 있는 파일: QA 중 수정하는 모든 bug fix 파일
- 완료 기준: Share/Tile/MediaStore/SAF/AI/Topic/Export 흐름이 점검되고 known issue가 문서화됨
- 검증 방법: `./gradlew test`, `./gradlew assembleDebug`, 실제 기기 수동 시나리오
- 작업자가 수정해도 되는 파일 범위: QA 문서, 승인된 작은 fix
- 수정하면 안 되는 파일 범위: 승인 없는 구조 변경, 새 기능 추가
- 관련 task 문서 경로: `docs/tasks/T-900-qa-build-test.md`

## 지금 가능한 작업

현재 바로 시작 가능한 task는 없습니다.

`T-010-agents-and-docs-setup`, `T-000-current-code-audit`, `T-020-architecture-baseline`, `T-030-data-model-audit`, `T-050-permission-and-manifest-baseline`, `T-040-navigation-baseline`, `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`, `T-150-gemini-topic-recommendation`, `T-160-storage-quota-cleanup`, `T-170-repository-integration`, `T-200-home-ux-redesign`, `T-210-data-list-filter-selection`, `T-220-save-feedback-bottom-sheet`, `T-230-logs-tab-flow`, `T-240-settings-ux-storage-permission`, `T-300-topic-create-flow`, `T-310-topic-data-selection-flow`, `T-400-topic-analysis-draft`, `T-410-topic-action-draft`, `T-500-calendar-intent-draft`, `T-510-notes-share-draft`, `T-520-reminder-intent-draft`는 완료된 상태입니다. `T-900`은 자동 빌드/테스트와 QA 리포트 작성까지 진행했지만 실제 기기/에뮬레이터가 없어 수동 QA를 완료하지 못했습니다.

## 아직 시작하면 안 되는 작업 예시

- 대규모 새 기능 추가: 현재 MVP 구현이 끝났으므로 먼저 `T-900`에서 QA와 known issue 정리를 진행해야 함
- `T-900-qa-build-test`: 실제 device/emulator 연결과 Gemini key가 필요하므로 현재 환경에서는 완료 처리 금지

## 추천 다음 작업

1. Android device/emulator 연결
2. 필요 시 `local.properties`에 Gemini key 설정
3. `T-900-qa-build-test`를 다시 In Progress로 바꾸고 수동 QA 시나리오 실행
