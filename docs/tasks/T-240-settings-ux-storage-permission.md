# T-240 Settings 수집 기간, 용량, 권한 UX

## 목적

사용자가 자동 수집 기간, 저장 용량 한도, 자동 삭제 정책, 권한 상태를 조정하고 확인할 수 있게 합니다.

## 작업 상태

- Status: Not Ready
- Owner: 미배정
- Branch: `feat/T-240-settings-ux-storage-permission`
- Depends on: `T-040-navigation-baseline`, `T-160-storage-quota-cleanup`, `T-170-repository-integration`
- Blocked by: `T-040`, `T-160`, `T-170` 미완료
- Ready criteria: settings storage model과 quota policy가 확정됨
- Can run in parallel with: `T-200`, `T-210`, `T-230`
- Cannot run with: `T-160` policy 변경 작업

## 수정 허용 파일

- `app/src/main/java/.../presentation/settings/`
- Settings ViewModel
- settings state tests
- `docs/tasks/T-240-settings-ux-storage-permission.md`

## 수정 금지 파일

- storage cleanup 핵심 알고리즘 변경
- Manifest 권한 선언 변경
- Home/Inbox UI 세부 변경
- Gemini key 저장 정책 변경

## 구현 내용

- 수집 기간 preset: 마지막 종료 이후, 1시간, 24시간, 7일, custom을 제공합니다.
- 저장 용량 사용량과 한도를 표시합니다.
- 자동 삭제 정책을 짧게 보여줍니다.
- 권한 상태와 필요한 조치 버튼을 제공합니다.
- Gemini key는 하드코딩하지 않는다는 안내와 로컬 설정 기준을 유지합니다.

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

- 수집 기간 설정이 저장되고 다음 수집에 반영됩니다.
- 용량 상태가 표시됩니다.
- 권한 상태가 사용자에게 이해되게 표시됩니다.

## 검증 방법

- 설정 변경 후 재실행 확인
- custom 기간 입력 확인
- 권한 허용/거부 상태 확인
- fake settings repository 테스트

## PR에 반드시 적을 내용

- 설정 항목 목록
- 수집 기간 반영 방식
- 저장 용량 표시 방식
- 권한 안내 문구
