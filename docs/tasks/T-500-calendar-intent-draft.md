# T-500 Samsung Calendar intent 전송

## 목적

사용자가 검토한 일정 초안을 Samsung Calendar 또는 기본 Calendar insert intent로 보냅니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-500-calendar-intent-draft`
- Depends on: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 완료됨
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

T-500 구현 결과:

- `SamsungCalendarInsertIntentFactory`를 추가해 `ACTION_INSERT`와 Calendar Events URI 기반 insert spec을 생성합니다.
- title, description, location, begin/end time, all-day 값을 Calendar insert extra로 전달합니다.
- `SamsungCalendarPackagePolicy`를 추가해 `com.samsung.android.calendar`를 우선하고, 없으면 `com.samsung.android.app.calendar`를 사용합니다.
- Samsung Calendar package가 없으면 package를 지정하지 않은 기본 Calendar insert intent로 fallback합니다.
- begin/end 시간이 없거나 end가 start보다 빠르면 전송하지 않고 짧은 Toast로 안내합니다.
- 분석 화면의 Calendar 카드에만 `캘린더로 보내기` 버튼을 표시합니다.
- 전송 시작 후 action 상태를 `EXPORTED`로 바꾸고 완료 카드처럼 접습니다.
- Manifest query는 `T-050`에서 이미 추가된 Samsung Calendar package query를 그대로 사용했습니다.

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

- 일정 초안 검토 후 Calendar 앱이 열립니다.
- 미설치/실패 케이스가 조용히 안내됩니다.
- 전송 성공 후 카드 상태가 완료로 바뀔 수 있는 event가 반환됩니다.

## 검증 방법

- Samsung Calendar 설치 기기 수동 테스트
- 기본 Calendar fallback 테스트
- invalid time payload 테스트
- intent 생성 단위 테스트

실제 검증:

- `SamsungCalendarInsertIntentFactoryTest`로 action/data/title/description/location/time/all-day/package와 invalid time 처리를 검증했습니다.
- `SamsungCalendarPackagePolicyTest`로 Samsung Calendar package 우선순위와 fallback null 처리를 검증했습니다.
- `TopicActionDraftUiStateMapperTest`로 Calendar 카드 export 가능 여부와 schedule payload 노출을 검증했습니다.
- 전체 빌드/테스트는 `.\gradlew.bat assembleDebug test --console=plain`로 확인했고 성공했습니다.

## PR에 반드시 적을 내용

- 사용한 intent action/extras
- Samsung package 우선순위
- fallback 동작
- 수동 테스트 환경
