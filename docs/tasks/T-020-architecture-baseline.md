# T-020 앱 패키지 구조와 의존성 기준 정리

## 목적

현재 코드 감사 결과를 바탕으로 Kotlin + Jetpack Compose MVP에 필요한 패키지 구조, Gradle 의존성, Hilt/Room/Compose 기본 구성을 확정합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `chore/T-020-architecture-baseline`
- Depends on: `T-000-current-code-audit`
- Blocked by: 없음
- Ready criteria: 재사용 후보, 폐기 대상, 현재 Gradle/Manifest 상태가 `docs/ARCHITECTURE.md`에 기록됨
- Can run in parallel with: 없음
- Cannot run with: `T-030`, `T-040`, `T-050`, 모든 앱 구현 task

## 수정 허용 파일

- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- 공통 패키지 골격 파일
- `docs/ARCHITECTURE.md`
- `docs/WORK_LOG.md`
- `docs/tasks/T-020-architecture-baseline.md`

## 수정 금지 파일

- 화면별 완성 UI
- 수집 기능 구현
- Gemini/OCR/OG 구현
- 외부 앱 export 구현

## 구현 내용

- 기존 패키지 구조를 MVP 구조와 비교합니다.
- 필요한 의존성을 최소 단위로 정리합니다.
- Hilt, Room, Compose, Coroutines 기준을 문서와 코드 구조에 맞춥니다.
- 큰 리팩토링이 필요하면 이유와 영향 범위를 먼저 문서화합니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [x] PR 작성 내용 정리

> 현재 작업공간은 아직 Git repository가 아니며, 사용자 승인 전 commit/push/PR 생성 금지 조건이 있으므로 실제 PR은 만들지 않았습니다.

## 완료 기준

- Android Studio sync가 성공합니다.
- 공통 패키지 구조가 `docs/ARCHITECTURE.md`와 일치합니다.
- 이후 모델/Navigation/Manifest task가 참조할 기준이 명확합니다.

## 검증 방법

- Android Studio sync 대상 Gradle baseline 생성
- `.\gradlew.bat assembleDebug`
- `.\gradlew.bat test`
- 변경된 Gradle/패키지 파일 목록 확인

## T-020 결과 요약

- 새 Android 프로젝트 baseline을 생성했습니다.
- Package/namespace/applicationId는 `com.smartclipboard.ai`로 정했습니다.
- App display name은 `SmartClipboard`로 정했습니다.
- Gradle wrapper는 `8.10.2`, AGP는 `8.7.3`, Kotlin은 `2.0.21`로 구성했습니다.
- 현재 설치 SDK와 AGP 권장 범위에 맞춰 `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26`으로 고정했습니다.
- Hilt, Room, Compose, Coroutines/Flow 의존성 기준을 추가했습니다.
- Gemini API key는 `local.properties -> BuildConfig.GEMINI_API_KEY` 방식으로만 주입하며, source에는 하드코딩하지 않습니다.
- Manifest는 launcher Activity만 포함한 최소 상태입니다. Share Target, TileService, TransparentActivity, Samsung app queries, 권한은 `T-050`에서 다룹니다.
- `assembleDebug`와 `test`가 통과했습니다.

## PR에 반드시 적을 내용

- 변경한 공통 구조
- 추가/변경한 의존성
- 이후 task에 영향을 주는 결정
- 범위 밖 구현 없음 확인
