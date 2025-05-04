# 2조 조별 과제

## 주제 5

`describe`, `review`, `improve(code_suggestions_prompts)` 프롬프트 분석.

# 개념

### 티켓

- 개발팀이나 조직이 처리해야 할 작업 단위를 의미하며, 일반적으로 **기능 요청, 버그 리포트, 작업 등**의 형태로 사용
- 티켓은 **PR**과 연동되어 작업 흐름을 추적하고, **PR 리뷰 시** **요구사항 충족 여부**를 판단하는 기준
- 티켓 시스템 : Jira, Github Issues, GitLab Issues 등
- ex)

| 필드 | 내용                                                                                        |
| --- |-------------------------------------------------------------------------------------------|
| Ticket URL | `https://jira.mycorp.com/PROJ-123`                                                        |
| Title | `로그인 성공 시 대시보드로 리다이렉션`                                                                    |
| Labels | `["feature", "frontend"]`                                                                 |
| Description | - 로그인 성공 시 메인 페이지로 이동해야 함<br> - JWT 저장 방식 localStorage → cookie 변경<br>- QA는 크로스브라우징 체크 필요| 


### Jinja 템플릿 문법

```toml
{%- if is_ai_metadata %}
{%- endif %}

{%- for ticket in related_tickets %}
{% endfor %}
```

- Jinja는 Python 기반 템플릿 엔진
- **프롬프트(YAML 파일)를 동적으로  생성**하기 위해서 사용

| 기능 | 문법 예시 | 설명 |
| --- | --- | --- |
| 변수 출력 | `{{ 변수명 }}` | 변수를 텍스트에 삽입 |
| if 조건문 | `{% if 조건 %} ... {% endif %}` | 조건에 따라 내용 분기 |
| for 반복문 | `{% for item in 리스트 %} ... {% endfor %}` | 반복해서 내용 출력 |
| 주석 | `{# 주석내용 #}` | 템플릿 안에 주석 달기 |
| 필터 사용 | `{{ 변수명 | 필터명 }}` |

### Pydantic 문법

Pydantic은 LLM에게 백엔드가 기대하는 데이터 구조를 명확히 제시함으로써, **입력과 출력 간 데이터 정합성을 유지하기 위해 사용된다.**

즉, 백엔드와 LLM이 **동일한 데이터 계약 구조**를 기준으로 입출력을 주고받음으로써, 파싱 오류나 데이터 불일치 없이 **정형화된 결과를 보장**할 수 있다.

```toml
class SubPR(BaseModel):
relevant_files: List[str] = Field(description="The relevant files of the sub-PR")
title: str = Field(description="Short and concise title for an independent and meaningful sub-PR, composed only from the relevant files")
```

# 프롬프트 분석

## 프롬프트 공통점

### 시스템 프롬프트

1. **[페르소나 정의]**

> PR 리뷰어라는 역할 지정
> 

```toml
You are PR-Reviewer, a language model designed to review a Git Pull Request (PR).
```

2. **[지시문 작성]**

```toml
1. 입력 데이터 형식 정의
코드가 추가된 부분에는 앞에 '+' , 삭제된 부분에는 '-'를 붙여라, 변경이 되지 않으면 ' '
@@ ... @@ def func1():
 unchanged code line0
 unchanged code line1
+new code line2
-removed code line2
 unchanged code line3

@@ ... @@ def func2():
...
## File: 'src/file2.py'

2. 삭제된 부분보다 새로 생긴 부분에 집중해라
Your task is to provide a full description for the PR content - type, description, title and files walkthrough.
- Focus on the new PR code (lines starting with '+' in the 'PR Git Diff' section).
- Keep in mind that the 'Previous title', 'Previous description' and 'Commit messages' sections may be partial, simplistic, non-informative or out of date. Hence, compare them to the PR diff code, and use them only as a reference.
- The generated title and description should prioritize the most significant changes.
- If needed, each YAML output should be in block scalar indicator ('|')

Your task is to provide constructive and concise feedback for the PR.
The review should focus on new code added in the PR code diff (lines starting with '+')

3. 코드에서 가져온 변수명, 함수명, 파일 경로 등을 인용할 때는 작은 따옴표(’)가 아니라 백틱(`)을 사용해라
When quoting variables, names or file paths from the code, use backticks (`) instead of single quote (').

4. 전체 코드를 보내는 것이 아니니 AI 요약이나 다른 내용을 추측하거나 그것을 통해 단정짓지마라

5. Example output을 제시하며 이렇게 하기를 요구 -> Few Shot
Example output:
```yaml [마크 다운 펜스드 코드 블럭 문법)]
code_suggestions:
- relevant_file: |
    src/file1.py
  language: |
    python
  existing_code: |
    ...
  suggestion_content: |
    ...
  improved_code: |
    ...
  one_sentence_summary: |
    ...
  label: |
    ...
```

### JSON이 아닌 YAML로 출력 형태로 나타낸 이유 :

https://github.com/qodo-ai/pr-agent/issues/1296
![JSON이 아닌 YAML로 출력 형태로 나타낸 이유](https://private-user-images.githubusercontent.com/21198860/378166873-47207070-9d55-43f3-80e8-09eba2a5ce07.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NDYzNDgwNjEsIm5iZiI6MTc0NjM0Nzc2MSwicGF0aCI6Ii8yMTE5ODg2MC8zNzgxNjY4NzMtNDcyMDcwNzAtOWQ1NS00M2YzLTgwZTgtMDllYmEyYTVjZTA3LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA1MDQlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNTA0VDA4MzYwMVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTlkOGE5YjU2ZTAyMzVlNzRhZWM1MmU3NGRjYWQ2Y2Y1ZGIxNGMwNDEyMzAyZDMxNDQyM2NlOTNjZjRhZWNmZjgmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.naYSGbf5bsUBxLG4BtioXGS8Q_CMsnoDHGBm2a73Puw)

GPT 모델들이 JSON 으로 구조화 된 출력을 지원하고 있어도 JSON 형식은 따옴표나, 괄호 같은 형식을 지키기 위한 불필요한 토큰이 많은 것에 비해 YAML은 들여쓰기만 잘 하면 되기 때문에 task에 불필요한 토큰을 아끼고 원래 task에 집중할 수 있게 됨.

### 유저 프롬프트

- **PR 메타데이터 + diff 본문**을 전달한다.

```markdown
--PR Info--
Title: '{{title}}'
...
The PR Diff:
======
{{ diff | trim }}
======
```

- 옵션으로 `duplicate_prompt_examples` 가 켜지면 위 **예시 YAML** 블록을 다시 보여 줘 “이렇게 출력하라”를 재강조한다.

```python
{%- if duplicate_prompt_examples %}
```

## 프롬프트별 기능 차이점

### `/describe`

> `/describe` 는 코드에 관여하지 않는 사람도 읽을 수 있게 요약하는 것이 목적
> 

- 커스텀 라벨 지원

> 해결해야할 이슈들과 맞게 PR 유형을 세분화 하기 위한 목적 ex) `Refactor`, `Test`
> 

```toml
{%- if enable_custom_labels %}

{{ custom_labels_class }}

{%- endif %}
```

- 파일 단위 변동사항 워크스루

> 파일 단위 변동 사항을 통해 리뷰 및 체크 리스트 자동화에 활용하는 목적
> 

```toml
{%- if enable_semantic_files_types %}

class FileDescription(BaseModel):
    filename: str = Field(description="The full file path of the relevant file")
{%- if include_file_summary_changes %}
    changes_summary: str = Field(description="concise summary of the changes in the relevant file, in bullet points (1-4 bullet points).")
{%- endif %}
    changes_title: str = Field(description="one-line summary (5-10 words) capturing the main theme of changes in the file")
    label: str = Field(description="a single semantic label ...
{%- endif %}
```

- 사용자가 추가한 지시문 블록을 삽입

> 기본 프롬프트(toml 파일)를 수정하지 않고 런타임에 동적 규칙을 주입 가능
> 

```toml
{%- if extra_instructions %}

Extra instructions from the user:
=====
{{extra_instructions}}
=====
{% endif %}
```

---

### `/review`
> PR이 요구사항, 코드 품질, 보안 등을 만족하는지 체크하고 평가하는 것이 목적

- 관련된 티켓에 대한 검사

```toml
{%- if related_tickets %}

class TicketCompliance(BaseModel):
    ticket_url: str = Field(description="Ticket URL or ID")
    ticket_requirements: str = Field(description="Repeat, in your own words ...
    ...
{%- endif %}
```

- 리뷰 난이도 추정

```toml
{%- if require_estimate_effort_to_review %}
    estimated_effort_to_review_[1-5]: int = Field(description="Estimate, on a scale of 1-5 (inclusive), the time and effort required to review this PR ...
{%- endif %}
```

- PR의 코드 품질에 대한 점수 제공

```toml
{%- if require_score %}
    score: str = Field(description="Rate this PR on a scale of 0-100 (inclusive) ...
{%- endif %}
```

- 보안과 관련된 조언 제공

```toml
{%- if require_security_review %}
    security_concerns: str = Field(description="Does this PR code introduce possible vulnerabilities such as exposure of sensitive information ...
{%- endif %}
```

- 대형(내용이 많은) PR의 경우 Sub-PR 로 분할

```toml
{%- if require_can_be_split_review %}
    can_be_split: List[SubPR] = Field(min_items=0, max_items=3, description="Can this PR, which contains {{ num_pr_files }} changed files in total, be divided into smaller sub-PRs ...
{%- endif %}
```

- 테스트 코드가 적혀있는지 확인

```toml
{%- if require_tests %}
    relevant_tests: str = Field(description="yes\\no question: does this PR have relevant tests added or updated ?")
{%- endif %}
```

- 리뷰 전에 작성자에게 질의를 할 수 있는 상호작용이 가능한 모드

```toml
{%- if question_str %}
    insights_from_user_answers: str = Field(description="shortly summarize the insights you gained from the user's answers to the questions")
{%- endif %}
```
---

### `/improve`
> 추가된 코드들에 대해 버그, 성능, 가독성 등에 대한 문제에 대해 어떻게 바꾸는게 좋은지 제안하는 것이 목적

> improve의 경우 `pr_code_suggestions_prompts.toml` , `pr_code_suggestions_prompts_not_decoupled.toml` 2가지 파일이 존재
> 

- `decoupled diff` 와  `표준 git diff` 차이

> decoupled diff 는 하나의 코드 청크가 new hunk와 old hunk 로 분리되어 있음
> 

> 표준 git diff 는 같은 청크 안에 `+`, `-` 라인이 뒤섞여 있음
> 

```toml
1. decoupled diff
@ ... @@ def func1():
__new hunk__
 ...
__old hunk__
 ...
 
2. 표준 git diff
@@ ... @@ def func1():
 unchanged code line0
 unchanged code line1
+new code line2
-removed code line2
 unchanged code line3
```

decoupled diff 를 사용하면 삭제된 코드(old hunk)를 별도의 블록에 넣으니 새로운 코드(new hunk)만 적용하면 되니 토큰 절약이나 변화에 대해 파악이 쉽다.

모두 decoupled diff 를 사용하면 좋겠지만 git에서 기본적으로 노출되는 방식은 +, - 를 이용한 것이 표준 형식이기 때문에 설정을 못바꾸는 저장소의 경우에는 git에서 그대로 수집해도 프롬프트가 작동해야하기 때문이다.

- 2가지 개선 모드에 대한 선택

> `전체적인 코드 품질 개선 모드`와 `치명적인 버그 개선 모드`가 있음
> 

```toml
{%- if not focus_only_on_problems %}
Your task is to examine the provided code diff, focusing on new code ... and offer concise, actionable suggestions to fix possible bugs and problems, and enhance code quality and performance.
{%- else %}
Your task is to examine the provided code diff, focusing on new code ... and offer concise, actionable suggestions to fix critical bugs and problems.
{%- endif %}
```

- 라벨에 대한 목록을 분류

```toml
{%- if not focus_only_on_problems %}
    label: str = Field(description="... 'security', 'possible bug', 'possible issue', 'performance', ...")
{%- else %}
    label: str = Field(description="'security', 'critical bug', 'general'. ...
{%- endif %}
```

## /describe·/review 는 표준 git diff 하나의 형식만 존재하고 /improve 는 왜 표준 git diff와 decoupled diff 두 가지 형식이 존재하는가?

/describe, /review 는 요약 / 검토 단계는 변경 내용이 중요하다. 

삭제(-) · 추가(+) 라인이 섞여 있어도 사람이든 LLM이든 맥락 파악에 큰 지장 없다.

/improve 는 변경 후 코드를 그대로 받아서 수정된 코드를 제안하는 것이 중요하다.

표준 git diff는 +/-가 뒤섞여 있어 불필요한 삭제 코드를 따로 걸러야 한다

왜냐하면 모델이 엉뚱한 곳을 고칠 위험 + 토큰 낭비

# 느낀 점

많은 사람들이 기여한 오픈소스임에도 프롬프트가 생각보다 단순했다고 느꼈다. 코드 리뷰는 꽤나 복잡하고 어려운 일이라고 생각했는데 CoT, ReAct 같이 더 정확하고 추론하는 기법을 사용하지 않은게 신기했다.