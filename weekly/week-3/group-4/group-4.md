# 3주차 조별과제: PR Agent 개발 환경 설정 및 주요 태스크 코드 분석
## [4조] 주제 3: review(설정 및 코드) 조사, 분석, 사용, 정리
- 진행 방식: 조별 회의 진행 후 review 기능 관련 설정 옵션 분담, 팀원 개별 branch 생성해 PR /review 사용해보고 담당한 옵션 분석 및 정리
- 수행 결과: 전원 완료 (이서현, 정동환, 박영신, 박상민, 김범진)
---
## configuration.toml의 review 관련 옵션들
```toml
[pr_reviewer] # /review #
# enable/disable features -> (1)에서 소개
require_score_review=false
require_tests_review=true
require_estimate_effort_to_review=true
require_can_be_split_review=false
require_security_review=true
require_ticket_analysis_review=true
# general options -> (2)에서 소개
persistent_comment=true
extra_instructions = ""
final_update_message=true
# review labels -> (3)에서 소개
enable_review_labels_security=true
enable_review_labels_effort=true
# specific configurations for incremental review (/review -i) -> (4)에서 소개
require_all_thresholds_for_incremental_review=true
minimal_commits_for_incremental_review=100
minimal_minutes_for_incremental_review=50
enable_intro_text=true
enable_help_text=false # Determines whether to include help text in the PR review. Enabled by default.
```
## (1) enable/disable features

> // 조사자: 박상민

기본 세팅:
```toml
require_score_review=false
require_tests_review=true
require_estimate_effort_to_review=true
```
### 기본 세팅인 경우 예시
![image](https://github.com/user-attachments/assets/7fae19fd-e87e-4741-9b55-1c59da09757e)

### require_score_review: PR 평가 점수 표시 기능 활성화(true)/비활성화(false)
![image](https://github.com/user-attachments/assets/15d89906-ab20-4c4e-95ea-75fdb6b269eb)

### require_tests_review: 테스트 리뷰 활성화(true)/비활성화(false) -> 테스트를 추가했을 때 "PR contains tests", 관련 테스트가 없으면 "No relevant tests"
![image](https://github.com/user-attachments/assets/bd1b8bc9-4263-4fed-afe4-10827a51072e)

#### 테스트 커버리지 관련 테스트
![image](https://github.com/user-attachments/assets/62153105-57b6-4692-b383-68ca56299f14)

### require_estimate_effort_to_review: 리뷰 난이도(n/5) 라벨 표시 기능 활성화(true)/비활성화(false)

#### 리뷰 관련 model 요청 및 프롬프트 분석
외부 LLM 모델에게 전송하는 prompt는 두 부분 으로 구성됨: system prompt, user prompt
1. 시스템 프롬프트 (System Prompt)
   - 시스템 프롬프트는 LLM에게 기본적인 역할과 임무를 설명함
   - 프롬프트에 담긴 정보
    - PR-Reviewer라는 역할 부여
    - Git Pull Request(PR)를 리뷰하는 임무 설명
    - 특히 새로 추가된 코드(+로 시작하는 라인)에 집중해서 리뷰할 것을 지시
        - PR에서 어떻게 파일과 코드 변경이 표시되는지, 코드 인용 시 백틱(`) 사용한다는 등 LLM이  prompt를 잘 해석할 수 있게끔 가이드(Context) 제공
    - 사용자로부터의 추가 지시사항 (한국어(ko-kr)로 응답해야 한다는 걸 추가했지만, yaml 파일의 key 값이 영어)
    - 출력 형식에 대한 명확한 설명 (YAML 형식, `Example output`)
    - Pydantic 정의를 통한 응답 구조 설명
2. 사용자 프롬프트 (User Prompt)
   - 사용자 프롬프트는 실제 리뷰할 PR 정보가 포함됨
    - PR 정보 (날짜, 제목, 브랜치)
    - PR diff 코드
  
3. model 응답
```yaml
review:
  estimated_effort_to_review_[1-5]: | # PR 복잡도 평가: 1-5 척도로 리뷰 난이도 측정
    2
  score: 95 # 코드 품질 점수: 0-100 척도로 코드 품질 평가
  relevant_tests: | # 테스트 관련성: 관련 테스트가 추가/업데이트되었는지 여부
    yes
  key_issues_to_review: [] # 주요 검토 이슈: 중요한 버그나 성능 문제에 대한 목록(0-3개)
  security_concerns: | # 보안 문제: 잠재적인 보안 취약점 식별
    No
```

---

> // 조사자: 정동환

```toml
require_can_be_split_review=false
require_security_review=true
require_ticket_analysis_review=true
```
### require_can_be_split_review: 비활성화 시(false) PR이 여러 주제를 포함하고 있는지 확인하고 더 작은 PR로 분할할 수 있는지 확인하는 섹션 추가
- 기본값 false
- true일 시 동작하는 로직은 코드에서 찾지 못하였으나, 모델에 영향을 주는 인자로 추측


### require_security_review: 활성화 시(true) PR에 보안 또는 취약점 문제가 있는지 확인하는 섹션 추가
- 기본값 true. true면 `pr_agent/tools/pr_reviewer.py`에서 아래 코드 실행
  - `data['review']['security_concerns']`: AI 모델이 생성한 리뷰 데이터에서 `'security_concerns'` 항목의 값을 가져옴
  - 주석에서 이 값이 일반적으로 `"yes, because..."` 형식으로 작성됨을 알 수 있음
  - 추출한 텍스트를 소문자로 변환한 후 `'yes'`나 `'true'`라는 단어가 포함되어 있는지 확인
  - 이 단어들이 포함되어 있으면 보안 우려사항이 있다고 판단하고 PR에 `"Possible security concern"` 라벨을 추가
```
if get_settings().pr_reviewer.enable_review_labels_security and get_settings().pr_reviewer.require_security_review:
  security_concerns = data['review']['security_concerns']  # yes, because ...
  security_concerns_bool = 'yes' in security_concerns.lower() or 'true' in security_concerns.lower()
  if security_concerns_bool:
  review_labels.append('Possible security concern')
```
### require_ticket_analysis_review: 활성화 시(true) PR에 GitHub 또는 Jira 티켓 링크가 포함된 경우 PR이 실제로 티켓 요구사항을 충족했는지 확인하는 섹션 추가
- 기본값 true. `pr_reviewer.py`의 137번째 줄에서 `await extract_and_cache_pr_tickets(self.git_provider, self.vars)`
- `require_ticket_analysis_review`이 true일시 `pr_agent/tools/ticket_pr_compliance_check.py` 내부에서 아래 로직 실행
  1. 이미 캐시된 티켓이 있는지 확인
  2. 캐시된 티켓이 없으면 새로 추출
  3. 추출된 티켓 정보(메인 이슈와 하위 이슈 모두)를 설정에 저장하고 vars에 추가
- `check_tickets_relevancy()` 티켓과 관련성을 검사하는 함수로 보이는데, 현재는 모두 true를 반환하고 있음
  ```
  def check_tickets_relevancy():
    return True
  ```

#### github actions 동작에 관한 코드 분석 - Dockerfile부터 연관된 파일 추적
1. DockerFile.github_action
2. pr-agent/github_action/entrypoint.sh
3. app/pr_agent/servers/github_action_runner.py의 메인 함수(run_action)
   - GitHub Actions에서 제공하는 환경 변수들을 가져옴
   - OpenAI API 키와 조직 ID도 환경 변수에서 가져옴
   - 불러온 환경 변수를 get_settings()의 set 함수를 이용해 세팅
4. 이벤트 페이로드 로드
5. 레포지토리 설정 적용
6. 이벤트 핸들링
7. 이슈 코멘트

---

## (2) General Options
```toml
persistent_comment=true
extra_instructions = ""
final_update_message = true
```
### persistent_comment: true로 설정하면, 리뷰 댓글이 지속적으로 유지.
- 기본값 true. 즉, 새로운 리뷰 요청이 있을 때마다 기존 리뷰 댓글을 수정하여 반영합니다. (댓글이 새로 달리는 게 아니라 기존 것을 업데이트)
### final_update_message: true로 설정하면, 온라인 코멘트 모드에서 지속적인 리뷰 댓글이 업데이트될 때 PR에 "리뷰가 업데이트되었습니다"라는 짧은 메시지와 링크가 자동으로 추가.
- 기본값 true.
### extra_instructions: 리뷰 도구에게 주는 선택적인 추가 지시사항
- AI가 리뷰할 때 해당 조건을 반영합니다. (예시: "파일 X의 변경 사항에 집중하고, ...는 무시하라")
### 3개 설정의 리뷰 동작 차이 실험 진행
| 설정 항목 | 실험 목적 |
|------|------|
| `persistent_comment`	| 리뷰 댓글 덮어쓰기 vs 새로 달림 차이 확인 |
| `extra_instructions`	| AI가 지시사항에 따라 리뷰 스타일을 바꾸는지 확인 |
| `final_update_message`	| 댓글 수정 시 "업데이트 안내 코멘트"가 생기는지 확인 |

각 설정마다 커밋 메시지에 `[test-x]` 을 prefix로 달아서 (ex: `[test-persistent-true]`) 본 테스트의 커밋 목적을 한눈에 볼 수 있게 설정하였음

### **[1] 기존 설정 그대로**

| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | true |
| `extra_instructions` | "” |
| `final_update_message` | true |

![image](https://github.com/user-attachments/assets/4697c578-9e2f-42e0-8781-5360ec446283)

- `persistent_comment`=true **확인**
    - pr이 수정되었을 때(커밋 추가), 리뷰가 새로 생성되는 것이 아니라 기존의 것이 업데이트됨
- `extra_instructions` 설정해둔 게 없기에 변화 없음
- `final_update_message`=true **확인**
    ![image](https://github.com/user-attachments/assets/a2d49f76-52d9-4303-8ca6-58e97a426fbd)

    - **위와 같이 업데이트 안내 댓글이 생성됨**
        - 리뷰가 기존 댓글을 덮어썼을 때, GitHub PR에 "리뷰가 이 커밋까지 업데이트되었다"는 짧은 안내 문구를 표시해주는 기능

### **[2] `extra_instructions` 추가**

| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | true |
| `extra_instructions` | “Ignore the new_feature function. Only focus on test_behavior.” |
| `final_update_message` | true |

```bash
python3 -m pr_agent.cli --pr_url https://github.com/ossca-2025-pr-agent-mentoring-group4/pr-agent/pull/1 review \
  --pr_reviewer.extra_instructions="Ignore the new_feature function. Only focus on test_behavior."
```

- 위의 명령어로 new_feature 함수의 리뷰는 무시하도록 진행
  ![image](https://github.com/user-attachments/assets/6722d2f9-7ebb-4462-8786-15285ca6a4dd)

- test_behavior에 대해서는 `test_behavior()` 함수가 **테스트용 함수**로 감지되었고, 그 안의 assert 문이 **논리적으로 잘못되었다**고 판단하며 언급하였지만
- extra_instructions="Ignore the new_feature function. Only focus on test_behavior." 이 문구로 인해 new_feature에 대해서는 언급조차 하지 않음(같은 문제가 있더라도)

### **[3]** `persistent_comment` =false로 수정

> (예상) 새로운 댓글에 리뷰를 생성할 것
> 

| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | false |
| `extra_instructions` | "” |
| `final_update_message` | true |

![image](https://github.com/user-attachments/assets/da6840ec-598c-488d-b6b6-a068b3e8c4e6)

- 기존 리뷰는 그대로 두고

![image](https://github.com/user-attachments/assets/5b7ba1e6-7713-441d-aa42-be5dddc5f441)

- 최근 커밋 아래 새로운 리뷰가 생성됨
- 즉, 기존 리뷰에 이어서 수정되는 것이 아닌, 새로 생성됨을 확인할 수 있음

+`extra_instructions`을 설정하지 않아서, test_behavior과 new_feature 모두 확인하는 것을 볼 수 있음

### **[4]** `persistent_comment` =true로 수정 / false에서 바로 true로 바꾸면 ??

→ 맨 처음 생성된 리뷰에서 이어질까 아니면 최근에 생성된 리뷰에서 이어질까?

| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | true |
| `extra_instructions` | "” |
| `final_update_message` | true |

![image](https://github.com/user-attachments/assets/125261dd-87cd-4101-af9b-9137186c39f8)

- 가장 최근 것이 아닌, 처음으로 이어졌던 리뷰에서 내용이 이어짐!!
- 가장 처음 생성된 "persistent comment"를 찾아서 거기에 계속 덮어씀 (예전의 것으로 회귀하여 계속 이어짐)

### **[5]** `final_update_message`=false로 수정

> 예상했던 바: persistent comment가 갱신되더라도 "업데이트 알림 댓글"이 추가되지 않을 것!


| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | true |
| `extra_instructions` | "” |
| `final_update_message` | false |

![image](https://github.com/user-attachments/assets/c9407c16-7232-4069-a6e2-5162df2d9ef8)

가장 최근 커밋 b588986 → comment가 추가되지 않았으나

![image](https://github.com/user-attachments/assets/806c46f0-d0e5-4d6e-b6c8-428fe85c8e8a)

기존 리뷰 댓글에는 반영이 됨!! review 부분에도 반영이 안 될 줄 알았는데, 그렇지 않아서 신기!

| 설정 값 | 효과 | PR 코멘트의 변화 |
| --- | --- | --- |
| `final_update_message = true` (기본값) | Persistent 코멘트가 업데이트될 때, 아래와 같이 "Persistent review updated to latest commit ..." 이라는 짧은 알림 코멘트가 추가로 작성됨 | 추가 메시지 표시됨 |
| `final_update_message = false` | Persistent 코멘트는 업데이트되지만, 아래에 별도 코멘트는 생성되지 않음 | 별도 알림 없음 |

한 번 더 했을 때도 동일한 결과

### **[6]** `extra_instructions` 잘못된 지시 넣어보기

| 설정값 | 값 |
| --- | --- |
| `persistent_comment` | true |
| `extra_instructions` | "Focus only on the function calculate_summary. Ignore all other code.” |
| `final_update_message` | true |

```python
def test_behavior():
    # 테스트용 코드
    assert 1 + 1 == 3  # 일부러 오류 넣기
    return "test A"

def new_feature():
    # 새로운 기능
    print("New feature")
```

위 파일에 없는 calculate_summary 함수에 대해서만 집중하라 지시했으나,

![image](https://github.com/user-attachments/assets/594fd2e5-f93a-45af-9f4c-fe23122955bc)

그 말을 그대로 듣진 않는 것으로 확인

## general options 최종 정리

| 옵션명 | 설명 | 기본값 |
| --- | --- | --- |
| `persistent_comment` | 리뷰를 덮어쓰기 모드로 유지할지 여부
`true`로 설정 시, 같은 PR에 대해 리뷰를 여러 번 요청하면 기존 리뷰를 업데이트함 | `true` |
| `extra_instructions` | AI에게 주는 추가 지시문
ex: `"Focus on X file only"` 처럼 특정 파일이나 함수에만 집중하도록 유도 | `""` (비어 있음) |
| `final_update_message` | 리뷰 업데이트 시 추가 코멘트를 달 것인지 여부
`true`면 마지막에 `"Persistent review updated to latest commit"` 같은 업데이트 메시지를 별도 댓글로 추가 | `true` |
