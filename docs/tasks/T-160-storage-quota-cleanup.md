# T-160 저장 용량 한도와 자동 삭제 정책

## 목적

자동 수집 자료가 설정한 용량을 넘으면 사용자에게 중요한 자료를 보존하면서 오래된 데이터를 안전하게 정리합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-160-storage-quota-cleanup`
- Depends on: `T-030-data-model-audit`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`
- Blocked by: `T-140` 미완료
- Ready criteria: 중요 표시, Topic 연결, internal copy 여부, storage size 계산 방식이 확정됨
- Can run in parallel with: `T-150`
- Cannot run with: `T-030`, `T-170`, `T-240`

## 수정 허용 파일

- `app/src/main/java/.../storage/`
- cleanup use case
- settings repository storage fields
- storage policy tests
- `docs/tasks/T-160-storage-quota-cleanup.md`

## 수정 금지 파일

- Inbox UI
- Settings UI
- DataItem model 계약 변경
- MediaStore 수집 로직 변경

## 구현 내용

- 삭제 우선순위를 정합니다: cache/temp, 중요하지 않은 internal copy, Topic 미연결 오래된 DataItem 순서.
- MediaStore 원본은 삭제하지 않습니다.
- 중요 표시/Topic 연결/사용자 보존 항목은 자동 삭제 대상에서 제외합니다.
- 삭제 결과를 Settings와 Logs가 참고할 수 있게 기록합니다.

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

- quota 초과 시 안전한 순서로 삭제 후보를 선택합니다.
- 보존 대상은 삭제되지 않습니다.
- Settings에서 보여줄 사용량 값을 계산할 수 있습니다.

## 검증 방법

- quota 계산 단위 테스트
- 삭제 제외 대상 테스트
- 오래된 데이터 우선 삭제 테스트
- MediaStore 원본 미삭제 확인

## PR에 반드시 적을 내용

- 삭제 우선순위
- 보존 대상 기준
- Settings에 제공되는 값
- 데이터 손실 방지 근거
