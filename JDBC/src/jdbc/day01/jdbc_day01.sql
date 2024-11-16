---- **** === 오라클 계정 생성하기 시작 === **** ----

-- 오라클 계정 생성을 위해서는 SYS 또는 SYSTEM 으로 연결하여 작업을 해야 합니다. [SYS 시작] --
show user;
-- USER이(가) "SYS"입니다.

-- 오라클 계정 생성시 계정명 앞에 c## 붙이지 않고 생성하도록 하겠습니다.
alter session set "_ORACLE_SCRIPT"=true;
-- Session이(가) 변경되었습니다.

-- 오라클 계정명은 JDBC_USER 이고 암호는 gclass 인 사용자 계정을 생성합니다.
create user JDBC_USER identified by gclass default tablespace users; 
-- User JDBC_USER이(가) 생성되었습니다.

-- 위에서 생성되어진 JDBC_USER 이라는 오라클 일반사용자 계정에게 오라클 서버에 접속이 되어지고,
-- 테이블 생성 등등을 할 수 있도록 여러가지 권한을 부여해주겠습니다.
grant connect, resource, create view, unlimited tablespace to JDBC_USER;
-- Grant을(를) 성공했습니다.

---- **** === 오라클 계정 생성하기 끝 === **** ----

show user;
-- USER이(가) "JDBC_USER"입니다.

create table tbl_memo
(no          number(4)
,name        Nvarchar2(20) not null
,msg         Nvarchar2(100) not null
,writeday    date default sysdate
,constraint  PK_tbl_memo_no primary key(no)
);
-- Table TBL_MEMO이(가) 생성되었습니다.

drop table sdfkjdfkjdf purge;

create sequence seq_memo
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_MEMO이(가) 생성되었습니다.

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday from tbl_memo order by no desc


update tbl_memo set writeday = writeday - 1     -- 글번호 1번의 작성일을 어제로 변경
where no = 1;
-- 1 행 이(가) 업데이트되었습니다.

commit;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where no = '1'      -- 또는 where no = 1 / String 타입은 호환가능
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where no = 'wefwef'      
order by no desc;
-- ORA-01722: 수치가 부적합합니다

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where name = '손혜정'      -- 존재함
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where name = '차은우'      -- 존재하지 않음
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where msg like '%' || '점심' || '%'      
order by no desc;

select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from tbl_memo
where to_char(writeday, 'yyyy-mm-dd') = '2024-03-06'
order by no desc;

---- tbl_memo 테이블에 updateday 컬럼을 추가한다.  ----
alter table tbl_memo
add updateday date;
-- Table TBL_MEMO이(가) 변경되었습니다.

select name, msg, updateday
from tbl_memo
where no = 1;

select name, msg, updateday
from tbl_memo
where no = 17;

update tbl_memo set name = '정혜손', msg = '칼퇴'
where no = 1;

commit;
-- 커밋 완료.

select *
from user_tables
where table_name = 'TBL_MEMO';

select *
from user_sequences
where sequence_name = 'SEQ_MEMO';










