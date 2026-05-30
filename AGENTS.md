# SmartClipboard AGENTS.md

이 문서는 SmartClipboard 프로젝트에서 Codex와 모든 작업자가 반드시 따르는 협업 규칙입니다. 개인 Codex 세션의 기억보다 이 문서와 `docs/` 문서를 우선합니다.

## 기본 원칙

- 모든 설명, 작업 요약, PR 설명은 한국어 중심으로 작성합니다.
- Android Studio + Kotlin + Jetpack Compose 기준을 유지합니다.
- 초기 Git bootstrap 이후에는 `main` 브랜치에서 직접 기능 구현을 하지 않습니다.
- 기능별 브랜치에서만 작업합니다.
- Codex는 승인된 `Ready` task에 대해 로컬 브랜치 생성, commit, branch 전환을 관리할 수 있습니다.
- 원격 GitHub 연결 또는 push는 remote URL과 인증 상태가 준비된 경우에만 진행합니다.
- 승인되지 않은 task를 시작하지 않습니다.
- 현재 task 범위 밖 파일을 수정하지 않습니다.
- 공통 파일 수정이 필요하면 먼저 이유와 영향 범위를 설명하고 사용자 또는 프로젝트 오너에게 승인 요청합니다.
- 요청 범위 밖 대규모 리팩토링을 하지 않습니다.
- 완료된 task 또는 다른 사람이 담당한 task를 새 승인 없이 수정하지 않습니다.
- 전체 `docs/IMPLEMENTATION_PLAN.md`의 완료 체크는 프로젝트 오너가 관리합니다.
- 각 작업자는 자기 task 문서의 체크리스트만 갱신합니다.
- 승인된 task 작업이 끝나면 Codex가 검증 결과와 함께 로컬 commit을 만들 수 있습니다.
- 구현 후 변경 파일, 테스트 결과, 남은 이슈, PR 작성 내용을 정리합니다.
- task의 완료 기준을 만족하면 멈춥니다. Codex가 임의로 새 일을 만들어 이어서 진행하지 않습니다.

## 작업 전 반드시 읽을 문서

작업자는 구현 또는 문서 변경 전 아래 문서를 읽습니다.

1. `README.md`가 있으면 먼저 읽습니다.
2. `docs/PROJECT_SPEC.md`
3. `docs/ARCHITECTURE.md`
4. `docs/DATA_COLLECTION_STRATEGY.md`
5. `docs/UX_FLOW.md`
6. `docs/IMPLEMENTATION_PLAN.md`
7. `docs/BRANCH_RULES.md`
8. 담당 `docs/tasks/T-*.md`

## 금지 구현

아래 구현은 기술적으로 부적절하거나 Android 정책 리스크가 있으므로 금지합니다.

- 백그라운드 클립보드 지속 감시
- 클립보드 히스토리 전체 접근 시도
- 화면 내용 자동 감시
- 접근성 API를 통한 무단 수집
- 다른 앱의 공유 흐름 자동 가로채기
- 사용자 검토 없이 AI가 외부 앱에 일정, 노트, 리마인더를 바로 생성
- Gemini API key 등 비밀값을 소스 코드나 GitHub에 하드코딩

## 작업 의존성 규칙

- Status가 `Ready`인 작업만 추천합니다.
- `Not Ready`, `Blocked`, `In Progress`, `Done` 상태의 작업은 시작하지 않습니다.
- `Depends on`에 적힌 선행 작업이 끝나지 않은 경우, 왜 지금 시작할 수 없는지 설명합니다.
- 의존성을 무시하고 구현을 진행하지 않습니다.
- 모든 관련 작업이 막혀 있다면 새 작업을 임의로 만들지 말고 blocker를 보고합니다.
- 사용자가 명시적으로 요청하지 않는 한 새 task를 만들지 않습니다.
- Git 브랜치는 `docs/BRANCH_RULES.md`의 task 단위 규칙을 따릅니다.
- 사용자가 “다음에 뭐 해야 해?”라고 물으면 다음을 구분해서 답합니다.
  1. 지금 가능한 작업
  2. 아직 시작하면 안 되는 작업과 이유
  3. 추천하는 다음 작업

## 공통 파일 보호 규칙

아래 영역은 충돌 위험이 높으므로 task 문서에 명시되지 않으면 수정하지 않습니다.

- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/.../domain/model/`
- `app/src/main/java/.../data/model/`
- `app/src/main/java/.../data/source/local/`
- `app/src/main/java/.../domain/repository/`
- `app/src/main/java/.../data/repository/`
- navigation 관련 파일
- `app/src/main/java/.../ui/theme/`
- `AGENTS.md`
- `docs/IMPLEMENTATION_PLAN.md`
- `docs/ARCHITECTURE.md`

## 작업 완료 보고 형식

작업 후 아래 내용을 정리합니다.

- task id
- 작업 브랜치
- 변경 파일 목록
- 테스트/빌드 결과
- 수동 확인 결과
- 남은 이슈
- 범위 밖 수정 여부
- PR에 적을 요약
