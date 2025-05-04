## [local]

`description_path` : PR 설명을 읽어올 .md 파일 경로

`review_path` : 리뷰 결과를 출력할 .md 파일 경로 지정

---

github에 의존하지 않고, 로컬에서만 실행해볼 때 유용함.

<br/>

## [gerrit]

`url` : Gerrit 서버 주소 ([보통 **SSH** 사용](https://kojub.tistory.com/22))

`user` : Gerrit 사용자

`patch_server_endpoint` : PR-Agent가 만든 패치를 저장할 별도 서버 주소

`patch_server_token` : 패치 서버 접근 시 사용할 인증 토큰

---

> gerrit이란?
> 
- [Google에서 만든 Git 기반의 코드 리뷰 툴](gerritcodereview.com)
- 리뷰를 강제로 하게끔 하는 툴이기 때문에, 코드 통제가 많이 필요한 대규모 팀에서 쓰기 적합한 툴 ( + 보안강화 - SSH 사용하는 거 부터가… )

---

> Gerrit이 git기반 코드리뷰 툴인데, 왜 또 pr-agent 같은 코드리뷰 툴을 쓰는가?
> 
- Gerrit은 AI 리뷰 기능이 없어서 pr-agent같은 도구로 도움받는 거임.
- Gerrit의 patch 서버에 pr-agent가 해석할 수 있는 코드 diff 정보 제공 → 이 diff 정보에 접근할 수 있도록 설정해주는 파라미터가 [gerrit]

<br/>

## [azure_devops_server]

Azure DevOps PR에서 쓸 수 있는 명령어 목록

```bash
[azure_devops_server]
pr_commands = [
    "/describe",
    "/review",
    "/improve",
]
```

---

[ 사전 설정 ] 

1. azure-pipelines.yml 파일 생성 및 Azure 설정 관련 소스코드 작성
    - 소스코드
        
        ```bash
        # Opt out of CI triggers
        trigger: none
        
        # Configure PR trigger
        pr:
          branches:
            include:
            - '*'
          autoCancel: true
          drafts: false
        
        stages:
        - stage: pr_agent
          displayName: 'PR Agent Stage'
          jobs:
          - job: pr_agent_job
            displayName: 'PR Agent Job'
            pool:
              vmImage: 'ubuntu-latest'
            container:
              image: codiumai/pr-agent:latest
              options: --entrypoint ""
            variables:
              - group: pr_agent
            steps:
            - script: |
                echo "Running PR Agent action step"
        
                # Construct PR_URL
                PR_URL="${SYSTEM_COLLECTIONURI}${SYSTEM_TEAMPROJECT}/_git/${BUILD_REPOSITORY_NAME}/pullrequest/${SYSTEM_PULLREQUEST_PULLREQUESTID}"
                echo "PR_URL=$PR_URL"
        
                # Extract organization URL from System.CollectionUri
                ORG_URL=$(echo "$(System.CollectionUri)" | sed 's/\/$//') # Remove trailing slash if present
                echo "Organization URL: $ORG_URL"
        
                export azure_devops__org="$ORG_URL"
                export config__git_provider="azure"
        
                pr-agent --pr_url="$PR_URL" describe
                pr-agent --pr_url="$PR_URL" review
                pr-agent --pr_url="$PR_URL" improve
              env:
                azure_devops__pat: $(azure_devops_pat)
                openai__key: $(OPENAI_KEY)
              displayName: 'Run Qodo Merge'
        ```
        
2. config 파라미터에 `git_provider="azure”` 추가
3. .secrets.toml 파일 생성 및 PAT token 작성
    
    ```bash
    # .secret.toml
    [azure_devops]
    org = "https://dev.azure.com/YOUR_ORGANIZATION/"
    # pat = "YOUR_PAT_TOKEN" needed only if using PAT for authentication
    ```
    
4. Azure DevOps에서 pr-agent를 웹훅으로 연동