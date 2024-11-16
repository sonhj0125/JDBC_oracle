package jdbc.day04.board.model;

import java.util.List;
import java.util.Map;

import jdbc.day04.board.domain.MemberDTO;

public interface MemberDAO {
	
	// 회원가입(insert) 메소드
	int memberRegister(MemberDTO member);

	// 로그인처리(select) 메소드
	MemberDTO login(Map<String, String> paraMap);

	// 모든 회원 조회(select) 메소드
	List<MemberDTO> showAllMember(String sortChoice);
	
	
	
	
	
	
}
