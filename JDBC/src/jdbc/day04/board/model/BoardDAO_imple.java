package jdbc.day04.board.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.day04.board.dbconnection.MyDBConnection;
import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;
import jdbc.day04.board.domain.MemberDTO;

public class BoardDAO_imple implements BoardDAO {
	
	// field, attribute, property, 속성
	private Connection conn = MyDBConnection.getConn();
	private PreparedStatement pstmt;	// null 이 초기값
	private ResultSet rs;
	
	
	
	// method, operation, 기능
	
	// === 자원 반납을 해주는 메소드 === //
	private void close() {
		
		try {
			
			if(rs != null) 		{rs.close(); 	rs = null;}
			if(pstmt != null) 	{pstmt.close(); pstmt = null;}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	} // end of private void close()

	
	
	
	// == 게시판 글쓰기 == //
	// === Transaction 처리 ===
    //     (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
    //     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
    //     insert 와 update 가 모두 성공해야만 commit 할 것이다.)
	
	// 게시판 글쓰기 Transaction 처리하여 성공되어지면 1 을 리턴시켜줄 것이고,
	// 장애(오류)가 발생되어 실패하면 -1 을 리턴시켜 줄 것이다.
	
	@Override
	public int write(BoardDTO bdto) {
		
		int result = 0;
		
		// Transaciton 처리를 위해서 수동 commit 으로 전환시킨다.
		try {
			
			conn.setAutoCommit(false);
			
			String sql = " insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd) "
					   + " values(seq_board.nextval, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bdto.getFk_userid());
			pstmt.setString(2, bdto.getSubject());
			pstmt.setString(3, bdto.getContents());
			pstmt.setString(4, bdto.getBoardpasswd());
			
			int n1 = pstmt.executeUpdate();	// sql문의 실행
			
			if(n1 == 1) {	// tbl_board 테이블에 insert 가 성공되었다면
				sql = " update tbl_member set point = point + 10 "
					+ " where userid = ? ";
	
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, bdto.getFk_userid());
				
				int n2 = pstmt.executeUpdate();	// sql문의 실행
				
				if(n2 == 1) {	// tbl_member 테이블에 update 가 성공되었다면
					conn.commit();	// 둘다 성공했으므로 커밋해주기
					result = 1;
					
				} // end of if(n2 == 1)
				
			} // end of if(n1 == 1)
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 2290) {
				System.out.println(">> 아이디 "+ bdto.getFk_userid() +"님의 포인트는 30을 초과할 수 없기 때문에 오류발생 하였습니다. <<\n");
			}
			else {
				e.printStackTrace();
			}
			try {
				conn.rollback();	// 롤백을 해준다.
				result = -1;
			} catch (SQLException e2) { }
			
		} finally {
			
			try {
				conn.setAutoCommit(true);	// 수동 commit 을 자동 commit으로 복원 시킨다.
				
			} catch (SQLException e2) { }		
			
			close();	// 자원반납하기
		}
		
		return result;
		
	} // end of public int write(BoardDTO bdto)


	

	// == 글목록 보기 == //
	@Override
	public List<BoardDTO> boardList() {
		
		List<BoardDTO> boardList = new ArrayList<>();
		
		try {
			
			String sql = " SELECT V1.boardno "
					   + "      , CASE WHEN V2.comment_cnt IS NULL THEN V1.subject ELSE V1.subject || ' [' || V2.comment_cnt || '] ' "
					   + "        END AS subject "
					   + "      , V1.name, V1.writeday, V1.viewcount "
					   + " FROM "
					   + " ( "
					   + " SELECT B.boardno "
					   + "      , CASE WHEN length(B.subject) > 15 then substr(B.subject, 1, 13) || '..' else B.subject end AS subject "
					   + "      , M.name "
					   + "      , to_char(B.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					   + "      , B.viewcount "
					   + " FROM tbl_board B JOIN tbl_member M "
					   + " ON B.fk_userid = M.userid "
					   + " ) V1 "
					   + " LEFT JOIN "
					   + " ( "
					   + " SELECT fk_boardno, count(*) AS comment_cnt "
					   + " FROM tbl_comment "
					   + " GROUP BY fk_boardno "
					   + " ) V2 "
					   + " ON V1.boardno = V2.fk_boardno "
					   + " ORDER BY V1.boardno "; 		
			
			pstmt = conn.prepareStatement(sql);			
				
			rs = pstmt.executeQuery();	// SQL문 실행
			
			while(rs.next()) {
				
				BoardDTO bdto = new BoardDTO();
				bdto.setBoardno(rs.getInt("boardno"));
				bdto.setSubject(rs.getString("subject"));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString("name"));
				bdto.setMember(member);
				
				bdto.setWriteday(rs.getString("writeday"));
				bdto.setViewcount(rs.getInt("viewcount"));
				
				////////////////////////////////////////////////
				// 리스트에 담기
				
				boardList.add(bdto);
				
			} // end of while(rs.next())
			
		} catch (SQLException e) {
				e.printStackTrace();
		} finally {
			close();
		}
		
		return boardList;
		
	} // end of public List<BoardDTO> boardList()


	

	// == 글내용보기 == //
	// 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	// 다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.
	@Override
	public BoardDTO viewContents(Map<String, String> paraMap) {
		
		BoardDTO bdto = null;
		
		// 글제목, 글내용, 작성자명, 조회수 보여주기		
		try {
			
			String sql = " SELECT V.subject, V.contents, M.name, V.viewcount, V.fk_userid "
					   + " FROM "
					   + " ( "
					   + " SELECT subject "
					   + "      , contents "
					   + "      , viewcount "
					   + "      , fk_userid "
					   + " FROM tbl_board  "
					   + " WHERE boardno = ? "
					   + " ) V JOIN tbl_member M "
					   + " ON V.fk_userid = M.userid "; 		
			
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, paraMap.get("boardno"));
						
			rs = pstmt.executeQuery();	// SQL문 실행
			
			if(rs.next()) {
				bdto = new BoardDTO();
				
				bdto.setSubject(rs.getString("subject"));
				bdto.setContents(rs.getString("contents"));
				
				MemberDTO member = new MemberDTO();
				member.setName(rs.getString("name"));
				bdto.setMember(member);
				
				bdto.setViewcount(rs.getInt("viewcount"));	// DB에 올림
				
				// 로그인한 사용자가 다른 사용자의 글을 조회할 경우에만 조회수 1이 증가한다.
				if( !(paraMap.get("login_userid").equals(rs.getString("fk_userid"))) ) {
					
					sql = " update tbl_board set viewcount = viewcount + 1 "
						+ " where boardno = ? ";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("boardno"));
					
					int n = pstmt.executeUpdate();	// SQL문 실행
					
					bdto.setViewcount(bdto.getViewcount() + 1);	// 다른사람이 조회했을 경우, DB에 올린 viewcount를 1이 증가된 값으로 읽어오기
				}
				
			} // end of if(rs.next())
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 1722) {
				System.out.println(">> [경고] 글번호는 정수만 가능합니다. <<\n");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			close();
		}
		
		return bdto;
		
	} // end of public BoardDTO viewContents(Map<String, String> paraMap)



	// == 조회수 증가는 없고, 단순히 글내용만 보여주기 == //
	@Override
	public BoardDTO viewContents_2(String boardno) {
		
		BoardDTO bdto = null;
			
		try {
			
			String sql = " SELECT subject, contents, fk_userid, boardpasswd "
					   + " FROM tbl_board"
					   + " WHERE boardno = ? ";
					   	
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, boardno);
						
			rs = pstmt.executeQuery();	// SQL문 실행
			
			if(rs.next()) {
				bdto = new BoardDTO();
				
				bdto.setSubject(rs.getString("subject"));
				bdto.setContents(rs.getString("contents"));
				bdto.setFk_userid(rs.getString("fk_userid"));
				bdto.setBoardpasswd(rs.getString("boardpasswd"));
				
			} // end of if(rs.next())
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 1722) {
				System.out.println(">> [경고] 글번호는 정수만 가능합니다. <<\n");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			close();
		}
		
		return bdto;
	} // end of public BoardDTO viewContents_2(String boardno)



	// == 글 수정하기 == //
	@Override
	public int updateBoard(Map<String, String> paraMap) {
		
		int result = 0;
	
		try {
			
			String sql = " update tbl_board set subject = ?, contents = ? "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("subject"));
			pstmt.setString(2, paraMap.get("contents"));
			pstmt.setString(3, paraMap.get("boardno"));
		
			result = pstmt.executeUpdate();	// sql문의 실행
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 1722) {
				System.out.println(">>[경고] 글번호는 정수로만 입력하세요!! << \n");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			
			close();	// 자원반납하기
		}
		
		return result;
	
	} // end of public int updateBoard(Map<String, String> paraMap)



	// == 글 삭제하기 == //
	@Override
	public int deleteBoard(String boardno) {
		
		int result = 0;
		
		try {
			
			String sql = " delete from tbl_board "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
		
			result = pstmt.executeUpdate();	// sql문의 실행
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 1722) {
				System.out.println(">>[경고] 글번호는 정수로만 입력하세요!! << \n");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			
			close();	// 자원반납하기
		}
		
		return result;
	}



	// == 댓글 쓰기 == //
	@Override
	public int writeComment(CommentDTO cmtdto) {
		
		int result = 0;
		
		// Transaciton 처리를 위해서 수동 commit 으로 전환시킨다.
		try {
			
			conn.setAutoCommit(false);
			
			String sql = " insert into tbl_comment(commentno, fk_boardno, fk_userid, contents) "
					   + " values(seq_comment.nextval, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, cmtdto.getFk_boardno());
			pstmt.setString(2, cmtdto.getFk_userid());
			pstmt.setString(3, cmtdto.getContents());
			
			int n1 = pstmt.executeUpdate();	// sql문의 실행
			
			if(n1 == 1) {	// tbl_comment 테이블에 insert 가 성공되었다면
				sql = " update tbl_member set point = point + 5 "
					+ " where userid = ? ";
	
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, cmtdto.getFk_userid());
				
				int n2 = pstmt.executeUpdate();	// sql문의 실행
				
				if(n2 == 1) {	// tbl_comment 테이블에 update 가 성공되었다면
					conn.commit();	// 둘다 성공했으므로 커밋해주기
					result = 1;
					
				} // end of if(n2 == 1)
				
			} // end of if(n1 == 1)
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 2291) {	// fk제약 위배시(없는 게시글번호 예(33539) 적을 시)
				/*
	              오류 보고 -
	              ORA-02291: 무결성 제약조건(JDBC_USER.FK_TBL_COMMENT_FK_BOARDNO)이 위배되었습니다- 부모 키가 없습니다
	            */
				
				System.out.println(">>[경고] 입력하신 원글번호 "+ cmtdto.getFk_boardno() +"는 게시글에 존재하지 않습니다.");
				result = -1;
			}		
			else if(e.getErrorCode() == 2290) {	// 체크제약(포인트는 30점을 넘을 수 없다)
				System.out.println(">> 아이디 "+ cmtdto.getFk_userid() +"님의 포인트는 30을 초과할 수 없기 때문에 오류발생 하였습니다. <<\n");
				try {
					conn.rollback();	// 롤백을 해준다.
					result = -1;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}	
				
			}
			else {
				e.printStackTrace();
			}
			
			try {
				conn.rollback();	// 롤백을 해준다.
				result = -1;
			} catch (SQLException e2) { }
			
		} finally {
			
			try {
				conn.setAutoCommit(true);	// 수동 commit 을 자동 commit으로 복원 시킨다.
				
			} catch (SQLException e2) { }		
			
			close();	// 자원반납하기
		}
	
		return result;
		
	} // end of public int writeComment(CommentDTO cmtdto)



	// 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한 tbl_comment 테이블과 tbl_member 테이블을 JOIN 해서 보여준다.)
	@Override
	public List<CommentDTO> commentList(String boardno) {
		
		List<CommentDTO> commentList = new ArrayList<>();
		
		try {
			
			String sql = " SELECT C.contents, M.name, to_char(C.writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					   + " FROM "
					   + " ( "
					   + "    select * "
					   + "    from tbl_comment "
					   + "    where fk_boardno = ? "
					   + " ) C JOIN tbl_member M "
					   + " ON C.fk_userid = M.userid "
					   + " ORDER BY C.commentno desc ";
					   	
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, boardno);
						
			rs = pstmt.executeQuery();	// SQL문 실행
			
			while(rs.next()) {
				CommentDTO cmtdto = new CommentDTO();
				
				cmtdto.setContents(rs.getString("contents"));
				
				MemberDTO mdto = new MemberDTO();
				mdto.setName(rs.getString("name"));
				cmtdto.setMember(mdto);
				
				cmtdto.setWriteday(rs.getString("writeday"));
				
				commentList.add(cmtdto);
				
			} // end of while(rs.next())
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	
		return commentList;
		
	} // end of public List<CommentDTO> commentList(String boardno)



	
	// 최근 1주일내에 작성된 게시글만 DB에서 가져오기
	@Override
	public Map<String, Integer> statistics_by_Week() {
		
		Map<String, Integer> resultMap = new HashMap<>();
		 
		try {
			
			String sql = " select count(*) AS TOTAL "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 6, 1, 0 )) AS PREVIOUS6 "
				    + "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 5, 1, 0 )) AS PREVIOUS5 "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 4, 1, 0 )) AS PREVIOUS4 "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 3, 1, 0 )) AS PREVIOUS3 "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 2, 1, 0 )) AS PREVIOUS2 "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 1, 1, 0 )) AS PREVIOUS1 "
					+ "  , SUM(decode( to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd'), 0, 1, 0 )) AS TODAY "
					+ " from tbl_board "
					+ " where to_date(to_char(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd') - to_date(to_char(writeday, 'yyyy-mm-dd'), 'yyyy-mm-dd') < 7 ";
					   	
			pstmt = conn.prepareStatement(sql);			
				
			rs = pstmt.executeQuery();	// SQL문 실행
			
			rs.next();
			
			resultMap.put("TOTAL", rs.getInt("TOTAL"));
			resultMap.put("PREVIOUS6", rs.getInt("PREVIOUS6"));
			resultMap.put("PREVIOUS5", rs.getInt("PREVIOUS5"));
			resultMap.put("PREVIOUS4", rs.getInt("PREVIOUS4"));
			resultMap.put("PREVIOUS3", rs.getInt("PREVIOUS3"));
			resultMap.put("PREVIOUS2", rs.getInt("PREVIOUS2"));
			resultMap.put("PREVIOUS1", rs.getInt("PREVIOUS1"));
			resultMap.put("TODAY", rs.getInt("TODAY"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return resultMap;
		
	} // end of public Map<String, Integer> statistics_by_Week()



	// 이번달 일자별 게시글 작성건수 보기
	@Override
	public List<Map<String, String>> statistics_by_CurrentMonth() {
		
		List<Map<String, String>> mapList = new ArrayList<>();
		
		try {
			
			String sql = " select decode( grouping(to_char(writeday, 'yyyy-mm-dd')), 0, to_char(writeday, 'yyyy-mm-dd'), 1, '전체' ) AS WRITEDAY "
					   + "     , count(*) AS CNT "
					   + " from tbl_board "
					   + " where to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm') "
					   + " group by rollup(to_char(writeday, 'yyyy-mm-dd')) ";
					
			pstmt = conn.prepareStatement(sql);			
				
			rs = pstmt.executeQuery();	// SQL문 실행
			
			while(rs.next()) {
				
				Map<String, String> map = new HashMap<>();
				map.put("WRITEDAY", rs.getString("WRITEDAY"));
				
				// map.put("CNT", String.valueOf(rs.getInt("CNT")));
				// 또는
				map.put("CNT", rs.getString("CNT"));
				
				mapList.add(map);
				
			} // end of while(rs.next())
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return mapList;
		
	} // end of public List<Map<String, String>> statistics_by_CurrentMonth()

	
	
	
	
	
	
	
	
	
	
	
}
