# T-120 앱 실행 시 Last Sync Time 기준 이미지 자동 수집

## 목적

앱 실행 또는 재진입 시 마지막 동기화 이후 추가된 모든 이미지를 MediaStore에서 조회해 `DataItem`으로 저장합니다.

## 작업 상태

- Status: Ready
- Owner: 미배정
- Branch: `feat/T-120-media-store-batch-query`
- Depends on: `T-050-permission-and-manifest-baseline`, `T-030-data-model-audit`, `T-040-navigation-baseline`, `T-100-share-target-flow`, `T-110-quick-tile-flow`
- Blocked by: 없음
- Ready criteria: 이미지 권한 전략, last sync 저장 위치, DataItem image 계약, 앱 root navigation 기준, 공통 저장 결과 처리 패턴이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-030`, `T-050`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../collection/media/`
- last sync preference 파일
- media import tests
- `docs/tasks/T-120-media-store-batch-query.md`

## 수정 금지 파일

- DataItem model 계약 변경
- Manifest/권한 선언 변경
- Home/Inbox UI 대규모 변경
- 저장 용량 삭제 정책

## 구현 내용

- Last Sync Time 이후 MediaStore 이미지를 batch query합니다.
- 모든 새 이미지를 기본 수집 대상으로 봅니다.
- 중복 URI/ID를 방지합니다.
- 성공 시 last sync를 안전하게 갱신합니다.
- 실패한 항목은 상태를 남기고 앱 실행을 막지 않습니다.

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

- 앱 재실행 시 새 이미지가 자동 저장됩니다.
- 권한 거부 시 앱이 깨지지 않고 Settings/Home에서 안내할 수 있는 상태가 남습니다.
- 중복 수집이 발생하지 않습니다.

## 검증 방법

- 새 스크린샷 촬영 후 앱 실행
- 갤러리 이미지 추가 후 앱 실행
- 권한 허용/거부 케이스 확인
- fake MediaStore datasource 테스트

## PR에 반드시 적을 내용

- API 버전별 권한 동작
- last sync 갱신 조건
- 중복 방지 기준
- 실패/부분 성공 처리
