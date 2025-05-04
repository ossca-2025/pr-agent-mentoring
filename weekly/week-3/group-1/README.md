## 3주차 목표 및 과제

- 목표: PR Agent 개발 환경 설정 및 주요 태스크 코드 분석
- 1조 주요 과제: 기본 설정(usage 및 기본 설정) 조사, 분석, 사용, 정리

<br/>

# PR Agent 기본 설정

PR Agent의 기본 설정은 다양한 configuration 파일을 기반으로 구성된다.  
전역 설정 파일(`configuration.toml` 등)을 중심으로, 프로젝트별 `pyproject.toml`의 `[tool.pr-agent]` 섹션 또는 Wiki 설정 등이 병합되어 최종 설정이 완성된다. 이러한 설정은 우선순위에 따라 동적으로 반영되며, CLI 또는 GitHub Action 환경에서도 유연하게 적용된다.  

## Configuration file 종류 및 활용

Configuration File을 설정하는 방법은 다음의 세 가지가 있다. `우선순위: Wiki > Local > Global`

1. Wiki configuration page 💎
2. Local configuration file
3. Global configuration file 💎

### 1. Wiki configuration file 💎

Repository Wiki에 `.pr_agent.toml` 페이지를 만들어서 하는 방법이다. 

- 장점: 설정을 바꿀 때마다 커밋을 새로 하지 않아도 됨
- 느낀 점: configuration file의 변경 이력이 남으면서 간단하고 빠른 수정이 가능하다는 점과 Repository 내에 위치할 수 있다는 점 때문에 좋은 방식 같음

### 2. Local configuration file

 Repository의 default 브랜치에 `.pr_agent.toml` 파일을 업로드 하는 방법이다. 설정 적용을 위해선 PR을 만들기 전에 파일을 업로드해야 한다.

- 지원 플랫폼: GitHub, GitLab, Bitbucket, Azure DevOps
- 느낀 점: configuration file의 변경 이력이 남고 PR 생성 이후에 수정한 설정도 반영되어 좋은 방식 같음

### 3. Global configuration file 💎

organization에 pr-agent-settings라는 이름의 Repository를 만들고, 거기에 `.pr_agent.toml` 파일을 추가하는 방법이다.  
이 파일은 organization의 전체 Repository에 적용되는 global configuration file로 사용된다.

- 우선 순위: 개별 Repo의 `.pr_agent.toml` > organization의 pr-agent-settings Repo
- 지원 플랫폼: GitHub, GitLab, Bitbucket
- 장점: organization 전체에 공통 설정 적용 가능, 개별 Repo에서 필요 시 덮어쓰기 가능

<br/>

## AI 모델 변환

PR Agent는 OpenAI, Anthropic Claude, Azure OpenAI, Hugging Face 등 다양한 LLM 사용을 지원한다.    
기본적으로는 OpenAI의 모델을 사용하지만, 설정을 통해 원하는 LLM으로 쉽게 변경할 수 있다.  
PR Agent는 이를 위해 LLM을 사용하는 모델 관련 로직은 litellm 라이브러리를 통해 처리하고 있다.

### 1. LiteLLM 소개

LiteLLM은 다양한 LLM API들을 공통된 인터페이스로 호출할 수 있게 해주는 Python 경량 라이브러리이다.  
OpenAI, Anthropic Claude, Azure OpenAI, Hugging Face, Mistral 등 여러 LLM 서비스들을 같은 방식으로 사용할 수 있게 추상화한다.

주요 특징은 다음과 같다.
- 통합 인터페이스: `litellm.completion()` 하나로 여러 LLM 백엔드를 호출 가능
- LLM 교체 간단화: 모델/벤더를 바꿔도 코드 변경 최소화
- 설정 유연성: `.env`, config 객체, 명령줄 인자 등으로 API 키 및 모델 설정
- Retry/Timeout 처리: API 실패에 대비한 안정적 호출 기능 내장

이러한 특징을 기반으로 벤더를 자주 바꾸거나, fallback 구조가 필요한 API 호출 구조에서 사용된다.
PR Agent에서도 LLM 변경이 용이하도록 이를 활용한 것으로 보인다.

### 2. AI 모델 설정 방법

AI 모델 설정은 다음 두 파일 일부를 수정하는 것으로 가능하다.

- pr_agent/settings/configuration.toml
  - 지원하는 모델명은 `pr_agent/algo/__init__.py`에서 확인 가능
```toml
[config]
# models
model="gemini/gemini-2.0-flash"
fallback_models=["gemini/gemini-2.0-flash"]
```
- .env (혹은 settings/.secrets.toml)
```env
CONFIG__GIT_PROVIDER="github"
GITHUB__USER_TOKEN="<your-user-token>"
GOOGLE_AI_STUDIO.GEMINI_API_KEY="<your-api-key>"
```

<br/>

## PR Agent 코드 흐름 분석

사용자 입력 ~ PR 반영의 과정은 크게 다음과 같이 정의할 수 있다.
<img width="940" alt="image" src="https://github.com/user-attachments/assets/9f652499-995f-4823-8cb5-80a2778c2e41" />

이 중 각종 설정 파일들을 로드하는 시점은 `CLI 엔트리 포인트`이고, 이를 기반으로 변환된 LLM API를 호출하는 시점은 `PR Agent command 실행`이다.

### 1. 사용자 입력

사용자 입력이 발생하면 pyproject.toml에 정의된 스크립트를 통해 로직이 시작된다.

```toml
[project.scripts]
pr-agent = "pr_agent.cli:run"
```

### 2. CLI 엔트리 포인트

사용자 입력을 파싱하고 각종 설정들을 로드하여 병합한다.

- argparse로 인자 파싱
- Dynaconf를 통해 설정 파일들을 병합하여 설정 객체 생성
  - .env (환경변수)
  - .pr_agent.toml
  - settings/*.toml (내부 기본값 다수 포함)
- get_settings() 호출하여 전역 설정 객체 반환

### 3. PR Agent command 실행

설정 및 인자 파싱 이후, 사용자 요청에 따라 해당 명령어를 실행한다.

- PRAgent 인스턴스를 생성하고 .run() 또는 .run_command() 호출
- 내부적으로 commands 모듈에서 각 명령별 핸들러 실행
  - review.py, describe.py 등
- 이 단계에서 PR 정보(GitHub API를 통해 PR diff, 메타데이터 등)를 수집하여 LLM에 전달할 prompt 준비

### 4. LLM 응답 처리

수집한 PR 정보와 설정된 prompt를 기반으로 LLM에게 요청을 보내고, 응답을 분석한다.

- LiteLLM을 통해 GPT, Gemini 등 설정된 모델에 request 전송
  - llm_completion(prompt=..., model="gpt-4")
- 필요에 따라 응답을 포스트 프로세싱하여 markdown, comment 포맷 등으로 정리

### 5. PR 코멘트/수정

모델 응답을 GitHub PR에 반영한다.

- GitHub API를 사용해 코멘트 생성
  - create_review_comment, update_pull_request_description 등
- 명령어에 따라 다르게 적용됨
  - review: PR 라인별 코멘트 및 종합 리뷰 생성
  - describe: PR 제목 및 설명 업데이트
  - ask: PR 관련 질문에 대한 답변 코멘트
- 이후 상태 메시지 출력 및 종료

<br/>

# PR Agent Configuration 정리

## 모델 설정

[모델 설정 🔗](./configurations/model-config.md)

- config
- pr_custom_prompt
- pr_config
- litellm
- pinecone
- lancedb

## PR 관리

[PR 관리 🔗](./configurations/pr-management.md)

- pr_reviewer
- pr_description
- pr_questions
- pr_code_suggestions
- checks

## 기능 및 동작

[기능 및 동작 🔗](./configurations/features-and-actions.md)

- pr_add_docs
- pr_update_changelog
- pr_analyze
- pr_test
- pr_improve_component
- auto_best_practices

## 버전관리 플랫폼

[버전관리 플랫폼 🔗](./configurations/version-control.md)

- github
- github_action_config
- github_app
- gitlab
- bitbucket_app
- bitbucket_server

## CI/CD 및 배포

[CI/CD 및 배포 🔗](./configurations/ci-cd-and-deployment.md)

- local
- gerrit
- azure_devops_server

## 기타

[기타 🔗](./configurations/etc.md)

- pr_similiar_issue
- pr_find_similiar_component
- best_practices
- pr_help
- pr_help_docs