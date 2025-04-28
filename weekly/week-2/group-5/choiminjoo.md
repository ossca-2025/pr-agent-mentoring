5조 최민주 2주차 과제입니다.

## RAG

### Retrieval-Augmented Generation (검색 증강 생성)

- LLM + 외부 데이터 검색 기능
- LLM 만 사용할 경우와 비교해 더 정확하고 최신의 답변을 제공
- 실시간 정보나 특정 도메인에 대한 전문적인 질문에 답변 가능

### RAG 의 정의

검색 (Retrieval)
- 사용자가 입력한 질문과 관련된 정보를 외부 데이터베이스나 인터넷에서 검색
증강 (Augmentation)
- 검색한 정보를 정리하고 보강
생성 (Generation)
- LLM을 이용해 증강된 정보를 바탕으로 자연스러운 답변 생성

### RAG 의 동작 순서 (LangChain 활용)

1. Vector DB에 데이터 저장
2. 검색기 설정
3. 프롬프트 설정
4. LLM 설정
5. 2~4 를 연결하는 Chain 생성
6. Chain 을 실행해 질문에 대한 답변 얻기

### RAGAS : 품질 평가

### **Dataset**

데이터는 총 4가지를 준비합니다.

- `question` : 사용자의 질문
- `ground truth` : 질문에 맞는 정답 (= 모범 답안)
- `answer` : LLM이 생성한 답변
- `context` : LLM이 답변을 생성하기 위해 참고한 정보 (=context)

### **평가 지표**

보편적으로 다음 4개의 평가 지표를 이용합니다.

- `Faithfulness` : 주어진 context에 대한 생성된 답변의 일관성에 대한 값
    
    (context - answer)
    
- `Context Recall` : ground_truth의 문장 중 contex로부터 추론할 수 있는 문장의 비율을 측정한 값
    
    (ground truth - context)
    
- `Context Precision` : contexts에 존재하는 ground_truth와 관련된 항목을 높은 순위로 잘 검색해 왔는지의 여부를 평가하는 지표
    
    (grund truth - context 및 참조 순위와 지표)
    
- `Answer Relevance` : 생성된 답변이 주어진 질문과 얼마나 관련성이 있는지에 대한 값
    
    (answer - question)
    

---

## MCP

### Model Context Protocol?

MCP는 애플리케이션이 LLM에 컨텍스트를 제공하는 방식을 표준화하는 개방형 프로토콜입니다. MCP는 AI 애플리케이션을 위한 USB-C 포트와 같습니다. USB-C가 다양한 주변 기기 및 액세서리에 기기를 연결하는 표준화된 방식을 제공하는 것처럼, MCP는 AI 모델을 다양한 데이터 소스 및 도구에 연결하는 표준화된 방식을 제공합니다.

### **왜 MCP인가?**

MCP는 LLM을 기반으로 에이전트와 복잡한 워크플로를 구축하는 데 도움을 줍니다. LLM은 데이터 및 도구와 통합해야 하는 경우가 많으며, MCP는 다음과 같은 기능을 제공합니다.

- LLM이 직접 연결할 수 있는 미리 구축된 통합 목록이 점점 늘어나고 있습니다.
- LLM 공급자와 공급업체 간 전환의 유연성
- 인프라 내에서 데이터를 보호하기 위한 모범 사례

### **일반 아키텍처**

MCP는 본질적으로 호스트 애플리케이션이 여러 서버에 연결할 수 있는 클라이언트-서버 아키텍처를 따릅니다.

![image.png](attachment:45bb88e4-6cd9-427a-821e-4722e15d4a86:image.png)

- **MCP 호스트**
    
    : MCP를 통해 데이터에 액세스하려는 Claude Desktop, IDE 또는 AI 도구와 같은 프로그램
    
- **MCP 클라이언트**
    
    : 서버와 1:1 연결을 유지하는 프로토콜 클라이언트
    
- **MCP 서버**
    
    : 표준화된 모델 컨텍스트 프로토콜을 통해 각각 특정 기능을 노출하는 경량 프로그램
    
- **로컬 데이터 소스**
    
    : MCP 서버가 안전하게 액세스할 수 있는 컴퓨터의 파일, 데이터베이스 및 서비스
    
- **원격 서비스**
    
    : MCP 서버가 연결할 수 있는 인터넷(예: API를 통해)을 통해 사용 가능한 외부 시스템
    

https://modelcontextprotocol.io/introduction

### 예제 : [**FastAPI-MCP, FastAPI로 만든 API 서버를 MCP 도구로 자동 노출하는 도구**](https://discuss.pytorch.kr/t/fastapi-mcp-fastapi-api-mcp/6629)

https://discuss.pytorch.kr/t/fastapi-mcp-fastapi-api-mcp/6629

---

## A2A

A2A(Agent-to-Agent)란 **자율적인 에이전트(Agent) 간의 직접적인 상호작용 및 협업 구조**를 의미하며, 이는 멀티에이전트시스템(MAS, Multi-Agent Systems)이나 LLM 기반 AI 에이전트 아키텍처에서 점차 중요한 컴포넌트로 부각되고 있다. A2A의 핵심은 단일 에이전트가 독립적으로 모든 태스크를 수행하는 것이 아니라, **다수의 이질적이거나 동질적인 에이전트가 역할을 분담하거나 지식을 교환하면서 문제를 분산 처리하는 것**이다.

### ✅ A2A의 정의 및 구성 요소

Agent-to-Agent 시스템은 다음과 같은 요소들로 구성된다:

1. **에이전트(Agent):**
    
    각 에이전트는 독립적인 목표(goal), 상태(state), 행동(action) 집합을 가지며, 종종 추론 엔진과 메모리 모듈, 행동 계획기(Planner)를 포함한다.
    
2. **A2A 통신 프로토콜 (A2A Communication Protocol):**
    
    에이전트 간 메시지를 주고받기 위한 통신 구조. 이는 보통 자연어, 구조화된 DSL(Domain-Specific Language), 혹은 XML/JSON 기반 프로토콜로 구현된다. 일부 시스템은 FIPA-ACL (Foundation for Intelligent Physical Agents - Agent Communication Language) 같은 표준을 따른다.
    
3. **상태 공유 및 컨텍스트 유지:**
    
    개별 에이전트의 로컬 상태(local state) 외에도, 공유 메모리(shared memory), 글로벌 context store, 혹은 Pub-Sub 모델을 통해 상태 동기화가 일어날 수 있다.
    
4. **Task Delegation & Execution:**
    
    하나의 에이전트가 서브태스크를 다른 에이전트에게 위임(delegate)하고, 해당 결과를 집계하는 형태. 이 때 task specification과 result contract 정의가 필요하다.
    

---

### ✅ A2A의 주요 패러다임

A2A는 그 아키텍처적 특성에 따라 다음과 같은 패러다임으로 분류할 수 있다:

| 패러다임 | 설명 |
| --- | --- |
| **협력적 협상(Collaborative Negotiation)** | 여러 에이전트가 공동 목표를 향해 상호협상하며 역할 분담 |
| **계약 기반 작업 분배(Contract Net Protocol)** | 마스터 에이전트가 태스크를 브로드캐스트하고, 워커들이 입찰(bid)을 통해 수행 권한을 획득 |
| **지식 전파(Knowledge Propagation)** | 하나의 에이전트가 획득한 지식 또는 결과를 다른 에이전트에게 전파하여 문제 해결 성능 향상 |
| **계층적 위임(Hierarchical Delegation)** | 상위 관리자(agent manager)가 서브에이전트를 트리구조로 조직하여 복잡한 태스크를 해결 |

### ✅ A2A in LLM Agent Architectures

최근 LLM 기반의 Agent 시스템에서도 A2A는 핵심적인 요소로 등장하고 있다. 대표적인 구현 및 컨셉은 다음과 같다:

- **AutoGen (Microsoft Research):**
    
    `AssistantAgent`, `UserProxyAgent`, `GroupChatManager` 등이 서로 메시지를 주고받으며 task를 협력적으로 해결함. 에이전트 간 turn-taking과 메시지 persistence가 보장됨.
    
- **CrewAI:**
    
    다양한 역할(role)을 가진 agent들이 pipeline 형태로 동작하며, Planner나 CrewManager가 task 분배를 orchestrate함.
    
- **LangGraph + LangChain:**
    
    LangGraph는 각 Agent를 Node로 간주하여 DAG 또는 Graph 구조로 연결하고, 상태 기반 전이(State transition) 및 A2A 메시징을 가능하게 함.
    

### ✅ A2A의 기술적 도전 과제

A2A 시스템을 구현할 때 다음과 같은 기술적 문제가 자주 대두된다:

1. **상태 관리의 일관성 (State Consistency):**
    
    비동기적 Agent 간 통신 중 상태 충돌을 방지하는 동기화 메커니즘 필요.
    
2. **메시지 해석 능력 (Message Understanding):**
    
    특히 자연어 기반 A2A에서는 에이전트 간 메시지를 정확히 해석하고 context-aware하게 반응할 수 있는 프롬프트 엔지니어링 및 메시지 포맷 표준화가 중요함.
    
3. **작업 책임성 (Accountability & Traceability):**
    
    어느 에이전트가 어떤 작업을 수행했는지에 대한 추적 가능성 확보를 위한 로그 기록과 monitoring 시스템이 요구됨.
    
4. **상호 운용성 (Interoperability):**
    
    서로 다른 Agent framework 혹은 Agent 모델 간의 호환성 문제를 해결하기 위한 통합 레이어 또는 표준화된 API 필요.
    

### ✅ A2A 시스템의 응용 분야

| 분야 | 예시 |
| --- | --- |
| **지식 검색 기반 RAG 시스템** | Retriever Agent ↔ Rewriting Agent ↔ QA Agent |
| **멀티모달 처리** | Image Captioner ↔ Language Agent ↔ Action Planner |
| **자동화된 SW 개발** | Issue Parser ↔ Code Generator ↔ Code Reviewer |
| **시뮬레이션 기반 학습환경** | AI NPC 간 상호작용 (e.g., Voyager, ChatSim) |
| **협상 및 협업 시뮬레이션** | Virtual Negotiator ↔ Pricing Agent ↔ Constraint Resolver |

---

### 🔍 결론

Agent-to-Agent(A2A)는 단순한 태스크 수행을 넘어, **복잡한 문제 해결을 위한 집단지성의 인공적 구현 방식**이라 할 수 있다. LLM 기반 Agent Framework의 진화와 함께, A2A는 단순한 RPC(Remote Procedure Call)를 넘어서 **자율성과 사회성을 갖춘 디지털 액터(digital actors)** 간의 상호작용을 가능하게 하며, 궁극적으로는 인공 일반 지능(AGI)으로 가는 중간 단계의 중요한 기제로 작동할 것이다.