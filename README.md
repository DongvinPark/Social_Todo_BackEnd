# 할 일을 미루지 않게 만들어주는 소셜 투두 서비스

## 프로젝트 기획 동기

* 혼자서만 사용하는 투두 서비스는 할 일을 적어 놓고도 귀찮아서 무시하는 경우가 많습니다.
* 이러한 일을 방지하고자 투두 리스트를 마감 기한을 정해서 공개하여 다른 유저들로부터 응원/잔소리를 들어가면서 할 일을 미루지 않게 해주는 서비스를 기획했습니다.


## 프로젝트 핵심 기능

1. [nGrinder를 이용하여 상황별로 EC2가 감당 가능한 동시접속자 수와 이 때의 TPS를 측정했습니다.](https://github.com/DongvinPark/Social_Todo_Backend_Load_Test)
2. 마감 날짜(==디데이)를 정해서 프라이빗 투두와 공개 투두를 작성할 수 있습니다.
3. 디데이가 오늘 날짜인 공개 투투 아이템들 중에서 자신이 팔로우 한 사람들의 공개 투두 아이템들을 자신의 타임라인에서 확인할 수 있습니다.
4. 다른 유저의 공개 투두 아이템에 응원 또는 잔소리 버튼을 누르거나, 다른 사람을 팔로우 하면 알림이 전송됩니다.
5. 레디스를 사용하여 DB I/O를 줄였습니다.
6. 스프링 @Async를 사용하여 단기간에 들어오는 대량의 응원/잔소리 요청을 비동기로 처리함으로써 사용자가 느끼는 응답 대기 시간을 감소시켰습니다.

## 프로젝트 구조
  <img src="https://user-images.githubusercontent.com/99060708/234712298-9b2ab4ca-6a12-4956-a710-f83d5690398d.jpeg" width="790" height="500"/>


- ERD
  ![A899F8EC-F2CA-4168-9F58-350CB40710D0](https://user-images.githubusercontent.com/99060708/213955342-3e2a41f1-1f1f-42ce-a4ed-95c91f5cb43f.jpeg)

## 기술 스택 - AWS에 배포하는 것을 목표로 선정하였습니다.
- ![java8](https://img.shields.io/badge/-JAVA%208-orange)
- ![spring badge](https://img.shields.io/badge/-Spring%20Boot%202.7.6-green)
- ![Junit5](https://img.shields.io/badge/-Junit%205-yellow)
- ![redis](https://img.shields.io/badge/-AWS%20ElasticCache%20for%20Redis-red)
- ![EC2](https://img.shields.io/badge/-AWS%20EC2%20Ubuntu%20LTS%2020.04-lightgrey)
- ![AWS RDS](https://img.shields.io/badge/-AWS%20RDS%20MariaDB%2010.6.8-blue)

## 핵심 기능 코드
- [레디스를 이용해서 유저 확인을 위한 DB I/O를 감소시켰습니다.](https://github.com/DongvinPark/Social_Todo_BackEnd/blob/10_Refactoring_Applied/src/main/java/com/example/socialtodobackend/security/JWTAuthenticationFilter.java)
- [레디스를 이용해서 타임라인 요청 시에 필요한 DB I/O를 감소시켰습니다.](https://github.com/DongvinPark/Social_Todo_BackEnd/blob/10_Refactoring_Applied/src/main/java/com/example/socialtodobackend/service/UserService.java)
- [응원/잔소리 요청을 레디스에서 처리하게 만들어서 DB I/O를 감소시켰습니다.](https://github.com/DongvinPark/Social_Todo_BackEnd/blob/10_Refactoring_Applied/src/main/java/com/example/socialtodobackend/service/SupportService.java)
- [스프링 @Async를 위한 스레드 풀의 개수를 셋팅할 때 로컬 환경에서 직접 테스트를 해보고 결정하였습니다.](https://github.com/DongvinPark/Spring_Async_Test)
- [스프링 @Async를 사용하여 대량의 응원/잔소리 트래픽을 처리할 때 발생할 수 있는 예외들을 잡아내기 위한 처리를 해주었습니다.](https://github.com/DongvinPark/Social_Todo_BackEnd/blob/10_Refactoring_Applied/src/main/java/com/example/socialtodobackend/configuration/async/CustomAsyncExceptionHandler.java)
- [서비스 계층 메서드에 대하여 Junit5 테스트 코드를 모두 작성하고 통과하였습니다.](https://github.com/DongvinPark/Social_Todo_BackEnd/tree/10_Refactoring_Applied/src/test/java/com/example/socialtodobackend/service)

## AWS 배포 후 테스트 로그 기록
- 아래의 파일은 AWS EC2에 배포 후 직접 요청을 보내서 테스트를 완료한 과정을 기록한 파일입니다.
- 테스트에서 사용한 URL, JSON이 모두 포함돼 있습니다.
- [소셜 투두 서비스 실제 작동 로그 파일 - 2023년 1월22일 기록.docx](https://github.com/DongvinPark/Social_Todo_BackEnd/files/10476339/-.2023.1.22.docx)
