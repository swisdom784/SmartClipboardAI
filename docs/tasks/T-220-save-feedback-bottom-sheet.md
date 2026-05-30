# T-220 저장 피드백 Toast/BottomSheet UX

## 목적

Share Target과 Quick Tile 저장 후 앱 전체가 켜진 것처럼 보이지 않도록 짧고 단정한 피드백을 제공합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-220-save-feedback-bottom-sheet`
- Depends on: `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`
- Blocked by: `T-120` 미완료
- Ready criteria: Share/Tile/MediaStore 저장 결과 이벤트가 전달됨
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

- Share 저장 후 피드백이 1~2초 내 표시됩니다.
- Tile 저장 후 피드백이 조용히 표시됩니다.
- 오류 문구가 OpenAI/Samsung 스타일의 단정한 톤을 유지합니다.

## 검증 방법

- 링크 공유 수동 확인
- 텍스트 공유 수동 확인
- Tile 저장 수동 확인
- 실패 상태 fake 테스트

## PR에 반드시 적을 내용

- 사용자-facing 문구
- 자동 종료 타이밍
- 실패/부분 실패 UX
- 스크린샷 또는 동작 설명
