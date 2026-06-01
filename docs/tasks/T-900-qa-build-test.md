# T-900 전체 QA, 빌드, 오류 처리, 문서 정리

## 목적

MVP 주요 흐름을 실제 기기와 테스트 환경에서 검증하고, 오류 처리와 문서를 출시 가능한 수준으로 정리합니다.

## 작업 상태

- Status: Blocked
- Owner: Codex
- Branch: `test/T-900-qa-build-test`
- Depends on: `T-200`, `T-210`, `T-220`, `T-230`, `T-240`, `T-300`, `T-310`, `T-400`, `T-410`, `T-500`, `T-510`, `T-520`
- Blocked by: 연결된 Android device/emulator 없음, 실제 Gemini key 없음
- Ready criteria: Android device/emulator 연결, 필요 시 `local.properties`의 Gemini key 설정
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

T-900 현재 결과:

- 자동 빌드/테스트는 통과했습니다.
- Debug APK 산출물과 SHA-256을 `docs/QA_REPORT.md`에 기록했습니다.
- `adb devices`에서 연결된 device/emulator가 없어 실제 설치 및 수동 QA는 실행하지 못했습니다.
- `local.properties`의 `gemini.api.key`가 비어 있어 실제 Gemini 네트워크 smoke test는 실행하지 못했습니다.
- 실제 기기 QA가 가능해지면 이 task를 다시 Ready 또는 In Progress로 바꿔 수동 시나리오를 실행합니다.

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

- MVP end-to-end 흐름이 실제 사용 가능한 수준으로 확인됩니다.
- 자동 수집/분석/초안/전송 실패 케이스가 문서화됩니다.
- 남은 이슈가 명확히 정리됩니다.

## 검증 방법

- `./gradlew test`
- `./gradlew assembleDebug`
- 실제 기기 수동 시나리오
- PR 템플릿 기준 확인

실제 검증:

- `git diff --check` 성공
- `.\gradlew.bat assembleDebug test --console=plain` 성공
- `adb devices` 실행 결과 연결된 device/emulator 없음
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- SHA-256: `957168B6F153B98A2489C0902AFE156F18F56D4692FE93FA5BCE10654DCF0DD3`
- 수동 QA 시나리오와 blocker는 `docs/QA_REPORT.md`에 기록했습니다.

## PR에 반드시 적을 내용

- 실행한 테스트 명령
- 실제 기기/OS 버전
- 수동 확인 시나리오
- 남은 이슈와 severity
