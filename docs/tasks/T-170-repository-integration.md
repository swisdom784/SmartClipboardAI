# T-170 DataRepository 통합 정리

## 목적

수집, 전처리, 추천, 저장 정책을 Home/Inbox/Topic/Analysis ViewModel이 사용할 수 있는 안정된 API로 통합합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `chore/T-170-repository-integration`
- Depends on: `T-100`, `T-110`, `T-120`, `T-130`, `T-140`, `T-150`, `T-160`
- Blocked by: 없음
- Ready criteria: 완료됨
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

T-170 구현 결과:

- `HomeRepositoryState`와 `InboxFilter`를 추가했습니다.
- `DataRepository`가 Home 상태, Inbox 필터, 현재 추천 세션, 추천 refresh, 저장 사용량, 저장 cleanup API를 제공합니다.
- `DataRepositoryImpl`이 추천 세션과 저장 cleanup manager를 ViewModel용 facade로 노출합니다.
- `RepositoryRecommendationDataSource`는 `DataRepository` 대신 `DataItemDao`를 사용해 순환 의존성을 피합니다.
- `DataRepositoryImplIntegrationTest`가 Home state combine, Inbox filter, 추천 refresh, storage usage API를 검증합니다.

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
