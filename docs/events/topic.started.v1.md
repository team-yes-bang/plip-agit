# topic.started (v1)

## 개요

묶인 토픽이 시작될 때 발행되는 이벤트입니다. agit-service는 `topics[].startedAt`을 갱신합니다.

- **Topic:** `topic.started`
- **발행 주체:** topic-service
- **구독자:** agit-service 읽기 모델 투영

## Payload

```json
{
  "agitUuid": "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e",
  "topicId": "0190abcd-1111-7abc-def0-123456789abc",
  "startedAt": "2026-08-14T04:00:00Z",
  "occurredAt": "2026-08-14T04:00:00Z"
}
```

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| agitUuid | String (UUID) | O | 아지트 UUID |
| topicId | String | O | 토픽 식별자 |
| startedAt | String (ISO 8601) | O | 토픽 시작 시각 |
| occurredAt | String (ISO 8601) | O | 이벤트 발생 시각 |

## Key

- Kafka Message Key: `agitUuid`

## 투영 규칙

- `startedAt`이 없으면 skip
- 아지트 읽기 문서가 없으면 skip
- `topics[]`에 없으면 `topicId` + `startedAt`으로 추가
