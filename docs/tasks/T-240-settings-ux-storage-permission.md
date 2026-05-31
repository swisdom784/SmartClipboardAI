# T-240 Settings 수집 기간, 용량, 권한 UX

## 목적

사용자가 자동 수집 기간, 저장 용량 한도, 자동 삭제 정책, 권한 상태를 조정하고 확인할 수 있게 합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-240-settings-ux-storage-permission`
- Depends on: `T-040-navigation-baseline`, `T-160-storage-quota-cleanup`, `T-170-repository-integration`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-200`, `T-210`, `T-230`
- Cannot run with: `T-160` policy 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/settings/`
- Settings ViewModel
- settings state tests
- `docs/tasks/T-240-settings-ux-storage-permission.md`

## 수정 금지 파일

- storage cleanup 핵심 알고리즘 변경
- Manifest 권한 선언 변경
- Home/Inbox UI 세부 변경
- Gemini key 저장 정책 변경

## 구현 내용

- 수집 기간 preset: 마지막 종료 이후, 1시간, 24시간, 7일, custom을 제공합니다.
- 저장 용량 사용량과 한도를 표시합니다.
- 자동 삭제 정책을 짧게 보여줍니다.
- 권한 상태와 필요한 조치 버튼을 제공합니다.
- Gemini key는 하드코딩하지 않는다는 안내와 로컬 설정 기준을 유지합니다.

T-240 구현 결과:

- `SmartClipboardSettings`와 `SettingsPreferencesStore`를 추가해 수집 기간, custom 시간, 저장 용량 한도를 SharedPreferences에 저장합니다.
- 수집 기간은 마지막 종료 이후, 1시간, 24시간, 7일, 직접 설정을 제공합니다.
- 고정 기간 또는 custom 기간을 적용하면 기존 MediaStore checkpoint를 조정해 다음 이미지 수집 범위에 반영합니다.
- `SettingsViewModel`이 저장 사용량을 `DataRepository.getStorageUsage()`로 읽고, 정리 버튼은 `cleanupStorage()`를 호출합니다.
- 이미지 권한 상태를 `MediaPermissionPolicy` 기준으로 읽고, Settings 화면에서 권한 요청 버튼을 MainActivity permission launcher로 연결했습니다.
- Gemini API 키는 하드코딩하지 않고 local properties 기반을 유지한다는 안내만 표시합니다.

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

- 수집 기간 설정이 저장되고 다음 수집에 반영됩니다.
- 용량 상태가 표시됩니다.
- 권한 상태가 사용자에게 이해되게 표시됩니다.

## 검증 방법

- 설정 변경 후 재실행 확인
- custom 기간 입력 확인
- 권한 허용/거부 상태 확인
- fake settings repository 테스트

실제 검증:

- `SettingsUiStateMapperTest`로 저장 사용량 표시, 초과 상태, 권한 상태, collection window checkpoint 계산을 검증했습니다.
- `.\gradlew.bat testDebugUnitTest --tests com.smartclipboard.ai.presentation.settings.SettingsUiStateMapperTest` 성공.
- 전체 빌드/테스트 결과는 PR 작성 전 `.\gradlew.bat assembleDebug test`로 확인합니다.

## PR에 반드시 적을 내용

- 설정 항목 목록
- 수집 기간 반영 방식
- 저장 용량 표시 방식
- 권한 안내 문구
