## 좌석 예약 시스템

### Pre-requirement

    * Spring Boot 2.7.16
    * OpenJDK 11.0.14
    * Kotlin
    * JPA
    * Mysql 8.0
    * Gradle
    * Docker

### Run On Local Environment

    # Mysql 실행
    1. docker-compose -f mysql-local.yml up -d
    2. docker exec -it ${CONTAINER_ID} bash
    3. mysql -u root -p
    4. 패스워드 'Abcd123@' 입력
    5. create database demo;

    # Application 실행
    - 처음 배치 스키마 생성을 위해 application.yml에 해당 부분을 아래와 같이 설정 (data.sql 실행)
        spring:
          sql:
            init:
              mode: always # 이후 never로 변경

### Test

    # cURL
    - Controller 패키지 하위 도메인별 http 파일 확인

    # 작성된 Test Code 실행

### Swagger

    http://localhost:8080/swagger-ui/index.html

### Mysql Table

    create table member (
        id bigint not null auto_increment comment 'ID',
        created_at datetime(6) comment '생성 시간',
        updated_at datetime(6) comment '수정 시간',
        seat_number varchar(10) comment '좌석번호',
        work_type varchar(30) comment '근무형태 타입',
        primary key (id)
    ) engine=InnoDB

    create table seat (
       id bigint not null auto_increment comment 'ID',
        created_at datetime(6) comment '생성 시간',
        updated_at datetime(6) comment '수정 시간',
        reserve_available_type varchar(255) comment '예약 가능 여부 타입',
        seat_number varchar(10) comment '좌석번호',
        primary key (id)
    ) engine=InnoDB

    create table seat_history (
       id bigint not null auto_increment comment 'ID',
        created_at datetime(6) comment '생성 시간',
        updated_at datetime(6) comment '수정 시간',
        member_id bigint comment '직원 ID',
        reserve_type varchar(30) comment '예약 타입',
        seat_id bigint comment '좌석 ID',
        seat_number varchar(10) comment '좌석번호',
        primary key (id)
    ) engine=InnoDB