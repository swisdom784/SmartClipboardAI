# T-210 Inbox 자료 모음, 필터, 선택 UX

## 목적

수집된 이미지, 링크, 텍스트, 파일을 사용자가 필요할 때 확인하고 삭제/중요 표시/Topic 추가할 수 있는 Inbox를 만듭니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-210-data-list-filter-selection`
- Depends on: `T-040-navigation-baseline`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-200`, `T-230`, `T-240`
- Cannot run with: `T-040`, repository query 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/inbox/`
- Inbox ViewModel
- Inbox UI tests
- `docs/tasks/T-210-data-list-filter-selection.md`

## 수정 금지 파일

- DataItem model/DAO 계약 변경
- MediaStore/Share 수집 구현
- TopicAction/AI 분석 구현
- Home UI 세부 구현

## 구현 내용

- Inbox 첫 화면에 고정 카테고리 카드를 둡니다.
- 이미지, 스크린샷, 다운로드 이미지, 공유 링크, 공유 텍스트, 파일/PDF, 중요, 미분석, 최근 수집 그룹을 제공합니다.
- 카테고리 안에서는 list/grid 전환을 아주 작은 버튼으로 제공합니다.
- 삭제, 중요 표시, Topic 추가 entry를 둡니다.

T-210 구현 결과:

- `InboxUiState`, `InboxCategoryItem`, `InboxDataItem`으로 Inbox 전용 UI 상태를 분리했습니다.
- 고정 카테고리는 최근, 이미지, 링크, 텍스트, 파일, 중요, 미분석 순서로 제공합니다.
- 이미지 카테고리는 `IMAGE`, `SCREENSHOT`, `DOWNLOAD_IMAGE`를 함께 묶습니다.
- `InboxViewModel`은 `DataRepository.observeInboxItems()`를 구독하고, 카테고리 선택과 리스트/그리드 전환 상태를 관리합니다.
- 중요 표시는 기존 `getDataItem` + `saveDataItem` 경로로 `isImportant`를 토글합니다.
- 삭제는 기존 `getDataItem` + `saveDataItem` 경로로 `deletedAtMillis`를 기록해 soft-delete 처리합니다.
- Topic 추가 entry는 UI 진입점만 제공합니다. 실제 Topic 연결은 `T-300`, `T-310` 범위입니다.
- 기존 `InboxShellScreen`은 route 변경 없이 새 `InboxRoute`를 호출하도록 연결했습니다.

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

- 카테고리 카드와 세부 자료 목록이 동작합니다.
- list/grid 전환이 layout shift 없이 동작합니다.
- 자료 삭제와 중요 표시가 repository를 통해 반영됩니다.

## 검증 방법

- fake DataItem UI 테스트
- list/grid 전환 수동 확인
- 삭제/중요 표시 수동 확인
- 긴 텍스트/작은 화면 레이아웃 확인

실제 검증:

- `InboxUiStateMapperTest`로 고정 카테고리 순서/개수, 링크 필터, grid mode 유지, item title/meta 변환을 검증했습니다.
- `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.inbox.InboxUiStateMapperTest` 성공.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- 카테고리 기준
- 선택/삭제/중요 표시 동작
- list/grid 전환 방식
- UI 스크린샷 또는 동작 설명
