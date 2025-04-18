<!-- 헤더 배너 -->
<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:36D1DC,100:5B86E5&height=200&section=header&text=Seungmin%20Lee's%20Week%201%20Report&fontSize=32&fontColor=ffffff"/>
</p>

---

# <p align="center">💬 PR Agent 멘토링 Week 1 보고서</p>

---

## <p align="center">😊 안녕하세요!</p>

<table align="center">
<tr>
<td align="center" width="160px">
  <img src="https://github.com/Akileox/pr-agent-mentoring/blob/week1/group3-seungmin-lee/weekly/week-1/group-3/images/seungmin.jpg?raw=true" width="300" alt="Seungmin's Photo" style="border-radius: 8px;"/>
</td>
<td>

- **이름**: 이승민 (Seungmin Lee)  
- **학교**: 고려대학교 컴퓨터학과  
- **학년**: 1학년  
- **관심 분야**:  
  - 인간처럼 사고하는 AI & Low-data learning (FISIL 등)
  - 뇌신호 및 생체 데이터 기반 AI  
- **현재 활동**:  
  - 2025-1 OSSCA PR_Agent   
  - 로컬 LLM 프로젝트 (Ollama + Docker)  
  - KUICS(KU 보안 동아리)  

</td>
</tr>
</table>

---

## 🏷️ 기술 스택

### ✅ 
<p align="center">
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
  <img src="https://img.shields.io/badge/Markdown-000000?style=for-the-badge&logo=markdown&logoColor=white"/>
  <img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/C-00599C?style=for-the-badge&logo=c&logoColor=white"/>
</p>

### 🛠️ 학습 중
<p align="center">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=next.js&logoColor=white"/>
  <img src="https://img.shields.io/badge/Assembly-000000?style=for-the-badge&logoColor=white"/>
</p>

---

## 1. 사전 학습 내용 정리
<aside>
🌙 Week1. Git Collaboration & LLM Basics (2025.04.14. ~ 04.20)
</aside>


## 🧠 1. Pre-learning Materials

### 1.1 Git & GitHub Flow

Git의 핵심: 데이터를 파일 시스템 스냅샷의 연속으로 취급, 매우 작은 크기

![시간 순으로 프로젝트의 스냅샷 저장](/images/gitflow.png?raw=true)

시간 순으로 프로젝트의 스냅샷 저장

- 0. CLI란 무엇일까? [처음에 어려웠던 점] “컴퓨터와 대화하기”
    - GUI 이전에 “**시간 순서**에 따라 명령을 내릴 수 있음”
    - POSIX 표준 (UNIX, LINUX, MacOS 제어) → Windows만 따로 놀아서 이것도 공부해야겠다고 생각함
    - [VIM 영상](https://youtu.be/cY0JxzENBJg)
- 1. Git 입문 - 생활코딩 [세부 명령어는 그때그때 구글링]
    - [Git의 효용](https://youtube.com/playlist?list=PLuHgQVnccGMCNJESahrVV-uYGMNYK_vMf)
    - [Git 명령어](https://youtube.com/playlist?list=PLuHgQVnccGMATJK16UJ9Fjay0ozrSZKiI)

        → [Staging area & Commit 설명](https://youtu.be/tP_Q_o8KOUA?list=PLuHgQVnccGMATJK16UJ9Fjay0ozrSZKiI&t=270)

        **status**(상태 확인, Untracked) / **git commit -m “message”**  
        → status를 다시 입력하면 nothing to commit | **log**: 버전 제작 관련 제공 (-p: 패치 내역 확인 가능)

![git commit 구조 설명 이미지](/images/gitcommit.png?raw=true)

- working tree: 수정 내역  
- staging area: commit을 위해 수정 파일 **올려놓는 곳**  
- Repository: 수정사항을 반영한 버전이 저장되는 곳  
- Git은 모든 파일을 자동으로 트래킹하지 않는다.  
  → add하지 않은 경우 내버려둠 (**백업이 되지 않음**) → 파일의 **Grouping** 가능  
- diff: 스스로 version 간 차이점을 알려줌  

#### [시점 변환] 버전을 이전으로 돌아가려면?

1. `git checkout <commit ID>`  
   → HEAD를 이전 버전으로 가리킴

2. `git reset --hard <commit ID>`  
   → 그 커밋 **자체로 되돌아감** (수정 내역 포함 완전 초기화)  
   → ⚠ 공유된 버전은 절대 reset 금지

3. `git revert <commit ID>`  
   → 지정된 커밋을 취소하되, 새로운 커밋으로 생성  
   → 과거 버전을 없애지 않고 되돌리는 개념 (파이썬 슬라이싱과 유사)  
   → 여러 단계 revert는 충돌 발생 가능 (v4 → v2: 오류)

#### Git 주요 명령어 요약

```bash
git init               # 새로운 Git 저장소 초기화
git clone <url>        # 원격 저장소를 로컬로 복제
git add .              # 변경된 모든 파일 스테이징
git commit -m "msg"    # 커밋 메시지와 함께 저장
git commit -am "msg"   # add + commit (트래킹 파일만)
git push               # 원격 저장소에 푸시
git pull               # 원격에서 변경사항 가져오기
git branch             # 브랜치 목록
git checkout <branch>  # 브랜치 전환
git merge <branch>     # 병합
git rebase <branch>    # 리베이스
git reset <commit>     # 커밋 상태로 되돌림
```

---

#### 3. GitHub Flow 이해하기

- Fork → Clone → Branch 생성 → Commit → Push → Pull Request → 코드 리뷰 → Merge
- [GitHub Flow 소개 영상](https://youtu.be/EV3FZ3cWBp8)
- [생활코딩 GitHub Flow 시리즈](https://youtube.com/playlist?list=PLuHgQVnccGMDWjb0TWItMCfDWDs8Y3Oo7)

---

### 1.2 Large Language Models (LLMs)

- [LLM 분석 영상](https://www.youtube.com/watch?v=6PTCwRRUHjE)
- 시청 및 작성 중입니다.

---

### 1.3 Google Gemini API

→ Project/ai-test 아래에서 호출 테스트 진행 완료

- [Google AI Studio](https://aistudio.google.com/welcome)

```python
from dotenv import load_dotenv
from google import genai
import os

load_dotenv()

client = genai.Client(api_key=os.environ["GEMINI_API_KEY"])

response = client.models.generate_content(
    model="gemini-2.0-flash", 
    contents=["Explain how AI works in a few words"]
)
print(response.text)

# 출력 예시: AI learns from data to make predictions or decisions.
```

📎 [공식 문서](https://ai.google.dev/gemini-api/docs/text-generation?hl=ko)

---

### 1.4 PR Agent

- [GitHub Repository](https://github.com/qodo-ai/pr-agent)
- [공식 문서](https://qodo-merge-docs.qodo.ai/)
- PR을 읽고 요약 및 리뷰 코멘트 자동 생성
- GPT 기반의 코드 협업 도구로 오픈소스 프로젝트에 적용 가능

---

### 1.5 Python

#### 클래스 개념

```python
class FourCal:
    def setdata(self, first, second):
        self.first = first
        self.second = second
    def add(self):
        return self.first + self.second
    def mul(self):
        return self.first * self.second
    def sub(self):
        return self.first - self.second
    def div(self):
        return self.first / self.second
```

![파이썬 클래스 구조 설명](/images/pythonclass.png)

- 생성자 `__init__`, self, 메서드 오버라이딩, 상속 구조 학습
- 클래스 변수와 객체 변수의 차이
- `super()`로 부모 클래스 초기화 가능


#### 예외 처리 패턴

- try - except - else - finally
- 예외 강제 발생 `raise`
- `pass` 문으로 무시 가능
- 파일 입출력 마무리에 `finally` 사용


#### 정규표현식

- 정규식으로 텍스트 필터링 및 유효성 검사 가능  
📘 [위키독스 정규표현식 문서](https://wikidocs.net/4308)


#### GitHub 꾸미기

- [GitHub 프로필 꾸미기 영상](https://youtu.be/w9DfC2BHGPA)


---

## 2. 실습 과제 수행 내역

- [x] 저장소 Fork 및 로컬 Clone (WSL 환경에서 진행)
  - `git clone`으로 내 GitHub 계정에 fork한 저장소를 복제하였으며, 작업 디렉토리는 `~/project/pr-agent-mentoring` 아래에 생성
- [x] 가상환경(venv) 설정
  - `python3 -m venv .venv`로 Python 가상환경 구성 후, `source .venv/bin/activate` 명령어로 활성화
- [x] 브랜치 생성 및 이동
  - `git checkout -b week1/group3-seungmin-lee`로 작업용 브랜치 분리
  - 브랜치 명명 규칙은 저장소 규칙과 개인 식별성을 고려해 구성함
- [x] 보고서 파일 작성
  - `weekly/week-1/group-3/seungmin-lee.md` 경로에 본 파일을 생성 및 기록
- [x] 이미지 업로드 및 마크다운 내 삽입 실습
  - 로컬 이미지 파일(`seungmin.jpg`)을 프로젝트 내 `images/` 디렉토리에 저장한 뒤 마크다운 경로를 통해 불러옴
- [x] GitHub Flow 기반의 협업 이해
  - `commit`, `push`, `pull request` 등의 절차를 실습하며, PR 기반 협업 흐름을 경험 중

> 🔹 **질문**: PR 과정에서 코드 변경 이외에도 문서 업데이트(PR Agent 보고서 등)에 대한 자동 리뷰는 어떻게 트리거되는지, 세부 조건이 궁금합니다.

---

## 3. 어려웠던 점 및 질문

- **브랜치 전략**의 개념은 직관적이었지만, 실제 협업 시 브랜치 간 충돌 처리나 `rebase`의 적용 시점이 아직 명확하지 않음
- WSL을 활용한 Git 작업은 환경 구성에는 편리했으나, GitHub와의 연결(SSH 키, credential 저장 등)에 대해 실습 중 약간의 혼란이 있었음
- PR Agent가 자동으로 comment를 생성하는 로직(예: 어떤 기준으로 summary를 작성하는지)에 대한 원리를 아직 완전히 이해하지 못함
- `add`, `commit`, `push` 명령어는 익숙해졌으나, `reset`, `revert`, `cherry-pick` 등은 실습 전 개념 수준에서 정리 중

> 🔹 **추가 궁금점**: PR Agent가 리뷰 comment를 생성하지 않는 경우는 어떤 케이스인지? 예: 문서 변경만 있는 경우에는 작동하지 않는지?

---

## 4. 회고

이번 주는 **기초 Git 흐름을 직접 체득하고**, PR 기반의 협업 방식이 단순한 명령어 암기 그 이상임을 깨닫는 시간이었다.  
특히 `add → commit → push → PR → review → merge`라는 일련의 흐름이 단순 반복이 아니라  
의사소통과 코드 품질을 위한 **중간 장치**라는 점에서 매우 인상 깊었다.

또한, PR Agent를 통해 자동화된 리뷰 시스템을 경험하며  
AI가 **'개발자의 협업 도우미'로 실질적으로 작동할 수 있음**을 확인한 것도 흥미로웠다.  
아직 초보 개발자로서 이해가 부족한 부분이 많지만,  
단순히 따라 하기보다는 스스로 질문하고 학습 경로를 만들며 실력을 쌓아가는 것이 이번 활동의 가장 큰 수확이었다고 생각한다.



> *작성자: Seungmin Lee | Group 3 | OSSCA PR Agent Mentoring 2025*
