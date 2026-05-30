# SmartClipboard 프로젝트 명세

## 프로젝트 목적

SmartClipboard는 사용자가 일상적으로 모으는 사진, 스크린샷, 복사한 텍스트, 복사한 링크를 하나의 자료 흐름으로 정리하고, 이를 작업 주제인 `Topic`과 AI Agent의 실행 가능한 초안으로 연결하는 Android 앱입니다.

앱의 목표는 단순 저장이 아니라, 흩어진 자료를 사용자의 다음 작업으로 이어주는 것입니다. 사용자는 자료를 직접 정리하는 부담을 줄이고, AI가 준비한 요약, 일정, 리마인더, 노트 초안을 검토한 뒤 실행합니다.

## 사용자-facing 이름

- 앱 표시 이름: `SmartClipboard`
- 프로젝트 또는 레포 이름은 필요에 따라 `SmartClipboardAI`를 사용할 수 있습니다.
- UI 문구는 `SmartClipboard에 담았어요`처럼 짧고 단정한 톤을 사용합니다.

## 핵심 사용자

- 여러 앱에서 링크, 캡처, 메모를 모아두는 대학생, 팀 프로젝트 참여자, 리서치 사용자
- 여행, 회의, 과제, 일정 준비처럼 여러 자료를 한 주제로 묶어야 하는 사용자
- AI가 바로 실행하기보다 초안을 만들고 사용자가 검토하는 흐름을 원하는 사용자
- Galaxy 기본 앱처럼 조용히 자료를 챙겨주는 비서형 경험을 기대하는 사용자

## 핵심 문제

- 복사한 텍스트와 링크는 시간이 지나면 맥락을 잃기 쉽습니다.
- 스크린샷과 사진은 갤러리에 묻히고 작업 단위로 정리되지 않습니다.
- Android 정책상 백그라운드에서 클립보드나 화면을 몰래 감시할 수 없습니다.
- AI가 외부 앱에 바로 실행하면 사용자가 통제권을 잃습니다.
- 기능이 많아지면 앱이 복잡해져 사용자가 무엇을 해야 하는지 알기 어려워집니다.

## 앱이 해결하는 방식

1. 사용자의 명시적 액션 또는 앱 실행 시점의 허용된 Android API로 자료를 수집합니다.
2. 모든 입력을 `DataItem`으로 저장합니다.
3. OCR, OG 추출, Gemini 정리, 클러스터링으로 자료를 보강합니다.
4. AI가 이번 실행에서 정리할 만한 Topic 후보를 제안합니다.
5. 사용자는 추천을 수락/수정하거나 직접 작업을 입력합니다.
6. 확정된 Topic을 기준으로 `TopicAnalysis`와 `TopicAction` 초안을 생성합니다.
7. 사용자는 초안을 검토하고 수정한 뒤 Samsung Notes, Samsung Calendar, Samsung Reminder로 보냅니다.

## 이번 버전 MVP 범위

- Share Target 기반 링크/텍스트/이미지/파일 수신
- Quick Settings Tile + 투명 Activity 기반 최근 클립보드 수집
- 앱 실행 시 Last Sync Time 기준 모든 새 이미지 자동 수집
- Storage Access Framework 기반 직접 파일 선택
- 링크 OG/본문 추출
- 이미지/스크린샷 OCR
- Gemini 기반 자료 정리, Topic 후보 추천, Summary/Calendar/TODO 초안 생성
- Home / Inbox / Logs / Settings 탭 구조
- ChatGPT/Codex식 단순 Home UX
- Inbox에서 전체 자료 확인, 필터, 그리드/리스트 전환, 삭제, 중요 표시
- Logs에서 사용자가 확인한 작업 중심 기록 보기
- Samsung Notes, Calendar, Reminder 전송
- 자동 삭제 정책과 저장 용량 설정
- 협업 가능한 문서, task, branch, PR 기준

## 이번 버전에서 하지 않을 것

- 백그라운드 클립보드 지속 감시
- 화면 자동 감시
- 접근성 API를 통한 무단 수집
- 다른 앱 공유 흐름 중간 가로채기
- 클립보드 히스토리 전체 접근
- 사용자 검토 없는 외부 앱 자동 실행
- 과거 AI 추천 후보를 영구 보관
- 실제 모델 학습 또는 장기 개인화 학습
- Samsung 전용 비공개 SDK 의존

## 주요 입력 데이터

- 직접 찍은 사진
- 스크린샷
- 다운로드 이미지
- 복사한 텍스트
- 복사한 링크
- 사용자가 직접 선택한 파일/PDF

## 주요 결과물

### DataItem

모든 입력의 공통 저장 단위입니다. 타입, 원본 URI 또는 텍스트, 출처, 생성 시각, 분석 상태, enrichment, cluster 정보를 가집니다.

### Topic

사용자가 최종 확정한 작업 주제입니다. AI 추천은 Topic 후보일 뿐이며, 사용자가 수락하거나 수정한 뒤에만 Topic으로 저장됩니다.

### TopicAnalysis

Topic과 연결된 DataItem을 분석한 중간 결과입니다. 요약, 핵심 포인트, 사용된 자료 ID를 포함합니다.

### TopicAction

사용자가 검토할 수 있는 실행 초안입니다. MVP에서는 Summary, Calendar, TODO를 우선합니다. Samsung Notes, Calendar, Reminder로 전송될 payload를 포함합니다.

## 사용자 검토 원칙

AI Agent는 초안을 만들 수 있지만 최종 실행은 사용자가 검토한 뒤 명시적으로 수행합니다. Notes, Calendar, Reminder 전송은 사용자가 버튼을 눌렀을 때만 실행됩니다.

## Done line

- Product Done: 실제 사용 가능한 SmartClipboard 앱이 완성되어 주요 흐름을 안정적으로 사용할 수 있습니다.
- MVP Done: 수집, 분석, 추천, Topic 확정, 초안 생성, 삼성 앱 전송까지 end-to-end로 동작합니다.
- Task Done: task 문서의 완료 기준과 검증 방법을 충족하고, 변경 요약과 PR 내용을 정리한 상태입니다.

