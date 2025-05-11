## [pr_similar_issue]

Github  issue 또는 PR에 대해 유사한 과거 이슈를 찾아줌

https://qodo-merge-docs.qodo.ai/tools/similar_issues/#how-to-use

### Config 설정

configuration.toml

```bash
[pr_similar_issue]
skip_comments = false
force_update_dataset = false
max_issues_to_scan = 500
vectordb = "pinecone"
```

`skip_comments` 

- 이슈(Indexing) 과정에서 각 이슈의 댓글까지 함께 처리할지 여부
    - False : _process_issue()가 댓글 목록을 가져와 댓글 하나하나를 벡터(Record)로 색인
    - True: 오직 이슈 제목과 본문막 색인 (get_comments() 호출 X)
        - 색인 시간이 짧아짐.

`force_update_dataset` 

- 기존에 만들어둔 벡터 DB 인덱스가 이미 존재할 경우, 전체 레포지토리를 처음부터 인덱싱할지의 여부
    - False
        - 인덱스가 없으면 전체 인덱싱.
        인덱스가 존재하면, 마지막으로 색인된 벡터 이후 새 이슈의 부분만 Upsert
    - True
        - 전체 리포지토리 처음부터 다시 인덱싱 및 Overwrite.

`max_issues_to_scan`

- 색인 또는 업데이트 시에 최대 몇 개의 과거 이슈까지 순회할지 상한선을 설정
    - 색인시간이 과도하게 늘어나는 것을 방지

`vectordb`

- 유사도 검색을 위해 벡터 데이터를 백엔드에 저장해둠
- get_settings().pr_similar_issue.vectordb 값에 따라 Vector DB 분기 (pinecone, lancedb)
    - lanceDB설정
        - 테이블 존재 여부 및 `force_update_dataset`에 따라
            - **전체 인덱싱**: `_update_table_with_issues` 호출
            - **업데이트 인덱싱**: 신규 이슈만 `_update_table_with_issues` 호출
    - Pinecode 설정
        - 기존 인덱스 유무 및 설정에 따라
            - 전체 인덱싱 : 리포지토리의 모든 이슈를 `_update_index_with_issues`로 색인
            - 업데이트 인덱싱 : 마지막으로 색인된 이슈 이후 신규 이슈만 `_update_index_with_issues upsert`

### 사용방법

```bash
python3 cli.py --issue_url=... similar_issue
```

<br/>

## [pr_find_similar_component]

PR 내 클래스, 메서드, 함수 등 특정 코드 컴포넌트와 유사한 코드를 레포지토리에서 찾아주는 기능

주어진 컴포넌트 이름을 기반으로 코드 베이스를 스캔해, 추출된 키워드를 사용해 유사도를 계싼한뒤 상위 결과 반환.

### 사용방법

- 수동

```bash
# COMPONENT_NAME: PR 내에서 찾고자 하는 클래스, 메서드, 함수 등의 정확한 이름
/find_similar_component COMPONENT_NAME

# 동일 이름이 여러 위치에 있을 경우, 대상 좁힐 수 있음
/find_similar_component COMPONENT_NAME \
  --pr_find_similar_component.file=FILE_PATH \
  --pr_find_similar_component.class_name=CLASS_NAME

/analyze 
# 검색하고자 하는 컴포넌트 행의 similar 체크박스를 클릭
```

- analyze 테이블

```bash
/analyze
```

### 설정옵션

configuration.toml

`class_name`

`file`

- **역할**: 검색 대상 파일(또는 모듈) 경로를 지정
    - 클래스명이 지정되지 않았거나, 전체 파일 단위로 검색하고 싶을 때 사용. 파일 전체 내용을 벡터화해 비교합니다.

`search_from_org = false`

- 유사 컴포넌트를 검색할 범위를 지정합니다.
    - `false` (기본): 현재 리포지토리 내에서만 검색
    - `true` : 조직(organization)  모든 저장소에서 검색

`allow_fallback_less_words = true`

- 검색 키워드 수가 부족할 때의 동작을 제어
    - `true` : 추출된 키워드 수가 `number_of_keywords`보다 적어도, 가능한 만큼만 사용해 결과 반환
    - `false` : 충분한 키워드가 확보되지 않으면 검색 자체를 취소하거나 에러 처리
    - 키워드 추출이 실패한 경우에도 “0개 결과”가 아닌 “최선의 결과”를 보여주도록 보장
- `number_of_keywords = 5`
- 검색 쿼리를 구성하기 위해 자동 추출할 핵심 키워드 수
    - 지정된 클래스/파일 내용에서 TF-IDF, 키워드 추출 알고리즘 등을 통해 가장 대표성이 높은 단어 또는 식별자를 최대 이 수만큼 뽑아냅니다.
- `number_of_results = 5`
    - 반환할 유사 코드 스니펫 최대 개수

<br/>

## [best_practices]

PR 코드 리뷰 시 AI 모델에 조직·프로젝트별 코딩 가이드라인을 제공

AI 모델은 best_practices.md에 정의된 파일을 참조하여 PR 제출자가 코드 스타일 등 사전 정의된 규칙을 준수하도록 돕는 역할

### 설정방법

- 레포 root에 best_practices.md 파일을 추가
- 문법: Markdown 헤더(##)로 섹션 구분 후, 리스트 형식으로 규칙나열

```bash
# [Python]
## Project best practices
- Make sure that I/O operations are encapsulated in a try-except block
- Use the `logging` module for logging instead of `print` statements
- Use `is` and `is not` to compare with `None`
- Use `if __name__ == '__main__':` to run the code only when the script is executed
- Use `with` statement to open files
```

### 설정옵션

```bash
content = ""
organization_name = ""
max_lines_allowed = 800
enable_global_best_practices = false
```

`enable_global_best_practices = false`

- false :  기본적으로 로컬
- true : 조직 공통 레포지토리에 있는 글로벌 가이드라인 함께 적용

`organization_name`

- 제안 라벨이 `{organization_name} best practice` 형태로 바뀜

`max_lines_allowed`

- 파일을 800자 이하로 관리, AI 모델이 더 긴 document는 읽지 못할 수 있음

<br/>

## [pr_help]

`/help` 

PR내에서 사용가능한 모든 툴과 설명목록을 표시해주는 기본도구

### 설정옵션

`force_local_db` 

- 도움말 내용 조회 시 원격 위키(또는 온라인 문서) 대신 로컬 인덱스된 데이터베이스를 강제 사용할지 여부를 결정
- false : 기본값. `/help` 실행 시 온라인 문서를 먼저 조회하고, 로컬 DB는 보조 수단으로 활용
- true: 온라인 조회를 건너뛰고 로컬에 색인된 스니펫만 반환
- 인터넷 연결이 불안정 또는 사내 네트워크를 이용해야할때 등 로컬 인덱스를 활용해여할때 사용

`num_retrieved_snippets` 

- 로컬 DB(또는 위키 스니펫)에서 `/help` 실행 시 사용자에게 반환할 최대 스니펫 개수 설정
- 기본 5
- 너무 많은 스니펫이 반환되지 않도록 핵심적인 상위 n개의 결과만 보여줘 학습과 탐색을 돕는역할

### 사용 방법

```bash
/help --pr_help.force_local_db=true
```

<br/>

## [pr_help_docs]

Help Docs 도구가 **어떤 리포지토리**의 **어느 브랜치**에서, **어떤 경로**의 문서 파일을 찾아 **언제** 어떻게 사용할지 설정. 새 이슈가 열리거나 `/help-docs` 명령이 실행될 때, 관련 문서를 자동으로 검색해 스니펫을 제공

### 설정옵션

`repo_url`

- 문서를 검색할 대상 리포지토리 URL. 비워두면 PR/이슈가 열린 **현재 리포지토리** 사용
- 기본값 : “”

`repo_default_branch`

- 문서를 검색할 기본 브랜치 이름

`docs_path`

- 문서 파일이 위치한 디렉토리 경로
- docs → docs/하위의 .md, .mdx, .rst 파일 검색

`exclude_root_readme`

- true: 리포지토리의 최상위 (Readme.md등) 파일 검색에서 제외
- 기본값 : false

`supported_doc_exts`

- docs_path 하위에서 파일만 명시된 파일만 대상으로 지정. 외 파일은 무시
- default:  `".md", ".mdx", ".rst"`

`enable_help_text`

- 스니펫 위아래에 기본 안내 문구를 포함할지 여부
- default: false

### 동작방식

Github 워크플로우나 PR-Agent 이벤트 핸들러가 ‘새 이슈’가 열렸을때 pr_help_docs를 트리거 하도록 설정하면, 이슈 본문 키워드와 매칭되는 문서 스니펫을 자동으로 이슈 코멘트로 달아줄 수 있다.

### 사용방법

`/help-docs` ( 또는 `/help-docs query 키워드`) 명령어를 PR 코멘트에 입력하면, 설정을 기반으로 문서를 검색해 스니펫을 반환

```bash
/help-docs pagination
```

위 명령 수행 시 “pagination” 키워드와 매칭되는 문서 스니펫 3~5개(기본)와 함께, 사용법이 추가된 코멘트가 생성