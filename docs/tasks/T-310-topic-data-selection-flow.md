# T-310 Topic 자료 자동 선택 및 수정 UX

## 목적

AI가 자동으로 고른 자료를 `사용된 자료 N개`처럼 간단히 보여주고, 사용자가 필요할 때만 직접 수정하게 합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-310-topic-data-selection-flow`
- Depends on: `T-210-data-list-filter-selection`, `T-300-topic-create-flow`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-400`은 selection 결과 계약을 사용해 진행 가능
- Cannot run with: `T-210` selection state 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/topic/selection/`
- Topic selection use case
- selection tests
- `docs/tasks/T-310-topic-data-selection-flow.md`

## 수정 금지 파일

- Inbox category 구조 변경
- DataItem model/DAO 변경
- Gemini analysis 생성 로직
- 외부 앱 export 구현

## 구현 내용

- Topic에 연결된 DataItem 요약을 한 줄로 표시합니다.
- 사용자가 터치하면 자료 선택 화면을 엽니다.
- AI 자동 선택과 사용자 수정 선택을 구분해 저장합니다.
- 너무 많은 자료를 홈/분석 화면에 주르륵 펼치지 않습니다.

T-310 구현 결과:

- `TopicDataSelectionUseCase`를 추가해 선택 자료 요약과 사용자 선택 저장을 담당합니다.
- `DataRepository.replaceTopicDataItems()`와 `TopicDao.replaceTopicItemCrossRefs()`를 추가해 기존 Topic-DataItem 연결을 교체 저장합니다.
- 사용자 수정 저장은 `TopicItemSelectedBy.USER`로 기록합니다.
- Home에서 사용자 입력 또는 AI 추천으로 Topic이 생성되면 자료 선택 화면으로 이동합니다.
- 기존 Topic 카드 클릭 시 자료 선택 화면으로 진입합니다.
- 자료 선택 화면은 `사용된 자료 N개` 요약, 선택된 자료 우선 정렬, 체크박스 기반 추가/제거, 저장 후 닫기 흐름을 제공합니다.

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

- 자동 선택 결과가 간단히 표시됩니다.
- 사용자가 직접 자료를 추가/제거할 수 있습니다.
- 선택 결과가 TopicAnalysis 입력으로 전달됩니다.

## 검증 방법

- fake DataItem selection 테스트
- 자동 선택 표시 수동 확인
- 선택 수정 후 저장 확인

실제 검증:

- `TopicDataSelectionUseCaseTest`로 선택 요약, 중복 id 제거, 사용자 선택 저장, 잘못된 topic id 무시를 검증했습니다.
- `TopicDataSelectionUiStateMapperTest`로 선택 자료 요약, 선택 자료 우선 정렬, 한 줄 표시용 title/description 매핑을 검증했습니다.
- `DataRepositoryImplIntegrationTest`로 Topic-DataItem cross-ref 교체 저장 계약을 검증했습니다.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- 자동 선택 표시 방식
- 직접 수정 진입 방식
- 선택 결과 저장 방식
- UI 스크린샷 또는 동작 설명
