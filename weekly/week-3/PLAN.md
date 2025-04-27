## 🗓️ 3주차 계획: PR Agent 개발 환경 설정 및 주요 태스크 코드 분석

### 🎯 학습 목표

- PR Agent의 아키텍처와 핵심 기능을 이해한다.
- PR Agent의 주요 도구의 동작 방식을 코드레벨에서 분석하고 이해한다.
  - 분석: code, prompt, configuration
  - 도구: /describe, /review, /improve 

### 🔍 주요 활동

- pr agent 로컬 개발 환경 설정
  - llm은 gemini api 연동
  - https://github.com/qodo-ai/pr-agent/blob/main/CONTRIBUTING.md
  - https://qodo-merge-docs.qodo.ai/installation/locally/#run-from-source
- PR Agent 기본 설정 및 코드 분석
  - config 섹션의 옵션들: https://github.com/ossca-2025/pr-agent/blob/main/pr_agent/settings/configuration.toml
  - https://qodo-merge-docs.qodo.ai/usage-guide/
- PR Agent 주요 Tool 코드, 프롬프트 및 설정(configuration) 분석
  - https://qodo-merge-docs.qodo.ai/tools/
  - /describe, /review, /improve 

### 📚 학습, 실습 주제



PR Agent 기본 설정 및 Tool별 세부 설정에 대해 이해하고, 직접 사용해보고, 정리하기  
- 조별로 [pr agent repo](https://github.com/ossca-2025/pr-agent) fork
- 샘플 PRs 생성
- 기본 설정(config) 변경해가며 테스트해보기
- /describe, /review, /improve 등 다양한 옵션으로 사용해보기
- 결과를 마크다운 문서로 정리
- 멘토링용 저장소에 PR 형태로 제출


### 🛠️ 조별 과제
과제에 포함되어야 할 내용  
- 기본 설정, 도구별 설정 설명
- 기본 설정, 도구별 코드 및 프롬프트 분석 요약 정리

#### 과제1
- 기본 설정(usage 및 기본 설정) 조사, 분석, 사용, 정리

#### 과제2
- describe(설정 및 코드) 조사, 분석, 사용, 정리

#### 과제3
- review(설정 및 코드) 조사, 분석, 사용, 정리

#### 과제4
- improve(설정 및 코드) 조사, 분석, 사용, 정리

#### 과제5
- describe, review, improve 프롬프트 완벽 분석.