package jdbc.day04.board.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jdbc.day04.board.dbconnection.MyDBConnection;
import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;
import jdbc.day04.board.domain.MemberDTO;
import jdbc.day04.board.model.*;


public class Controller {
	
	// field
	MemberDAO mdao = new MemberDAO_imple();
	BoardDAO bdao = new BoardDAO_imple();
	
	
	
	
	// method
	// *** 시작메뉴 *** //
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
						member = login(sc);		
						if(member != null) {
							isLogin = true;
						}
						break;
						
					case "3":	// 프로그램종료
						MyDBConnection.closeConnection();	// Connection 객체 자원반납
						return;	 // menu_Start(Scanner sc) 메소드 종료함.
			
					default:
						System.out.println(">>> 메뉴에 없는 번호입니다. 다시 선택하세요!! <<<");
						break;
					} // end of switch (s_Choice)
					///////////////////////////////////////////////////////////
					
				} while(!("2".equals(s_Choice) && isLogin == true));
			
			} // end of if(isLogin == false)
			
			
			if(isLogin == true)	{ 	// 로그인을 한 후
				
				String admin_menu = "admin".equals(member.getUserid())?"4. 관리자전용(모든회원정보조회)":"";
				
				System.out.println("\n>>> ---- 시작메뉴 ["+ member.getName() +"님 로그인중..] ---- <<<\n"
							+ "1. 로그아웃		 2. 나의정보보기		3. 게시판가기		"+admin_menu+"\n"
							+ "---------------------------------------------------------------\n");
				
				System.out.print("▷ 메뉴번호 선택 : ");
				s_Choice = sc.nextLine();
				
				switch (s_Choice) {
					case "1":		// 로그아웃
						member = null;
						isLogin = false;
						System.out.println(">>> 로그아웃 되었습니다. <<<\n");
						break;
		
					case "2":		// 나의 정보보기
						System.out.println(member);
						break;
						
					case "3":		// 게시판가기
						menu_Board(member, sc);	// 게시판 메뉴에 들어간다.
						
						break;
						
					case "4":		// 관리자로 로그인 시 모든회원조회
						if("admin".equals(member.getUserid())) {
							
							System.out.println("▷ 정렬 [1. 회원명의 오름차순		/ 2. 회원명의 내림차순		/\n"
											  +"	   3. 가입일자의 오름차순		/ 4. 가입일자의 내림차순]");
							System.out.print("정렬번호 선택 : ");				
							String sortChoice = sc.nextLine();
							
							showAllMember(sortChoice);
							
						}
						else {
							System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
						}
						
						break;
						
					default:
						System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
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
	private void showAllMember(String sortChoice) {
		
		if("1".equals(sortChoice) || "2".equals(sortChoice) ||
		   "3".equals(sortChoice) || "4".equals(sortChoice) ) {
			
			List<MemberDTO> memberList = mdao.showAllMember(sortChoice);		// 복수 행 불러오기 : List
			
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
			
		}
		
		else {
			System.out.println(">> 정렬에 없는 번호입니다.!! <<");
		}
		
		
	} // end of private void showAllMember(String sortChoice)



	
	// *** 게시판 메뉴 *** //
	private void menu_Board(MemberDTO member, Scanner sc) {
		
		String s_menuNo = "";
		
		do {
		//////////////////////////////////////////////////////////
			System.out.println("------------------- 게시판메뉴 ["+ member.getName() +"님 로그인중..] -------------------\n"
					+ "1.글목록보기     2.글내용보기    3.글쓰기      4.댓글쓰기 \n"
					+ "5.글수정하기     6.글삭제하기    7.최근1주일간 일자별 게시글 작성건수 \n"
					+ "8.이번달 일자별 게시글 작성건수    9.나가기 \n"
					+ "----------------------------------------------------------------");
			
			System.out.print("▷ 메뉴번호 선택 : ");
			s_menuNo = sc.nextLine();
		
			switch (s_menuNo) {
			case "1":	// 글목록보기
				boardList();
				break;
				
			case "2":	// 글내용보기
				viewContents(member.getUserid(), sc);
				break;
				
			case "3":	// 글쓰기
				int n = write(member, sc);
				
				// 글쓰고 포인트가 올라가야함. 안올라가면 전부 롤백이 필요. -> Transaction(트랜잭션)
				
				if(n == 1) {	// 글쓰기 성공함
					System.out.println(">> 글쓰기 성공!! <<");
				}
				else if(n == 0) {	// 글쓰기 취소함
					System.out.println(">> 글쓰기 취소!! <<");
				}
				else if(n == -1){	// 글쓰기 오류가 난 경우
					System.out.println(">> 글쓰기 실패!! <<");
				}
				
				break;
		
			case "4":	// 댓글쓰기
				n = writeComment(member, sc);
				
				// 댓글쓰고 포인트가 올라가야함. 안올라가면 전부 롤백이 필요. -> Transaction(트랜잭션)
				
				if(n == 1) {	// 댓글쓰기 성공함
					System.out.println(">> 댓글쓰기 성공!! <<");
				}
				else if(n == 0) {	// 댓글쓰기 취소함
					System.out.println(">> 댓글쓰기 취소!! <<");
				}
				else if(n == -1){	// 댓글쓰기 오류가 난 경우 (체크제약 30점 포인트)
					System.out.println(">> 댓글쓰기 실패!! <<");
				}
				
				break;
		
			case "5":	// 글수정하기
				updateBoard(member.getUserid(), sc);
				break;
				
			case "6":	// 글삭제하기
				deleteBoard(member.getUserid(), sc);
				break;

				
			case "7":	// 최근1주일간 일자별 게시글 작성건수
				
				if("admin".equals(member.getUserid())) {
					
					statistics_by_Week();
					
				}
				else {
					System.out.println(">>[경고] 관리자만 접근 가능한 메뉴입니다. <<");
				}
				
				break;
				
				
			case "8":	// 이번달 일자별 게시글 작성건수
				
				if("admin".equals(member.getUserid())) {
					
					statistics_by_CurrentMonth();
				}
				else {
					System.out.println(">>[경고] 관리자만 접근 가능한 메뉴입니다. <<");
				}
				
				break;
				
				
			case "9":	// 나가기
				
				break;
		
			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
				
			} // end of switch (s_menuNo)
			//////////////////////////////////////////////////////////
			
		} while(!("9".equals(s_menuNo)));
		
	} // end of private void menu_Board(MemberDTO member, Scanner sc)





	// *** 1. 글목록보기 해주는 메소드 *** //
	private void boardList() {
		
		List<BoardDTO> boardList = bdao.boardList();
		
		if(boardList.size() > 0) {	// 게시글이 존재하는 경우
			System.out.println("\n-------------------------- [게시글 목록] ----------------------------");
	        System.out.println("글번호\t글제목\t\t작성자\t작성일자\t\t조회수");
	        System.out.println("---------------------------------------------------------------------"); 
	        
	        StringBuilder sb = new StringBuilder();
	        
	        for(int i=0; i<boardList.size(); i++) {
	        	sb.append(boardList.get(i).boardListOne()+"\n");
	        	// 꺼내온 boardList.get(i)는 BoardDTO 이다.
	        	
	        } // end of for(int i=0; i<boardList.size(); i++)
	        
	        System.out.println(sb.toString());
	        
		} 
		else {	// 게시글이 존재하지 않는 경우
			System.out.println(">> 글 목록이 없습니다. << \n");
		}
		
	} // end of private void boardList()

	
	
	

	// *** 2. 글내용보기 해주는 메소드 *** //
	// == 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	//    다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.
	private void viewContents(String login_userid, Scanner sc) {
		
		System.out.println("\n>>> 글내용 보기 <<<");
		
		System.out.print("▷ 글번호 : ");
		String boardno = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("boardno", boardno);
		paraMap.put("login_userid", login_userid);
		
		BoardDTO bdto = bdao.viewContents(paraMap);
		
		if(bdto != null) {
			// 존재하는 글번호를 입력한 경우
			System.out.println("[글제목] " + bdto.getSubject()+ "\n"
							 + "[글내용] " + bdto.getContents() + "\n"
							 + "[작성자] " + bdto.getMember().getName()+ "\n"
							 + "[조회수] " + bdto.getViewcount());
		}
		else {
			// 존재하지 않는 글번호 또는 글번호를 숫자가 아닌 문자로 입력한 경우
			System.out.println(">> 글번호"+ boardno +" 은 글목록에 존재하지 않습니다. <<\n");
		}
		/////////////////////////////////////////////////////////////////////////////
		
		System.out.println("[댓글]\n"+"-".repeat(50));
		
		List<CommentDTO> commentList = bdao.commentList(boardno);
		// 원글에 대한 댓글을 가져오는 것(특정 게시글 글번호에 대한 tbl_comment 테이블과 tbl_member 테이블을 JOIN 해서 보여준다.)
		
		if(commentList.size() > 0) {	// 댓글이 존재하는 원글인 경우
			System.out.println("댓글내용\t\t작성자명\t작성일자");
			System.out.println("-".repeat(50));
			
			StringBuilder sb = new StringBuilder();
			
			for(CommentDTO cmtdto : commentList) {
				sb.append(cmtdto.getContents() + "\t" + cmtdto.getMember().getName() + "\t" + cmtdto.getWriteday() +"\n");		
			} // end of for(CommentDTO cmtdto : commentList)
			
			System.out.println(sb.toString());
			
		}
		else {	// 댓글이 존재하지 않는 원글인 경우
			System.out.println(">> 댓글 내용 없음 << \n");
		}
		
	} // end of private void viewContents(String userid, Scanner sc)
	
	
		
		
	// *** 3. 글쓰기를 해주는 메소드 *** //
	// === Transaction 처리 ===
    //     (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
    //     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
    //     insert 와 update 가 모두 성공해야만 commit 할 것이다.)
	private int write(MemberDTO member, Scanner sc) {
		
		int result = 0;
		
		System.out.println(">>> 글쓰기 <<<");
		
		System.out.println("1. 작성자명 : "+ member.getName());
		
		System.out.print("2. 글제목[최대 100글자] : ");
		String subject = sc.nextLine();
		
		System.out.print("3. 글내용[최대 200글자] : ");
		String contents = sc.nextLine();
		
		System.out.print("4. 글암호[최대 20글자] : ");
		String boardpasswd = sc.nextLine();
		
		BoardDTO bdto = new BoardDTO();
		bdto.setFk_userid(member.getUserid());
		bdto.setSubject(subject);
		bdto.setContents(contents);
		bdto.setBoardpasswd(boardpasswd);
		
		do {
			
			/////////////////////////////////////////////////////
			System.out.print(">> 정말로 글쓰기를 하시겠습니까?[Y/N] => ");
			String yn = sc.nextLine();
			
			if("y".equalsIgnoreCase(yn)) {
				
				int subject_length = subject.length();
				int contents_length = contents.length();
				int boardpasswd_length = boardpasswd.length();
				
				if( (1 <= subject_length && subject_length <= 100) &&
					(1 <= contents_length && contents_length <= 200) &&
					(1 <= boardpasswd_length && boardpasswd_length <= 20)) {
					
					result = bdao.write(bdto);	// 게시판 글쓰기
				}
				else {
					System.out.println(">> 입력한 데이터가 너무 크므로 입력이 불가합니다.!! <<");
				}
				
				break;
			}
			else if("n".equalsIgnoreCase(yn)) {	// 게시판 글쓰기 취소
				
				break;
			}
			else {
				System.out.println(">> Y 또는 N 만 입력하세요!! <<\n");
			}
			/////////////////////////////////////////////////////
		
		} while(true);
		
		return result;
		
	} // end of private int write(MemberDTO member, Scanner sc)




	

	
	// *** 4. 댓글쓰기를 해주는 메소드 *** //
	private int writeComment(MemberDTO member, Scanner sc) {
		
		int result = 0;
		
		System.out.println("\n>>> 댓글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + member.getName());
		
		int fk_boardno = 0;
		
		do {
			
			///////////////////////////////////////////////////////////////
			System.out.print("2. 원글의 글번호 : ");
			String s_fk_boardno = sc.nextLine();	// "똘똘이" 와 같은 문자가 들어오면 안된다.!!!
			
			try {
			
				fk_boardno = Integer.parseInt(s_fk_boardno);
				
				if(fk_boardno < 1) {
					System.out.println(">>[경고] 원글의 글번호는 1이상인 정수로만 입력하셔야 합니다.!! <<\n");
				}
				else {
					break;
				}
			
			} catch (NumberFormatException e) {
				System.out.println(">>[경고] 원글의 글번호는 정수로만 입력하셔야 합니다.!! <<\n");
			}
			//////////////////////////////////////////////////////////////
		
		} while(true);

		String contents = "";
		
		do {
			//////////////////////////////////////////////////////////////
			System.out.print("3. 댓글내용 : ");
			contents = sc.nextLine();
		/*
	        댓글의 내용을 입력할 때 그냥 엔터
	        또는 공백만으로 입력하거나 
	        또는 tbl_comment 테이블의 contents 컬럼의 크기(최대 100글자)보다 더 많은 글자를 입력하는 경우 
	    */
			if( contents.isBlank() ) {	// 그냥 엔터 또는 공백으로 입력한 경우
				System.out.println(">>[경고] 댓글 내용은 필수로 입력하셔야 합니다.!! <<\n");
			}
			else if( contents.length() > 100 ) {
				System.out.println(">>[경고] 댓글 내용은 최대 100글자 이내로 입력하셔야 합니다.!! <<\n");
			}
			else {
				
				break;
			}
			//////////////////////////////////////////////////////////////
		} while(true);
		
		String yn = "";
		
		do {
			//////////////////////////////////////////////////////////////
			System.out.print("▷ 정말로 댓글쓰기를 하시겠습니까?[Y/N] : ");
			yn = sc.nextLine();
			
			if("y".equalsIgnoreCase(yn)) {
				
				CommentDTO cmtdto = new CommentDTO();
				cmtdto.setFk_boardno(fk_boardno);			// 원글의 글번호
				cmtdto.setFk_userid(member.getUserid());	// 작성자 아이디
				cmtdto.setContents(contents);				// 댓글내용
				
				result = bdao.writeComment(cmtdto);
				// 성공 1, 체크제약위반 -1
				
			}
			else if("n".equalsIgnoreCase(yn)) {	// 취소 0
				
			}
			else {
				System.out.println(">>[경고] Y 또는 N만 입력하세요.!! <<\n");
			}
			//////////////////////////////////////////////////////////////
			
		} while ( !("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)) );
		
		return result;
		
	} // end of private int writeComment(MemberDTO member, Scanner sc)



	
	
	
	// *** 5. 글수정 하기를 해주는 메소드 *** //
	private void updateBoard(String login_userid, Scanner sc) {
		
		System.out.println("\n>>> 글 수정하기 <<<");
		
		System.out.print("▷ 수정할 글번호 : ");
		String boardno = sc.nextLine();
		
		BoardDTO bdto = bdao.viewContents_2(boardno);	
		// 조회수 증가는 없고, 단순히 글내용만 보여주기
		
		if(bdto == null) {
			// 수정할 글번호가 글목록에 존재하지 않는 경우
			System.out.println(">> 글번호 "+ boardno +" 은 글목록에 존재하지 않습니다. <<\n");
		}
		else {
			// 수정할 글번호가 글목록에 존재하는 경우
			
			if( !(login_userid.equals(bdto.getFk_userid())) ) {
				// 수정할 글번호가 다른 사용자가 쓴 글인 경우라면
				System.out.println("[경고] 다른 사용자의 글은 수정 불가합니다.!! \n");
			}
			else {
				// 수정할 글번호가 자신이 쓴 글인 경우라면
				System.out.print("▷ 글암호 : ");
				String boardpasswd = sc.nextLine();
				
				if( !(boardpasswd.equals(bdto.getBoardpasswd())) ) {
					// 글암호가 일치하지 않는 경우
					System.out.println("[경고] 입력하신 글암호가 작성시 입력한 글암호와 일치하지 않으므로 수정 불가합니다.!! \n");
				}	
				else {
					// 글암호가 일치하는 경우
					System.out.println("--------------------------------------"); 
	                System.out.println("[수정전 글제목] " + bdto.getSubject());
	                System.out.println("[수정전 글내용] " + bdto.getContents());
	                System.out.println("--------------------------------------");
	                
	                System.out.print("▷ 글제목[최대 100글자, 변경하지 않으려면 엔터] : ");
	                
	                String subject = sc.nextLine();
	                
	                if(subject != null && subject.length() == 0) {
	                	subject = bdto.getSubject();		// 원래 내용을 담음
	                }
	                
	                System.out.print("▷ 글내용[최대 200글자, 변경하지 않으려면 엔터] : ");
	                
	                String contents = sc.nextLine();
	            
	                if(contents != null && contents.length() == 0) {
	                	contents = bdto.getContents();		// 원래 내용을 담음
	                }
	                
	                if( subject.length() > 100 || contents.length() > 200 ) {
	                	System.out.println("[경고] 글제목은 최대 100글자 이며, 글내용은 최대 200글자 이내이어야 합니다. \n");
	                }
	                else {
	                	
	                	String yn = "";
	                	do {
		                	///////////////////////////////////////////////////////////////
		                	System.out.print("▷ 정말로 글을 수정하시겠습니까?[Y/N] : ");
		                	yn = sc.nextLine();
		                	
		                
								if("y".equalsIgnoreCase(yn)) {
									
									Map<String, String> paraMap = new HashMap<>();
				                	
				                	paraMap.put("boardno", boardno);
				                	paraMap.put("subject", subject);
				                	paraMap.put("contents", contents);
				                	
				                	int n = bdao.updateBoard(paraMap);   // 글 수정하기
				                	
				                	if(n==1) {
				                		System.out.println(">> 글수정 성공!! <<\n");
				                	}
				                	else {
				                		System.out.println(">> SQL 구문 오류 발생으로 인해 글수정이 실패되었습니다. << \n");
				                	}
				                	
								}
								else if("n".equalsIgnoreCase(yn)) {
									System.out.println(">> 글수정을 취소하셨습니다. <<\n");
								}
								else {
									System.out.println(">> Y 또는 N만 입력하세요.!! <<\n");
								}
								///////////////////////////////////////////////////////////////
						} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
	                
	                } // 글제목, 글내용 글자수 제한
				} // 글 암호 일치 여부 판단
			} // 사용자 일치 여부 판단
		} // 수정할 글번호 존재 여부 판단
		
	} // end of private void updateBoard(String userid, Scanner sc)





	// *** 6. 글삭제 하기를 해주는 메소드 *** //
	private void deleteBoard(String login_userid, Scanner sc) {
		
		System.out.println("\n>>> 글 삭제하기 <<<");
		
		System.out.print("▷ 삭제할 글번호 : ");
		String boardno = sc.nextLine();
		
		BoardDTO bdto = bdao.viewContents_2(boardno);	
		// 조회수 증가는 없고, 단순히 글내용만 보여주기
		
		if(bdto == null) {
			// 삭제할 글번호가 글목록에 존재하지 않는 경우
			System.out.println(">> 글번호 "+ boardno +" 은 글목록에 존재하지 않습니다. <<\n");
		}
		else {
			// 삭제할 글번호가 글목록에 존재하는 경우
			
			if( !(login_userid.equals(bdto.getFk_userid())) ) {
				// 삭제할 글번호가 다른 사용자가 쓴 글인 경우라면
				System.out.println("[경고] 다른 사용자의 글은 삭제 불가합니다.!! \n");
			}
			else {
				// 삭제할 글번호가 자신이 쓴 글인 경우라면
				System.out.print("▷ 글암호 : ");
				String boardpasswd = sc.nextLine();
				
				if( !(boardpasswd.equals(bdto.getBoardpasswd())) ) {
					// 글암호가 일치하지 않는 경우
					System.out.println("[경고] 입력하신 글암호가 작성시 입력한 글암호와 일치하지 않으므로 삭제 불가합니다.!! \n");
				}	
				else {
					// 글암호가 일치하는 경우
					System.out.println("--------------------------------------"); 
	                System.out.println("[삭제할 글제목] " + bdto.getSubject());
	                System.out.println("[삭제할 글내용] " + bdto.getContents());
	                System.out.println("--------------------------------------");
	                
                	String yn = "";
                	
                	do {
	                	///////////////////////////////////////////////////////////////
	                	System.out.print("▷ 정말로 글을 삭제하시겠습니까?[Y/N] : ");
	                	yn = sc.nextLine();
	                	
							if("y".equalsIgnoreCase(yn)) {
								
			                	int n = bdao.deleteBoard(boardno);   // 글 삭제하기
			                	
			                	if(n==1) {
			                		System.out.println(">> 글삭제 성공!! <<\n");
			                	}
			                	else {
			                		System.out.println(">> SQL 구문 오류 발생으로 인해 글삭제가 실패되었습니다. << \n");
			                	}
			                	
							}
							else if("n".equalsIgnoreCase(yn)) {
								System.out.println(">> 글삭제를 취소하셨습니다. <<\n");
							}
							else {
								System.out.println(">> Y 또는 N만 입력하세요.!! <<\n");
							}
							///////////////////////////////////////////////////////////////
					} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
	                
	                } // 글 암호 일치 여부 판단
				} // 사용자 일치 여부 판단
			} // 수정할 글번호 존재 여부 판단
		
	} // end of private void deleteBoard(String userid, Scanner sc)


	
	
	
	// === 현재일로부터 일수만큼 더하거나 빼서 날짜를 리턴시켜주는 메소드 === //
	private String addDay(int n) {
		
		Calendar currentDate = Calendar.getInstance(); 
		// 현재날짜와 시간을 얻어온다.
		
		currentDate.add(Calendar.DATE, n); 
		// currentDate.add(Calendar.DATE, 1);
	    // ==> currentDate(현재날짜) 에서 두번째 파라미터에 입력해준 숫자(그 단위는 첫번째 파라미터인 것이다. 지금은 Calendar.DATE 이므로 날짜수이다) 만큼 더한다. 
	    // ==> 위의 결과는 currentDate 값은 1일 더한 값으로 변한다. 
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		return dateFormat.format(currentDate.getTime());
		
	} // end of private String addDay(int n)
	
	
	
	
	// *** 7. 최근1주일간 일자별 게시글 작성건수 메소드 *** //
	private void statistics_by_Week() {
		
		System.out.println("\n" + "-".repeat(35) + " [최근 1주일간 일자별 게시글 작성건수] " + "-".repeat(35));
		// 만약 오늘이 2024-03-14 이라면
		// 전체	2024-03-08	2024-03-09	2024-03-10	2024-03-11	2024-03-12	2024-03-13	2024-03-14	
		
		String title = "전체\t";
		
		for(int i =0; i<7; i++) {
			
			title += addDay(i-6) + "    ";		// -6	-5	 -4	  -3	-2	 -1	   0
			
		} // end of for(int i =0; i<7; i++)
		
		System.out.println(title);
		
		System.out.println("-".repeat(100));
		
		// 최근 1주일내에 작성된 게시글만 DB에서 가져온 결과물
		Map<String, Integer> resultMap = bdao.statistics_by_Week();	// select
		// Map 은 1개 행으로 보면 된다. == DTO 와 유사
		
		String result = resultMap.get("TOTAL") + "\t" +
						resultMap.get("PREVIOUS6") + "\t" +
						resultMap.get("PREVIOUS5") + "\t" +
						resultMap.get("PREVIOUS4") + "\t" +
						resultMap.get("PREVIOUS3") + "\t" +
						resultMap.get("PREVIOUS2") + "\t" +
						resultMap.get("PREVIOUS1") + "\t" +
						resultMap.get("TODAY");
		
		System.out.println(result);
		
	} // end of private void statistics_by_Week()
	

	
	

	// *** 8. 이번달 일자별 게시글 작성건수 메소드 *** //
	private void statistics_by_CurrentMonth() {
		
		Calendar currentDate = Calendar.getInstance(); 
		// 현재날짜와 시간을 얻어온다.
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월");
		
		String currentMonth = dateFormat.format(currentDate.getTime());
		
		System.out.println("\n>>> ["+ currentMonth +" 일자별 게시글 작성건수] <<<"); 
		
		List<Map<String, String>> mapList = bdao.statistics_by_CurrentMonth();
		
		if(mapList.size() > 0 ) {
			System.out.println("----------------------");
			System.out.println(" 작성일자\t	작성건수");
			System.out.println("----------------------");
			
			StringBuilder sb = new StringBuilder();
			
			for( Map<String, String> map : mapList ) {
				sb.append(map.get("WRITEDAY") + "\t" + map.get("CNT") + "\n"); 
				
			} // end of for( Map<String, String> map : mapList )
			
			System.out.println(sb.toString());
			
		}
		else {
			System.out.println(">> 게시된 글이 존재하지 않습니다. <<\n");
		}
		
	} // end of private void statistics_by_CurrentMonth()

	
	
	
	
}
