
## 4주차 목표 및 과제

- PR Agent 에 기여할만한 아이디어 고민하기
- Top 3 아이디어 정리 내용

<br/>

## 아이디어 조사

- GitLab 푸시 이벤트 시 .pr_agent.toml 설정이 적용되지 않는 버그를 수정 [#1686](https://github.com/qodo-ai/pr-agent/issues/1686)
- PR-Agent에 reasoning_effort 설정을 추가하여 LLM 추론 수준을 제어할 수 있는 기능을 도입
- PR에서 함수 호출 흐름을 자동으로 감지해 Mermaid 기반의 Sequence Diagram을 생성
- LocalProvider 기능 도입
- 여러개의 PR중에서도 위험도, CI상태 작성 시간 등을 기반으로 리뷰 우선순위 자동정렬 기능
- 예외 처리 핸들러 별도로 생성
- 사용자 맞춤형 PR 요약 템플릿 기능

## TOP 3 아이디어 

### [Mermaid 기반의 Diagram 🧜‍](./ideation/sequence_diagram.md)

### [LLM Reasoning effort 🧜‍](./ideation/llm_reasoning_effort.md)

### [Local Provider 기능 🧜‍](./ideation/local_provider.md)
