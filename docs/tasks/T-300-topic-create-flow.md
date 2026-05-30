# T-300 사용자 입력 기반 Topic 생성

## 목적

Home 입력 또는 AI 추천 수락으로 사용자 검토 가능한 `Topic`을 생성합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-300-topic-create-flow`
- Depends on: `T-200-home-ux-redesign`
- Blocked by: `T-200` 미완료
- Ready criteria: Home 입력 이벤트와 Topic repository API가 확정됨
- Can run in parallel with: `T-310`은 UI 계약 합의 후 가능
- Cannot run with: Topic model/repository 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/topic/`
- Topic create ViewModel/use case
- Topic create tests
- `docs/tasks/T-300-topic-create-flow.md`

## 수정 금지 파일

- DataItem model/DAO 변경
- Home layout 대규모 변경
- AI analysis 생성 로직
- 외부 앱 export 구현

## 구현 내용

- 사용자가 입력한 문장을 Topic 초안으로 만듭니다.
- AI 추천 카드를 수락하면 Topic source를 `AI 추천`으로 저장합니다.
- 사용자가 직접 만든 Topic은 source를 `사용자 요청`으로 저장합니다.
- Topic 생성 후 자료 선택 또는 분석 화면으로 이어질 이벤트를 제공합니다.

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

- 사용자 요청과 AI 추천 기반 Topic이 구분됩니다.
- 생성된 Topic이 Home/Logs에 연결될 수 있습니다.
- 빈 입력이나 중복 생성이 안전하게 처리됩니다.

## 검증 방법

- Topic create ViewModel 테스트
- Home 입력 수동 확인
- AI 추천 수락 수동 확인

## PR에 반드시 적을 내용

- Topic source 처리
- 생성 실패/빈 입력 처리
- 후속 화면 이동 방식
- UI 스크린샷 또는 동작 설명
