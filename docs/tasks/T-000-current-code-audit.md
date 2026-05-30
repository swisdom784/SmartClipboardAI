# T-000 현재 코드 감사 및 재사용 후보 정리

## 목적

현재 로컬 프로젝트 구조, 참고 GitHub 초안, 기존 구현 클래스를 읽고 유지할 부분, 수정할 부분, 버릴 부분을 문서화합니다. 기능 구현은 하지 않습니다.

## 작업 상태

- Status: Done
- Owner: 미배정
- Branch: `docs/T-000-current-code-audit`
- Depends on: 없음
- Blocked by: 없음
- Ready criteria: 협업 기준 문서가 생성되어 있고, 코드 구현을 시작하지 않는다는 조건이 확인됨
- Can run in parallel with: 없음
- Cannot run with: 공통 모델/DB/Repository/Navigation/Manifest/Gradle 작업 전체

## 수정 허용 파일

- `docs/ARCHITECTURE.md`
- `docs/WORK_LOG.md`
- `docs/tasks/T-000-current-code-audit.md`

## 수정 금지 파일

- 앱 소스 코드 전체
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`
- 다른 task 문서

## 구현 내용

- `README.md`가 있으면 읽고 현재 의도를 정리합니다.
- `rg --files`로 현재 프로젝트 파일을 확인합니다.
- 기존 Kotlin/Compose/Manifest/Gradle 파일이 있다면 읽고 재사용 후보를 분류합니다.
- 참고 가능한 클래스 후보를 `docs/ARCHITECTURE.md`에 반영합니다.
- 폐기하거나 다시 설계해야 할 UI/UX 구조를 명확히 기록합니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [x] PR 작성

체크 메모:

- 빌드 확인은 현재 로컬에 Android 앱 코드가 없어 “빌드 대상 없음”으로 확인했습니다.
- PR은 실제 생성하지 않았고, PR에 적을 내용만 아래 기준에 맞춰 준비했습니다.

## 완료 기준

- 재사용 후보 클래스와 파일이 문서화되어 있습니다.
- 위험하거나 금지된 구현이 있다면 이유와 함께 기록되어 있습니다.
- UI/UX에서 버릴 구조와 참고할 구조가 구분되어 있습니다.
- 다음 task인 `T-020-architecture-baseline`을 Ready로 전환할 수 있는 판단 근거가 있습니다.

## 검증 방법

- `rg --files` 결과를 기준으로 누락된 주요 파일이 없는지 확인합니다.
- README, docs, Gradle, Manifest, 주요 Kotlin 파일을 읽었다는 기록을 `docs/WORK_LOG.md`에 남깁니다.
- 앱 코드가 수정되지 않았음을 변경 파일 목록으로 확인합니다.

## PR에 반드시 적을 내용

- 읽은 파일/폴더 목록
- 유지/수정/폐기 판단
- 다음 task를 열어도 되는지에 대한 의견
- 앱 코드 변경 없음 확인

## 감사 결과 요약

- 로컬 작업공간은 Git repository가 아니며, Android 앱 코드가 아직 없습니다.
- 참고 GitHub 초안에는 MVVM, Room, Hilt, Share Target, Tile, MediaStore, Topic/Action 골격이 있으므로 구조 참고 가치가 있습니다.
- 하드코딩된 Gemini API key, `KnowledgeEntity` 실험 DB, 대형 `MainScreen.kt`, 영구 `AiProposalEntity`, 휴리스틱 AI 추천은 그대로 가져오면 안 됩니다.
- 다음 task인 `T-020-architecture-baseline`은 시작 가능하지만, “기존 코드 정리”가 아니라 “새 로컬 Android 프로젝트 baseline 생성 + 참고 구조 선별 이식”으로 수행해야 합니다.
