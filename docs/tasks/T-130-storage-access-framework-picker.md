# T-130 Storage Access Framework 파일 직접 선택

## 목적

사용자가 직접 파일, 이미지, PDF를 선택해 SmartClipboard에 저장할 수 있는 SAF 기반 진입점을 만듭니다.

## 작업 상태

- Status: Ready
- Owner: 미배정
- Branch: `feat/T-130-storage-access-framework-picker`
- Depends on: `T-040-navigation-baseline`, `T-050-permission-and-manifest-baseline`, `T-100-share-target-flow`, `T-110-quick-tile-flow`, `T-120-media-store-batch-query`
- Blocked by: 없음
- Ready criteria: 파일 선택 진입 화면과 DataItem file 계약이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-040`

## 수정 허용 파일

- `app/src/main/java/.../collection/filepicker/`
- SAF entry UI hook
- SAF tests
- `docs/tasks/T-130-storage-access-framework-picker.md`

## 수정 금지 파일

- Navigation route 변경
- DataItem model 계약 변경
- MediaStore 자동 수집 로직
- AI 분석 로직

## 구현 내용

- `ActivityResultContracts.GetContent` 또는 `GetMultipleContents`를 사용합니다.
- 선택된 URI를 DataItem으로 저장합니다.
- MIME type과 표시 이름을 가능한 범위에서 기록합니다.
- Inbox 또는 Home의 작은 진입점과 연결합니다.

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

- 사용자가 파일을 선택하면 저장됩니다.
- 취소 시 아무 데이터도 생성되지 않습니다.
- 권한/URI 접근 실패가 안전하게 처리됩니다.

## 검증 방법

- 이미지 선택
- PDF 또는 일반 파일 선택
- 선택 취소
- fake repository 저장 테스트

## PR에 반드시 적을 내용

- 지원 MIME type
- 단일/다중 선택 여부
- 실패 처리
- UI 진입점 위치
