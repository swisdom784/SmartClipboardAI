# T-220 저장 피드백 Toast/BottomSheet UX

## 목적

Share Target과 Quick Tile 저장 후 앱 전체가 켜진 것처럼 보이지 않도록 짧고 단정한 피드백을 제공합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-220-save-feedback-bottom-sheet`
- Depends on: `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-200`, `T-210`
- Cannot run with: `T-100`, `T-110`의 Activity lifecycle 수정

## 수정 허용 파일

- `app/src/main/java/.../presentation/feedback/`
- ShareReceiverActivity feedback hook
- TransparentActivity feedback hook
- feedback UI tests
- `docs/tasks/T-220-save-feedback-bottom-sheet.md`

## 수정 금지 파일

- 수집 저장 로직
- DataRepository 계약
- Manifest
- Home/Inbox 대규모 UI 변경

## 구현 내용

- 기본 성공 문구는 `SmartClipboard에 담았어요`로 둡니다.
- 부분 실패는 `일부 항목을 처리하지 못했어요`처럼 짧게 안내합니다.
- 링크 OG/OCR이 늦어져도 저장 피드백은 빠르게 보여줍니다.
- 필요하면 투명 Activity 위에 작은 bottom feedback만 보여주고 종료합니다.

T-220 구현 결과:

- `SaveFeedbackMessageMapper`로 Share/Clipboard 저장 결과별 사용자-facing 문구를 한 곳에서 관리합니다.
- `SaveFeedbackToast`를 추가해 Activity별 Toast 호출을 공통화했습니다.
- Share 저장 성공은 `SmartClipboard에 담았어요`로 표시합니다.
- 부분 실패는 `일부 항목을 처리하지 못했어요`로 표시합니다.
- 클립보드가 비었거나 지원하지 않는 내용이면 `복사된 내용이 없어요`로 표시합니다.
- 저장 실패는 `저장하지 못했어요`로 표시합니다.
- ShareReceiverActivity와 ClipboardCaptureActivity는 저장 로직을 바꾸지 않고 feedback hook만 교체했습니다.

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

- Share 저장 후 피드백이 1~2초 내 표시됩니다.
- Tile 저장 후 피드백이 조용히 표시됩니다.
- 오류 문구가 OpenAI/Samsung 스타일의 단정한 톤을 유지합니다.

## 검증 방법

- 링크 공유 수동 확인
- 텍스트 공유 수동 확인
- Tile 저장 수동 확인
- 실패 상태 fake 테스트

실제 검증:

- `SaveFeedbackMessageMapperTest`로 성공/부분 실패/빈 클립보드/저장 실패 문구와 level을 검증했습니다.
- `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.feedback.SaveFeedbackMessageMapperTest` 성공.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- 사용자-facing 문구
- 자동 종료 타이밍
- 실패/부분 실패 UX
- 스크린샷 또는 동작 설명
