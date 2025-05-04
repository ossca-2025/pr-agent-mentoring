## [config]

기본적인 전역 설정을 담당하며, 주요 동작 방식과 모델 사용 등을 정의

```bash
[config]
model="o4-mini"
fallback_models=["gpt-4.1"]
response_language="en-US"
max_model_tokens=32000
temperature=0.2
verbosity_level=0
...
```

모델 설정 관련

| 설정 항목 | 설명 |
| --- | --- |
| `model` |  **기본으로 사용할 LLM의 이름**을 설정.  예시: `"gpt-4"`, `"gpt-3.5-turbo"`, `"gemini/gemini-1.5-flash"`   실제 실행 시, 이 모델이 가장 먼저 사용되며, 실패 시 fallback 모델로 넘어감. |
| `fallback_models` |  기본 모델이 과부하, 응답 실패 등으로 **작동하지 않을 경우 대체할 후보 모델 목록**.   리스트 형태로 작성하며, 우선순위대로 시도   예시: `["gpt-4.1", "gpt-3.5-turbo"]` |
| `max_model_tokens` |  LLM이 처리할 수 있는 **최대 토큰 수**를 설정  한 번의 요청에서 모델이 읽고 생성할 수 있는 최대 입력/출력 토큰 수의 합  예시: GPT-4는 약 32,000 토큰까지 지원 |
| `custom_model_max_tokens` |  직접 지정한 사용자 정의 모델의 최대 토큰 수를 오버라이드할 수 있음.   설정하지 않으면 기본값 또는 위의 `max_model_tokens`가 사용됨 |
| `model_token_count_estimate_factor`  | 모델 입력 토큰을 추정할 때 **예상보다 많은 토큰이 필요할 경우를 대비해 여유분**을 둠.   예시: `0.3` 설정 시, 실제 계산보다 30% 많은 토큰을 추정해 모델 오류를 줄임 |

응답 형식 및 로깅 관련

| 설정 항목 | 설명 |
| --- | --- |
| `response_language` | **AI 응답의 언어**를 설정.   ISO 639 (언어) + ISO 3166 (국가) 코드 형식 사용  예시: `"ko-KR"` → 한국어, `"en-US"` → 미국 영어 |
| `temperature` |  **AI 출력의 창의성**을 조절하는 파라미터   `0`에 가까울수록 **정확하고 일관된** 응답 , `1`에 가까울수록 **다양하고 창의적인** 응답  일반적으로 리뷰나 문서 생성용 AI에는 `0.2`~`0.5` 사이 사용 |
| `verbosity_level` | CLI 또는 GitHub에서 AI 동작과정의 **출력 로그 상세 정도**를 설정 `0`: 기본값, 적은 정보  , `1`: 중간 수준의 정보  , `2`: 디버깅용, 매우 자세한 로그 출력 |

configuration.toml에서 수정 가능

<br/>

## [pr_custom_prompt]

사용자가 직접 정의한 맞춤형 프롬프트(prompt)를 바탕으로 코드 리뷰 피드백을 생성할 수 있도록 함. 즉, 특정 기준이나 컨벤션에 맞춘 리뷰가 필요할 때 사용

```toml
[pr_custom_prompt]
prompt = """
Please review the code with the following criteria:
- Ensure functions follow proper naming conventions
- Check for magic numbers and suggest constants
- Recommend performance optimizations
"""
suggestions_score_threshold = 6
num_code_suggestions_per_chunk = 3
self_reflect_on_custom_suggestions = true
enable_help_text = false
```

| 설정 항목 | 설명 |
| --- | --- |
| `prompt` | PR에 대한 **AI 리뷰 프롬프트를 직접 작성**할 수 있고 코드에 대해 어떤 관점에서 피드백할지 명확하게 명시 가능 또한 여러 줄로 작성 가능하며, 마크다운 포맷도 지원 |
| `suggestions_score_threshold` |  **AI가 생성한 코드 제안(suggestion)의 품질 점수 기준**(0~10 사이 정수 ), 지정된 점수 이상인 제안만 실제 리뷰에 포함됨 |
| `num_code_suggestions_per_chunk` |  하나의 코드 덩어리(chunk)에 대해 **AI가 최대 몇 개의 제안을 생성할지** 설정 , 숫자가 높을수록 더 많은 대안/리팩토링 제안을 받을 수 있음 |
| `self_reflect_on_custom_suggestions`  |  **AI가 제안한 피드백을 다시 스스로 검토**해 정확성과 적절성을 높임 , `true`로 설정하면 제안 품질이 좋아지는 대신 시간이 더 소요될 수 있음 |
| `enable_help_text` |  PR에 AI가 자동으로 **도움말 텍스트를 추가할지 여부,** `false`로 설정하면 결과만 깔끔하게 출력됨 |

PR에서 아래 명령어로 사용

```bash
python3 -m pr_agent.cli --pr_url=<your-pr-url> custom_prompt
```

<br/>

## [pr_config]

PR Agent의 설정 변경 명령`/config` 와 관련된 설정 섹션, 해당 설정은 특정 설정을 동적으로 보고 수정할 수 있도록 지원

```toml
[pr_config]
```

현재는 빈 상태로 존재, but `/config` 명령어 실행 시 내부적으로 confguration.toml의 설정을 불러오고 사용자에게 보여주는데 활용

CLI 명령어

```bash
python3 -m pr_agent.cli --pr_url=<PR_URL> config
```

<br/>

## [litellm]

여러 LLM API(OpenAI, Azure, Anthropic 등)를 **하나의 통합 인터페이스로 사용할 수 있게 도와주는 경량 라이브러리**

**PR Agent는 선택적으로 LiteLLM을 통해 다양한 LLM을 호출할 수 있도록 설정을 지원**

```toml
[litellm]
# use_client = false
# drop_params = false
enable_callbacks = false
success_callback = []
failure_callback = []
service_callback = []
```

| 항목 | 설명 |
| --- | --- |
| `use_client` | LiteLLM의 클라이언트 객체를 사용할지 여부 (주석 처리 시 기본값 `false`) |
| `drop_params` | LLM 호출 시 전달되는 파라미터 일부를 제거할지 여부 (보안이나 경량화 목적) |
| `enable_callbacks` | 응답 성공/실패 시 사용자 정의 콜백 함수 사용 여부 |
| `success_callback` | LLM 호출 성공 시 실행할 콜백 함수 리스트 |
| `failure_callback` | 호출 실패 시 실행할 콜백 함수 리스트 |
| `service_callback` | 모든 호출에 대해 실행되는 콜백 함수 리스트 |

기본적으로 Litellm 사용은 필수가 아닌 옵션 !

실행 예시

```toml
[litellm]
use_client = true
enable_callbacks = true
success_callback = ["log_success"]
failure_callback = ["notify_admin"]
```

설명

| 항목 | 의미 |
| --- | --- |
| `use_client = true` | LiteLLM 클라이언트를 실제 사용하도록 설정 |
| `enable_callbacks = true` | 콜백 기능 전체 활성화 |
| `success_callback = ["log_success"]` | LLM 호출 성공 시 `log_success()` 함수 호출 |
| `failure_callback = ["send_slack_alert"]` | 실패 시 슬랙 경고 전송 |
| `service_callback = ["track_usage"]` | 모든 요청마다 호출, 예: 사용량 추적용 |

<br/>

## [pinecone]
PR Agent가 **유사한 과거 이슈나 PR을 찾을 때** 사용하는 **벡터 데이터베이스,**`/similar_issue` 명령어에서 사용

```toml
[pinecone]
api_key = "your-pinecone-api-key"
environment = "gcp-starter"
```

셜명

| 항목 | 설명 |
| --- | --- |
| `api_key` | Pinecone 서비스에 접근할 수 있는 **비밀 키**. [https://www.pinecone.io](https://www.pinecone.io/)에서 발급받아야 함. |
| `environment` | Pinecone 프로젝트의 환경, 예시: `"gcp-starter"`, `"us-west1-gcp"` 등. 계정의 콘솔에서 확인 가능. |

**Pinecone이 사용되는 기능**

- `pr_similar_issue` 섹션의 설정과 함께 작동
- `/similar_issue` 명령어로 GitHub 이슈와 PR을 벡터화하여 유사도 검색 수행
- `vectordb = "pinecone"` 설정 시 Pinecone 사용 (대안: `lancedb`)

<br/>

## [lancedb]

`pinecone`과 마찬가지로 **유사한 이슈/PR 검색을 위한 벡터 데이터베이스**인데, **로컬(내 컴퓨터)** 에 저장된 파일 기반으로 작동
클라우드 API 키 없이 사용할 수 있어 **개발/테스트용**으로 많이 활용

```toml
[lancedb]
uri = "./lancedb"
```

| 항목 | 설명 |
| --- | --- |
| `uri` | 벡터 데이터베이스가 저장될 경로.  예: `"./lancedb"`는 현재 디렉토리에 `lancedb` 폴더를 생성하여 사용 |

**동작방법**

- `/similar_issue` 명령어를 실행하면, PR이나 Issue를 벡터로 변환한 후 이 경로에 저장함
- 이후 검색 시에도 이 로컬 DB를 참조해 유사 이슈를 찾아줌
- Pinecone처럼 API 키 없이 사용 가능