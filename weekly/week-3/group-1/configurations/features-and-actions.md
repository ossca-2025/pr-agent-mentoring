## [pr_add_docs]

자동으로 문서화 스텁을 추가하거나 보강할 때 사용하는 커맨드의 동작을 정의

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `extra_instructions` | 문서 생성 시 AI에 추가로 전달할 사용자 지시사항 | "" |
| `docs_style` | 생성할 문서의 스타일 지정- "Sphinx", "Google Style", "Numpy Style", "PEP257", "reStructuredText" | "Sphinx" |
| `file` | 여러 컴포넌트 중 특정 파일만 대상으로 할 때 경로 지정 | "" |
| `class_name` | 동일 파일 내 여러 클래스 중 특정 클래스만 대상으로 할 때 사용 | "" |

## 사용 예시

- PR 코멘트: `/add_docs` 로 호출

```toml
[pr_add_docs]
extra_instructions = "함수 동작 예제를 추가하고, 반환값 설명을 구체적으로 작성하세요."
docs_style = "Google Style with Args, Returns"
file = "src/utils/math_ops.py"
class_name = "MatrixConverter"
```

## 적용 방법

1. 리포지토리 기본 브랜치 루트에 `.pr_agent.toml` 파일로 추가
2. PR 생성 후 코멘트로 각 명령어(`/add_docs`, `/update_changelog`, `/analyze,` `/test`, `/improve_component)` 입력
3. GitHub Actions에서 Codium-ai/pr-agent@main 액션 사용 시, 환경변수로 CONFIG_FILE=.pr_agent.toml 지정 가능

<br/>

## [pr_update_changelog]

PR 병합 전후에 자동으로 CHANGELOG를 갱신하는 동작을 제어

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `push_changelog_changes` | 변경된 CHANGELOG 파일을 커밋/푸시할지 여부 | false |
| `extra_instructions` | AI에 추가 지시사항 | "" |
| `add_pr_link` | CHANGELOG에 PR 링크를 자동 포함할지 여부 | true |
| `skip_ci_on_push` | 푸시 시 CI 실행 스킵 여부 ([skip ci] 태그 자동 추가 등) | true |

## 사용 예시

- PR 코멘트: `/update_changelog` 로 호출
```toml
[pr_update_changelog]
push_changelog_changes = true
extra_instructions = "Breaking change으로 표기하고, 섹션을 ‘### Removed’ 아래에 추가하세요."
add_pr_link = true
skip_ci_on_push = false
```

<br/>

## [pr_analyze]

PR의 전반적인 코드·설명 분석 커맨드를 위한 최소 설정

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `enable_help_text` | 분석 결과에 헬프 텍스트 포함 여부 | true |

## 사용 예시

- PR 코멘트: `/analyze` 로 호출

```toml
[pr_analyze]
enable_help_text = false
```

<br/>

## [pr_test]

코드 변경에 대해 자동으로 테스트 코드를 생성하는 커맨드를 설정

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `extra_instructions` | 테스트 생성 시 AI에 추가 지시사항 | "" |
| `testing_framework` | 사용할 테스트 프레임워크 명시 (예: pytest, unittest) | "" |
| `num_tests` | 생성할 테스트 케이스 수 (최대 5) | 3 |
| `avoid_mocks` | 실제 객체 기반 테스트 우선 생성 
(true면 Mock 대신 실제 객체 사용) | true |
| `file` | 여러 대상 중 특정 파일만 테스트 생성 시 지정 | "" |
| `class_name` | 특정 클래스 내부 메서드만 테스트할 때 사용 | "" |
| `enable_help_text` | 테스트 코드에 헬프(가이드) 텍스트 포함 여부 | false |

## 사용 예시

- PR 코멘트: `/test` 로 호출

```toml
[pr_test]
extra_instructions = "예외 케이스를 포함한 경계값 테스트도 작성하세요."
testing_framework = "pytest"
num_tests = 5
avoid_mocks = false
file = "src/api/client.py"
```

<br/>

## [pr_improve_component]

특정 컴포넌트 개선 제안을 생성하는 커맨드를 조정

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `num_code_suggestions` | 제안할 코드 수정안 개수 | 4 |
| `extra_instructions` | 추가 지시사항 | "" |
| `file` | 대상 파일 지정 | "" |
| `class_name` | 대상 클래스 지정 | "" |

## 사용 예시

- PR 코멘트: `/improve_component` 로 호출

```toml
[pr_improve_component]
num_code_suggestions = 2
extra_instructions = "성능 최적화 관점으로 제안해주세요."
file = "src/models/transformer.py"
class_name = "AttentionLayer"
```

<br/>

## [auto_best_practices]

PR 전반에 모범 사례 패턴을 자동 감지·적용하도록 설정

| **키** | **설명** | **기본값** |
| --- | --- | --- |
| `enable_auto_best_practices` | 전체 자동 모범 사례 기능 활성/비활성 | true |
| `utilize_auto_best_practices` | 개선(/improve) 툴 내에서 모범 사례 활용 여부 | true |
| `extra_instructions` | 모범 사례 생성 시 AI에 추가 지시사항 | "" |
| `content` | 특정 조직·프로젝트에 특화된 Best Practice 패턴 정의 | "" |
| `max_patterns` | 감지할 최대 패턴 수 | 5 |

## 사용 예시

- 자동적으로 PR 전반에 Best Practices 체크가 수행

```toml
[auto_best_practices]
enable_auto_best_practices = true
utilize_auto_best_practices = false
extra_instructions = "보안 취약점 관련 패턴 우선 검토"
content = "Python, Django, REST API"
max_patterns = 10
```