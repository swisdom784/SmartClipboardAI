# T-150 Gemini 기반 이번 실행 Topic 추천

## 목적

새로 수집되거나 보강된 자료를 바탕으로 이번 실행에서만 보여줄 AI 추천 후보를 생성합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-150-gemini-topic-recommendation`
- Depends on: `T-140-enrichment-ocr-og-pipeline`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: `T-160` 중 파일 충돌이 없는 경우
- Cannot run with: `T-030`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../processing/gemini/`
- recommendation use case/repository
- Gemini fake tests
- `docs/tasks/T-150-gemini-topic-recommendation.md`

## 수정 금지 파일

- API key 하드코딩
- Home UI 완성 작업
- DataItem/Topic model 계약 변경
- 외부 앱 export 구현

## 구현 내용

- Gemini API key는 `local.properties`와 BuildConfig 기반으로 주입합니다.
- 이번 실행 추천만 표시할 수 있도록 recommendation session 개념을 둡니다.
- 과거 AI 추천 후보는 영구 보관하지 않는 정책을 지킵니다.
- 사용자가 수락한 추천만 Topic/Logs로 남길 수 있게 합니다.

T-150 구현 결과:

- `GeminiTopicRecommendationManager`가 현재 추천 세션을 새로 만들고 이전 세션을 교체합니다.
- 추천 후보는 `InMemoryRecommendationSessionStore`에만 보관되며, 사용자 수락 전 DB에 영구 저장하지 않습니다.
- Gemini API key는 `BuildConfig.GEMINI_API_KEY`를 통해 주입하고, key가 비어 있으면 `SKIPPED` 세션으로 처리합니다.
- 실제 Gemini 호출은 `HttpGeminiTextClient`가 `gemini-2.5-flash:generateContent` REST endpoint로 수행합니다.
- prompt는 세부 실행 버튼 대신 `최근 자료 정리`, `새 자료 다시 보기`, `AI 다시 분석` 같은 흐름형 추천을 요구합니다.
- JSON parsing은 `GeminiRecommendationParser`와 `GeminiGenerateContentResponseParser` 테스트로 고정했습니다.

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

- fake Gemini로 추천 생성 테스트가 가능합니다.
- 실제 key가 있는 로컬에서 smoke test가 가능합니다.
- 추천 실패 시 Home 흐름이 깨지지 않습니다.

## 검증 방법

- fake Gemini response 단위 테스트
- key 없음 상태 수동 확인
- key 있음 상태 smoke test
- 과거 추천 미노출 확인

## PR에 반드시 적을 내용

- key 관리 방식
- prompt 입력/출력 구조
- 추천 보관 정책
- 실패 fallback
