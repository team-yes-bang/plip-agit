# agit.deleted (v1)

## 개요

호스트가 혼자 남아 아지트를 나가며 소프트 삭제한 뒤 발행되는 이벤트입니다.

- **Topic:** `agit.deleted`
- **발행 주체:** agit-service (삭제 TX 후)
- **구독자:** 읽기 모델 투영 (예정)

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "userUuid": "01912345-6789-7abc-def0-123456789abc",
  "occurredAt": "2026-08-14T04:00:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID (UUIDv7) |
| userUuid | String (UUID) | O | 삭제를 유발한 호스트 사용자 UUID |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 발행 시점

- 호스트 혼자 나가기 완료 후 (아지트 status `DELETED`)

## 비고

- 이벤트 발행 실패 시 삭제 자체는 롤백되지 않습니다 (at-most-once).
