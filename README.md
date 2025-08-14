
## 🗂️ 팀 문서 & 자료 링크

<p align="center">
  <a href="https://www.notion.so/229656e282d88069a69fe9a18ca1cc58?v=229656e282d88040a866000cc977e780">📒 팀 노션</a> &nbsp;|&nbsp;
  <a href="https://www.notion.so/230656e282d8801991e7fda7326561a8">📚 개발 위키</a> &nbsp;|&nbsp;
  <a href="https://www.notion.so/Ground-Rule-229656e282d88055a6bccf41c7cf1064">📏 그라운드 룰</a> &nbsp;|&nbsp;
<!--   <a href="https://kkori.site/">🚀 데모 페이지</a> -->
</p>

<br/>
<br/>

## 🧑‍💻 역할 및 기여

| 담당자 | 주요 작업                                                                                                                                                                                       |
|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **강진규** | - CI/CD 파이프라인 구축 및 배포 자동화 설정<br>- 실시간 채팅 기능 WebSocket 구현<br>- WebRTC 시그널링 서버 구현 및 P2P 연결 관리<br>- 꼬리질문 생성 로직 및 STT API 연동 구현<br>- 음성 처리 및 텍스트 변환 기능 개발<br>- 면접 세션 관리 및 WebSocket 기반 면접 로직 개발 |
| **배수한** | - 면접 세션 클래스 설계 및 상태 관리 로직 개발 및 구현<br>- WebSocket 기반 면접 비즈니스 로직 전반 개발<br>- 연결 끊김 시 재연결 로직 및 상태 복구 구현<br>- 면접 결과 저장 및 조회 API 구현<br>- 면접방 관리 및 사용자 권한 처리 로직 개발                                 |
| **유윤지** | **질문 세트**<br>- 질문 세트 전체 로직 구현<br>- 버전 관리 시스템 구현<br>- 권한 기반 접근 제어<br>- 데이터 조회 로직 최적화<br>- 페이징 및 필터링 시스템<br>**인증 및 사용자 관리**<br>- 카카오 OAuth2 로그인 구현<br>- 게스트 로그인 시스템 구현<br>- JWT 토큰 기반 인증 시스템<br>- 사용자 권한 관리 및 어노테이션<br>- 보안 설정 및 CORS 처리<br>- 쿠키 기반 토큰 관리 |

<br/>
<br/>

## 👋 프로젝트 소개

### 🎤 *"AI 기반 실시간 면접 연습 플랫폼"*

**Kkori**는 사용자가 **실시간 WebSocket 통신을 통해 면접을 연습**하고,  
**AI 기반 음성 인식과 꼬리질문 생성**으로 실제 면접과 같은 경험을 제공하는 **면접 연습 서비스**입니다.

- 사용자는 **혼자 연습(SOLO)** 또는 **둘이서 연습(PAIR)** 모드를 선택할 수 있어요.
- **실시간 음성 인식(STT)** 으로 답변을 텍스트로 변환하고 **AI가 꼬리질문을 자동 생성**합니다.
- **WebRTC 기반 영상/음성 통화**와 **실시간 채팅**으로 생생한 면접 환경을 제공해요.

<br/>
<br/>

## 🧩 **고민과 해결 방안**

### 🔄 **WebSocket 재연결 전략으로 안정적인 면접 환경 구축**

실시간 면접 시스템에서 네트워크 불안정이나 브라우저 강제 종료로 인한 WebSocket 연결 끊김은 치명적인 문제였습니다. 연결이 끊어지면 진행 중인 면접이 중단되고, 사용자는 처음부터 다시 시작해야 하는 상황이 발생할 수 있었습니다.

전통적인 heartbeat나 ping/pong 방식은 지속적인 메시지 교환으로 인한 오버헤드가 발생했고, 우리 서비스의 특성상 대부분의 이벤트가 broadcast되어 즉각적인 연결 끊김 감지가 어려웠습니다.

이를 해결하기 위해 **"마지막 이벤트 재발송"** 전략을 도입했습니다. 각 사용자별로 마지막 발생 이벤트를 메모리에 저장하고, 재연결 시 해당 이벤트를 재전송하여 끊어진 지점부터 자연스럽게 이어갈 수 있도록 구현했습니다.

JWT 기반 사용자 인증과 userId 매핑을 통해 재연결 시 이전 세션 상태를 정확히 복구할 수 있었고, 연결 상태 모니터링 오버헤드 없이도 빠른 재연결이 가능한 안정적인 면접 환경을 구축했습니다.

### 🔐 **다중 로그인 방식과 권한 기반 접근 제어 시스템**
카카오 로그인 사용자와 게스트 사용자 모두 지원해야 했는데, 기존 Spring Security 구조만으로는 질문 세트 조회 시 로그인 상태에 따른 데이터 제공 권한 관리를 해결할 수 없었습니다. 단순한 authenticated/unauthenticated 구분만으로는 카카오/게스트/비로그인 상태를 구분할 수 없었기 때문에 `@LoginUser(required=false)` 어노테이션을 통해 로그인 방식을 선택할 수 있도록 했습니다.

JWT 토큰 기반 통합 인증으로 카카오/게스트 로그인을 동일하게 처리하면서, 비로그인 사용자에게는 공개(`isPublic=true`) 질문 세트만 조회하도록 제공하는 제어가 가능해졌고, Repository 계층에서 userId 파라미터 유무에 따라 동적으로 쿼리를 변경해 성능과 보안을 모두 확보했습니다.

### 📚 **복잡한 질문세트 버전 관리와 데이터 무결성 보장**
면접 질문 세트는 지속적으로 수정되어야 하고, 기존(수정 전)에 있던 질문 세트는 무결성을 보장해야 했습니다. 단순 수정 방식으로는 무결성을 보장할 수 없고, 완전 복사 방식으로는 저장 공간을 비효율적으로 관리하게 됩니다.

버전 관리를 통해 부모-자식 관계 기반으로 Question 엔티티는 불변으로 설계해 여러 버전에서 재사용하고, QuestionSet에 parentVersionId와 versionNumber를 추가해 수정 시 새로운 버전을 생성하는 방식으로 구현했습니다. QuestionSetQuestionMap을 통해서 질문 순서와 버전별 매핑을 관리할 수 있도록 구현했습니다.

### 🔄 **MultipleBagFetchException과 복잡한 연관관계 조회 최적화**
질문 세트 상세 조회 시, Question과 Tag를 동시에 JOIN FETCH하면 (Hibernate에서는 여러 컬렉션을 동시에 FETCH JOIN할 때 카르테시안 곱으로 인해 중복 데이터와 메모리 오버헤드 문제를 방지하기 위한 에러를 발생시킨다.) MultipleBagFetchException이 발생하는 문제가 있었습니다.

`findByIdWithQuestions`와 `findByWithTags`를 별도로 구현해 default 메서드인 `findByIdWithQuestionsAndTags`에서 두 번의 쿼리로 나누어 로딩하도록 설계했습니다.
이 과정에서 영속성 컨텍스트의 1차 캐시를 활용해 두 번째 쿼리에서는 이미 로딩된 엔티티에 태그로 정보만 추가 로딩되도록 하여, N+1 문제 없이 조회할 수 있도록 구현했습니다.

### ⚡ **트랜잭션 격리 수준과 동시성 제어**
질문 세트 복사나 버전 생성 시, 여러 테이블을 거쳐 복잡한 데이터 조작이 발생합니다. 원본 질문세트 조회, 새로운 질문세트 생성, 질문 매핑 복사, 태그 관계 복사 등의 과정에서 동시성 문제가 발생할 수 있었습니다. 특히 같은 질문세트를 여러 사용자가 동시에 복사하거나 버전을 생성할 때 데이터 불일치나 중복 생성 문제가 우려되었고, 기본 격리 수준으로는 충분한 보장을 할 수 없었습니다.

`@Transactional(isolation = Isolation.READ_COMMITED, rollbackFor = Exception.class, timeout = 30)`을 도입해 READ_COMMITED와 타임아웃 기반 트랜잭션 제어를 도입했습니다.
엔티티에 JPA `@Version` 어노테이션을 추가해 낙관락을 구현하고, 동시 수정 시 OptimisticLockExcpetion이 발생할 수 있도록 해 데이터 충돌을 감지할 수 있도록 했습니다. 30초 타임아웃 설정으로 장시간 대기하는 트랜잭션을 방지해 데드락 상황을 예방했습니다. `rollbackFor = Exception.class`로 모든 예외 상황에서 롤백이 수행되도록 해 데이터 무결성을 보장했습니다.

### 🏷️ **태그 기반 검색과 인덱스 전략**
질문 세트가 많아질수록 사용자들이 원하는 질문 세트를 빠르게 찾을 수 있는 검색 시스템이 필요했습니다. 단순 제목 기반 검색으로는 한계가 있을 수 있어 태그 시스템을 도입했지만, 복잡한 JOIN 연산으로 인해 성능 이슈가 발생했습니다. 특히 QuestionSet, QuestionSetTag, Tag 세 테이블을 JOIN하면서 동시에 권한 검증(isPublic, ownerUserId)까지 처리해야 하는 상황이었기 때문에 쿼리 최적화가 필요했습니다.

복합 인덱스를 활용해 태그 조건과 권한 조건을 동시에 최적화하고, Repository 쿼리에서는 `JOIN FETCH qs.ownerUserId`를 사용해 N+1 문제를 방지하면서, `WHERE t.tag IN :tagNames AND (:userId IS NULL OR qs.ownerUserId.userId = :userId OR qs.isPublic = true)` 조건으로 태그 필터링과 권한 검증을 한 번에 처리하도록 구현했습니다.

(위키 추가 필요)    
[Wiki로 자세히 보기](https://www.notion.so/230656e282d8801991e7fda7326561a8)

<br/>

### 🛡️ **백**


[Wiki로 자세히 보기]()

<br />

### 🚀 프론트


[Wiki로 자세히 보기]()

<br/>
<br/>

## 🎯 주요 기능 소개

### 🎤 실시간 면접 세션 관리
> **"혼자도, 둘이서도! 다양한 면접 연습 모드"**

**사용자는 SOLO 또는 PAIR 모드를 선택해 면접을 시작**할 수 있습니다.  
**WebSocket 기반 실시간 통신**으로 면접 상태가 실시간으로 동기화되며,  
**면접관/면접자 역할을 자유롭게 바꿔가며** 연습할 수 있어요.

<img src="" width="700" alt="면접 세션 관리" />

<br/>
<br/>

### 🤖 AI 기반 꼬리질문 생성
> **"답변에 따라 달라지는 똑똑한 질문!"**

**사용자의 답변을 실시간으로 분석**하여  
**GPT API를 통해 맞춤형 꼬리질문을 자동 생성**합니다.  
**STT(Speech-to-Text) 기술**로 음성 답변을 텍스트로 변환하고,  
이를 바탕으로 **개인화된 후속 질문**을 제공해요.

<img src="" width="700" alt="AI 꼬리질문" />

<br/>
<br/>

### 🎥 WebRTC 화상 면접
> **"실제 면접과 똑같은 화상 환경!"**

**WebRTC 시그널링 서버**를 통해 P2P 화상 통화를 지원하며,  
**실시간 음성/영상 스트리밍**으로 실제 면접과 동일한 환경을 제공합니다.  
**화면 공유와 음성 제어** 기능으로 다양한 면접 상황을 연출할 수 있어요.

<img src="" width="700" alt="WebRTC 화상면접" />

<br/>
<br/>

### 💬 실시간 채팅 시스템
> **"면접 중에도 소통은 계속되어야죠!"**

**WebSocket 기반 실시간 채팅**으로 면접 진행 중  
**참여자들이 즉시 소통**할 수 있습니다.  
**방별 채팅방 관리**와 **메시지 브로드캐스팅**으로  
원활한 커뮤니케이션을 지원해요.

<img src="" width="700" alt="실시간 채팅" />

<br/>
<br/>

### 📊 면접 결과 분석
> **"연습 결과를 한눈에! 성장하는 나를 확인하세요"**

**면접 진행 과정과 답변 내용을 자동 저장**하여  
**개인별 면접 기록과 통계를 제공**합니다.  
**질문별 답변 시간, STT 정확도, 면접 횟수** 등  
다양한 지표로 **면접 실력 향상도를 추적**할 수 있어요.

<img src="" width="700" alt="면접 결과 분석" />

<br/>
<br/>

## ✅ 서비스 구조도

<img src="https://raw.githubusercontent.com/SwnBae/kkori_img/main/arc.png" width="700" alt="Kkori 서비스 아키텍처" />

<br/>
<br/>

## ⚒️ Tech Stacks

| 분류 | 기술 스택                                                                                                |
|------|------------------------------------------------------------------------------------------------------|
| **Frontend** | [![My Skills](https://skillicons.dev/icons?i=react,vite,tailwind,ts,nodejs)](https://skillicons.dev) |
| **Backend** | [![My Skills](https://skillicons.dev/icons?i=java,spring,hibernate)](https://skillicons.dev)          |
| **Database / Infra** | [![My Skills](https://skillicons.dev/icons?i=mysql,nginx,aws)](https://skillicons.dev)               |
| **배포** | [![My Skills](https://skillicons.dev/icons?i=docker,jenkins)](https://skillicons.dev)                 |
| **협업 / 개발도구** | [![My Skills](https://skillicons.dev/icons?i=git,gitlab,notion,jira)](https://skillicons.dev)        |

<br/>
<br/>

## 🤼 팀원 소개

| 이찬 | 김형진 | 강진규 | 배수한 | 유윤지 | 장동현 |
|:---:|:---:|:---:|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/3bc958ec-4303-4559-b20e-465fe1776e17" width="120"> | <img src="https://avatars.githubusercontent.com/u/49364688?v=4" width="120"> | <img src="https://avatars.githubusercontent.com/u/64190888?v=4" width="120"> | <img src="https://avatars.githubusercontent.com/u/128581113?v=4" width="120"> | <img src="https://avatars.githubusercontent.com/u/105447233?v=4" width="120"> | <img src="https://avatars.githubusercontent.com/u/205485545?v=4" width="120"> |
| **Frontend** | **Frontend** | **Backend** | **Backend** | **Backend** | **Backend** |
| [@today-is-first](https://github.com/today-is-first) | [@hyeongjin-kim](https://github.com/hyeongjin-kim) | [@jin0410](https://github.com/jin0410) | [@SwnBae](https://github.com/SwnBae) | [@llcodingll](https://github.com/llcodingll) | [@SuitGGam](https://github.com/SuitGGam) |

