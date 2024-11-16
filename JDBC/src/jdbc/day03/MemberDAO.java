package jdbc.day03;

import java.util.List;
import java.util.Map;

public interface MemberDAO {
	
	// 회원가입(insert) 메소드
	int memberRegister(MemberDTO member);

	// 로그인처리(select) 메소드
	MemberDTO login(Map<String, String> paraMap);

	// 회원탈퇴하기(delete) 메소드
	int memberDelete(int userseq);

	// 모든 회원 조회(select) 메소드
	List<MemberDTO> showAllMember();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
} // end of MemberDTO login(Map<String, String> paraMap)
