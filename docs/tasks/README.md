# Task 문서 사용법

이 폴더의 문서는 작업자가 실제 구현 전에 확인해야 하는 task 단위 기준입니다.

## 기본 규칙

- `Status: Ready`인 문서만 시작합니다.
- `Not Ready`, `Blocked`, `In Progress`, `Done` 문서는 시작하지 않습니다.
- 각 작업자는 자기 task 문서의 체크리스트만 수정합니다.
- `docs/IMPLEMENTATION_PLAN.md`의 전체 상태 변경은 프로젝트 오너가 관리합니다.
- 공통 파일 수정이 필요하면 먼저 이유와 영향 범위를 설명하고 승인을 받습니다.
- Git/브랜치/commit/push는 프로젝트 오너가 Codex에게 위임한 범위 안에서 수행합니다.

## Ready 작업 찾기

팀원이 “나 뭐 해야 해?”라고 물으면 Codex는 아래 순서로 답합니다.

1. `docs/IMPLEMENTATION_PLAN.md`에서 `Status: Ready`인 task만 찾습니다.
2. Ready task의 `Depends on`이 모두 `Done`인지 확인합니다.
3. Ready task가 없으면 새 task를 만들지 않고 blocker를 보고합니다.
4. Ready task가 여러 개면 충돌 가능성이 낮고 병렬 가능한 작업을 먼저 추천합니다.

## 현재 Ready 작업

현재 Ready 작업은 아래 1개입니다.

- `T-930-topic-selection-large-library-ux`

`T-920-gemini-recommendation-analysis-e2e`는 유효한 Gemini key가 필요합니다. `T-940-device-manual-qa-suite`는 `T-930` 완료와 ADB 연결 기기가 필요합니다.
