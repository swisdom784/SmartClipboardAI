# T-050 권한, Manifest, Android component 기준 정리

## 목적

Share Target, Quick Settings Tile, Transparent Activity, MediaStore/SAF, Samsung app queries에 필요한 Manifest와 권한 기준을 안전하게 확정합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `chore/T-050-permission-and-manifest-baseline`
- Depends on: `T-020-architecture-baseline`, `T-030-data-model-audit`
- Blocked by: 없음
- Ready criteria: component 패키지 위치와 DataRepository 계약이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-100`, `T-110`, `T-120`, `T-130`, `T-500`, `T-510`, `T-520`

## 수정 허용 파일

- `app/src/main/AndroidManifest.xml`
- 권한 helper 파일
- component skeleton 파일
- `docs/DATA_COLLECTION_STRATEGY.md`
- `docs/tasks/T-050-permission-and-manifest-baseline.md`

## 수정 금지 파일

- 수집 저장 세부 구현
- 화면 완성 UI
- Gemini/OCR/OG 구현
- Data model 계약 변경

## 구현 내용

- ShareReceiverActivity intent-filter를 선언합니다.
- TileService와 TransparentActivity 선언을 정리합니다.
- API 버전별 이미지 읽기 권한 전략을 정리합니다.
- Samsung Notes/Calendar/Reminder package query를 추가합니다.
- 금지 구현으로 보일 수 있는 accessibility/screen monitoring 권한은 추가하지 않습니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [x] PR 작성 내용 정리

> 현재 작업공간은 아직 Git repository가 아니며, 사용자 승인 전 commit/push/PR 생성 금지 조건이 있으므로 실제 PR은 만들지 않았습니다.

## 완료 기준

- Manifest merge가 성공합니다.
- Share target/Tile/queries가 후속 task에서 사용할 수 있게 선언됩니다.
- 불필요하거나 정책 위험이 있는 권한이 없습니다.

## T-050 결과 요약

- `AndroidManifest.xml`에 Share Target, Quick Settings Tile, Clipboard transparent Activity, Samsung app queries를 추가했습니다.
- 이미지 수집 권한은 API 버전별로 `READ_MEDIA_IMAGES`, `READ_MEDIA_VISUAL_USER_SELECTED`, `READ_EXTERNAL_STORAGE(maxSdkVersion=32)` 기준을 명시했습니다.
- `READ_CALENDAR`, `WRITE_CALENDAR`, `QUERY_ALL_PACKAGES`, accessibility 관련 권한은 추가하지 않았습니다.
- `ShareReceiverActivity`, `ClipboardCaptureActivity`, `ClipboardCaptureTileService`는 후속 task가 구현할 skeleton으로만 추가했습니다.
- `MediaPermissionPolicy`와 단위 테스트로 이미지 권한 분기 기준을 고정했습니다.

## 검증 방법

- Android Studio manifest merged view 확인
- 앱 설치 확인
- Share target 표시 여부와 Tile 등록 가능 여부 수동 확인
- `.\gradlew.bat testDebugUnitTest`
- `.\gradlew.bat assembleDebug test`

## PR에 반드시 적을 내용

- 추가한 권한과 이유
- 추가한 component와 이유
- Samsung package queries
- 정책상 금지 구현이 없다는 확인
