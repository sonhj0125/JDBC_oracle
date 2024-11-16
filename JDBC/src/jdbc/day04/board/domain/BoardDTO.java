package jdbc.day04.board.domain;

import java.util.List;

//데이터베이스에서 domain 이란?
//엔티티의 속성들이 가질 수 있는 값들의 집합을 뜻하는 것이다.
//대부분의 DBMS(database management system)에서 도메인이란 속성에 대응하는 컬럼에 대한 데이터 타입(Data Type)과 길이를 의미한다.

//=== DTO(Data Transfer Object, 데이터전송(운반)객체 )
//쉽게 말해서 DTO는 테이블의 1개 행(ROW)을 말한다.
//어떤 테이블에 데이터를 insert 하고자 할때 DTO에 담아서 보낸다.
//또한 어떤 테이블에서 데이터를 select 하고자 할때도 DTO에 담아서 읽어온다.

public class BoardDTO {		// BoardDTO는
							// 오라클의 tbl_board 테이블에 해당함.
							// tbl_board 테이블은 tbl_member 테이블을 부모테이블로 하고 있는 자식테이블이다.
							// 자식테이블에 부모테이블을 넣으면 join 가능
	// ==== field ==== // 
	// insert 용
	private int boardno; 		// 글번호
	private String fk_userid; 	// 작성자아이디
	private String subject; 	// 글제목
	private String contents; 	// 글내용
	private String writeday;    // 작성일자 
	private int viewcount; 		// 조회수 
	private String boardpasswd; // 글암호
	
	////////////////////////////////////////////////////////
	// select 용
	private MemberDTO member;	// tbl_board 테이블과 tbl_member 테이블을 JOIN. 글쓴이에 대한 모든 정보
    							// MemberDTO member 은 오라클의 tbl_member 테이블(부모 테이블)에 해당함.
	
	
	
	
	
	// ==== method ==== // 
	////////////////////////////////////////////////////////
	
	public int getBoardno() {
		return boardno;
	}
	public void setBoardno(int boardno) {
		this.boardno = boardno;
	}
	public String getFk_userid() {
		return fk_userid;
	}
	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public int getViewcount() {
		return viewcount;
	}
	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}
	public String getBoardpasswd() {
		return boardpasswd;
	}
	public void setBoardpasswd(String boardpasswd) {
		this.boardpasswd = boardpasswd;
	}

	public MemberDTO getMember() {
		return member;
	}
	public void setMember(MemberDTO member) {
		this.member = member;
	}
	
	////////////////////////////////////////////////////////
	
	public String boardListOne() {
		// 글번호\t글제목\t\t작성자\t작성일자\t\t조회수
		
		return boardno+"\t"+subject+"\t\t"+member.getName()+"\t"+writeday+"\t\t"+viewcount;
		
	} // end of public String boardListOne()
	
	
	
	
	
	
	
	
	

}
