show user;
-- USER이(가) "JDBC_USER"입니다.

select *
from tbl_member
order by userseq desc;

update tbl_member set status = 1;
commit;
-- 커밋 완료.


---- *** 게시판 테이블 생성하기 *** ----
create table tbl_board
(boardno       number         not null        -- 글번호
,fk_userid     varchar2(30)   not null        -- 작성자아이디
,subject       Nvarchar2(100) not null        -- 글제목
,contents      Nvarchar2(200) not null        -- 글내용
,writeday      date default sysdate not null  -- 작성일자 
,viewcount     number default 0 not null      -- 조회수 
,boardpasswd   varchar2(20) not null          -- 글암호
,constraint PK_tbl_board_boardno primary key(boardno)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
);
-- Table TBL_BOARD이(가) 생성되었습니다.

create sequence seq_board
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_BOARD이(가) 생성되었습니다. 

desc tbl_board;

select *
from tbl_board
order by boardno desc;

desc tbl_member;
-- 글번호   글제목   작성자   작성일자    조회수

-----------------------------------------------------------------------------------------
/*
   Transaction(트랜잭션) 처리 실습을 위해서
   tbl_member 테이블의 point 컬럼의 값은 최대 30을 넘지 못하도록 check 제약을 걸도록 하겠습니다.
*/
-----------------------------------------------------------------------------------------
alter table tbl_member
add constraint CK_tbl_member_point check( point between 0 and 30 );
-- Table TBL_MEMBER이(가) 변경되었습니다.

select *
from tbl_member
order by userseq desc;

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
/*
오류 보고 -
ORA-02290: 체크 제약조건(JDBC_USER.CK_TBL_MEMBER_POINT)이 위배되었습니다
*/

select *
from tbl_member
order by userseq desc;

rollback;
-- 롤백 완료.



-- 글번호      글제목     작성자     작성일자    조회수

select *
from tbl_board;

select *
from tbl_member;

-- ** 글목록보기 ** --
-- 글제목을 15자까지 보여주기, 15자가 넘어가면 13자리를 보여주고 ..을 찍기
SELECT B.boardno
     , CASE WHEN length(B.subject) > 15 then substr(B.subject, 1, 13) || '..' else B.subject end AS subject
     , M.name
     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
     , B.viewcount
FROM tbl_board B JOIN tbl_member M
ON B.fk_userid = M.userid
ORDER BY B.boardno desc;


-- ** 글내용보기1 ** --
SELECT V.subject, V.contents, M.name, V.viewcount, V.fk_userid
FROM
(
SELECT CASE WHEN length(subject) > 15 then substr(subject, 1, 13) || '..' else subject end AS subject
     , contents
     , viewcount
     , fk_userid
FROM tbl_board 
WHERE boardno = '1'
) V JOIN tbl_member M
ON V.fk_userid = M.userid;


-- ** 글내용보기2 ** --
SELECT subject, contents, fk_userid, boardpasswd
FROM tbl_board
WHERE boardno = 1;


---- *** 댓글 테이블 생성하기 *** ----
create table tbl_comment
(commentno   number         not null        -- 댓글번호
,fk_boardno  number         not null        -- 원글의 글번호 
,fk_userid   varchar2(30)   not null        -- 작성자 아이디
,contents    Nvarchar2(100) not null        -- 댓글내용
,writeday    date default sysdate not null  -- 작성일자
,constraint PK_tbl_comment_commentno primary key(commentno)
,constraint FK_tbl_comment_fk_boardno foreign key(fk_boardno) references tbl_board(boardno) on delete cascade
,constraint FK_tbl_comment_fk_userid foreign key(fk_userid) references tbl_member(userid)
);
-- Table TBL_COMMENT이(가) 생성되었습니다.

create sequence seq_comment
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_COMMENT이(가) 생성되었습니다.


select *
from tbl_comment;


-- 글제목에 댓글번호 뜨게 하기
SELECT B.boardno 
     , CASE WHEN length(B.subject) > 15 then substr(B.subject, 1, 13) || '..' else B.subject end AS subject
     , M.name 
     , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday 
     , B.viewcount 
FROM tbl_board B JOIN tbl_member M 
ON B.fk_userid = M.userid 
ORDER BY B.boardno desc;

-- fk_boardno 그룹 한 다음 count 하면 글에 존재하는 댓글의 수 확인 가능

SELECT fk_boardno, count(*) AS comment_cnt
FROM tbl_comment
GROUP BY fk_boardno;

-- outer join 해야함 --> 댓글이 없는 글도 확인할 수 있어야하기 때문

SELECT V1.boardno
     , CASE WHEN V2.comment_cnt IS NULL THEN V1.subject ELSE V1.subject || ' [' || V2.comment_cnt || '] ' 
       END AS subject
     , V1.name, V1.writeday, V1.viewcount
FROM
(
    SELECT B.boardno 
         , CASE WHEN length(B.subject) > 15 then substr(B.subject, 1, 13) || '..' else B.subject end AS subject
         , M.name 
         , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday 
         , B.viewcount 
    FROM tbl_board B JOIN tbl_member M 
    ON B.fk_userid = M.userid 
) V1
LEFT JOIN
(
    SELECT fk_boardno, count(*) AS comment_cnt
    FROM tbl_comment
    GROUP BY fk_boardno
) V2
ON V1.boardno = V2.fk_boardno
ORDER BY V1.boardno;



-- 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한 tbl_comment 테이블과 tbl_member 테이블을 JOIN 해서 보여준다.)
SELECT C.contents, M.name, to_char(C.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
FROM
(
    select *
    from tbl_comment
    where fk_boardno = 8
) C JOIN tbl_member M
ON C.fk_userid = M.userid
ORDER BY C.commentno desc;



-- **** 7. 최근 1주일내에 작성된 게시글을 날짜별로 개수를 출력 **** --
update tbl_board set writeday = writeday - 7
where boardno = 1;
-- 1 행 이(가) 업데이트되었습니다.

commit;

select writeday
     , to_char(writeday, 'yyyy-mm-dd hh24:mi:ss')
     , to_char(writeday, 'yyyy-mm-dd')
     , to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd')
     , to_char(to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 'yyyy-mm-dd hh24:mi:ss')
     , sysdate
     , to_char(to_date(to_char(sysdate, 'yyyy-mm-dd')), 'yyyy-mm-dd hh24:mi:ss')
from tbl_board;


select to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd')
     , to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd')
     , to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd')
from tbl_board;

select *
from tbl_board
where to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7;

select count(*) AS TOTAL
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 6, 1, 0 )) AS PREVIOUS6
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 5, 1, 0 )) AS PREVIOUS5
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 4, 1, 0 )) AS PREVIOUS4
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 3, 1, 0 )) AS PREVIOUS3
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 2, 1, 0 )) AS PREVIOUS2
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 1, 1, 0 )) AS PREVIOUS1
     , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 0, 1, 0 )) AS TODAY    
from tbl_board
where to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7;


-- **** 8. 이번달 일자별 게시글 작성건수 **** --
select decode( grouping(to_char(writeday, 'yyyy-mm-dd')), 0, to_char(writeday, 'yyyy-mm-dd'), 1, '전체' ) AS WRITEDAY
     , count(*) AS CNT
from tbl_board
where to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm')
group by rollup(to_char(writeday, 'yyyy-mm-dd'));


















