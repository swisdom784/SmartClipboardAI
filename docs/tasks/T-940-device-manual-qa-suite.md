# T-940 남은 실기기 수동 QA 체크리스트 완료

## 목적

SAF picker, 권한 거부/부분 허용, Quick Settings Tile 실제 클립보드 저장, Samsung Notes/Calendar/Reminder handoff를 실제 기기에서 끝까지 확인한다.

## 작업 상태

- Status: Not Ready
- Owner: Unassigned
- Branch: `test/T-940-device-manual-qa-suite`
- Depends on: `T-910`, `T-930`
- Blocked by: ADB 연결 기기 필요, `T-910` 미완료, `T-930` 미완료
- Ready criteria: Gemini 진단 UX와 대량 자료 선택 UX 안정화 완료
- Can run in parallel with: 없음
- Cannot run with: UI/Manifest/Permission 변경 작업

## 수정 허용 파일

- `docs/QA_REPORT.md`
- `docs/WORK_LOG.md`
- 승인된 작은 bug fix 파일
- 관련 테스트 파일

## 수정 금지 파일

- 새 기능 추가
- 승인 없는 Manifest/Permission 대규모 변경
- 모델/DB/Repository 구조 변경
- 비밀값 기록

## 구현 내용

- SAF 파일 선택 취소/이미지/PDF/중복 URI 시나리오를 확인한다.
- 이미지 권한 허용/거부/부분 허용 상태를 확인한다.
- 실제 텍스트/링크를 복사한 뒤 QS Tile로 저장되는지 확인한다.
- Samsung Notes/Calendar/Reminder 앱으로 handoff되는지 확인한다.
- 각 실패 케이스는 bug task로 분리할지 QA 문서에 기록한다.

## 체크리스트

- [ ] 코드 읽기
- [ ] 관련 문서 확인
- [ ] 선행 task 완료 여부 확인
- [ ] 실기기 ADB 연결 확인
- [ ] SAF picker 수동 확인
- [ ] 권한 상태 수동 확인
- [ ] QS Tile 실제 클립보드 수집 확인
- [ ] Samsung Notes handoff 확인
- [ ] Samsung Calendar handoff 확인
- [ ] Samsung Reminder handoff 확인
- [ ] 빌드 확인
- [ ] 테스트/수동 확인
- [ ] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- `docs/QA_REPORT.md`의 `QA-002`, `QA-004`, `QA-005`가 확인되거나 후속 bug task로 분리된다.
- 실제 기기 기준으로 MVP 사용 가능/불가 판정이 명확해진다.

## 검증 방법

- `.\gradlew.bat assembleDebug test --console=plain`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- 실기기 수동 QA
- 필요 시 DB pull 확인

## PR에 반드시 적을 내용

- 사용 기기와 Android 버전
- 수동 확인한 시나리오
- 통과/실패 항목
- 후속 bug task 필요 여부
