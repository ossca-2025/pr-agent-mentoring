# [Sequence Diagram ]

## 현재 문제점

`함수 호출 흐름 시각화 X`

- 기존 PR 요약은 텍스트 중심이라, 복잡한 함수 간 상호작용이나 이벤트 흐름을 한눈에 파악하기 어려움 

`코드 리뷰 생상선 저하` 

- 리뷰어가 코드 읽기와 동시에 호출 관계를 추적해야하므로 피로도가 높고, 놓치는 경로가 발생할 수 있음

`다계층, 이벤트 기반 아키텍처 대응 한계`

- 마이크로서비스나 이벤트 드리븐 구조처럼 호출 스택이 여러 레이어를 거칠 때 텍스트만으로는 전체 흐름 이해가 불가능

## 구현방법

### `1. 구성 옵션 반영`

- configuration.toml의 `diagram = true` 옵션을 읽어 시퀀스 다이어그램 생성 여부를 제어
- `--pr_description.add_diagram` 플래그로 제어

### `2. AST 기반 호출 관계 추출`
- Python 표준 ast 모듈을 써서 저장소 내 .py 파일을 순회하며 
  - FunctionDef 노드 내 Call 노드를 탐색
  - 변경된 함수만(GitPatchProcessor 결과) 또는 전체 함수(필터 비사용 시)에서 호출 관계(caller→callee)를 수집

### `3. 시퀀스 다이어그램 생성`
- algo/sequence_diagram_generator.py의 SequenceDiagramGenerator 클래스에서 추출된 호출 쌍을 mermaid-builder 라이브러리로 입력하여 
- .mmd 텍스트 파일과 .svg 이미지 파일을 동시에 출력

### `4. PR 설명 자동 삽입`
/describe 의 옵션으로 반영 → PRDescription class의 run()에서 다이어그램 반영


## 기대효과
#### **가시성 향상**
- 복잡한 호출 흐름을 그림으로 확인 → 리뷰 초점이 로직 검증으로 전환
    
#### **리뷰 속도·정확도 개선**
- 호출 경로 자동 맵핑으로 경로 누락 없이 전체 로직 이해
    
#### **온보딩·지식 공유**
- 신규 팀원이 PR을 볼 때 시스템 구조를 빠르게 파악 가능
    
#### **확장성 기반 마련** 
- Mermaid 외 PlantUML 등 다른 다이어그램 형식 추가도 쉽게 확장 가능


## 참조
####  Mermaid Sequence Diagram
Mermaid 시퀀스 다이어그램은 간단한 텍스트 문법으로 시스템 내 객체(또는 함수) 간의 호출 흐름과 상호작용을 시각화해 주는 다이어그램. <br>
GitHub, GitLab, Notion, Obsidian 같은 여러 도구에서 Markdown 안에 바로 렌더링할 수 있어, 코드 리뷰나 문서에 바로 삽입가능
```markdown
sequenceDiagram
    participant Alice
    participant Bob
    Alice->>John: Hello John, how are you?
    loop HealthCheck
        John->>John: Fight against hypochondria
    end
    Note right of John: Rational thoughts <br/>prevail!
    John-->>Alice: Great!
    John->>Bob: How about you?
    Bob-->>John: Jolly good!
```

#### PlantUML
텍스트 기반 DSL(Domain-Specific Language)을 사용해 UML 및 다양한 소프트웨어 다이어그램을 생성할 수 있는 오픈소스 도구입니다 
자바(Java)로 구현되어 있으며, 내부적으로 Graphviz를 활용해 다이어그램의 레이아웃을 자동으로 처리합니다 
간단한 문법으로 시퀀스, 클래스, 활동, 컴포넌트, 배포, 상태 다이어그램뿐 아니라 C4, BPMN, ERD, Gantt 차트, 마인드맵 등 다양한 형식도 지원
```markdown
+--------------------------------------+
|         TEDx Talks Recommendation    |
|                System                |
+--------------------------------------+
| +----------------------------------+ |
| |          Visitor                 | |
| +----------------------------------+ |
| | + View Recommended Talks         | |
| | + Search Talks                   | |
| +----------------------------------+ |
+--------------------------------------+
                   |
                   |
                   V
+--------------------------------------+
|         Authenticated User           |
+--------------------------------------+
| +----------------------------------+ |
| |          User                    | |
| +----------------------------------+ |
| | + View Recommended Talks         | |
| | + Search Talks                   | |
| | + Save Favorite Talks            | |
| +----------------------------------+ |
+--------------------------------------+
                   |
                   |
                   V
+--------------------------------------+
|         Admin                        |
+--------------------------------------+
| +----------------------------------+ |
| |          Admin                   | |
| +----------------------------------+ |
| | + CRUD Talks                     | |
| | + Manage Users                   | |
| +----------------------------------+ |
+--------------------------------------+
```