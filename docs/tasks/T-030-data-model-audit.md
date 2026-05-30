# T-030 DataItem/Topic/Analysis/Action 모델 및 Room 기준 정리

## 목적

모든 수집, 분석, Topic, 외부 앱 전송 흐름이 공유할 데이터 모델과 Room 저장 계약을 확정합니다.

## 작업 상태

- Status: Done
- Owner: Codex
- Branch: `chore/T-030-data-model-audit`
- Depends on: `T-020-architecture-baseline`
- Blocked by: 없음
- Ready criteria: 패키지 구조와 Gradle/Room/Hilt 기준이 확정됨
- Can run in parallel with: 없음
- Cannot run with: `T-040`, `T-050`, `T-100`, `T-120`, `T-140`, `T-170`

## 수정 허용 파일

- `app/src/main/java/.../domain/model/`
- `app/src/main/java/.../data/model/`
- `app/src/main/java/.../data/source/local/`
- `app/src/main/java/.../domain/repository/`
- `app/src/main/java/.../data/repository/`
- 관련 단위 테스트
- `docs/ARCHITECTURE.md`
- `docs/tasks/T-030-data-model-audit.md`

## 수정 금지 파일

- Compose 화면 구현
- Android Manifest
- Share/Tile/MediaStore 세부 구현
- Samsung export 세부 구현

## 구현 내용

- `DataItem`, `Topic`, `TopicAnalysis`, `TopicAction`의 domain model을 확정합니다.
- Entity/DAO/Database 구조를 정의합니다.
- TopicAction은 Summary, Calendar, TODO를 우선하되 Notes/Reminder export payload로 변환 가능하게 설계합니다.
- 중요 표시, 분석 상태, retry count, cluster id, last sync 관련 필드를 검토합니다.

## 체크리스트

- [x] 코드 읽기
- [x] 관련 문서 확인
- [x] 선행 task 완료 여부 확인
- [x] 구현
- [x] 빌드 확인
- [x] 테스트/수동 확인
- [x] 변경 요약 작성
- [x] PR 작성 내용 정리

> 현재 작업공간은 아직 Git repository가 아니며, 사용자 승인 전 commit/push/PR 생성 금지 조건이 있으므로 실제 PR은 만들지 않았습니다.

## 완료 기준

- 핵심 model/entity/dao/repository 계약이 빌드됩니다.
- 이후 수집/AI/UI task가 사용할 API가 문서화됩니다.
- 모델 변경 리스크와 migration 필요 여부가 기록됩니다.

## T-030 결과 요약

- `DataItem`, `Topic`, `TopicAnalysis`, `TopicAction` domain model을 추가했습니다.
- `DataItemEntity`, `TopicEntity`, `TopicItemCrossRefEntity`, `TopicAnalysisEntity`, `TopicActionEntity`를 추가했습니다.
- `DataItemDao`, `TopicDao`, `TopicAnalysisDao`, `TopicActionDao`, `SmartClipboardDatabase`를 추가했습니다.
- `DataRepository` interface와 `DataRepositoryImpl` 기본 구현을 추가했습니다.
- Entity는 enum 값을 문자열로 저장하고 mapper에서 domain enum으로 복원합니다.
- `TopicAction`은 사용자가 검토하는 초안이며, Calendar/Notes/Reminder 전송은 후속 export task에서 처리합니다.
- `SmartClipboardDatabase`는 version `1`, `exportSchema = false`로 시작합니다. schema export는 Git/CI 운영 기준이 정해진 뒤 별도 승인 task로 전환합니다.

## 검증 방법

- Room compile 확인
- mapper 단위 테스트
- `.\gradlew.bat testDebugUnitTest`
- `.\gradlew.bat assembleDebug test`
- `docs/ARCHITECTURE.md` 모델 관계와 코드 비교

## PR에 반드시 적을 내용

- 모델 필드와 의도
- 저장/조회 API
- migration 또는 schema 변경 여부
- 충돌 가능성이 있는 후속 task
