# T-410 TopicAction 초안 생성 및 검토 상태

## 목적

TopicAnalysis를 바탕으로 Notes/Calendar/Reminder 카드에 표시할 실행 초안을 생성하고, 사용자가 수정/전송/완료 상태를 관리할 수 있게 합니다.

## 작업 상태

- Status: Ready
- Owner: 미배정
- Branch: `feat/T-410-topic-action-draft`
- Depends on: `T-400-topic-analysis-draft`
- Blocked by: 없음
- Ready criteria: TopicAnalysis 저장과 Action payload 모델이 준비됨
- Can run in parallel with: `T-500`, `T-510`, `T-520`은 payload contract freeze 후 가능
- Cannot run with: TopicAction model 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/analysis/action/`
- TopicAction use case/ViewModel
- action draft tests
- `docs/tasks/T-410-topic-action-draft.md`

## 수정 금지 파일

- Samsung intent launcher 세부 구현
- Gemini key 관리
- TopicAnalysis 생성 로직 변경
- Navigation route 변경

## 구현 내용

- Notes 카드에는 Summary payload를 표시합니다.
- Calendar 카드에는 일정 payload를 표시합니다.
- Reminder 카드에는 TODO payload를 표시합니다.
- 각 카드 우상단에 작은 회색 `수정` 액션을 둡니다.
- 전송 완료된 카드는 높이가 줄어든 완료 상태로 전환합니다.
- 뒤로가기 시 완료/미완료 유지/취소 선택 흐름을 제공합니다.

## 체크리스트

- [ ] 코드 읽기
- [ ] 관련 문서 확인
- [ ] 선행 task 완료 여부 확인
- [ ] 구현
- [ ] 빌드 확인
- [ ] 테스트/수동 확인
- [ ] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- 카드 preview가 길면 축약됩니다.
- 수정 화면에서 full content를 바꿀 수 있습니다.
- 모든 필수 카드 완료 시 작업이 자동 완료됩니다.
- 즉시 완료가 가능합니다.

## 검증 방법

- action state ViewModel 테스트
- 카드 collapse 수동 확인
- 뒤로가기 dialog 수동 확인
- 작은 화면 레이아웃 확인

## PR에 반드시 적을 내용

- 카드 상태 모델
- 수정 액션 방식
- 완료/미완료 처리
- UI 스크린샷 또는 동작 설명
