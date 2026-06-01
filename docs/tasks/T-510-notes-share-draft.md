# T-510 Samsung Notes share 전송

## 목적

사용자가 검토한 요약/노트 초안을 Samsung Notes로 보냅니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-510-notes-share-draft`
- Depends on: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-500`, `T-520`
- Cannot run with: `T-050` Manifest 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../export/notes/`
- Notes action card hook
- notes export tests
- `docs/tasks/T-510-notes-share-draft.md`

## 수정 금지 파일

- TopicAction model 변경
- Manifest queries 변경
- Calendar/Reminder export 구현
- AI prompt 변경

## 구현 내용

- `Intent.ACTION_SEND`와 `type = text/plain`을 사용합니다.
- `Intent.EXTRA_TEXT`에 노트 초안 전체를 넣습니다.
- package는 `com.samsung.android.app.notes`를 지정합니다.
- 미설치 시 사용자에게 짧게 안내합니다.
- 사용자가 버튼을 눌렀을 때만 실행합니다.

T-510 구현 결과:

- `SamsungNotesShareIntentFactory`를 추가해 `ACTION_SEND`, `text/plain`, `Intent.EXTRA_TEXT`, `com.samsung.android.app.notes` package 지정 spec을 생성합니다.
- 긴 본문은 preview 길이로 자르지 않고 전체 텍스트를 `EXTRA_TEXT`로 보냅니다.
- `SamsungNotesExportLauncher`를 추가해 Samsung Notes 설치 여부를 확인하고 `startActivity`를 실행합니다.
- 미설치, 빈 내용, 실행 실패는 짧은 Toast 문구로 안내합니다.
- 분석 화면의 NOTE 카드에만 `노트로 보내기` 버튼을 표시합니다.
- 전송 시작 후 action 상태를 `EXPORTED`로 바꾸고 카드가 완료 카드처럼 접히도록 처리합니다.
- Manifest query는 `T-050`에서 이미 추가된 `com.samsung.android.app.notes`를 그대로 사용했습니다.

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

- Notes 초안 검토 후 Samsung Notes로 공유됩니다.
- 미설치 케이스가 안전하게 처리됩니다.
- 전송 후 카드 완료 이벤트가 반환됩니다.

## 검증 방법

- Samsung Notes 설치 기기 수동 테스트
- 미설치 환경 fallback 안내 확인
- 긴 텍스트 전송 테스트
- intent 생성 단위 테스트

실제 검증:

- `SamsungNotesShareIntentFactoryTest`로 action/type/package/text/long text/blank content 처리를 검증했습니다.
- `TopicActionDraftUseCaseTest`로 export 후 `EXPORTED` 상태와 완료 시각 기록을 검증했습니다.
- `TopicActionDraftUiStateMapperTest`로 `EXPORTED` 카드 접힘과 전체 완료 판정을 검증했습니다.
- 전체 빌드/테스트는 `.\gradlew.bat assembleDebug test --console=plain`로 확인했고 성공했습니다.

## PR에 반드시 적을 내용

- 사용한 intent action/extras
- 미설치 처리
- 긴 텍스트 처리
- 수동 테스트 환경
