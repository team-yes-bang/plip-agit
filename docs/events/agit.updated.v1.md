# agit.updated (v1)

## 개요

아지트 메타(제목·소개·정원·섬네일) 수정 완료 시 발행되는 이벤트입니다.

- **Topic:** `agit.updated`
- **발행 주체:** agit-service (수정 TX 후)
- **구독자:** 읽기 모델 투영 (예정)

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "agitName": "주말 보드게임",
  "description": "가볍게 즐겨요",
  "maximumCapacity": 8,
  "thumbnailPath": "agits/thumbnails/sample.png",
  "occurredAt": "2026-08-14T04:00:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID (UUIDv7) |
| agitName | String | O | 아지트 제목 |
| description | String | O | 소개글 |
| maximumCapacity | Number | O | 최대 인원 |
| thumbnailPath | String | X | 섬네일 경로 |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 발행 시점

- 아지트 메타 수정 완료 후

## 비고

- 이벤트 발행 실패 시 수정 자체는 롤백되지 않습니다 (at-most-once).
