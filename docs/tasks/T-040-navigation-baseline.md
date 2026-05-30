# T-040 Compose Navigation 기준 정리

## 목적

Home, Inbox, Logs, Settings, Topic, Analysis 화면으로 이동할 route와 root scaffold 기준을 확정합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `chore/T-040-navigation-baseline`
- Depends on: `T-020-architecture-baseline`, `T-030-data-model-audit`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 패키지 구조, 핵심 모델, Manifest/component 기준이 확정됨
- Can run in parallel with: 없음
- Cannot run with: 화면 UI task 전체

## 수정 허용 파일

- `app/src/main/java/.../presentation/navigation/`
- `app/src/main/java/.../MainActivity.kt`
- 각 화면의 빈 shell composable
- navigation 관련 테스트
- `docs/tasks/T-040-navigation-baseline.md`

## 수정 금지 파일

- 화면별 완성 UI
- Repository 계약 변경
- Manifest
- 수집/AI/export 로직

## 구현 내용

- 탭 구조 Home / Inbox / Logs / Settings route를 만듭니다.
- Topic 생성, 자료 선택, Analysis 결과 화면으로 이동하는 route를 정의합니다.
- 화면 shell은 비어 있거나 최소 placeholder로만 둡니다.
- root scaffold와 navigation 상태 소유 위치를 문서화합니다.

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

- 앱 실행 후 기본 탭 이동이 가능합니다.
- 후속 UI task가 같은 route 이름을 참조할 수 있습니다.
- navigation 파일 외 대규모 UI가 들어가지 않습니다.

## T-040 결과 요약

- `AppRoute`와 `TopLevelDestination` route 계약을 추가했습니다.
- Home, Inbox, Logs, Settings top-level tab shell을 추가했습니다.
- Topic create, Topic data selection, Topic analysis detail route 계약을 추가했습니다.
- `MainActivity`를 `SmartClipboardRoot`로 연결했습니다.
- 화면은 후속 UI task를 위한 placeholder 수준으로만 구성했습니다.
- Gradle/Manifest/Repository/Data model은 수정하지 않았습니다.

## 검증 방법

- 앱 실행 수동 확인
- route 이동 smoke test
- 변경 파일이 navigation 범위 안에 있는지 확인
- `.\gradlew.bat testDebugUnitTest`
- `.\gradlew.bat assembleDebug test`

## PR에 반드시 적을 내용

- route 목록
- root scaffold 구조
- 후속 UI task가 사용해야 할 entry point
- 화면 세부 구현 없음 확인
