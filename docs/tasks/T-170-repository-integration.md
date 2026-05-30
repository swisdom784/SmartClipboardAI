# T-170 DataRepository 통합 정리

## 목적

수집, 전처리, 추천, 저장 정책을 Home/Inbox/Topic/Analysis ViewModel이 사용할 수 있는 안정된 API로 통합합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `chore/T-170-repository-integration`
- Depends on: `T-100`, `T-110`, `T-120`, `T-130`, `T-140`, `T-150`, `T-160`
- Blocked by: Phase 2/3 개별 구현 미완료
- Ready criteria: 수집/전처리/추천/정리 기능의 인터페이스가 모두 구현됨
- Can run in parallel with: 없음
- Cannot run with: repository를 만지는 모든 task

## 수정 허용 파일

- `app/src/main/java/.../domain/repository/`
- `app/src/main/java/.../data/repository/`
- repository integration tests
- `docs/ARCHITECTURE.md`
- `docs/tasks/T-170-repository-integration.md`

## 수정 금지 파일

- 화면 완성 UI
- Android Manifest
- Gradle 의존성 변경
- 외부 앱 intent 구현

## 구현 내용

- ViewModel이 필요한 query/command API를 정리합니다.
- Flow 기반 상태 구독을 안정화합니다.
- 수집/분석/삭제/추천 상태를 일관된 모델로 반환합니다.
- 후속 UI task가 repository 내부 구현을 알 필요 없게 만듭니다.

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

- 주요 ViewModel에서 사용할 repository API가 확정됩니다.
- integration test가 주요 데이터 흐름을 검증합니다.
- 공통 파일 변경 내용이 문서화됩니다.

## 검증 방법

- repository integration test
- fake datasource test
- ViewModel smoke compile

## PR에 반드시 적을 내용

- 공개 repository API
- 변경된 데이터 흐름
- 후속 UI task가 써야 할 메서드
- breaking change 여부
