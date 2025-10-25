<h1 align="center">
  <br>
  <img src="https://github.com/user-attachments/assets/fc4eee91-ae47-4e53-92c6-81c0c6a6c57b" alt="Wedy logo" width="200" />
  <p>
  Wedy
  <p>
</h1>
<h4 align="center">예비 신랑 신부를 위한 일정 관리 & 커플 플래너</h4>

## 📘 프로젝트 개요

> 커플이 함께 일정과 태스크를 관리하고, 결혼 준비 과정을 공유할 수 있는 **플래너 서비스**입니다.

# 개발 기간 (프로토타입 단계 종료)

2025/07/01 ~ 2025/07/30 (4주)

<hr>
--- 

- 핵심 기능(OAuth2, 커플 매칭) 구현 완료

OAuth2 소셜 로그인 (Kakao)

- Spring Security 기반 OAuth2 Client 구성
- Access/Refresh Token 발급 및 JWT 통합

<br>
회원가입 시 닉네임 유효성 검증 (Custom Validator 적용)
<br>
<br>

커플코드 기반 매칭 기능: 초대 코드 입력 시 양방향 관계 생성

- `Couple` 엔티티 중심으로 `Member` 양방향 연결
- 예외처리: 이미 매칭된 사용자 / 잘못된 코드
- 마이페이지 기본 기능 구현

# 프로젝트 이미지

<br>

<img width="" height="515" alt="스크린샷 2025-09-12 오전 7 52 49" src="https://github.com/user-attachments/assets/9048e991-6694-4680-bfd7-5c0e1690b1c2" />


<img width="" height="256" alt="스크린샷 2025-09-12 오전 7 54 25" src="https://github.com/user-attachments/assets/b4863a0c-11d5-4cf0-bebe-99d273073f9c" />

<img width="" height="344" alt="스크린샷 2025-09-12 오전 7 55 30" src="https://github.com/user-attachments/assets/94896167-a98e-47ef-8f4b-b73ca6ce79a4" />



<img width="" height="364" alt="스크린샷 2025-09-12 오전 7 56 35" src="https://github.com/user-attachments/assets/8322c723-e1e2-4d3d-b9f6-b9a5be9d6801" />

<img width="" height="415" alt="스크린샷 2025-09-12 오전 7 57 19" src="https://github.com/user-attachments/assets/8ba45a8c-d7e3-4f7b-8157-59d77921c772" />

<img width="" height="445" alt="스크린샷 2025-09-12 오전 7 58 16" src="https://github.com/user-attachments/assets/8d324ed5-b991-4557-a230-b4fe4e837888" />


<img width="" height="292" alt="스크린샷 2025-09-12 오전 7 58 27" src="https://github.com/user-attachments/assets/5a0e7c49-a73c-4baf-9830-a48b81ce835b" />

# 팀원

| <img src="https://github.com/user-attachments/assets/984d3041-b787-4da3-b07e-f2132411193e" width="150"> | <img src="https://github.com/user-attachments/assets/caf98b12-21c5-4396-80b5-d3054a36d33b" width="150"> |
|:-------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------:|
|                               [HyungGeun](https://github.com/HyungGeun94)                               |                                  [JeHyuck](https://github.com/jehyuck)                                  |
|                                                   BE                                                    |                                                   BE                                                    |

# 기술 스택

	• 프로그래밍 언어 및 프레임워크
      Java 17, SpringBoot

	• 데이터베이스 
      mysql 8.0

	• 도커 및 컨테이너화
      Docker, Docker Compose

    • 인프라 및 클라우드 관련 서비스
      aws ec2, route53, ELB, ECR, IAM, S3

    • DevOps 및 CI/CD 관련 도구
       github actions, postman, swagger

    • 협업 및 개발 도구 
       github, notion, discord, intelliJ

# ERD / 구조도 (작성예정)

# 주요 기능

```
회원가입 및 커플코드 매칭
```

```
함께 할 태스크 등록
```

# 주요 기능 일부 상세 코드

( 작성 예정 )

# 💭회고 (후기)

OAuth2 기반 인증과 커플 매칭 로직을 중심으로 서비스의 핵심 흐름을 구현했습니다.
<br>
<br>
도메인 중심 설계를 적용하며, 협업 과정에서 백엔드 간의 구조적 통일성과 설계 방향에 대해 논의할 수 있던 프로젝트입니다.
<br>
<br>
비록 프로토타입 단계에서 마무리되었지만, 추후 확장을 고려한 기반 설계를 목표로 진행되었습니다.


<p align="center"><i>© 2025 Team Wedy</i></p>

