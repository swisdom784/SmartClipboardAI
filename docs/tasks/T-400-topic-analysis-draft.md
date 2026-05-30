# T-400 TopicAnalysis 생성 흐름

## 목적

확정된 Topic과 연결 DataItem을 Gemini에 보내 요약, 핵심 포인트, 사용된 자료 근거를 포함한 `TopicAnalysis`를 생성합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-400-topic-analysis-draft`
- Depends on: `T-150-gemini-topic-recommendation`, `T-310-topic-data-selection-flow`
- Blocked by: `T-150`, `T-310` 미완료
- Ready criteria: Gemini manager, Topic/DataItem 연결, selection 결과가 준비됨
- Can run in parallel with: `T-410`은 DTO 계약 합의 후 가능
- Cannot run with: Gemini manager 계약 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../processing/gemini/analysis/`
- `app/src/main/java/.../presentation/analysis/`
- TopicAnalysis tests
- `docs/tasks/T-400-topic-analysis-draft.md`

## 수정 금지 파일

- Gemini key 하드코딩
- TopicAction model 변경
- Samsung export 구현
- Inbox 자료 선택 구조 변경

## 구현 내용

- Topic title, user request, selected DataItems를 분석 입력으로 구성합니다.
- Gemini 응답을 TopicAnalysis로 저장합니다.
- 실패 시 retry 가능한 상태를 남깁니다.
- 사용자가 볼 수 있는 요약과 내부 근거 데이터를 구분합니다.

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

- TopicAnalysis가 생성/저장됩니다.
- 실패/재시도 상태가 UI에서 읽을 수 있게 남습니다.
- 사용된 DataItem ID가 기록됩니다.

## 검증 방법

- fake Gemini analysis 테스트
- 실제 key smoke test
- 실패 응답/timeout 처리 테스트

## PR에 반드시 적을 내용

- Gemini prompt 입력 구조
- TopicAnalysis 저장 구조
- 실패/재시도 처리
- 실제 key 없이 테스트 가능한 방식
