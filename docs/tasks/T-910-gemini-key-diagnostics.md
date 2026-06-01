# T-910 Gemini key 검증과 실패 진단 UX

## 목적

Gemini key가 비어 있거나 invalid이거나 네트워크/API 권한 문제로 실패할 때 앱이 조용히 실패하지 않고, Settings/Home에서 사용자가 확인 가능한 상태를 제공한다.

## 작업 상태

- Status: Ready
- Owner: Unassigned
- Branch: `feat/T-910-gemini-key-diagnostics`
- Depends on: `T-900`
- Blocked by: 없음
- Ready criteria: `QA-003`, `QA-007` 재현 완료
- Can run in parallel with: `T-930-topic-selection-large-library-ux`
- Cannot run with: Gemini generator/client contract 변경 작업

## 수정 허용 파일

- `app/src/main/java/com/smartclipboard/ai/processing/gemini/**`
- `app/src/main/java/com/smartclipboard/ai/presentation/settings/**`
- `app/src/main/java/com/smartclipboard/ai/presentation/home/**`
- 관련 테스트 파일
- `docs/QA_REPORT.md`
- `docs/WORK_LOG.md`

## 수정 금지 파일

- TopicAnalysis/TopicAction DB schema
- Samsung export intent 구현
- Manifest/Gradle 대규모 변경
- 비밀값을 소스나 문서에 기록하는 변경

## 구현 내용

- Gemini 실패 원인을 `MissingKey`, `InvalidKey`, `NetworkFailure`, `ApiFailure`, `ParseFailure`처럼 구분한다.
- `HttpGeminiTextClient`의 HTTP error body를 안전하게 분류하되 key 값은 로그/문서/UI에 노출하지 않는다.
- Home에서 추천 실패/건너뜀 상태를 조용하지만 확인 가능한 카드나 상태 문구로 보여준다.
- Settings의 Gemini 섹션에서 key 설정 여부와 마지막 진단 결과를 보여준다.
- 직접 Gemini smoke test 또는 debug-only 진단 use case로 invalid key를 재현한다.

## 체크리스트

- [ ] 코드 읽기
- [ ] 관련 문서 확인
- [ ] 선행 task 완료 여부 확인
- [ ] 실패 원인 모델 테스트 작성
- [ ] invalid key 직접 테스트 결과 반영
- [ ] Home/Settings 상태 표시 테스트 작성
- [ ] 구현
- [ ] 빌드 확인
- [ ] 테스트/수동 확인
- [ ] 변경 요약 작성
- [ ] PR 작성

## 완료 기준

- blank key와 invalid key가 서로 다른 상태로 구분된다.
- invalid key일 때 Home 추천이 조용히 사라지기만 하지 않는다.
- Settings에서 Gemini 상태를 확인할 수 있다.
- key 값은 어떤 로그/문서/UI에도 노출되지 않는다.

## 검증 방법

- `.\gradlew.bat testDebugUnitTest --console=plain`
- `.\gradlew.bat assembleDebug --console=plain`
- invalid key 직접 Gemini smoke test
- 유효한 key가 준비되면 성공 smoke test

## PR에 반드시 적을 내용

- 관련 QA issue: `QA-003`, `QA-007`
- key 값을 노출하지 않았다는 확인
- invalid key, blank key, network failure 테스트 결과
- Home/Settings에서 사용자가 보는 상태 변화
