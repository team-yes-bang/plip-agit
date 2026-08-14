# agit.invite-code-reissued (v1)

## 개요

초대 코드 재발급 완료 시 발행되는 이벤트입니다.

- **Topic:** `agit.invite-code-reissued`
- **발행 주체:** agit-service (재발급 TX 후)
- **구독자:** 읽기 모델 투영 (예정)

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "previousCode": "A1B2C3",
  "code": "D4E5F6",
  "occurredAt": "2026-08-14T04:00:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID (UUIDv7) |
| previousCode | String | O | 이전 초대 코드 |
| code | String | O | 새 초대 코드 |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 발행 시점

- 초대 코드 재발급 완료 후

## 비고

- 이벤트 발행 실패 시 재발급 자체는 롤백되지 않습니다 (at-most-once).
