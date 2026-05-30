# T-110 Quick Settings Tile + TransparentActivity 최근 클립보드 수집

## 목적

사용자가 Quick Settings Tile을 눌렀을 때 투명 Activity가 포커스를 얻고, Android가 허용하는 최신 Primary Clip 1개를 저장합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-110-quick-tile-flow`
- Depends on: `T-050-permission-and-manifest-baseline`, `T-040-navigation-baseline`, `T-100-share-target-flow`
- Blocked by: 없음
- Ready criteria: TileService/TransparentActivity 선언, clipboard 저장 API, 앱 root navigation 기준, 공통 저장 결과 처리 패턴이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-050`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../collection/clipboard/`
- `TileService`
- `TransparentActivity`
- clipboard flow tests
- `docs/tasks/T-110-quick-tile-flow.md`

## 수정 금지 파일

- 백그라운드 클립보드 감시 구현
- 클립보드 히스토리 접근 시도
- Manifest 변경
- DataItem/Repository 계약 변경

## 구현 내용

- Tile 클릭 시 TransparentActivity를 실행합니다.
- Activity foreground 상태에서 ClipboardManager의 Primary Clip을 읽습니다.
- 텍스트/링크만 저장하고, 비어 있거나 지원하지 않는 clip은 짧게 안내합니다.
- 저장 후 닫히는 흐름과 feedback hook을 연결합니다.

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

## T-110 결과 요약

- `ClipboardCaptureTileService`에서 Tile 클릭 시 `ClipboardCaptureActivity`를 열고 Quick Settings 패널을 접습니다.
- Android 14 이상에서는 `PendingIntent` 기반 `startActivityAndCollapse`를 사용하고, 이전 버전에서는 기존 Intent overload를 사용합니다.
- `ClipboardCaptureActivity`는 window focus를 얻은 뒤 Primary Clip을 한 번만 읽고 종료합니다.
- `ClipboardCaptureHandler`는 텍스트/링크만 `DataItemSource.CLIPBOARD_TILE`로 저장합니다.
- 빈 클립보드, 미지원 clip, 저장 실패를 별도 결과로 처리합니다.
- 백그라운드 감시, 클립보드 히스토리 접근, Manifest/DataItem/Repository 계약 변경은 하지 않았습니다.

## 완료 기준

- Tile 클릭으로 최신 클립보드 텍스트/링크가 저장됩니다.
- 백그라운드 감시처럼 동작하지 않습니다.
- 실패 시 사용자에게 조용한 안내를 제공합니다.

## 검증 방법

- 실제 기기 또는 에뮬레이터에서 Tile 추가
- 텍스트 복사 후 Tile 클릭
- 링크 복사 후 Tile 클릭
- 빈 클립보드 또는 미지원 clip 확인

## PR에 반드시 적을 내용

- Android 버전별 확인 결과
- 백그라운드 감시가 아님을 보장하는 구조
- 실패 UX 문구
- 수정한 공통 파일 여부
