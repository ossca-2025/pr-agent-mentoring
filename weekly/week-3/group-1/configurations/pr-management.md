## [pr_reviewer]

Pull Request에 대한 AI 리뷰 기능을 제어하며, 어떤 항목을 필수로 검토할지, 리뷰 라벨 부착 여부, 출력 형식 등을 조정

```toml
[pr_reviewer]
require_score_review=false
require_tests_review=true
require_estimate_effort_to_review=true
require_can_be_split_review=false
require_security_review=true
require_ticket_analysis_review=true
persistent_comment=true
extra_instructions=""
final_update_message=true
enable_review_labels_security=true
enable_review_labels_effort=true
require_all_thresholds_for_incremental_review=false
minimal_commits_for_incremental_review=0
minimal_minutes_for_incremental_review=0
enable_intro_text=true
enable_help_text=false
```

| 설정 항목                                           | 설명                                   |
| ----------------------------------------------- | ------------------------------------ |
| `require_score_review`                          | PR의 품질 점수에 대한 평가를 필수로 포함할지 여부        |
| `require_tests_review`                          | 테스트 코드 존재 여부에 대한 리뷰를 필수로 수행          |
| `require_estimate_effort_to_review`             | 개발 작업량(노력 추정치)을 평가하도록 설정             |
| `require_can_be_split_review`                   | PR을 더 작은 단위로 나눌 수 있는지 분석하도록 설정       |
| `require_security_review`                       | 보안 관련 변경 사항이 있는지 확인하고 리뷰에 반영         |
| `require_ticket_analysis_review`                | 연관된 이슈/티켓 분석을 요구하는지 여부               |
| `persistent_comment`                            | 리뷰 코멘트를 항상 PR에 남겨두고 삭제하지 않도록 설정      |
| `extra_instructions`                            | AI 리뷰에 추가 지시 사항을 입력할 수 있는 사용자 정의 문자열 |
| `final_update_message`                          | 리뷰 완료 시점에 최종 메시지를 출력할지 여부            |
| `enable_review_labels_security`                 | 보안 관련 리뷰가 있을 경우 라벨을 자동으로 부착          |
| `enable_review_labels_effort`                   | 작업량 평가가 포함될 경우 effort 관련 라벨 부착       |
| `require_all_thresholds_for_incremental_review` | 증분 리뷰 시 모든 기준을 충족해야 리뷰를 수행하도록 설정     |
| `minimal_commits_for_incremental_review`        | 증분 리뷰 실행을 위한 최소 커밋 수 기준              |
| `minimal_minutes_for_incremental_review`        | 증분 리뷰 실행을 위한 최소 시간 기준 (단위: 분)        |
| `enable_intro_text`                             | 리뷰의 시작에 도입 문구를 포함시킬지 여부              |
| `enable_help_text`                              | 리뷰 결과에 도움말 텍스트를 포함시킬지 여부             |

<br/>

## [pr_description]

AI가 PR 설명을 요약하거나 생성하는 데 사용하는 다양한 출력 옵션, 형식, 처리 전략을 정의

```toml
[pr_description]
publish_labels=false
add_original_user_description=true
generate_ai_title=false
use_bullet_points=true
extra_instructions=""
enable_pr_type=true
final_update_message=true
enable_help_text=false
enable_help_comment=true
publish_description_as_comment=false
publish_description_as_comment_persistent=true
enable_semantic_files_types=true
collapsible_file_list="adaptive"
collapsible_file_list_threshold=6
inline_file_summary=false
use_description_markers=false
include_generated_by_header=true
enable_large_pr_handling=true
max_ai_calls=4
async_ai_calls=true
```

| 설정 항목                                       | 설명                                                    |
| ------------------------------------------- | ----------------------------------------------------- |
| `publish_labels`                            | AI가 생성한 설명에 따라 라벨을 자동으로 부착할지 여부                       |
| `add_original_user_description`             | 기존 사용자가 작성한 설명을 유지하면서 AI 설명을 덧붙일지 여부                  |
| `generate_ai_title`                         | PR 제목도 AI가 자동으로 생성할지 여부                               |
| `use_bullet_points`                         | PR 설명 항목들을 bullet 형식으로 출력                             |
| `extra_instructions`                        | 설명 생성에 반영할 추가 지시 사항 (프롬프트 확장용)                        |
| `enable_pr_type`                            | 버그, 기능 추가 등 PR 유형 자동 분류 기능 활성화                        |
| `final_update_message`                      | 설명 업데이트 완료 후 사용자에게 안내 메시지를 출력                         |
| `enable_help_text`                          | 설명 결과에 도움말 포함 여부 (보통 학습용)                             |
| `enable_help_comment`                       | 설명을 일반 텍스트가 아닌 GitHub 코멘트로도 제공                        |
| `publish_description_as_comment`            | 생성된 설명을 PR description이 아닌 코멘트로만 게시할지 여부              |
| `publish_description_as_comment_persistent` | 해당 코멘트를 항상 유지 (수정되지 않음)                               |
| `enable_semantic_files_types`               | 파일 변경 내용을 의미론적으로 분석하여 설명에 반영                          |
| `collapsible_file_list`                     | 변경 파일 목록을 접을 수 있도록 표시 (`true`, `false`, `'adaptive'`) |
| `collapsible_file_list_threshold`           | 파일 수가 이 값보다 많을 경우 자동 접기                               |
| `inline_file_summary`                       | 파일별 요약을 PR 설명 안에서 인라인으로 표시할지 여부                       |
| `use_description_markers`                   | 마커(`<!-- -->`)로 설명 구역을 감쌀지 여부 (구조적 추출용)               |
| `include_generated_by_header`               | 설명 상단에 "generated by PR-Agent" 같은 안내 추가               |
| `enable_large_pr_handling`                  | 대규모 PR에서 효율적으로 나눠 처리할지 여부                             |
| `max_ai_calls`                              | 큰 PR 처리 시 AI 호출 횟수 제한                                 |
| `async_ai_calls`                            | 병렬로 여러 파일을 AI에 동시에 보내 효율적으로 처리                        |

<br/>

## [pr_questions]

사용자가 PR에 대해 자연어 질문을 던질 수 있는 /ask 기능의 동작 방식을 제어  

```toml
[pr_questions]
enable_help_text=false
use_conversation_history=true
```

| 설정 항목                      | 설명                                    |
| -------------------------- | ------------------------------------- |
| `enable_help_text`         | AI 응답에 도움말 텍스트(사용법, 힌트 등)를 포함할지 여부    |
| `use_conversation_history` | 이전 질문-응답 이력을 고려해 더 일관성 있는 응답을 생성할지 여부 |


<br/>

## [pr_code_suggestions]

PR 코드 변경 내용에 대해 AI가 리팩토링, 버그 수정, 개선 제안을 생성하도록 하는 /improve 명령의 동작 방식을 제어

```toml
[pr_code_suggestions]
max_context_tokens=24000
commitable_code_suggestions=false
dual_publishing_score_threshold=-1
focus_only_on_problems=true
extra_instructions=""
enable_help_text=false
enable_chat_text=false
persistent_comment=true
max_history_len=4
publish_output_no_suggestions=true
apply_suggestions_checkbox=true
suggestions_score_threshold=0
new_score_mechanism=true
new_score_mechanism_th_high=9
new_score_mechanism_th_medium=7
auto_extended_mode=true
num_code_suggestions_per_chunk=4
max_number_of_calls=3
parallel_calls=true
final_clip_factor=0.8
decouple_hunks=false
demand_code_suggestions_self_review=false
code_suggestions_self_review_text="**Author self-review**: I have reviewed the PR code suggestions, and addressed the relevant ones."
approve_pr_on_self_review=false
fold_suggestions_on_self_review=true
publish_post_process_suggestion_impact=true
wiki_page_accepted_suggestions=true
allow_thumbs_up_down=false
```

| 설정 항목                                    | 설명                                            |
| ---------------------------------------- | --------------------------------------------- |
| `max_context_tokens`                     | LLM에게 보낼 수 있는 최대 토큰 수                         |
| `commitable_code_suggestions`            | AI 제안을 GitHub UI에서 바로 커밋 가능한 형식으로 출력          |
| `dual_publishing_score_threshold`        | 점수가 해당 값 이상이면 테이블과 커밋 형식 둘 다로 출력 (`-1`은 비활성화) |
| `focus_only_on_problems`                 | 명백한 문제에 대해서만 제안을 생성 (불필요한 제안 제거)              |
| `extra_instructions`                     | 추가적인 리뷰 조건, 지침을 프롬프트에 포함 가능                   |
| `enable_help_text`                       | 제안 결과에 도움말 포함 여부                              |
| `enable_chat_text`                       | 대화형으로 응답 생성할지 여부 (미사용 시 정적 응답)                |
| `persistent_comment`                     | 리뷰 코멘트를 항상 남기고 수정하거나 제거하지 않음                  |
| `max_history_len`                        | 이전 리뷰 히스토리 참조 최대 개수                           |
| `publish_output_no_suggestions`          | 개선할 사항이 없어도 결과 메시지 출력 여부                      |
| `apply_suggestions_checkbox`             | 제안에 "적용 여부 체크박스" 포함                           |
| `suggestions_score_threshold`            | 제안의 품질 점수 기준 (0\~10)                          |
| `new_score_mechanism`                    | 개선된 새로운 점수 시스템 사용 여부                          |
| `new_score_mechanism_th_high`            | 상위 평가 기준점 (9 이상이면 매우 유효한 제안으로 간주)             |
| `new_score_mechanism_th_medium`          | 중간 수준 평가 기준점 (7 이상)                           |
| `auto_extended_mode`                     | `--extended` 모드를 자동으로 활성화                     |
| `num_code_suggestions_per_chunk`         | 코드 덩어리당 최대 제안 수                               |
| `max_number_of_calls`                    | LLM 호출 횟수 제한 (extended 모드)                    |
| `parallel_calls`                         | 여러 청크를 동시에 병렬로 처리                             |
| `final_clip_factor`                      | 토큰 길이 초과 방지를 위해 마지막 결과를 잘라내는 비율               |
| `decouple_hunks`                         | 여러 변경 청크를 독립적으로 처리할지 여부                       |
| `demand_code_suggestions_self_review`    | 작성자에게 제안에 대한 자가 검토 체크를 요구                     |
| `code_suggestions_self_review_text`      | 자가 검토 체크박스 옆에 표시될 문구                          |
| `approve_pr_on_self_review`              | 자가 검토가 완료되면 자동으로 PR을 승인 (Pro 기능)              |
| `fold_suggestions_on_self_review`        | 자가 검토 완료 시 제안 목록을 접음 (Pro 기능)                 |
| `publish_post_process_suggestion_impact` | 개선 사항의 영향도 후처리 결과를 출력                         |
| `wiki_page_accepted_suggestions`         | 수락된 제안을 위키 페이지에도 반영할지 여부                      |
| `allow_thumbs_up_down`                   | 제안에 👍/👎 반응을 허용할지 여부                         |

<br/>

## [checks]

PR과 연계된 자동화된 검사 결과(GitHub Actions, CI/CD 테스트 등)를 기반으로, AI가 품질 피드백을 제공하는 기능을 제어 (일반적으로 Pro 기능으로 제공)

```toml
[checks]
enable_auto_checks_feedback=true
excluded_checks_list=["lint"]
persistent_comment=true
enable_help_text=true
final_update_message=false
```

| 설정 항목                         | 설명                                               |
| ----------------------------- | ------------------------------------------------ |
| `enable_auto_checks_feedback` | 빌드, 테스트, 린트 등 자동 검사 결과를 기반으로 AI 피드백을 생성할지 여부     |
| `excluded_checks_list`        | 피드백 생성에서 제외할 체크 이름 목록 (예: `"lint"`, `"codecov"`) |
| `persistent_comment`          | 피드백 코멘트를 PR에서 유지 (삭제되거나 덮어씌워지지 않음)               |
| `enable_help_text`            | 체크 피드백에 설명 및 도움말 문구를 추가                          |
| `final_update_message`        | 체크 피드백이 완료되었음을 알리는 마무리 메시지를 출력할지 여부              |
