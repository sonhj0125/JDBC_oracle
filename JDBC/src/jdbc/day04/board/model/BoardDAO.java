package jdbc.day04.board.model;

import java.util.List;
import java.util.Map;

import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;

public interface BoardDAO {

	int write(BoardDTO bdto);	// 게시판 글쓰기
	// === Transaction 처리 ===
    //     (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
    //     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
    //     insert 와 update 가 모두 성공해야만 commit 할 것이다.)

	List<BoardDTO> boardList();		// 글목록보기

	BoardDTO viewContents(Map<String, String> paraMap);	// 글내용보기
	// 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	// 다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.

	
	BoardDTO viewContents_2(String boardno);	// 조회수 증가는 없고, 단순히 글내용만 보여주기

	
	
	int updateBoard(Map<String, String> paraMap);	// 글수정하기

	
	
	int deleteBoard(String boardno);	// 글삭제하기

	
	
	int writeComment(CommentDTO cmtdto);	// 댓글쓰기

	
	// 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한 tbl_comment 테이블과 tbl_member 테이블을 JOIN 해서 보여준다.)
	List<CommentDTO> commentList(String boardno);

	
	
	// 최근 1주일내에 작성된 게시글만 DB에서 가져오기
	Map<String, Integer> statistics_by_Week();

	
	// 이번달 일자별 게시글 작성건수 보기
	List<Map<String, String>> statistics_by_CurrentMonth(); 

	
	
	
}
