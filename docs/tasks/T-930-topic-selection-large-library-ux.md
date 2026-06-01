# T-930 대량 자료 선택 UX 안정화

## 목적

1만 개 이상 MediaStore 자료가 있어도 Topic 자료 선택 화면에서 원하는 자료를 찾고, 선택 저장 버튼에 쉽게 접근해 분석으로 이동할 수 있게 한다.

## 작업 상태

- Status: Ready
- Owner: Unassigned
- Branch: `feat/T-930-topic-selection-large-library-ux`
- Depends on: `T-900`
- Blocked by: 없음
- Ready criteria: `QA-006` 재현 완료
- Can run in parallel with: `T-910-gemini-key-diagnostics`
- Cannot run with: Topic selection model/repository contract 변경 작업

## 수정 허용 파일

- `app/src/main/java/com/smartclipboard/ai/presentation/topic/selection/**`
- `app/src/test/java/com/smartclipboard/ai/presentation/topic/selection/**`
- `docs/QA_REPORT.md`
- `docs/WORK_LOG.md`

## 수정 금지 파일

- DataItem/Topic DB schema
- Repository contract 대규모 변경
- Gemini analysis flow
- Samsung export flow

## 구현 내용

- 선택 저장 버튼을 긴 목록 아래에만 두지 않고 sticky bottom action 또는 상단 action으로 접근 가능하게 한다.
- 최근 자료, 공유 자료, 이미지, 링크, 텍스트, 파일 필터를 제공한다.
- 대량 자료에서 현재 표시되는 자료 수와 전체 자료 수를 사용자에게 간단히 보여준다.
- 200개 제한 정책을 유지한다면 “최근 200개 표시” 같은 기준을 UI/문서에 명확히 한다.
- 선택된 자료가 현재 필터/표시 제한 밖에 있어도 summary와 저장 대상에서 사라지지 않게 한다.

## 체크리스트

- [ ] 코드 읽기
- [ ] 관련 문서 확인
- [ ] 선행 task 완료 여부 확인
- [ ] 대량 자료 UX 실패 테스트 작성
- [ ] sticky action 또는 접근 가능한 저장 UX 구현
- [ ] 필터/검색 상태 테스트 작성
- [ ] 구현
- [ ] 빌드 확인
- [ ] 테스트/수동 확인
- [ ] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- 대량 자료 환경에서 화면이 OOM 없이 열린다.
- 저장 버튼에 스크롤 끝까지 내리지 않고 접근할 수 있다.
- 사용자가 표시 중인 자료 범위를 이해할 수 있다.
- 선택된 자료가 표시 제한 때문에 저장 대상에서 유실되지 않는다.

## 검증 방법

- `.\gradlew.bat testDebugUnitTest --console=plain`
- `.\gradlew.bat assembleDebug --console=plain`
- 1만 개 이상 data_items가 있는 실기기에서 Topic 자료 선택 화면 smoke test

## PR에 반드시 적을 내용

- 관련 QA issue: `QA-006`
- 대량 자료 수
- 저장 버튼 접근성 확인 방식
- 필터/표시 제한 정책
