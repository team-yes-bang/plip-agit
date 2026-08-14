# agit.host-transferred (v1)

## 개요

아지트장 권한을 ACTIVE GUEST에게 위임한 뒤 발행되는 이벤트입니다.

- **Topic:** `agit.host-transferred`
- **발행 주체:** agit-service (위임 TX 후)
- **구독자:** 읽기 모델 투영 (예정)

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "previousHostUserUuid": "01912345-6789-7abc-def0-123456789abc",
  "newHostUserUuid": "01912345-6789-7abc-def0-123456789abd",
  "newHostNickname": "게스트",
  "occurredAt": "2026-08-14T04:00:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID (UUIDv7) |
| previousHostUserUuid | String (UUID) | O | 이전 아지트장 사용자 UUID |
| newHostUserUuid | String (UUID) | O | 새 아지트장 사용자 UUID |
| newHostNickname | String | O | 새 아지트장 닉네임 |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 발행 시점

- 아지트장 위임 완료 후

## 비고

- 이벤트 발행 실패 시 위임 자체는 롤백되지 않습니다 (at-most-once).
