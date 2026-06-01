# T-920 Gemini 추천/분석 end-to-end 검증

## 목적

유효한 Gemini key로 Home 추천 생성, 추천 수락 Topic 생성, 자료 선택 저장, 분석 완료, Notes/Calendar/Reminder action draft 생성까지 실제 기기에서 확인한다.

## 작업 상태

- Status: Not Ready
- Owner: Unassigned
- Branch: `test/T-920-gemini-recommendation-analysis-e2e`
- Depends on: `T-910`
- Blocked by: `T-910` 미완료, 유효한 Gemini key 필요, ADB 연결 기기 필요
- Ready criteria: Gemini 진단이 성공 상태를 반환하고 실기기 연결 가능
- Can run in parallel with: 없음
- Cannot run with: Gemini diagnostics 또는 Topic selection UX 변경 작업

## 수정 허용 파일

- `docs/QA_REPORT.md`
- `docs/WORK_LOG.md`
- 승인된 debug-only QA helper
- 관련 테스트 파일

## 수정 금지 파일

- 승인 없는 모델/DB/Repository 변경
- Gemini prompt 대규모 변경
- 외부 앱 export 동작 변경
- 비밀값 기록

## 구현 내용

- 유효한 key로 Home 추천 카드가 생성되는지 확인한다.
- 추천 수락 시 `AI_RECOMMENDATION` Topic이 생성되는지 확인한다.
- 자료 선택 저장 후 분석 화면으로 이동하고 `TopicAnalysisStatus.DONE`이 저장되는지 확인한다.
- DONE 분석에서 Notes/Calendar/Reminder action draft가 생성되는지 확인한다.
- Gemini 실패와 retry/fallback 동작을 문서화한다.

## 체크리스트

- [ ] 코드 읽기
- [ ] 관련 문서 확인
- [ ] 선행 task 완료 여부 확인
- [ ] 유효한 Gemini key 확인
- [ ] 실기기 ADB 연결 확인
- [ ] Home 추천 생성 확인
- [ ] 추천 수락 Topic 생성 확인
- [ ] 자료 선택 저장 확인
- [ ] 분석 완료 확인
- [ ] action draft 생성 확인
- [ ] 빌드 확인
- [ ] 테스트/수동 확인
- [ ] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- Gemini 추천/분석 happy path가 실제 기기에서 통과한다.
- 실패 시 앱이 중단되지 않고 사용자에게 복구 가능한 상태를 제공한다.
- DB 상태와 UI 상태가 QA 문서에 기록된다.

## 검증 방법

- `.\gradlew.bat assembleDebug test --console=plain`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- 실기기 수동 QA
- DB pull 후 `topics`, `topic_analyses`, `topic_actions` 상태 확인

## PR에 반드시 적을 내용

- 사용 기기와 Android 버전
- key 성공/실패 상태
- 추천/분석/초안 생성 결과
- 남은 이슈
