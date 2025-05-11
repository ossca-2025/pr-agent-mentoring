# [LLM reasoning_effort]

## 현재 문제점

- OpenAI API에서는 reasoning_effort 파라미터를 통해 응답의 추론 수준을 조절할 수 있음.

- PR-Agent에서는 오직 내부 설정값(get_settings().config.reasoning_effort)만 사용하여 외부에서 동적으로 조절 불가.

- 운영 환경(로컬, CI/CD, 클라우드)별로 추론 강도를 다르게 설정할 수 있는 유연성 부족

## 구현방법

### `환경변수 우선 적용`

- 기존 get_settings().config.reasoning_effort 대신, 먼저 os.environ.get("REASONING_EFFORT")로 값 읽기.
- 환경변수 값이 존재하면 이를 사용하고, 없으면 내부 설정값으로 대체.

### `유효성 검증 강화`
- 허용 값 목록: "low", "medium", "high".
- 환경변수 값이 이 목록에 없으면 기본값 "medium" 사용.

### 코드 예시
```python
def get_reasoning_effort():
    val = os.environ.get("REASONING_EFFORT")
    if val in ("low", "medium", "high"):
        return val
    return get_settings().config.reasoning_effort or "medium"

client = OpenAI(reasoning_effort=get_reasoning_effort())
```

## 기대효과

- 운영 환경에 따라 REASONING_EFFORT 환경변수로 추론 강도를 유연하게 조절 가능
  - 낮은 추론 강도를 선택 시 API 토큰 사용량 및 응답 시간 감소로 비용 절감.
  - 복잡한 PR 분석 시 높은 추론 강도로 심층 분석 유도 가능
