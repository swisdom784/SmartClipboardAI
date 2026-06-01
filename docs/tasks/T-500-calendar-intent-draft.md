# T-500 Samsung Calendar intent 전송

## 목적

사용자가 검토한 일정 초안을 Samsung Calendar 또는 기본 Calendar insert intent로 보냅니다.

## 작업 상태

- Status: Ready
- Owner: 미배정
- Branch: `feat/T-500-calendar-intent-draft`
- Depends on: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: Calendar payload와 Samsung package query가 확정됨
- Can run in parallel with: `T-510`, `T-520`
- Cannot run with: `T-050` Manifest 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../export/calendar/`
- Calendar action card hook
- calendar export tests
- `docs/tasks/T-500-calendar-intent-draft.md`

## 수정 금지 파일

- TopicAction model 변경
- Manifest queries 변경
- Notes/Reminder export 구현
- AI prompt 변경

## 구현 내용

- `Intent.ACTION_INSERT`와 `CalendarContract.Events.CONTENT_URI`를 사용합니다.
- title, description, location, beginTimeMs, endTimeMs, isAllDay를 전달합니다.
- Samsung Calendar package를 우선 사용합니다.
- Samsung Calendar가 없으면 기본 Calendar insert intent로 fallback합니다.
- 사용자가 버튼을 눌렀을 때만 실행합니다.

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

- 일정 초안 검토 후 Calendar 앱이 열립니다.
- 미설치/실패 케이스가 조용히 안내됩니다.
- 전송 성공 후 카드 상태가 완료로 바뀔 수 있는 event가 반환됩니다.

## 검증 방법

- Samsung Calendar 설치 기기 수동 테스트
- 기본 Calendar fallback 테스트
- invalid time payload 테스트
- intent 생성 단위 테스트

## PR에 반드시 적을 내용

- 사용한 intent action/extras
- Samsung package 우선순위
- fallback 동작
- 수동 테스트 환경
