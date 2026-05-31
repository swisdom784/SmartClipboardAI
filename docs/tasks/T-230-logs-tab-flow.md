# T-230 사용자 확인 기록 Logs 탭

## 목적

사용자가 눈으로 확인한 작업 기록만 Logs 탭에 남기고, badge/status/source로 필터링할 수 있게 합니다.

## 작업 상태

- Status: Ready
- Owner: 미배정
- Branch: `feat/T-230-logs-tab-flow`
- Depends on: `T-040-navigation-baseline`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 사용자-visible log 저장 기준과 route가 확정됨
- Can run in parallel with: `T-200`, `T-210`, `T-240`
- Cannot run with: repository log 계약 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/logs/`
- log query/use case
- Logs ViewModel tests
- `docs/tasks/T-230-logs-tab-flow.md`

## 수정 금지 파일

- 내부 자동 이벤트 전체 기록 구현
- Home/Inbox UI 세부 변경
- Data model 계약 변경
- 외부 앱 export 구현

## 구현 내용

- 사용자 요청, AI 추천 수락, Topic 생성, 초안 생성, 외부 앱 전송, 완료/미완료 기록을 보여줍니다.
- OCR/OG/Gemini 내부 자동 이벤트는 기본 Logs에 보여주지 않습니다.
- badge 필터를 제공합니다.
- 완료/미완료 작업을 구분합니다.

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

- 사용자가 확인한 기록만 표시됩니다.
- badge 필터가 동작합니다.
- 기록을 눌러 관련 Topic/Action으로 이동할 수 있습니다.

## 검증 방법

- fake log data UI 테스트
- badge 필터 수동 확인
- 완료/미완료 기록 표시 확인

## PR에 반드시 적을 내용

- 기록에 남기는 이벤트 기준
- 기록하지 않는 이벤트 기준
- 필터 종류
- UI 스크린샷 또는 동작 설명
