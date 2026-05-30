# T-010 협업 기준 문서와 task 문서 생성

## 목적

여러 개발자가 GitHub 기반으로 병렬 협업할 수 있도록 AGENTS, 명세, 아키텍처, 수집 전략, UX, 구현 계획, 브랜치 규칙, task 상태 규칙, PR 템플릿을 생성합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `docs/T-010-agents-and-docs-setup`
- Depends on: 없음
- Blocked by: 없음
- Ready criteria: 사용자 요청으로 writing plans 단계 진입
- Can run in parallel with: 없음
- Cannot run with: 전체 문서 구조를 동시에 바꾸는 작업

## 수정 허용 파일

- `AGENTS.md`
- `docs/PROJECT_SPEC.md`
- `docs/ARCHITECTURE.md`
- `docs/DATA_COLLECTION_STRATEGY.md`
- `docs/UX_FLOW.md`
- `docs/IMPLEMENTATION_PLAN.md`
- `docs/BRANCH_RULES.md`
- `docs/WORK_LOG.md`
- `docs/TASK_STATUS_GUIDE.md`
- `docs/tasks/*.md`
- `.github/pull_request_template.md`

## 수정 금지 파일

- 앱 소스 코드 전체
- Gradle 파일
- Android Manifest

## 구현 내용

- 프로젝트 기준 문서를 한국어 중심으로 작성합니다.
- Ready task만 추천할 수 있는 상태 규칙을 문서화합니다.
- 기능별 브랜치/PR 단위로 작업이 나뉘도록 task 문서를 생성합니다.
- 공통 파일 충돌 위험과 승인 규칙을 명시합니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- 필수 문서가 모두 존재합니다.
- 각 task에 상태, 브랜치, 의존성, 수정 범위, 완료 기준이 있습니다.
- `T-000-current-code-audit`이 첫 Ready task로 명확합니다.

## 검증 방법

- 필수 파일 경로 존재 확인
- `Status: Ready` task가 의도한 만큼만 있는지 확인
- 앱 코드 변경 없음 확인

## PR에 반드시 적을 내용

- 생성한 문서 목록
- 첫 Ready task
- commit/push 없이 문서만 작성했다는 점
