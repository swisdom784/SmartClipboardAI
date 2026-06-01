# T-400 TopicAnalysis 생성 흐름

## 목적

확정된 Topic과 연결 DataItem을 Gemini에 보내 요약, 핵심 포인트, 사용된 자료 근거를 포함한 `TopicAnalysis`를 생성합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-400-topic-analysis-draft`
- Depends on: `T-150-gemini-topic-recommendation`, `T-310-topic-data-selection-flow`
- Blocked by: 없음
- Ready criteria: 완료됨
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

T-400 구현 결과:

- `TopicAnalysisUseCase`를 추가해 Topic과 연결 DataItem을 분석 입력으로 구성하고 `TopicAnalysis`를 저장합니다.
- 분석 시작 시 `RUNNING`, 성공 시 `DONE`, 실패 시 `FAILED` 상태를 저장합니다.
- `GeminiTopicAnalysisPromptBuilder`, `GeminiTopicAnalysisParser`, `GeminiTopicAnalysisGenerator`를 추가해 Gemini 분석 전용 JSON 입출력을 분리했습니다.
- 분석 evidence는 기존 `TopicAnalysis.evidence`에 `dataItemId=...` 형식으로 기록합니다.
- 자료 선택 저장 후 분석 화면으로 이어지고, 화면 진입 시 분석을 자동 시작합니다.
- 실패 상태에서는 사용자가 `다시 분석`을 누를 수 있습니다.

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

- TopicAnalysis가 생성/저장됩니다.
- 실패/재시도 상태가 UI에서 읽을 수 있게 남습니다.
- 사용된 DataItem ID가 기록됩니다.

## 검증 방법

- fake Gemini analysis 테스트
- 실제 key smoke test
- 실패 응답/timeout 처리 테스트

실제 검증:

- `TopicAnalysisUseCaseTest`로 RUNNING -> DONE 저장, 실패 시 RUNNING -> FAILED 저장을 검증했습니다.
- `GeminiTopicAnalysisParserTest`로 Gemini JSON 응답의 summary/evidence 파싱을 검증했습니다.
- `GeminiTopicAnalysisGeneratorTest`로 API key 전달, prompt 전달, key 없음 오류를 검증했습니다.
- `TopicAnalysisUiStateMapperTest`로 DONE/FAILED 상태 표시를 검증했습니다.
- 실제 Gemini key smoke test는 네트워크/환경 의존이 있어 단위 테스트에서는 fake client로 대체했습니다.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- Gemini prompt 입력 구조
- TopicAnalysis 저장 구조
- 실패/재시도 처리
- 실제 key 없이 테스트 가능한 방식
