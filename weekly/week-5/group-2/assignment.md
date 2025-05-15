## 과제 목표

PR-Agent 코드베이스 내 테스트 커버리지가 낮거나 비어 있는 테스트 파일에 대해,   
기능 단위로 테스트를 보강하고 모킹, 경계 케이스, 예외 처리 등을 중심으로 테스트 신뢰도를 높입니다.   
각 테스트 모듈별 책임 범위를 이해하고, 해당 기능을 실제 사용하는 사용자의 관점에서 다양한 테스트 시나리오를 구성합니다.


## 과제 범위

- `unittest/test_try_fix_yaml.py`
    - 송지웅, 이상현
- `unittest/fetching_sub_issues.py`
    - 김상은, 이예찬
- `unittest/test_fetching_handler.py`
    - 원종인

## 참여자

- 2조 팀원 전원

## 2주 플랜

- [x] 기여 대상 테스트 모듈 분배 및 범위 확정
- [x] 사전 코드 분석
- [x] 조별 과제 PR 업로드, 멘토님 피드백 듣기
- [ ] 멘토 피드백 반영
- [ ] 테스트 코드 작업 및 보완
- [ ] 이슈 생성
- [ ] 최종 PR 제출 및 리뷰 요청

### 5주차
- 할당된 테스트 항목에 대한 코드 작성

### 6주차
- 멘토님 컨펌 시 PR 유지 후 제출
- 메인테이너 승인 대기

## 기여할 부분
   - `test_try_fix_yaml.py`
     - Fix Yaml : 여섯번째 Fallback까지 테스트 할 수 있는 코드 작성
   - `test_fetching_sub_issues.py`
     - 모킹으로 API 호출 막기 (세번째 테스트 코드)
     - Gitlab 같은 Github외 Git 호스팅 플랫폼 기준으로도 테스트 코드 작성
   - `test_language_handler.py`
     - `def test_edge_case_languages_with_no_extensions(self):`    
     위 함수에 설정을 추가하여 다루는 방법에 대한 추가 처리
     - 실제로 other에 파일이 들어가는 테스트가 없어서 구현

