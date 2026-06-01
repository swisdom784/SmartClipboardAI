# QA Report

## 기준

- Date: 2026-06-01
- Branch: `test/T-900-qa-build-test`
- Scope: MVP end-to-end QA 준비
- Target: Android 앱 `SmartClipboard`

## 자동 검증 결과

| 항목 | 결과 | 메모 |
| --- | --- | --- |
| Gradle build/test | 성공 | `.\gradlew.bat assembleDebug test --console=plain` |
| Debug APK 생성 | 성공 | `app/build/outputs/apk/debug/app-debug.apk` |
| Git whitespace check | 성공 | `git diff --check` |
| adb 연결 확인 | 기기 없음 | `adb devices` 결과 연결된 device 없음 |

## APK 산출물

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Size: `55,619,511` bytes
- Last modified: `2026-06-01 18:29:05 KST`
- SHA-256: `957168B6F153B98A2489C0902AFE156F18F56D4692FE93FA5BCE10654DCF0DD3`

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

아래 항목은 연결된 Android 기기 또는 에뮬레이터에서 확인해야 합니다.

### 설치 및 첫 실행

- [ ] Debug APK 설치
- [ ] 앱 첫 실행
- [ ] 이미지 권한 요청 표시
- [ ] 권한 허용 후 Home 진입
- [ ] 권한 거부 후 Settings/상태 안내 확인

### Share Target 수집

- [ ] 브라우저 링크를 SmartClipboard로 공유
- [ ] 일반 텍스트를 SmartClipboard로 공유
- [ ] 이미지 1개를 SmartClipboard로 공유
- [ ] 이미지 여러 개를 SmartClipboard로 공유
- [ ] 공유 후 `SmartClipboard에 담았어요` Toast 확인
- [ ] Inbox에서 공유 항목 확인

### Quick Settings Tile 수집

- [ ] Quick Settings Tile 추가
- [ ] 텍스트 복사 후 Tile 클릭
- [ ] 링크 복사 후 Tile 클릭
- [ ] 투명 Activity가 오래 남지 않는지 확인
- [ ] 빈 클립보드/미지원 clip 안내 확인

### MediaStore 자동 이미지 수집

- [ ] 앱 종료 후 새 스크린샷 생성
- [ ] 앱 재실행 시 새 이미지 자동 수집
- [ ] 다운로드 이미지 자동 수집
- [ ] 카메라 사진 자동 수집
- [ ] Settings 수집 기간 변경 후 sync 범위 확인
- [ ] 부분 접근 권한 허용 시 제한 상태 확인

### SAF 직접 파일 선택

- [ ] Inbox에서 파일 추가
- [ ] 이미지 파일 선택
- [ ] PDF 또는 일반 파일 선택
- [ ] 선택 취소 시 아무 항목도 저장되지 않음
- [ ] 중복 URI 선택 시 중복 저장되지 않음

### OCR/OG/Gemini

- [ ] 이미지 OCR 결과가 enrichment로 반영되는지 확인
- [ ] 링크 OG title/description이 반영되는지 확인
- [ ] `local.properties`에 Gemini key가 있을 때 추천 생성 확인
- [ ] Gemini key가 없을 때 앱 흐름이 깨지지 않는지 확인
- [ ] 네트워크 실패 시 retry/fallback 확인

### Home/Inbox/Logs/Settings

- [ ] Home 이전 작업 리스트 표시
- [ ] Home 새 작업 입력
- [ ] 이번 실행 AI 추천 카드 표시
- [ ] Inbox 카테고리 필터
- [ ] Inbox 리스트/그리드 전환
- [ ] Inbox 중요 표시/삭제
- [ ] Logs badge 필터
- [ ] Settings 저장 용량 표시
- [ ] Settings 자동 정리 버튼
- [ ] Settings 수집 기간 preset/custom 저장

### Topic/Analysis/Action

- [ ] Home 입력으로 Topic 생성
- [ ] AI 추천 수락으로 Topic 생성
- [ ] Topic 자료 선택 화면 이동
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

## Known Issues / QA Blockers

| ID | Severity | 상태 | 내용 | 다음 조치 |
| --- | --- | --- | --- | --- |
| QA-001 | High | Open | 현재 QA 환경에 연결된 Android device/emulator가 없어 실제 설치와 수동 시나리오를 실행하지 못함 | Android Studio 또는 실기기 연결 후 수동 QA 실행 |
| QA-002 | High | Open | `local.properties`의 `gemini.api.key`가 비어 있어 실제 Gemini 네트워크 smoke test를 실행하지 못함 | key 설정 후 추천/분석 네트워크 확인 |
| QA-003 | Medium | Open | Samsung Notes/Calendar/Reminder handoff는 단위 테스트로 intent spec만 확인됨 | Galaxy 기기에서 실제 앱 전송 확인 |
| QA-004 | Medium | Open | MediaStore full library/partial access 동작은 실제 OS 권한 dialog가 필요함 | Android 13/14 기기에서 권한별 수동 확인 |

## Release Readiness

- Automated readiness: 통과
- Device readiness: 실제 기기 QA 전까지는 미완료
- MVP functional readiness: 자동 검증은 통과해야 하며, 실제 사용 가능 판정은 `QA-001`부터 `QA-004`까지 확인 후 내립니다.
