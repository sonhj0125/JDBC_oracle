package jdbc.day03;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberCtrl {
	
	// field, attribute, property, 속성
	MemberDAO mdao = new MemberDAO_imple();		// 다형성 MemberDAO_imple mdao= new MemberDAO_imple(); 
												// 대신 사용(interface에 있는 int memberRegister(MemberDTO member); 회원가입만 보여주기위해)
	
	
	// method, operation, 기능
	// **** 시작메뉴를 보여주는 기능(메소드) **** //
	
	public void menu_Start(Scanner sc) {
		
		MemberDTO member = null;
		String s_Choice = "";
		boolean isLogin = false;
		
		do {
			/////////////////////////////////////////////////////////////////////
			if(isLogin == false) {	// 로그인 하기 전
			
				do {
					/////////////////////////////////////////////////////////	
					System.out.println("\n>>> ---- 시작메뉴 ---- <<<\n"
									+ "1. 회원가입		2. 로그인		3. 프로그램종료	\n"
									+ "---------------------------------------------\n");
					System.out.print("▷ 메뉴번호 선택 : ");
					s_Choice = sc.nextLine();
					
					switch (s_Choice) {
					case "1":	// 회원가입
						memberRegister(sc);
						break;
						
					case "2":	// 로그인
						member = login(sc);		// 로그인 시도하기
						if(member != null) {
							isLogin = true;
						}
						break;
						
					case "3":	// 프로그램종료
						
						return;	 // menu_Start(Scanner sc) 메소드 종료함.
			
					default:
						System.out.println(">>> 메뉴에 없는 번호입니다. 다시 선택하세요!! <<<");
						break;
					} // end of switch (s_Choice)
					///////////////////////////////////////////////////////////
					
				} while(!("2".equals(s_Choice) && isLogin == true));
			
			} // end of if(isLogin == false)
			
			
			if(isLogin == true)	{ 	// 로그인을 한 후
				
				String admin_menu = "admin".equals(member.getUserid())?"4.모든회원조회":"";
				
				System.out.println("\n>>> ---- 시작메뉴 ["+ member.getName() +"님 로그인중..] ---- <<<\n"
							+ "1. 로그아웃		 2. 회원탈퇴하기		3. 나의정보보기		"+admin_menu+"\n"
							+ "---------------------------------------------------------------\n");
				
				System.out.print("▷ 메뉴번호 선택 : ");
				
				s_Choice = sc.nextLine();
				switch (s_Choice) {
					case "1":		// 로그아웃
						member = null;
						isLogin = false;
						System.out.println(">>> 로그아웃 되었습니다. <<<\n");
						break;
		
					case "2":		// 회원탈퇴하기	
						String yn = "";
						
						do {
							//////////////////////////////////////////////////////
							System.out.print("▷ 정말로 탈퇴하시겠습니까? [Y/N] : ");
							yn = sc.nextLine();
							
							if("y".equalsIgnoreCase(yn)) {
								int n = mdao.memberDelete(member.getUserseq());
								if(n == 1) {
								member = null;		// 로그아웃 처리 후 회원탈퇴
								isLogin = false;
								System.out.println(">>> 회원탈퇴를 성공했습니다. <<<");
								}
							} 
							else if("n".equalsIgnoreCase(yn)) {
								System.out.println(">>> 회원탈퇴를 취소하셨습니다. <<<");
							}
							else {
								System.out.println(">> y 또는 n을 눌러주세요.");	
							}
							//////////////////////////////////////////////////////
							
						} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
								
						break;
						
					case "3":		// 나의정보보기
					//	System.out.println(member.toString()); 또는
						System.out.println(member);
						break;
						
					case "4":		// admin으로 로그인 시 모든회원조회, 일반회원으로 로그인 시 메뉴에 없는 번호 문구 표시
						if("admin".equals(member.getUserid())) {
							showAllMember();
							
						}
						else {
							System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
						}
						
						break;
						
					default:
						
						break;
					
				} // end of switch (s_Choice)
				
			} // end of if(isLogin == true)
			///////////////////////////////////////////////////////////////////////
		
		} while(true);
		
	} // end of public void menu_Start(Scanner sc) 

	

	// *** 회원가입을 해주는 메소드 *** //
	private void memberRegister(Scanner sc) {	
		
		System.out.println("\n >>> ---- 회원가입 ---- <<<");
		
		System.out.print("1. 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("2. 비밀번호 : ");
		String passwd = sc.nextLine();
		
		System.out.print("3. 회원명 : ");
		String name = sc.nextLine();
		
		System.out.print("4. 연락처(휴대폰) : ");
		String mobile = sc.nextLine();
		
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);
		member.setPasswd(passwd);
		member.setName(name);
		member.setMobile(mobile);
		
		int n = mdao.memberRegister(member);
		
		if(n == 1) {
			System.out.println("\n>>> 회원가입을 축하드립니다. <<<");
		}
		else {
			System.out.println(">>> 회원가입이 실패되었습니다. <<<");
		}
		
	} // end of private void memberRegister(Scanner sc)
	
	
	
	// *** 로그인을 해주는 메소드 *** //
	private MemberDTO login(Scanner sc) {
		
		MemberDTO member = null;
		
		System.out.println("\n >>> --- 로그인 --- <<<");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("▷ 비밀번호 : ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >>> 로그인 성공!! <<< \n");
		}
		else {
			System.out.println("\n >>> 로그인 실패ㅜㅜ <<< \n");
		}
	
		return member;
	} // end of private MemberDTO login(Scanner sc)
	
	

	// *** 모든 회원을 조회해주는 메소드 *** //
	private void showAllMember() {
		
		List<MemberDTO> memberList = mdao.showAllMember();		// 복수 행 불러오기 : List
		
		if(memberList.size() > 0) {
			System.out.println("-".repeat(50));
			System.out.println("회원번호  아이디  회원명  연락처  포인트  가입일자  가입상태");
			System.out.println("-".repeat(50));
			
			StringBuilder sb = new StringBuilder();
			
			for(MemberDTO member : memberList) {
				
				String status = (member.getStatus() == 1) ? "가입중" : "탈퇴";
				
				sb.append(member.getUserseq() + "  " +
						  member.getUserid() + "  " +
						  member.getName() + "  " +
						  member.getMobile() + "  " +
						  member.getPoint() + "  " +
						  member.getRegisterday() + "  " +
						  status + "\n");
				
			} // end of for(MemberDTO member : memberList)
			
			System.out.println(sb.toString());
			
		}
		else {
			System.out.println(">> 가입된 회원이 1명도 없습니다. <<");
		}
		
	} // end of private void showAllMember()


	
	
	

}
