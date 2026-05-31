# T-200 ChatGPT/Codex 스타일 Home UX 재설계

## 목적

Home을 자료 목록 중심이 아니라 새 작업 입력, 이전 작업, 이번 AI 추천, 자동 수집/분석 상태 중심으로 재설계합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-200-home-ux-redesign`
- Depends on: `T-040-navigation-baseline`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-210`, `T-230`, `T-240`
- Cannot run with: `T-040`

## 수정 허용 파일

- `app/src/main/java/.../presentation/home/`
- Home ViewModel
- Home Compose previews/tests
- `docs/tasks/T-200-home-ux-redesign.md`

## 수정 금지 파일

- Navigation route 변경
- Repository 계약 변경
- DataItem/Topic model 변경
- Inbox/Logs/Settings 화면 구현

## 구현 내용

- 새 작업 입력 영역을 만듭니다.
- 이전 작업 리스트를 단순하게 표시합니다.
- 이번 실행에서 새로 나온 AI 추천만 표시합니다.
- 자동 수집/분석 상태 카드를 작게 둡니다.
- 최근 자료 요약은 접을 수 있는 패널로 둡니다.

T-200 구현 결과:

- `HomeUiState`, `HomeTaskItem`, `HomeCollectionSummary`, `HomeMaterialItem`으로 Home 전용 UI 상태를 분리했습니다.
- `HomeUiStateMapper`가 현재 추천 세션, 기존 Topic, 최근 자료를 작업 카드와 접을 수 있는 자료 요약으로 변환합니다.
- `HomeViewModel`이 `DataRepository.observeHomeState()`를 구독하고, Home 진입 시 이번 실행 추천 refresh를 한 번 요청합니다.
- `HomeScreen`은 새 작업 입력, 자동 정리 상태, 작업 리스트, 접이식 최근 자료 패널을 단순한 카드/행 구조로 표시합니다.
- 완료 작업 카드는 더 낮은 세로 높이로 표시되며, 추천/사용자 요청/진행 중/검토 필요/완료 배지를 구분합니다.
- 기존 `HomeShellScreen`은 route 변경 없이 새 `HomeRoute`를 호출하도록만 연결했습니다.

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

- Home이 단순하고 작업 중심으로 보입니다.
- AI 추천은 과거 기록이 아니라 이번 실행 추천만 노출됩니다.
- badge가 `사용자 요청`, `AI 추천`, `진행 중`, `검토 필요`, `완료`를 구분합니다.

## 검증 방법

- Compose preview 확인
- fake Home state ViewModel 테스트
- 좁은 화면/큰 화면 수동 확인

실제 검증:

- `HomeUiStateMapperTest`로 추천/Topic badge, 이번 추천 노출 조건, 자료 요약 문자열을 검증했습니다.
- `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.home.HomeUiStateMapperTest` 성공.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- Home 정보 구조
- badge 상태
- 자동 수집 상태 표시 방식
- UI 스크린샷 또는 동작 설명
