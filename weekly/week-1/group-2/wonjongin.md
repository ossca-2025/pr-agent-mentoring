# GitHub Flow 요약 정리

> [GitHub Docs - GitHub Flow](https://docs.github.com/en/get-started/using-github/github-flow)를 정리

> 해당 글은 velog에 정리한 내용을 한번 더 정리한 글로 구체적인 내용은 [UNUMUNU-GitHub Flow 공식 문서 정리](https://velog.io/@wonggamggik/GitHub-Flow-%EA%B3%B5%EC%8B%9D-%EB%AC%B8%EC%84%9C-%EC%A0%95%EB%A6%AC)를 참조

---

## 1. 브랜치 생성

브랜치는 작업 단위를 나누는 단위로 GitHub에서는 아래와 같은 방법으로 브랜치를 생성

- 방법 1: 브랜치 목록 페이지에서 생성

  - Repository → `View all branches` → `New branch`
  - 기준 브랜치 선택 후 이름 입력 → `Create branch`

- 방법 2: 드롭다운에서 바로 생성
  - 브랜치 드롭다운 클릭 → 새 이름 입력
  - `Create branch from [기준 브랜치]` 클릭

방법 3: 이슈에서 생성

- 이슈 상세 페이지 → 우측 `Create a branch` 클릭

## 2. 브랜치 삭제

- Settings → `Automatically delete head branches` 체크로 병합 후 자동 삭제 가능
- 수동 삭제는 브랜치 목록에서 휴지통 아이콘 클릭

## 3. 커밋

- 의미 있는 단위로 커밋할 것
- 커밋 메시지는 명확하게 작성
- 한 커밋에 한 가지 변경사항만 담기

## 4. Pull Request (PR)

- 작업 브랜치에서 `main` 등 기준 브랜치로 PR 생성
- `base`: 변경 적용 대상 / `compare`: 작업 브랜치
- 변경 내용, 이슈 번호, 이미지 등을 상세히 작성
- 권한이 없으면 fork 이후 작업 후 PR

## 5. 코드 리뷰 및 응답

- PR에 전체 또는 특정 줄에 댓글 작성 가능

## 6. Merge

- 리뷰 승인 후 Merge
- 충돌 시 GitHub에서 안내 제공
- Merge 후 브랜치 삭제

</br>
</br>
</br>

# Git 브랜치 전략 요약

> 해당 공부는 위 문서의 github-flow 문서에 대한 이해를 위한 공부에서 시작
> 아래의 글은 velog의 글을 한번 더 정리한 것이기에 구체적인 사항은 [UNUMUNU-Git 브랜치 전략](https://velog.io/@wonggamggik/Git-%EB%B8%8C%EB%9E%9C%EC%B9%98-%EC%A0%84%EB%9E%B5)을 참조

- 브랜치는 Git에서 코드의 독립적인 작업 흐름으로 원활한 협업을 위해 브랜치 전략 필요
- 대표적인 브랜치 전략으로는 `Git Flow`와 `GitHub Flow`가 있으며 이를 정리 및 비교
- 해당 글에서는 위 프로젝트가 지향하는 githib-flow에 대해서만 설명

## 브랜치 전략이란?

- 여러 개발자가 같은 저장소를 사용할 때 충돌을 피하고 작업을 분리하기 위한 규칙
- 작업 흐름을 표준화하여 협업을 효율화

## **GITHUB-FLOW 전략**

![](https://velog.velcdn.com/images/wonggamggik/post/50c8a17c-9392-4e91-bdbd-f0bbc71a52f7/image.png)

- git flow 전략은 좋은 방식이지만 단점은 복잡하다는 문제
- 흐름이 단순하고 규칙도 단순하게 만들었고 master 브랜치에 대한 규칙을 정립한다면 나머지는 관여 X

### **Github-Flow 흐름**

1. 브랜치 생성

![](https://velog.velcdn.com/images/wonggamggik/post/1188af6e-19f7-41ea-bd98-6d4232b2a425/image.png)

- master 브랜치는 기존에 두고 개발, 버그 수정 등 어떤 이유든 코드가 수정된다면 새로운 브랜치를 생성하여 작성

2. 개발, 커밋, 푸쉬

![](https://velog.velcdn.com/images/wonggamggik/post/d889d148-3dc9-4f3a-84b4-55c4526bbba1/image.png)

- 개발을 진행하면서 커밋 생성
- 원격 브랜치에 수시로 push

3. PR(Pull Request) 생성

![](https://velog.velcdn.com/images/wonggamggik/post/43b7f1ee-029c-4a48-abe5-82bfd005ed2e/image.png)

- 피드백, 도움이 필요할 때, merge 준비가 완료되면 pull request를 생성
- pull request는 코드 리뷰를 도와주는 시스템으로 자신의 코드를 공유하고 리뷰

4. 리뷰 & 토의

![](https://velog.velcdn.com/images/wonggamggik/post/58fd7d97-d678-43e6-8c73-8987fa826fdb/image.png)

- 해당 단계에서 리뷰와 토의 수행

5. 테스트

![](https://velog.velcdn.com/images/wonggamggik/post/99c7cc54-0cd3-4cda-9c71-72f87f99c25f/image.png)

- 리뷰와 토의가 끝나면 라이브 서버 혹은 테스트 환경에 배포
- 만약 문제가 발생하면 master 브랜치의 내용을 다시 배포하여 초기화

6. 최종 Merge

![](https://velog.velcdn.com/images/wonggamggik/post/b09f7f3c-22ad-4d49-933b-1dff1b5789a8/image.png)

- 라이브 서버에 배포했음에도 문제가 없다면 그대로 master 브랜치에 푸시하고 배포
  </br></br>
  > gitgub-flow는 하나의 새로운 브랜치를 생성하고 그 안에서 개발, 버그 수정 그 모든 것을 수행

## 둘 중 무엇을 골라야 하는가?

- 긴 기간에 걸쳐 개발을 하고 주기적으로 배포, QA, 테스트, hotfix 등을 수행할 수 있는 팀이라면 git-flow를 사용
- 수시로 릴리즈 되어야 할 필요가 있는 서비스를 지속적으로 테스트하고 배포한다면 github-flow를 사용

</br>
</br>
</br>

# Git 학습 내용 요약

> 깃 기초를 쌓는데 도움을 주는 사이트인 [Learn Git Branching](https://learngitbranching.js.org/?locale=ko)를 공부

> 구체적인 설명과 답안은 [UNUMUNU-Learn Git Branching - 메인 과정 - 풀이 및 정리](https://velog.io/@wonggamggik/Learn-Git-Branching-%EB%A9%94%EC%9D%B8-%EA%B3%BC%EC%A0%95-%ED%92%80%EC%9D%B4-%EB%B0%8F-%EC%A0%95%EB%A6%AC)을 참조

- git commit의 개념과 사용법
- 브랜치 생성, 전환, 병합 방법
- rebase 기본 개념 및 사용법
- HEAD 분리와 의미
- ^, ~를 활용한 상대 참조 방식
- reset과 revert의 차이와 활용
- cherry-pick을 이용한 특정 커밋 선택 적용
- 인터랙티브 rebase로 커밋 순서 변경, 삭제, 스쿼시
- commit --amend로 최근 커밋 수정
- git tag와 describe 명령어 학습
