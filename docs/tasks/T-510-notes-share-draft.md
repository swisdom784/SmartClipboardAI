# T-510 Samsung Notes share 전송

## 목적

사용자가 검토한 요약/노트 초안을 Samsung Notes로 보냅니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-510-notes-share-draft`
- Depends on: `T-410-topic-action-draft`, `T-050-permission-and-manifest-baseline`
- Blocked by: `T-410` 미완료
- Ready criteria: Notes payload와 Samsung Notes package query가 확정됨
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

- Notes 초안 검토 후 Samsung Notes로 공유됩니다.
- 미설치 케이스가 안전하게 처리됩니다.
- 전송 후 카드 완료 이벤트가 반환됩니다.

## 검증 방법

- Samsung Notes 설치 기기 수동 테스트
- 미설치 환경 fallback 안내 확인
- 긴 텍스트 전송 테스트
- intent 생성 단위 테스트

## PR에 반드시 적을 내용

- 사용한 intent action/extras
- 미설치 처리
- 긴 텍스트 처리
- 수동 테스트 환경
