# Board v1 프로젝트 가이드

Spring Boot 기반의 **게시판 + 회원 + 댓글** 웹 애플리케이션입니다.  
서버 사이드 렌더링(SSR)과 Mustache 템플릿, H2 인메모리 DB, 세션 기반 로그인으로 동작합니다.

---

## 1. 기술 스택

| 구분 | 기술 |
|------|------|
| 언어 | Java 21 |
| 빌드 | Gradle (Spring Boot 4.0.2) |
| 웹 | Spring Web MVC |
| 뷰 | Mustache (서버 사이드 렌더링) |
| DB | H2 (인메모리), JPA/Hibernate |
| 기타 | Lombok, DevTools, H2 Console |

---

## 2. 프로젝트 구조

```
src/main/java/com/example/boardv1/
├── Boardv1Application.java     # 메인 진입점
├── board/                      # 게시글 도메인
│   ├── Board.java              # 엔티티
│   ├── BoardControler.java     # 컨트롤러
│   ├── BoardService.java       # 비즈니스 로직
│   ├── BoardRepository.java    # JPA/EntityManager 접근
│   ├── BoardRequest.java       # 요청 DTO
│   └── BoardResponse.java      # 응답 DTO
├── user/                       # 회원 도메인
│   ├── User.java
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   ├── UserRequest.java
│   └── UserRespones.java
└── reply/                      # 댓글 도메인
    ├── Reply.java
    ├── ReplyController.java
    ├── ReplyService.java
    ├── ReplyRepository.java
    ├── ReplyRequest.java
    └── ReplyResponse.java
```

```
src/main/resources/
├── application.properties      # 서버, DB, Mustache, JPA 설정
├── db/data.sql                 # 초기 데이터 (user, board, reply)
└── templates/
    ├── header.mustache         # 공통 상단(네비게이션)
    ├── index.mustache          # 게시글 목록
    ├── board/
    │   ├── detail.mustache     # 게시글 상세 + 댓글
    │   ├── save-form.mustache  # 글쓰기 폼
    │   └── update-form.mustache# 수정 폼
    └── user/
        ├── login-form.mustache
        └── join-form.mustache
```

---

## 3. 데이터 모델 (ER 관계)

- **User** (`user_tb`): 회원 (id, username, password, email, createdAt)
- **Board** (`board_tb`): 게시글 (id, title, content, **user_id**, createdAt) → User와 N:1
- **Reply** (`reply_tb`): 댓글 (id, comment, **board_id**, **user_id**, createdAt) → Board, User와 각각 N:1

JPA 엔티티로 매핑되어 있으며, `data.sql`로 앱 기동 시 샘플 유저(ssar, cos), 게시글, 댓글이 들어갑니다.

---

## 4. 인증/권한 방식

- **인증**: `HttpSession`에 로그인한 `User` 객체를 `"sessionUser"` 키로 저장.
- **권한**:  
  - 글쓰기/글수정/글삭제/댓글삭제: 로그인 필수 + **해당 글/댓글의 작성자만** 가능.  
  - 컨트롤러에서 `session.getAttribute("sessionUser")`로 확인하고, 서비스에서 `sessionUserId`와 글/댓글의 `user.id`를 비교해 권한 체크.

---

## 5. URL 및 화면 흐름

### 5.1 회원 (User)

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/join-form` | 회원가입 폼 |
| POST | `/join` | 회원가입 처리 → `/login-form` 리다이렉트 |
| GET | `/login-form` | 로그인 폼 |
| POST | `/login` | 로그인 처리, 세션에 `sessionUser` 저장 → `/` 리다이렉트 |
| GET | `/logout` | 세션 무효화 → `/` 리다이렉트 |

### 5.2 게시글 (Board)

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/` | 게시글 목록 (index.mustache), `models`에 목록 전달 |
| GET | `/boards/save-form` | 글쓰기 폼 (로그인 필요) |
| POST | `/boards/save` | 글 저장 (로그인 필요) → `/` 리다이렉트 |
| GET | `/boards/{id}` | 게시글 상세 (board/detail), `model`에 DetailDTO 전달 |
| GET | `/boards/{id}/update-form` | 수정 폼 (본인 글만) |
| POST | `/boards/{id}/update` | 수정 처리 (본인 글만) → `/boards/{id}` 리다이렉트 |
| POST | `/boards/{id}/delete` | 삭제 (본인 글만) → `/` 리다이렉트 |
| GET | `/api/boards/{id}` | 상세 JSON API (동일 DetailDTO) |

### 5.3 댓글 (Reply)

| 메서드 | URL | 설명 |
|--------|-----|------|
| POST | `/api/replies/save` | 댓글 등록 (JSON body: boardId, comment), 로그인 필요. 201 + DTO 또는 401 |
| POST | `/replies/{id}/delete?boardId=숫자` | 댓글 삭제 (본인만) → `/boards/{boardId}` 리다이렉트 |

댓글 등록은 상세 페이지에서 `fetch("/api/replies/save", ...)`로 AJAX 호출 후, 성공 시 DOM에 새 댓글을 붙이는 방식입니다.

---

## 6. 레이어별 역할

- **Controller**: URL 매핑, 세션에서 `sessionUser` 조회, 요청 DTO 수신, 서비스 호출, 뷰 이름 반환 또는 리다이렉트/API 응답.
- **Service**: 트랜잭션(`@Transactional`), 권한 검사(글/댓글 소유자), DTO 생성 및 Repository 호출.
- **Repository**: `EntityManager`로 JPA 엔티티 조회/저장/삭제 (BoardRepository에 `replySave` 포함).
- **Entity**: DB 테이블과 1:1 매핑 (User, Board, Reply).
- **Request/Response DTO**: 클라이언트 ↔ 서버 데이터 전달 (예: BoardRequest.SaveOrUpdateDTO, BoardResponse.DetailDTO, ReplyResponse.DTO).

---

## 7. 주요 비즈니스 로직 요약

- **게시글 목록**: `BoardService.게시글목록()` → `BoardRepository.findAll()` (id 내림차순).
- **게시글 상세**: `BoardService.상세보기(id, sessionUserId)` → Board 조회 후 `BoardResponse.DetailDTO` 생성 (제목, 내용, 작성자, 본인 글 여부 `isOwner`, 댓글 목록 포함).
- **글쓰기/수정/삭제**: 세션 유저 확인 후, 수정/삭제는 글의 `user.id`와 `sessionUser.getId()` 비교로 본인만 허용.
- **댓글 쓰기**: `ReplyService.댓글쓰기()` 또는 `BoardService.댓글쓰기()` 경로로 Board 조회 후 Reply 엔티티 생성 및 persist (실제 댓글 저장은 ReplyService에서 BoardRepository.replySave 사용).
- **댓글 삭제**: `ReplyService.댓글삭제(id, sessionUserId)`에서 댓글 소유자만 삭제 허용.

---

## 8. 실행 방법

1. **빌드 및 실행**
   ```bash
   ./gradlew bootRun
   ```
2. 브라우저에서 `http://localhost:8080` 접속.
3. (선택) H2 콘솔: `application.properties`에 `spring.h2.console.enabled=true` 되어 있으므로 H2 Console URL로 접속 가능.

---

## 9. 초기 데이터 (data.sql)

- **유저**: ssar / 1234, cos / 1234
- **게시글**: user_id 1, 2에 각각 여러 개
- **댓글**: board_id 5, 6 등에 대한 댓글

이 데이터로 로그인 후 목록/상세/수정/삭제/댓글을 바로 확인할 수 있습니다.

---

이 문서는 프로젝트의 구조, URL, 인증/권한, 데이터 모델, 실행 방법을 한 곳에서 파악할 수 있도록 정리한 것입니다.
