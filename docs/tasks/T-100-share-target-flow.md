# T-100 Share Target 기반 링크/텍스트/이미지/파일 수신

## 목적

다른 앱의 Share Sheet에서 SmartClipboard를 선택했을 때 텍스트, 링크, 이미지, 파일 URI를 `DataItem`으로 저장합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-100-share-target-flow`
- Depends on: `T-050-permission-and-manifest-baseline`, `T-040-navigation-baseline`
- Blocked by: 없음
- Ready criteria: Manifest share target, DataRepository 저장 API, 앱 root navigation 기준이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-050`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../collection/share/`
- `ShareReceiverActivity`
- `app/src/main/java/.../di/`
- `app/src/main/res/values/strings.xml`
- share flow tests
- `docs/tasks/T-100-share-target-flow.md`

## 수정 금지 파일

- DataItem/Repository 계약 변경
- Manifest 변경
- Home/Inbox UI 대규모 변경
- AI 분석 로직

## 구현 내용

- `ACTION_SEND`, `ACTION_SEND_MULTIPLE` 입력을 분기합니다.
- 텍스트/URL/이미지 URI/file URI를 DataItem으로 변환합니다.
- 링크는 OG 추출 파이프라인으로 넘길 수 있는 상태를 남깁니다.
- 저장 성공/부분 실패/실패 결과를 feedback task가 사용할 수 있게 반환합니다.

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

## T-100 결과 요약

- `SharePayload`, `SharedUri`, `ShareSaveResult`로 공유 입력과 저장 결과를 분리했습니다.
- `ShareContentHandler`에서 텍스트, URL, 이미지 URI, 파일 URI를 `DataItem`으로 변환해 저장합니다.
- 중복 URI/텍스트는 한 번만 저장하고, 중복이 섞인 경우 `PartialSuccess`를 반환합니다.
- `ShareIntentReader`에서 `ACTION_SEND`, `ACTION_SEND_MULTIPLE`, `EXTRA_TEXT`, `EXTRA_STREAM`을 읽습니다.
- `ShareReceiverActivity`는 투명 Activity 상태로 저장 후 짧은 Toast를 보여주고 종료합니다.
- 실제 저장을 위해 Hilt `DatabaseModule`, `RepositoryModule`을 추가했습니다.
- DataItem/Repository 계약, Manifest, Navigation은 변경하지 않았습니다.

## 완료 기준

- Share Sheet에서 SmartClipboard로 보낸 자료가 저장됩니다.
- 중복/빈 payload/지원하지 않는 MIME type 처리가 안전합니다.
- 앱 전체 화면이 과하게 열리지 않는 흐름을 유지합니다.

## 검증 방법

- 브라우저 링크 공유
- 메모 앱 텍스트 공유
- 갤러리 이미지 공유
- 파일 앱 파일 공유
- fake repository 테스트

## PR에 반드시 적을 내용

- 지원 MIME type
- 실패 처리 방식
- 테스트한 공유 출처 앱
- 수정한 공통 파일 여부
