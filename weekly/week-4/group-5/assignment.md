# 🗓️ 4주차 계획: PR Agent Contribution Ideation

## 🛠️ 조별 과제

* 조별로 Top 3 PR Agent 개선 아이디어 정리 및 발표 (10분 분량)
* 각 아이디어는 What / Why / How / 구현 계획 포함

## Content

1. Rule-Based 코드 분석기(Pylint)와 LLM의 하이브리드 리뷰

2. 존재하지 않는 PR URL 호출 시 사용자 친화적인 에러 로그 개선

3. describe 명령어에 TODO/FIXME 자동 수집 기능 추가

---

## 1️⃣ Rule-Based 코드 분석기(Pylint)와 LLM의 하이브리드 리뷰

### What?

* PR Agent는 현재 LLM(GPT)에 전적으로 의존하여 코드 리뷰를 수행함
* 하지만 단순 스타일 오류나 사소한 규칙 위반은 LLM 호출 없이도 rule-based 도구로 검출 가능함

### Why?

#### LLM 기반 리뷰의 한계

* 모든 리뷰에 대해 LLM 호출 → 토큰 사용량 증가 및 API 호출 비용 발생
* 단순한 스타일 위반까지 비용을 낭비하게 됨
* GPT는 간단한 룰 위반을 놓치거나 판단이 일관되지 않음

#### Rule-Based 방식의 강점

| 항목     | 설명                               |
| ------ | -------------------------------- |
| 정밀도    | 명확한 규칙 기반으로 일관되고 확실한 오류 검출 가능    |
| 속도     | 분석 속도가 매우 빠름 (수 초 내 결과 출력)       |
| 비용 없음  | 외부 호출/토큰 소비 없이 로컬 실행 가능          |
| 적용 용이성 | 사전 학습 없이도 즉시 적용 가능 (컨벤션 기반 룰 제공) |

#### 유사 사례

> 학습 데이터 필터링, 라벨링 작업에서도 Rule 기반 필터가 더 정확한 경우가 많음

### How?

#### 리뷰 흐름 개선

```text
기존: PR 생성 → GPT 리뷰 → 결과 출력
개선: PR 생성
     └> Step 1. pylint 분석
          └> Step 2. 경미한 규칙 위반은 직접 코멘트
                      복잡한 오류만 LLM에 전달
```
---
#### Step 1. pylint 실행 (자동)

##### 실제 pylint 결과 예시
```python
simple.py:11:0: C0114: Missing module docstring (missing-module-docstring)
simple.py:28:4: R1705: Unnecessary "else" after "return"
```
PR이 올라오면 변경된 코드에 대해 pylint를 자동 실행함.
이때 출력되는 에러 메시지는 기계적이고 이해하기 어려운 문장임 (예: C0114, R1705).


- C0114: 모듈 위에 주석(docstring)이 없음 → 문서화 부족
- R1705: return 다음에 else가 또 있음 → 불필요한 else 사용

--- 
#### Step 2. 프롬프트 생성(자동 or 템플릿 기반 생성)

pylint 메시지를 GPT가 이해하도록 정제한 프롬프트로 만들어주자는 아이디어!

##### 프롬프트 예시
```text
아래는 pylint 결과입니다. 주요 위반 항목을 참고하여 리뷰를 작성해주세요:
- R1705: Unnecessary "else"
- C0114: Missing docstring
```

위 pylint 결과를 GPT가 이해할 수 있는 문장으로 바꿔주는 작업을 한다.

즉, pylint 결과를 GPT가 읽기 편하게 정제해주자는 것이다.

---

#### Step 3. GPT가 리뷰 코멘트 생성 (자동)
GPT는 위 프롬프트를 받고 사람처럼 자연어 리뷰를 작성해준다. 

예:
- "모듈에 docstring이 없습니다. 간단한 설명을 추가해주세요."
- "return 문 다음의 else는 불필요합니다. 제거하면 코드 가독성이 좋아집니다."

→ 이게 최종적으로 GitHub PR에 코멘트로 들어가거나, 리뷰 메시지로 쓰인다.

---

### 실제 적용 예시: PR에서 pylint + LLM 리뷰 흐름

- https://github.com/Judy-Choi/pr-agent-study/pull/1 테스트 파일 사용.

-  ![image](https://github.com/user-attachments/assets/9072a7de-0381-4b10-aff4-eeb1e58e3120)
- `simple.py` 파일에 대해 `pylint`를 실행했을 때의 결과
- PR Agent가 LLM 호출 전에 이 결과를 활용해 일부 오류는 자동 코멘트로 작성할 수 있다!

핵심 구현 아이디어 요약:
- `pr_code_suggestions.py` 내 LLM 호출 이전에 pylint 실행 hook 삽입 (-> PR이 들어오면 자동으로 pylint 실행됨 (Step 1 자동화))
- 사소한 rule 위반은 LLM 없이도 inline_comment() 호출 (-> 비용, 속도 최적화 (GPT 호출 없이 코멘트 달기))
- LLM에는 pylint result를 함께 context로 주입 (중요 오류만) (-> 프롬프트 + context injection으로 정확도 향상)

```text
PR 코드 변경과 함께 제공된 pylint 분석 결과를 참고해 주세요.
아래에 제시된 코드 위반 사항을 중심으로 개선 방향을 제안해 주세요:
[오류 요약 A]
[오류 요약 B]
```


---

## ✔️ 2️⃣ 존재하지 않는 PR URL 호출 시 사용자 친화적인 에러 로그 개선

---

### What?

* 존재하지 않는 PR URL을 입력하면 전체 `Python Traceback`이 출력되어 사용자에게 과도한 정보가 전달됨

예시) `python3 -m pr_agent.cli --pr_url https://github.com/Judy-Choi/pr-agent-study/pull/100 improve`  입력

```python
Traceback (most recent call last):
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/__init__.py", line 59, in get_git_provider_with_context
    git_provider = _GIT_PROVIDERS[provider_id](pr_url)
                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/github_provider.py", line 52, in __init__
    self.set_pr(pr_url)
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/github_provider.py", line 150, in set_pr
    self.pr = self._get_pr()
              ^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/github_provider.py", line 865, in _get_pr
    return self._get_repo().get_pull(self.pr_num)
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/venv/lib/python3.12/site-packages/github/Repository.py", line 3153, in get_pull
    headers, data = self._requester.requestJsonAndCheck(
                    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/venv/lib/python3.12/site-packages/github/Requester.py", line 442, in requestJsonAndCheck
    return self.__check(
           ^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/venv/lib/python3.12/site-packages/github/Requester.py", line 487, in __check
    raise self.__createException(status, responseHeaders, data)
github.GithubException.UnknownObjectException: 404 {"message": "Not Found", "documentation_url": "https://docs.github.com/rest/pulls/pulls#get-a-pull-request", "status": "404"}

The above exception was the direct cause of the following exception:

Traceback (most recent call last):
  File "<frozen runpy>", line 198, in _run_module_as_main
  File "<frozen runpy>", line 88, in _run_code
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/cli.py", line 99, in <module>
    run()
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/cli.py", line 93, in run
    result = asyncio.run(inner())
             ^^^^^^^^^^^^^^^^^^^^
  File "/opt/anaconda3/lib/python3.12/asyncio/runners.py", line 194, in run
    return runner.run(main)
           ^^^^^^^^^^^^^^^^
  File "/opt/anaconda3/lib/python3.12/asyncio/runners.py", line 118, in run
    return self._loop.run_until_complete(task)
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/opt/anaconda3/lib/python3.12/asyncio/base_events.py", line 687, in run_until_complete
    return future.result()
           ^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/cli.py", line 84, in inner
    result = await asyncio.create_task(PRAgent().handle_request(args.pr_url, [command] + args.rest))
             ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/agent/pr_agent.py", line 56, in handle_request
    apply_repo_settings(pr_url)
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/utils.py", line 16, in apply_repo_settings
    git_provider = get_git_provider_with_context(pr_url)
                   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "/Users/judy/Documents/__OSSCA__/pr-agent/pr_agent/git_providers/__init__.py", line 64, in get_git_provider_with_context
    raise ValueError(f"Failed to get git provider for {pr_url}") from e
ValueError: Failed to get git provider for https://github.com/Judy-Choi/pr-agent-study/pull/100
```
- 단순히(?) url 이 존재하지 않거나 접근할 수 없을 뿐인데.. 너무 에러메시지가 길다!
- 심지어 `github.GithubException.UnknownObjectException: 404` 라고 Exception 처리도 잘 해준다.

---

### Why? (어떤 문제가 존재할까)

| 문제            | 설명                                     |
| ------------- | -------------------------------------- |
| Traceback 노출  | 초보 사용자에겐 과도한 정보 → UX 저하                |
| 메시지 불명확       | 핵심 원인(404 Not Found)이 중간에 묻힘           |
| 404는 이미 잡힘    | GithubException 404는 감지되지만 메시지로 활용 안 됨 |
| 사용자 혼란 유발 최소화 | 요약된 메시지 + 해결 방법 안내 제공 필요               |

> 예시:
> `🙅🏻‍♂️ Error: Traceback...` →
> `🙆🏼‍♂️ Error: 해당 PR URL이 존재하지 않거나 접근 권한이 없습니다.`

---

### How?

---

#### 기존 코드

```python
github.GithubException.UnknownObjectException: 404 {"message": "Not Found", "documentation_url": "https://docs.github.com/rest/pulls/pulls#get-a-pull-request", "status": "404"}

...raise ValueError(f"Failed to get git provider for {pr_url}") from e
```

- 위에서 설명한 바와 같이, 현재 `get_git_provider_with_context()` 함수는 **모든 예외를 `ValueError`로 감싸서 다시 던지는 방식**을 사용하고 있다.
  이로 인해 실제 발생한 오류의 **종류(예: 404 Not Found)** 와 **해결 방향**이 **CLI 출력상 구분되지 않고**, 단순 Traceback으로만 노출된다.

-> 따라서 **에러 유형에 따라 조건 분기를 통해 적절한 사용자 메시지를 제공**하는 구조로 바꾸는 것이 필요하다.

---

#### 기존 코드 위치
```python
# pr_agent/git_providers/__init__.py
def get_git_provider_with_context(pr_url: str):
    try:
        provider_id = get_provider_id(pr_url)
        git_provider = _GIT_PROVIDERS[provider_id](pr_url)
        return git_provider
    except Exception as e:
        raise ValueError(f"Failed to get git provider for {pr_url}") from e
```
내부에서 어떤 에러가 발생하든 간에 전부 Traceback 포함한 ValueError로 래핑해서 던진다.
이로 인해 CLI에서 전체 Python Traceback이 그대로 출력되는 UX 문제가 생긴다.

---

#### 개선 아이디어
ValueError(...)로 감싸 던지는 대신,
실제 오류를 잡을 수 있는 상위 계층 (`apply_repo_settings`)에서
사용자 친화적인 에러 메시지를 직접 출력하도록 처리한다!

---

#### 개선 처리 코드

```python
# pr_agent/git_providers/utils.py
from github import GithubException

def apply_repo_settings(pr_url: str):
    try:
        git_provider = get_git_provider_with_context(pr_url)
    except GithubException as e:
        if hasattr(e, "status") and e.status == 404:
            get_logger().warning(f"Invalid PR URL or access denied: {pr_url}", artifact={"error": e})
            print(f"[Error] 해당 PR({pr_url})은 존재하지 않거나 접근할 수 없습니다.\n"
                  "👉 PR URL을 다시 확인해 주세요.\n"
                  "👉 비공개 저장소라면, 액세스 토큰 권한을 확인하세요.")
            print("예시: python3 -m pr_agent.cli --pr_url https://github.com/owner/repo/pull/123 improve")
            return None
        raise  # 그 외 예외는 기존처럼 처리

```
예외가 404인 경우에는 GithubException이 명확하게 반환되므로,

이 경우만 따로 잡아서 get_logger().warning(...)과 print(...)를 사용하여 간단하고 명확한 에러 메시지를 출력하고,

그 외 예외는 기존처럼 raise하여 개발자 디버깅 흐름은 유지할 수 있도록 한다.

---

#### 기존 흐름
```text
[CLI 명령 실행]
    ↓
[apply_repo_settings(pr_url)] 
    ↓
[get_git_provider_with_context(pr_url)]
    ↓
[Exception 발생 → ValueError로 감싸서 던짐]
    ↓
[apply_repo_settings는 이걸 그대로 받음 → 아무 메시지 없이 traceback 출력됨]
```

문제점
- 유저는 단순한 URL 오타에도 전체 Traceback을 봐야 한다.
- apply_repo_settings()는 그냥 raise만 하므로 사용자 친화적인 안내가 없다.

---

#### 개선 구조

```text
[CLI 명령 실행]
    ↓
[apply_repo_settings(pr_url)] 
    ↓
try:
    get_git_provider_with_context(pr_url)
except GithubException (status 404):
    print("해당 PR이 존재하지 않습니다!")  ← 바로 안내!
```

즉, ValueError가 아닌 더 구체적인 예외인 GithubException을 apply_repo_settings()에서 직접 잡아 사용자에게 짧고 명확한 메시지를 보여주는 구조로 바뀐 것이다.

---

### 효과

* 전체 Traceback 제거
* 명확한 사용자 메시지 제공
* 해결 방법까지 포함된 UX 안내

-> 따라서 이후 CLI 환경에서 사용자가 에러를 만났을 때,

단순히 "왜 에러가 발생했는가"보다 **"어떻게 해결할 수 있는가"에 집중할 수 있게 해준다!**

---

## ✔️ 3️⃣ describe 명령에 TODO/FIXME 자동 수집 기능 추가

---

### What?

![image](https://github.com/user-attachments/assets/64964ed4-5caa-49ea-9da1-f46fd2d8ef31)


* PR diff 내에 포함된 `# TODO`, `# FIXME`, `# REFACTOR` 등 주석을 자동 탐지해 PR description 하단에 정리

```python
## TODO / FIXME 목록

다음 항목은 PR 변경 내역에서 감지된 개선 예정 항목입니다:

- **file1.py:42** — 리팩토링 필요
- **utils.js:10** — 오류 처리 보완
```
리뷰 중 놓치기 쉬운 TODO, FIXME 항목을 한눈에 파악할 수 있음


---

### Why?

- TODO 주석은 임시 처리된 코드, 미완성 기능, 향후 개선 사항 등 **중요한 개발 힌트**를 담고 있음.
- 하지만 리뷰 중에는 **눈에 띄지 않거나 잊혀질 수 있음**.
- PR 설명 하단에 자동으로 정리하면, 리뷰어가 **명확하게 인지**하고 판단할 수 있음.

---

### How?

---

1. 정규표현식 `r'(TODO)\s*[:\-]?\s*(.+)'`으로 TODO 라인 수집 
- process_patch_lines() 단계에서 각 diff 라인을 확인하며, 정규식으로 TODO 라인을 필터링
![image](https://github.com/user-attachments/assets/247c5e8d-54b2-4508-a76e-903517cbc6dc)

- `process_patch_lines()` 함수는 PR의 diff를 분석하는 핵심 처리 단계이며,
- 각 수정된 줄을 순회하면서, "TODO" 패턴이 들어간 주석을 정규식으로 필터링함.

#### 왜 필요한가?
- TODO 같은 주석은 주로 개발 중 "임시 처리"나 "미완성 코드"를 나타낸다.
- 코드상에는 있지만 리뷰 중에 놓치기 쉽기 때문에, 정규식으로 정확히 감지하는 게 핵심이다!

---

2. 파일명 + 줄 번호와 함께 PR 설명에 삽입
해당 라인의 파일명과 줄 번호를 추출하여 PR 설명(pr_body) 하단에 자동 삽입

```python
## TODO / FIXME 목록

다음 항목은 PR 변경 내역에서 감지된 개선 예정 항목입니다:

- **file1.py:42** — 리팩토링 필요
- **utils.js:10** — 오류 처리 보완
```

#### 왜 필요한가?
- 리뷰어 입장에서 TODO가 어디 있는지 직접 찾아야 하는 수고를 줄인다.
- 자동 요약된 항목으로 리뷰 품질과 속도를 동시에 향상시킨다.

#### 어떻게 동작하는가?

patch 파싱 시 @@ 헤더로부터 라인 번호를 파악하고, 각 파일명과 함께 정리된 텍스트 블록을 `pr_body` 하단에 삽입한다.

---

3. 설정에 따라 활성화/비활성화 가능
```toml
[pr_description]
scan_patterns = ["TODO", "FIXME", "REFACTOR", "TEMP"]
```

#### 왜 필요한가?

- 팀마다 사용하는 주석 패턴이 다를 수 있기 때문에, (TODO, FIXME, NOTE, HACK 등)

- 설정을 통해 유연하게 필터 키워드를 정의할 수 있어야 함


#### 어떻게 동작하는가?

TOML에서 `scan_patterns` 리스트를 읽어와.
정규식을 동적으로 생성하여 적용함

---
  
4. 선택 확장 가능 아이디어
1) TODO ->  GitHub Issue 자동 전환
     - 감지된 TODO 주석을 기반으로 `gh issue create` API 호출 가능
2) 의존성 버전 변경 자동 감지
     - requirements.txt, package.json 등에서 version 필드를 비교해 변경 사항을 별도 summary 블록으로 정리 
---
