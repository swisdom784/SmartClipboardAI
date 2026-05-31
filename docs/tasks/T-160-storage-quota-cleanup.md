# T-160 저장 용량 한도와 자동 삭제 정책

## 목적

자동 수집 자료가 설정한 용량을 넘으면 사용자에게 중요한 자료를 보존하면서 오래된 데이터를 안전하게 정리합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `feat/T-160-storage-quota-cleanup`
- Depends on: `T-030-data-model-audit`, `T-120-media-store-batch-query`, `T-130-storage-access-framework-picker`, `T-140-enrichment-ocr-og-pipeline`, `T-150-gemini-topic-recommendation`
- Blocked by: 없음
- Ready criteria: 완료됨
- Can run in parallel with: 없음
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

T-160 구현 결과:

- `StorageQuotaPolicy`가 사용량, 초과량, 삭제 후보를 계산합니다.
- 삭제 후보는 `중요 표시`, `사용자 보존`, `Topic 연결` 항목을 제외합니다.
- 내부 복사본이 있는 항목을 먼저 정리 후보로 고르고, 이후 Topic에 연결되지 않은 오래된 DataItem을 soft-delete 후보로 고릅니다.
- `StorageCleanupManager`는 MediaStore 원본을 삭제하지 않고 앱 DB의 DataItem만 soft-delete합니다.
- `RoomStorageCleanupStore`가 active DataItem, Topic 연결 ID, soft-delete query를 연결합니다.

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
