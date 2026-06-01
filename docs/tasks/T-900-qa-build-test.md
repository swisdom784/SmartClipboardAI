# T-900 전체 QA, 빌드, 오류 처리, 문서 정리

## 목적

MVP 주요 흐름을 실제 기기와 테스트 환경에서 검증하고, 오류 처리와 문서를 출시 가능한 수준으로 정리합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `test/T-900-qa-build-test`
- Depends on: `T-200`, `T-210`, `T-220`, `T-230`, `T-240`, `T-300`, `T-310`, `T-400`, `T-410`, `T-500`, `T-510`, `T-520`
- Blocked by: 없음
- Ready criteria: 완료됨
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

T-900 최종 결과:

- 자동 빌드/테스트는 통과했습니다.
- `SM-S911N` 실기기에 Debug APK 설치와 MainActivity 실행을 확인했습니다.
- Share Target 링크 저장, MediaStore 새 스크린샷 수집, Home Topic 생성, Topic 자료 선택 진입을 확인했습니다.
- 대량 MediaStore 자료에서 Topic 자료 선택 화면이 OOM으로 종료되는 문제를 발견했고, selectable item을 200개로 제한하는 회귀 테스트와 수정으로 재검증했습니다.
- Quick Settings Tile은 추가/클릭과 무크래시까지 확인했지만, ADB에서 클립보드 값을 자동 주입하지 못해 실제 텍스트/링크 저장은 손검증으로 남겼습니다.
- Gemini end-to-end, SAF picker, Samsung Notes/Calendar/Reminder handoff는 시스템/외부 앱 상호작용이 필요해 `docs/QA_REPORT.md`에 남은 수동 확인 항목으로 기록했습니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [x] PR 작성

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
- `.\gradlew.bat testDebugUnitTest --console=plain` 성공
- `.\gradlew.bat assembleDebug test --console=plain` 성공
- `adb devices -l`에서 `SM-S911N` 연결 확인
- `adb install -r app/build/outputs/apk/debug/app-debug.apk` 성공
- `com.smartclipboard.ai/.presentation.main.MainActivity` 실행 및 포커스 확인
- `ACTION_SEND text/plain` 링크 공유 후 DB에 `LINK / SHARE_TARGET` 저장 확인
- 앱 재실행 후 새 스크린샷 `SmartClipboard_QA_20260601.png`가 `SCREENSHOT / MEDIASTORE`로 저장됨 확인
- Home 입력 `QA_topic_device`로 `USER_REQUEST / ACTIVE` Topic 생성 확인
- 대량 자료 선택 화면 OOM 수정 후 AndroidRuntime 크래시 없음 확인
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- SHA-256: `A400FE03C0270D7D1115B70AD6974C8171B397CEE0330574E3D108D05FE74DC5`
- 수동 QA 시나리오, 수정한 이슈, 남은 확인 항목은 `docs/QA_REPORT.md`에 기록했습니다.

## PR에 반드시 적을 내용

- 실행한 테스트 명령
- 실제 기기/OS 버전
- 수동 확인 시나리오
- 남은 이슈와 severity
