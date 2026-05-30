# T-140 OCR/OG 추출 전처리 파이프라인

## 목적

이미지 OCR과 링크 OG 추출을 자료 입력 직후 가능한 만큼 처리하고, 실패 시 최대 3회 retry 후 다음 실행에서 이어갈 수 있게 상태를 저장합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-140-enrichment-ocr-og-pipeline`
- Depends on: `T-030-data-model-audit`, `T-100-share-target-flow`, `T-120-media-store-batch-query`
- Blocked by: `T-120` 미완료
- Ready criteria: enrichment 상태, retry count, DataItem update API, 입력 직후 처리할 collection hook이 확정됨
- Can run in parallel with: `T-150`은 interface 합의 후 가능
- Cannot run with: `T-030`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../processing/ocr/`
- `app/src/main/java/.../processing/web/`
- enrichment repository/update use case
- OCR/OG tests
- `docs/tasks/T-140-enrichment-ocr-og-pipeline.md`

## 수정 금지 파일

- DataItem model 계약 변경
- 수집 Activity lifecycle 변경
- 화면 UI 완성 작업
- Gemini topic recommendation 구현

## 구현 내용

- 기존 `OCRProcessor`가 있으면 재사용 가능성을 먼저 평가합니다.
- 링크 OG 추출은 Jsoup 기반으로 `Dispatchers.IO`에서 실행합니다.
- 입력 직후 1~2초 안에 끝나지 않는 작업은 pending 상태로 넘깁니다.
- retry count 3회 초과 시 failed/pending review 상태로 남깁니다.

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

- 이미지 OCR 결과가 DataItem enrichment에 저장됩니다.
- 링크 OG title/description/image가 저장됩니다.
- 네트워크/OCR 실패가 앱 흐름을 막지 않습니다.

## 검증 방법

- fake OCR processor 테스트
- fake WebExtractor 테스트
- 실제 URL 수동 확인
- retry count 증가/중단 테스트

## PR에 반드시 적을 내용

- 재사용한 기존 클래스
- retry 정책
- Dispatchers.IO 사용 지점
- 실패 상태 처리 방식
