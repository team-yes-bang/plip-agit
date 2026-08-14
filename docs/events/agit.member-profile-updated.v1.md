# agit.member-profile-updated (v1)

## 개요

아지트 멤버 프로필(닉네임·이미지) 수정 완료 시 발행되는 이벤트입니다.

- **Topic:** `agit.member-profile-updated`
- **발행 주체:** agit-service (프로필 수정 TX 후)
- **구독자:** 읽기 모델 투영 (예정)

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "userUuid": "01912345-6789-7abc-def0-123456789abc",
  "nickname": "새닉네임",
  "profileImagePath": "profiles/a.png",
  "occurredAt": "2026-08-14T04:00:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID (UUIDv7) |
| userUuid | String (UUID) | O | 수정한 사용자 UUID |
| nickname | String | O | 닉네임 |
| profileImagePath | String | X | 프로필 이미지 경로 |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 발행 시점

- 본인 멤버 프로필 수정 완료 후

## 비고

- 이벤트 발행 실패 시 수정 자체는 롤백되지 않습니다 (at-most-once).
