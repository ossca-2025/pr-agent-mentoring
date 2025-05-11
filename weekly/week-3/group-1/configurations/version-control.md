## [github]

<br/>

## [github_action_config]

**GitHub Actions에서 PR-Agent를 사용할 때 workflow 파일에 설정할 수 있는 항목들**

```toml
auto_review = true
auto_describe = true
auto_improve = true
```

- PR 생성, 열림 등의 액션이 발생했을 때, 자동으로 `/review`, `/describe`, `/improve` 명령을 실행할지 여부.

```toml
pr_actions = ['opened', 'reopened', 'ready_for_review', 'review_requested']
```

- 자동 명령어 실행을 유발하는 트리거 액션

<br/>

## [github_app]

**GitHub App으로 연동 시 동작 방식**

```toml
bot_user = "github-actions[bot]"
```

- 자동 작업을 수행할 때 사용할 봇 계정 이름.

```toml
override_deployment_type = true
```

- App 설치 여부와 관계없이 강제로 deployment_type을 덮어씀.

**PR 이벤트 처리**

```toml
handle_pr_actions = ['opened', 'reopened', 'ready_for_review']
```

- 해당 PR 이벤트가 발생하면 아래 명령을 실행함.

```toml
pr_commands = [
    "/describe --pr_description.final_update_message=false",
    "/review",
    "/improve",
]
```

- PR 생성, 재열림 등 이벤트 발생 시 위 명령 자동 실행.

**PR에 커밋이 추가될 때 (`synchronize` 이벤트)**

```toml
handle_push_trigger = false
```

- `false`면 커밋 추가 시 자동 작업을 안 함.

```toml
push_trigger_ignore_bot_commits = true
push_trigger_ignore_merge_commits = true
```

- 봇 커밋이나 머지 커밋은 무시.

```toml
push_trigger_wait_for_initial_review = true
```

- 초기 리뷰가 먼저 있어야 이후 푸시에 반응함.

```toml
push_trigger_pending_tasks_backlog = true
push_trigger_pending_tasks_ttl = 300
```

- 푸시로 인해 생기는 작업을 백로그로 보관하고, 300초 내 처리 시도.

```toml
push_commands = [
    "/describe",
    "/review",
]
```

- 커밋이 추가되었을 때 자동으로 `/describe`, `/review` 수행.

<br/>

## [gitlab]

**GitLab에서 사용할 때 설정**

```toml
url = "https://gitlab.com"
```

- GitLab API URL (자체 호스팅인 경우 바꿔야 함.)

```toml
pr_commands = [
    "/describe --pr_description.final_update_message=false",
    "/review",
    "/improve",
]
```

```toml
handle_push_trigger = false
push_commands = [
    "/describe",
    "/review",
]
```

- GitHub처럼 푸시 이벤트 자동 트리거 여부 설정.

<br/>

## [bitbucket_app]

**GitHub처럼 푸시 이벤트 자동 트리거 여부 설정**

```toml
pr_commands = [
    "/describe --pr_description.final_update_message=false",
    "/review",
    "/improve --pr_code_suggestions.commitable_code_suggestions=true",
]
```

- PR 이벤트 발생 시 실행할 명령어.

```toml
pr_commands = [
    "/describe --pr_description.final_update_message=false",
    "/review",
    "/improve --pr_code_suggestions.commitable_code_suggestions=true",
]
```

- `true`로 설정하면 전체 파일 리뷰를 피하려고 시도.

<br/>

## [bitbucket_server]

**Bitbucket Server(사내 Bitbucket 인스턴스)에서 동작하도록 설정하는 부분**

```toml
# url = "https://git.bitbucket.com"
url = ""
```

- Bitbucket Server URL (자체 호스팅인 경우 바꿔야 함.)

```toml
pr_commands = [
    "/describe --pr_description.final_update_message=false",
    "/review",
    "/improve --pr_code_suggestions.commitable_code_suggestions=true",
]
```

- PR 이벤트 발생 시 실행할 명령어들.