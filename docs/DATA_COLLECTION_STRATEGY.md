# 데이터 수집 전략

SmartClipboard는 몰래 수집하는 앱이 아닙니다. Android 정책 안에서 사용자 액션을 최소화해 자료를 저장하고, 나중에 작업으로 연결합니다.

## Android API 제약상 불가능한 것

### 백그라운드 클립보드 지속 감시 불가

Android 10(API 29)부터 focus 없는 백그라운드 앱은 클립보드를 읽을 수 없습니다. 우리 앱이 foreground이거나 투명 Activity로 focus를 얻은 순간에만 최신 Primary Clip 1개를 읽습니다.

### 다른 앱 공유 흐름 자동 가로채기 불가

다른 앱이 다른 대상에 공유하려는 흐름을 중간에서 가로챌 수 없습니다. Android Share Sheet에서 사용자가 SmartClipboard를 직접 선택한 경우만 수집합니다.

### 화면 내용 자동 읽기 금지

화면 내용을 자동으로 읽어 저장하는 방식은 정책 리스크가 큽니다. 접근성 API를 무단 수집 목적으로 사용하지 않습니다.

## 가능한 수집 흐름

### 1. Share Target 기반 링크/텍스트/이미지 수신

사용자 액션:

- 다른 앱에서 공유 버튼을 누릅니다.
- Share Sheet에서 SmartClipboard를 선택합니다.

필요 컴포넌트:

- `ShareReceiverActivity`
- `ShareContentHandler`
- `ACTION_SEND`
- `ACTION_SEND_MULTIPLE`

Manifest 작업:

- `text/plain`
- `image/*`
- `application/pdf`
- `application/octet-stream`
- `ShareReceiverActivity`: `com.smartclipboard.ai.presentation.share.ShareReceiverActivity`
- `ACTION_SEND`, `ACTION_SEND_MULTIPLE` 모두 등록

권한:

- 공유받은 URI는 intent grant 범위 안에서 읽습니다.
- 보존 대상은 내부 저장소로 복사할 수 있습니다.

링크 처리:

- URL 저장 후 OG/본문 추출을 1~2초만 시도합니다.
- 성공하면 enrichment를 저장합니다.
- 늦거나 실패하면 pending 상태로 남기고 다음 앱 실행 또는 재분석에서 처리합니다.
- Jsoup OG 태그 추출은 `Dispatchers.IO`에서 실행합니다.

실패 처리:

- 빈 공유 데이터
- 읽기 권한 없음
- 파일 복사 실패
- OG 추출 timeout
- 최대 3회 retry 후 pending/failed 상태 저장

사용자 피드백:

- 성공: `SmartClipboard에 담았어요`
- 실패: `저장하지 못했어요`

### 2. Quick Settings Tile + Transparent Activity 기반 최근 클립보드 수집

사용자 액션:

- 텍스트나 링크를 복사합니다.
- Quick Settings Tile을 누릅니다.

필요 컴포넌트:

- `ClipboardCaptureTileService`
- `ClipboardCaptureActivity`
- `ClipboardDataSource`
- `ClipboardCaptureHandler`

Manifest 작업:

- `TileService` 등록
- `android.permission.BIND_QUICK_SETTINGS_TILE`
- 투명 Activity theme
- `ClipboardCaptureTileService`: `com.smartclipboard.ai.collection.clipboard.ClipboardCaptureTileService`
- `ClipboardCaptureActivity`: `com.smartclipboard.ai.presentation.clipboard.ClipboardCaptureActivity`

권한:

- 별도 runtime permission은 없지만 foreground/focus 제약이 있습니다.

실패 처리:

- 클립보드 비어 있음
- 텍스트 변환 실패
- 중복 저장
- Activity launch 실패

### 3. 앱 실행 시 Last Sync Time 기준 MediaStore Batch Query

사용자 액션:

- 사용자가 앱을 엽니다.
- 앱은 마지막 앱 종료 이후부터 현재 실행 시점까지의 새 이미지를 조회합니다.

수집 범위:

- 모든 새 이미지 자동 수집
- 스크린샷, 다운로드 이미지, 카메라 사진 포함
- 자동 수집된 항목은 DataItem으로 저장

필요 컴포넌트:

- `MainActivity`
- `MediaSyncManager`
- `MediaStoreDataSource`
- `MediaImportHandler`
- Settings storage policy

Manifest/권한:

- API 33 이상: `READ_MEDIA_IMAGES`
- API 34 이상: `READ_MEDIA_VISUAL_USER_SELECTED` 고려
- API 32 이하: `READ_EXTERNAL_STORAGE`
- MVP의 기본 목표는 모든 새 이미지 수집이므로 기본 권한 모드는 full library입니다.
- 사용자가 부분 접근만 허용한 경우에는 `READ_MEDIA_VISUAL_USER_SELECTED` fallback을 사용하고 Settings/Inbox에서 제한 상태를 설명합니다.

권한 helper:

- `MediaPermissionPolicy.requiredImagePermissions(sdkInt, mode)`
- `ImageAccessMode.FullLibrary`: API 33 이상 `READ_MEDIA_IMAGES`, API 32 이하 `READ_EXTERNAL_STORAGE`
- `ImageAccessMode.UserSelectedFallback`: API 34 이상 `READ_MEDIA_VISUAL_USER_SELECTED`

저장 정책:

- 기본은 MediaStore URI와 메타데이터 저장
- OCR/이미지 분석/Gemini 정리 결과는 DB 저장
- 사용자가 중요 표시, 보존, Topic 고정을 한 항목만 내부 저장소로 복사
- URI 접근이 깨져도 분석 결과와 fallback 정보를 남깁니다.

자동 수집 기간:

- 기본값: 마지막 앱 종료 이후부터 현재 실행 시점까지
- Settings에서 최근 1시간, 최근 24시간, 최근 7일, 사용자 지정 기간으로 변경 가능

실패 처리:

- 권한 없음
- 부분 접근
- MediaStore column 접근 실패
- 중복 URI
- 이미지 로딩 실패
- OCR 실패 시 최대 3회 retry 후 다음 실행에서 재시도

### 4. Storage Access Framework 직접 파일 선택

사용자 액션:

- 앱에서 파일 선택 버튼을 누릅니다.
- Android system picker에서 파일을 선택합니다.

필요 컴포넌트:

- `ActivityResultContracts.GetContent`
- `ActivityResultContracts.GetMultipleContents`
- 또는 `Intent.ACTION_GET_CONTENT`

권한:

- SAF picker가 반환한 URI grant를 사용합니다.
- 장기 보존이 필요하면 내부 저장소 복사 또는 persistable permission 여부를 별도 검토합니다.

실패 처리:

- 선택 취소
- 지원하지 않는 MIME type
- URI 읽기 실패
- 파일 크기 과대

## OCR 전략

- 이미지 OCR은 ML Kit 또는 현재 `SourceExtractor`/향후 `OcrProcessor` 구조를 활용합니다.
- OCR은 DataItem 저장 후 enqueue합니다.
- OCR 결과는 DataItem enrichment로 저장합니다.
- 실패 시 최대 3회 retry 후 다음 앱 실행 또는 `AI 다시 분석`에서 재시도합니다.

## OG 추출 전략

- 링크 미리보기는 Open Graph 태그를 우선합니다.
- 필요한 정보: `og:title`, `og:description`, `og:image`
- Jsoup 네트워크 작업은 `Dispatchers.IO`에서 처리합니다.
- 실패해도 원본 URL 저장은 유지합니다.

## 자동 삭제 정책

용량 한도 초과 시 아래 순서로 삭제합니다.

1. 썸네일/임시 파일/분석 중간 캐시
2. 중요 표시 또는 Topic 고정이 아닌 내부 원본 복사본
3. Topic에 연결되지 않고 중요 표시도 없는 오래된 DataItem

확정 Topic, 중요 표시, 사용자가 보존한 항목은 자동 삭제 대상에서 제외합니다.

## T-050 Manifest 기준

`T-050` 기준 Manifest에는 아래만 선언합니다.

- 인터넷: `INTERNET`
- 이미지 읽기: `READ_MEDIA_IMAGES`
- Android 14 이상 부분 접근 fallback: `READ_MEDIA_VISUAL_USER_SELECTED`
- Android 12 이하 이미지 접근: `READ_EXTERNAL_STORAGE`, `maxSdkVersion=32`
- Share Target Activity: `ShareReceiverActivity`
- Quick Settings Tile Service: `ClipboardCaptureTileService`
- Clipboard focus 획득용 투명 Activity: `ClipboardCaptureActivity`
- Samsung Notes/Calendar/Reminder package queries

일부러 추가하지 않는 권한:

- `READ_CALENDAR`, `WRITE_CALENDAR`: Calendar insert intent는 사용자 검토 화면을 열어 처리하므로 MVP baseline에서는 직접 캘린더 DB 권한을 요청하지 않습니다.
- 접근성 서비스 권한: 화면 자동 감시 또는 무단 수집으로 오해될 수 있어 사용하지 않습니다.
- `QUERY_ALL_PACKAGES`: 필요한 삼성 앱 package만 `<queries>`에 명시합니다.
