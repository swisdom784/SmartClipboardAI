# QA Report

## 기준

- Date: 2026-06-01
- Branch: `test/T-900-qa-build-test`
- Scope: MVP end-to-end QA 및 실제 기기 smoke test
- Target: Android 앱 `SmartClipboard`

## 실제 기기

| 항목 | 값 |
| --- | --- |
| Device | Samsung Galaxy `SM-S911N` |
| Android | 16 / SDK 36 |
| Install method | `adb install -r app/build/outputs/apk/debug/app-debug.apk` |
| Result | 설치 성공, MainActivity 실행 성공 |

## 자동 검증 결과

| 항목 | 결과 | 메모 |
| --- | --- | --- |
| Gradle build/test | 성공 | `.\gradlew.bat assembleDebug test --console=plain` |
| Debug unit test | 성공 | `.\gradlew.bat testDebugUnitTest --console=plain` |
| Debug APK 생성 | 성공 | `app/build/outputs/apk/debug/app-debug.apk` |
| Git whitespace check | 성공 | `git diff --check` |
| adb 연결 확인 | 성공 | `SM-S911N` 연결 |
| 실제 설치 | 성공 | `adb install -r` |
| 앱 실행 | 성공 | `com.smartclipboard.ai/.presentation.main.MainActivity` 포커스 확인 |

## APK 산출물

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Size: `55,659,396` bytes
- Last modified: `2026-06-01 21:00:41 KST`
- SHA-256: `A400FE03C0270D7D1115B70AD6974C8171B397CEE0330574E3D108D05FE74DC5`

## 자동 테스트 범위

- Data model, Room mapper, Repository facade
- Share Target 저장 흐름
- Quick Settings Tile 클립보드 저장 흐름
- MediaStore batch query 수집 흐름
- SAF 파일 선택 저장 흐름
- OCR/OG enrichment retry 흐름
- Gemini recommendation/analysis parser와 fallback 흐름
- Home, Inbox, Logs, Settings UI state mapper
- Topic 생성, 자료 선택, 분석, action draft 흐름
- Samsung Notes, Calendar, Reminder intent spec

## 실제 기기 수동 QA 시나리오

아래 항목은 `SM-S911N` 실기기에서 확인했습니다. 사용자의 실제 눈검증이나 외부 앱 handoff가 필요한 항목은 별도로 남겼습니다.

### 설치 및 첫 실행

- [x] Debug APK 설치
- [x] 앱 첫 실행
- [x] Home 진입
- [x] 기존 MediaStore 자료 요약 표시
- [ ] 이미지 권한 요청/거부/부분 허용 UX: 이미 권한이 허용된 기기 상태라 별도 초기화 후 재확인 필요

### Share Target 수집

- [x] ADB `ACTION_SEND text/plain` 링크를 SmartClipboard로 공유
- [ ] 일반 텍스트를 SmartClipboard로 공유
- [ ] 이미지 1개를 SmartClipboard로 공유
- [ ] 이미지 여러 개를 SmartClipboard로 공유
- [ ] 공유 후 `SmartClipboard에 담았어요` Toast 확인: ADB 실행에서는 시각 확인 제한
- [x] DB에서 `LINK / SHARE_TARGET` 저장 확인
- [x] Home 요약에서 링크 수 증가 확인

### Quick Settings Tile 수집

- [x] Quick Settings Tile 추가
- [x] Quick Settings Tile 클릭 명령 실행
- [ ] 텍스트 복사 후 Tile 클릭
- [ ] 링크 복사 후 Tile 클릭
- [x] Tile 클릭 후 AndroidRuntime 크래시 없음
- [ ] 빈 클립보드/미지원 clip 안내 확인

메모: 이 기기의 ADB shell은 `cmd clipboard`를 지원하지 않아 클립보드 값을 자동 주입하지 못했습니다. 실제 텍스트/링크 복사 후 Tile 클릭은 사용자 손검증이 필요합니다.

### MediaStore 자동 이미지 수집

- [x] 앱 종료 후 새 스크린샷 생성
- [x] 앱 재실행 시 새 이미지 자동 수집
- [ ] 다운로드 이미지 자동 수집
- [ ] 카메라 사진 자동 수집
- [ ] Settings 수집 기간 변경 후 sync 범위 확인
- [ ] 부분 접근 권한 허용 시 제한 상태 확인

검증 근거: `SmartClipboard_QA_20260601.png`가 `SCREENSHOT / MEDIASTORE / PENDING`으로 DB 저장됨.

### SAF 직접 파일 선택

- [ ] Inbox에서 파일 추가
- [ ] 이미지 파일 선택
- [ ] PDF 또는 일반 파일 선택
- [ ] 선택 취소 시 아무 항목도 저장되지 않음
- [ ] 중복 URI 선택 시 중복 저장되지 않음

메모: SAF는 시스템 picker 상호작용이 필요해 이번 ADB 중심 QA에서는 자동 확인하지 않았습니다.

### OCR/OG/Gemini

- [ ] 이미지 OCR 결과가 enrichment로 반영되는지 확인
- [ ] 링크 OG title/description이 반영되는지 확인
- [ ] `local.properties`에 Gemini key가 있을 때 추천 생성 확인
- [ ] Gemini key가 없을 때 앱 흐름이 깨지지 않는지 확인
- [ ] 네트워크 실패 시 retry/fallback 확인

메모: `local.properties`의 key는 설정됐지만, 실제 Gemini 추천/분석 end-to-end는 사용자 선택 저장 이후 별도 수동 확인이 필요합니다.

### Home/Inbox/Logs/Settings

- [x] Home 이전 작업 리스트 표시
- [x] Home 새 작업 입력
- [ ] 이번 실행 AI 추천 카드 표시
- [ ] Inbox 카테고리 필터
- [ ] Inbox 리스트/그리드 전환
- [ ] Inbox 중요 표시/삭제
- [ ] Logs badge 필터
- [ ] Settings 저장 용량 표시
- [ ] Settings 자동 정리 버튼
- [ ] Settings 수집 기간 preset/custom 저장

### Topic/Analysis/Action

- [x] Home 입력으로 Topic 생성
- [ ] AI 추천 수락으로 Topic 생성
- [x] Topic 자료 선택 화면 이동
- [ ] 자료 선택 수정 후 저장
- [ ] 분석 화면 자동 시작
- [ ] 분석 실패 시 다시 분석
- [ ] Notes/Calendar/Reminder 초안 카드 표시
- [ ] 초안 수정 dialog
- [ ] 즉시 완료
- [ ] 뒤로가기 완료/미완료/계속 보기 dialog

### Samsung 앱 전송

- [ ] Samsung Notes 설치 기기에서 Notes 전송
- [ ] Samsung Calendar 설치 기기에서 Calendar insert 전송
- [ ] Samsung Calendar 미설치 환경에서 기본 Calendar fallback
- [ ] Samsung Reminder 설치 기기에서 Reminder 전송
- [ ] 각 앱 미설치 시 안내 Toast 확인
- [ ] 전송 시작 후 카드가 `전송됨` 상태로 접히는지 확인

## 실제 기기에서 발견해 수정한 이슈

### QA-FIX-001: 대량 MediaStore 자료에서 Topic 자료 선택 화면 OOM

- 증상: 실기기 DB에 `data_items`가 약 1.9만 개 존재할 때 Topic 카드를 눌러 자료 선택 화면에 진입하면 앱이 `OutOfMemoryError`로 종료됨
- 원인: `TopicDataSelectionUiStateMapper`가 전체 `observeInboxItems()` 결과를 모두 Compose row로 넘김
- 수정: 선택된 자료를 우선 정렬한 뒤 자료 선택 화면에 전달하는 selectable item을 200개로 제한
- 회귀 테스트: `TopicDataSelectionUiStateMapperTest.limitsSelectableItemsForLargeLibrariesWhileKeepingSelectedItems`
- 재검증: 수정 APK 설치 후 동일 Topic 카드 진입 시 자료 선택 화면 정상 렌더링, AndroidRuntime 크래시 없음

## Known Issues / QA Blockers

| ID | Severity | 상태 | 내용 | 다음 조치 |
| --- | --- | --- | --- | --- |
| QA-001 | High | Fixed | 대량 MediaStore 자료에서 Topic 자료 선택 화면 진입 시 OOM 발생 | `QA-FIX-001`로 수정 및 재검증 |
| QA-002 | Medium | Open | Quick Settings Tile은 추가/클릭까지 확인했지만 ADB에서 클립보드 값을 자동 주입하지 못함 | 실제 텍스트/링크 복사 후 Tile 클릭 손검증 |
| QA-003 | High | Open | `local.properties`에 Gemini key 값은 있으나 직접 Gemini smoke test가 `API_KEY_INVALID`로 실패함. 앱 내 진단 UX는 `T-910`에서 추가됨 | 유효한 key 확인 후 `T-920` Gemini E2E 진행 |
| QA-004 | Medium | Open | Samsung Notes/Calendar/Reminder handoff는 단위 테스트로 intent spec만 확인됨 | Galaxy 기기에서 실제 앱 전송 확인 |
| QA-005 | Medium | Open | SAF picker, 권한 거부/부분 허용, Settings preset/custom 저장은 시스템 UI 손검증 필요 | Android Studio/실기기에서 수동 체크 |
| QA-006 | High | Open | Topic 자료 선택 화면은 OOM은 해결됐지만 대량 자료에서 저장 버튼이 긴 목록 아래에 있어 실제 선택 저장/분석 진입이 어렵다 | sticky action bar, 검색/필터/페이징 UX 개선 |
| QA-007 | Medium | Fixed | Home의 Gemini 추천 실패 상태가 사용자에게 노출되지 않고 READY 추천만 표시된다 | `T-910`에서 Home/Settings Gemini 상태 표시 추가 |

## 2026-06-01 추가 Gemini QA

- `local.properties`의 `gemini.api.key` 존재 여부: 설정됨
- 직접 Gemini endpoint smoke test: 실패
- 실패 응답: `API_KEY_INVALID`
- 자동 빌드/테스트: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 실기기 재설치/화면 QA: ADB 연결 해제로 중단

메모: key 값은 문서와 로그에 기록하지 않습니다. 현재 상태에서 Gemini 성공 E2E는 진행할 수 없고, 앱은 invalid key를 명확히 진단하거나 사용자에게 복구 가능한 상태로 안내하는 UX가 필요합니다.

## 2026-06-03 T-910 Gemini 진단 UX

- Gemini 실패 원인 분류 추가: missing key, invalid key, network failure, API failure, parse failure
- `HttpGeminiTextClient`가 HTTP error body 원문을 그대로 예외 메시지로 던지지 않고 안전한 실패 타입으로 변환하도록 수정
- invalid key 추천 실패 시 `RecommendationSessionStatus.FAILED`와 `Gemini API key를 확인해 주세요` 메시지 저장
- Home 자동 정리 패널에서 추천 실패/건너뜀 상태를 조용히 확인할 수 있도록 `HomeAiStatus` 추가
- Settings Gemini 섹션에서 key 설정 여부와 마지막 추천 세션 상태를 표시
- key 값은 UI, 문서, 로그에 기록하지 않음
- 자동 빌드/테스트: `.\gradlew.bat assembleDebug test --console=plain` 성공
- 직접 Gemini endpoint smoke test: 실패, HTTP 400
- 실기기 설치/실행: `SM-S911N`에 `app-debug.apk` 설치 성공, MainActivity 실행 및 앱 프로세스 유지 확인
- 실기기 Home 확인: `Gemini API key를 확인해 주세요`, `자료는 저장되어 있고, 다음 실행 때 다시 시도합니다.` 문구 표시 확인
- 실기기 Settings 확인: `Gemini 연결 확인 필요`, `Gemini API key를 확인해 주세요` 문구 표시 확인

## Release Readiness

- Automated readiness: 통과
- Device smoke readiness: 설치, 실행, Share Target, MediaStore, Topic 생성/자료 선택 진입 기준 통과
- MVP functional readiness: 핵심 앱 실행은 가능하나 Gemini end-to-end, SAF, QS clipboard content, Samsung 앱 handoff는 손검증 후 최종 판정
