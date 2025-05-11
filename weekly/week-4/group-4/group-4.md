# 4주차 조별과제: 조별 기여 아이디어 Top 3 정리 및 발표
> 4조 아이디어:
> 1. GitHub Action에서 OpenAI 외에 다른 AI 모델 적용 방법 예제 추가
> 2. 한 코멘트에서 여러 명령어 실행
> 3. get_settings() 호출 최소화·캐싱
> 4. re.compile() 반복 컴파일 제거

> 아이디어 중 투표해 Top 3를 선정하고, 팀원들 같이 공부를 진행한 뒤 공부한 내용을 합쳤습니다.

## 1. GitHub Action에서 OpenAI 외에 다른 AI 모델 적용 방법 예제 추가
> 아이디어: 정동환

### 아이디어
- 현재 GitHub Action workflow 예시들은 OpenAI 모델을 사용하는 경우만 작성이 되어있다. Gemini 등 다른 모델을 지원하지 않는 것으로 보이는데, 다른 모델도 지원 가능하게 확장해보고자 한다.
    - OpenAI workflow 설정 관련 yaml 파일 위치: `/.github/workflows/pr-agent-review.yaml`
    - 예제가 작성된 문서 위치: `/docs/docs/installation/github.md`
    - OpenAI 워크플로우 관련 파일: `pr-agent/servers/github_action_runner.py`
        ```py
        async def run_action():
        # Get environment variables
        GITHUB_EVENT_NAME = os.environ.get('GITHUB_EVENT_NAME')
        GITHUB_EVENT_PATH = os.environ.get('GITHUB_EVENT_PATH')
        OPENAI_KEY = os.environ.get('OPENAI_KEY') or os.environ.get('OPENAI.KEY')
        OPENAI_ORG = os.environ.get('OPENAI_ORG') or os.environ.get('OPENAI.ORG')
        GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN')
        # get_settings().set("CONFIG.PUBLISH_OUTPUT_PROGRESS", False)

        # Check if required environment variables are set
        if not GITHUB_EVENT_NAME:
            print("GITHUB_EVENT_NAME not set")
            return
        if not GITHUB_EVENT_PATH:
            print("GITHUB_EVENT_PATH not set")
            return
        if not GITHUB_TOKEN:
            print("GITHUB_TOKEN not set")
            return

        # Set the environment variables in the settings
        if OPENAI_KEY:
            get_settings().set("OPENAI.KEY", OPENAI_KEY)
        else:
            # Might not be set if the user is using models not from OpenAI
            print("OPENAI_KEY not set")
        if OPENAI_ORG:
            get_settings().set("OPENAI.ORG", OPENAI_ORG)
        get_settings().set("GITHUB.USER_TOKEN", GITHUB_TOKEN)
        get_settings().set("GITHUB.DEPLOYMENT_TYPE", "user")
        enable_output = get_setting_or_env("GITHUB_ACTION_CONFIG.ENABLE_OUTPUT", True)
        get_settings().set("GITHUB_ACTION_CONFIG.ENABLE_OUTPUT", enable_output)
        ```

### 공부 진행
- 로컬 CLI상에서 PR Agent를 Gemini로 사용할 때 `configuation.toml`, `.secrets.toml` 두 파일만 수정하면 되었던 것을 바탕으로 추적을 진행하였다.
- 설정을 파일 편집해 OpenAI 모델을 변경하거나 다른 LLM 모델을 사용하는 방법이 나와있는 문서: `/docs/docs/usage-guide/changing_a_model.md`
    - 기본적으로 `configuation.toml`의 `model`과 `fallback_model` 수정이 필요.
    - OpenAI 외 회사의 모델의 경우 `configuation.toml` 수정과 더불어 요구되는 API 키 등을 (CLI에서 사용시) `.secrets.toml` 또는 (GitHub App나 GitHub Action에서 사용시) `GitHub Settings > Secrets and variables`에 작성해야 함.
    - 이 중 후자의 작업을 GitHub Actions workflow에서 ENV 설정을 통해 진행 가능.

### 코드 분석
- `pr_agent/algo/ai_handlers/litellm_ai_handler.py`를 보면, `get_settings()`로부터 API 키가 가져와진다.
    ```py
        # Google AI Studio
        # SEE https://docs.litellm.ai/docs/providers/gemini
        if get_settings().get("GOOGLE_AI_STUDIO.GEMINI_API_KEY", None):
          os.environ["GEMINI_API_KEY"] = get_settings().google_ai_studio.gemini_api_key
    ```
- `pr_agent/config_loader.py`을 보면, 환경설정을 위해 Dynaconf라는 라이브러리를 사용하고 있다. `global_settings`에 `settings_files` 에 나열된 TOML 파일들(`/settings`하위)을 순서대로 읽어서 내부적으로 하나의 설정 객체로 병합(merge_enabled=True)한다.
- 따라서 아래 나열된 설정 파일 중 어떤 파일이든 [GOOGLE_AI_STUDIO] 테이블 하위에 gemini_api_key = "값"이 정의되어 있다면 값을 읽어온다.후속 파일이 앞선 파일을 덮어쓰며 우선권을 가져, 같은 키를 여러 파일에 정의한 경우 로드 순서 후반부의 값이 최종 설정으로 반영된다. 그리고 마지막으로 .env, 환경변수 값이 덮어씌워진다.
    ```py
    from os.path import abspath, dirname, join
    from pathlib import Path
    from typing import Optional

    from dynaconf import Dynaconf
    from starlette_context import context

    PR_AGENT_TOML_KEY = 'pr-agent'

    current_dir = dirname(abspath(__file__))
    global_settings = Dynaconf(
        envvar_prefix=False,
        merge_enabled=True,
        settings_files=[join(current_dir, f) for f in [
            "settings/configuration.toml",
            "settings/ignore.toml",
            "settings/language_extensions.toml",
            "settings/pr_reviewer_prompts.toml",
            "settings/pr_questions_prompts.toml",
            "settings/pr_line_questions_prompts.toml",
            "settings/pr_description_prompts.toml",
            "settings/code_suggestions/pr_code_suggestions_prompts.toml",
            "settings/code_suggestions/pr_code_suggestions_prompts_not_decoupled.toml",
            "settings/code_suggestions/pr_code_suggestions_reflect_prompts.toml",
            "settings/pr_information_from_user_prompts.toml",
            "settings/pr_update_changelog_prompts.toml",
            "settings/pr_custom_labels.toml",
            "settings/pr_add_docs.toml",
            "settings/custom_labels.toml",
            "settings/pr_help_prompts.toml",
            "settings/pr_help_docs_prompts.toml",
            "settings/pr_help_docs_headings_prompts.toml",
            "settings/.secrets.toml",
            "settings_prod/.secrets.toml",
        ]]
    )


    def get_settings(use_context=False):
        """
        Retrieves the current settings.

        This function attempts to fetch the settings from the starlette_context's context object. If it fails,
        it defaults to the global settings defined outside of this function.

        Returns:
            Dynaconf: The current settings object, either from the context or the global default.
        """
        try:
            return context["settings"]
        except Exception:
            return global_settings
    # 생략
    ```
- Dynaconf는 OS 환경변수도 함께 로드한다. GitHub Action Secrets에 환경변수를 작성하려면, 환경변수 이름을 “중첩 구조” 규칙에 맞게 설정하면 된다. (아래 코드는 `envvar_prefix=False`일때의 예시; prefix를 사용하고 싶을 경우 `envvar_prefix="DYNACONF"`와 같이 지정할 수 있다.)
    ```yaml
    env:
        GOOGLE_AI_STUDIO__GEMINI_API_KEY: ${{ secrets.GEMINI_API_KEY }}
    ```
### 결과
이에 따라, 다음과 같이 설정하면 github actions workflow에서 Gemini 설정이 가능하다.
1. config__{value}로 configuation.toml을 변경
2. gemini API key는 기존에 공식 문서에서 제안하는 방법대로 GitHub Actions Secret 통해 env: 블록에 선언하여 환경변수로 등록한다 (환경변수가 읽혀지므로 따로 `.secrets.toml`에 설정할 필요 없음)
    ```yaml
        on:
        pull_request:
            types: [opened, reopened, ready_for_review]
        issue_comment:

        jobs:
        pr_agent_job:
            if: ${{ github.event.sender.type != 'Bot' }}
            runs-on: ubuntu-latest
            permissions:
            issues: write
            pull-requests: write
            contents: write
            name: Run pr agent on every pull request, respond to user comments
            steps:
            - name: PR Agent action step
                id: pragent
                uses: qodo-ai/pr-agent@main
                env:
                GITHUB_TOKEN: ${{ secrets.USER_TOKEN }}
                GOOGLE_AI_STUDIO__GEMINI_API_KEY: ${{ secrets.GEMINI_API_KEY }}
                config__model: "gemini/gemini-2.0-flash"
                config__fallback_models: "gemini/gemini-1.5-flash"
    ```
- 실제로 적용된 PR: https://github.com/smartandhandsome/qodo-merge-test/pull/6
 ![alt text](image.png)
### 정리
Python 코드를 수정할 필요는 없었다. 다른 모델을 사용하고 싶으나 Dynaconf 및 toml 설정 구조에 대한 사전 지식이 없는 사용자를 위해, workflow에서 변수를 제어하는 방법에 대해 예제를 추가하는 기여가 가능해 보인다.