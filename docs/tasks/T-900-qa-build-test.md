# T-900 전체 QA, 빌드, 오류 처리, 문서 정리

## 목적

MVP 주요 흐름을 실제 기기와 테스트 환경에서 검증하고, 오류 처리와 문서를 출시 가능한 수준으로 정리합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `test/T-900-qa-build-test`
- Depends on: `T-200`, `T-210`, `T-220`, `T-230`, `T-240`, `T-300`, `T-310`, `T-400`, `T-410`, `T-500`, `T-510`, `T-520`
- Blocked by: Phase 4~7 주요 task 미완료
- Ready criteria: MVP 주요 구현 task가 Done이고 테스트 기기/환경이 준비됨
- Can run in parallel with: 작은 문서 보완 task
- Cannot run with: 대규모 기능 구현 task

## 수정 허용 파일

- QA 문서
- 승인된 작은 bug fix 파일
- 테스트 파일
- `docs/WORK_LOG.md`
- `docs/tasks/T-900-qa-build-test.md`

## 수정 금지 파일

- 승인 없는 구조 변경
- 새 기능 추가
- Data model/Repository/Navigation/Manifest 대규모 변경
- 사용자 승인 없는 release 설정 변경

## 구현 내용

- Share Target, Quick Tile, MediaStore, SAF 수집을 점검합니다.
- OCR/OG/Gemini retry와 실패 UX를 점검합니다.
- Home, Inbox, Logs, Settings, Topic, Analysis UX를 점검합니다.
- Samsung Notes/Calendar/Reminder 전송을 점검합니다.
- known issue와 후속 개선 사항을 문서화합니다.

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

- MVP end-to-end 흐름이 실제 사용 가능한 수준으로 확인됩니다.
- 자동 수집/분석/초안/전송 실패 케이스가 문서화됩니다.
- 남은 이슈가 명확히 정리됩니다.

## 검증 방법

- `./gradlew test`
- `./gradlew assembleDebug`
- 실제 기기 수동 시나리오
- PR 템플릿 기준 확인

## PR에 반드시 적을 내용

- 실행한 테스트 명령
- 실제 기기/OS 버전
- 수동 확인 시나리오
- 남은 이슈와 severity
