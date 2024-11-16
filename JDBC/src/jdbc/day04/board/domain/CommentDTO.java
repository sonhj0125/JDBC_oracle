package jdbc.day04.board.domain;

//데이터베이스에서 domain 이란?
//엔티티의 속성들이 가질 수 있는 값들의 집합을 뜻하는 것이다.
//대부분의 DBMS(database management system)에서 도메인이란 속성에 대응하는 컬럼에 대한 데이터 타입(Data Type)과 길이를 의미한다.

//=== DTO(Data Transfer Object, 데이터전송(운반)객체 )
//쉽게 말해서 DTO는 테이블의 1개 행(ROW)을 말한다.
//어떤 테이블에 데이터를 insert 하고자 할때 DTO에 담아서 보낸다.
//또한 어떤 테이블에서 데이터를 select 하고자 할때도 DTO에 담아서 읽어온다.

public class CommentDTO {	// CommentDTO 가 오라클의 tbl_comment 테이블(tbl_member 및 tbl_board 테이블의 자식 테이블)에 해당됨. 
	
	// ==== field ==== //
	// insert용
	private int commentno; 		// 댓글번호
	private int fk_boardno; 	// 원글의 글번호 
	private String fk_userid;  	// 작성자 아이디
	private String contents;  	// 댓글내용
	private String writeday;  	// 작성일자
	
	
	////////////////////////////////////////////////////////
	// select 용
	private MemberDTO member;	// tbl_comment 테이블과 tbl_member 테이블을 JOIN. 글쓴이에 대한 모든 정보


	
	// ==== method ==== // 
	////////////////////////////////////////////////////////
	
	
	public int getCommentno() {
		return commentno;
	}


	public void setCommentno(int commentno) {
		this.commentno = commentno;
	}


	public int getFk_boardno() {
		return fk_boardno;
	}


	public void setFk_boardno(int fk_boardno) {
		this.fk_boardno = fk_boardno;
	}


	public String getFk_userid() {
		return fk_userid;
	}


	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}


	public String getContents() {
		return contents;
	}


	public void setContents(String contents) {
		this.contents = contents;
	}


	public String getWriteday() {
		return writeday;
	}


	public void setWriteday(String writeday) {
		this.writeday = writeday;
	}


	public MemberDTO getMember() {
		return member;
	}


	public void setMember(MemberDTO member) {
		this.member = member;
	}
								
	
	
	
	
	
	
	
}
