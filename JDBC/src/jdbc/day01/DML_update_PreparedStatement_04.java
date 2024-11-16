package jdbc.day01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DML_update_PreparedStatement_04 {

	public static void main(String[] args) {
		
		Connection conn = null;	
		// Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바의 객체이다. Connection conn 가 오라클 데이터베이스 서버
		
		PreparedStatement pstmt = null;	
		// PreparedStatement pstmt 은 Connection conn(연결한 DB서버)에 전송할 SQL문(편지)을 전송(전달)을 해주는 객체(우편배달부)이다.
		
		ResultSet rs = null;
		// ResultSet rs 은 select 되어진 결과물이 저장되어지는 곳.
		
		Scanner sc = new Scanner(System.in);
		
		
		try {
			
			// >>> 1. 오라클 드라이버 로딩 <<<
			/*
	        === OracleDriver(오라클 드라이버)의 역할 ===
	        1). OracleDriver 를 메모리에 로딩시켜준다.
	        2). OracleDriver 객체를 생성해준다.
	        3). OracleDriver 객체를 DriverManager에 등록시켜준다.
	            --> DriverManager 는 여러 드라이버들을 Vector 에 저장하여 관리해주는 클래스이다.	
			*/
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			
			// >>> 2. 어떤 오라클 서버에 연결할지? <<<
			System.out.print("▶ 연결할 오라클 서버의 IP 주소 : ");
			String ip = sc.nextLine();	// 127.0.0.1
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe", "JDBC_USER", "gclass");
			// 비번이 틀릴경우 java.sql.SQLException: ORA-01017: 사용자명/비밀번호가 부적합, 로그온할 수 없습니다.
			
			// === Connection conn 에서 DML의 기본값(적지 않으면)은 auto commit 이다. === //
			// === Connection conn 에서 DML의 기본값인 auto commit 을 수동 commit 으로 전환한다. === //
				conn.setAutoCommit(false);	// 수동 commit 으로 전환됨.
			//	conn.setAutoCommit(true);	// 자동 commit 으로 전환됨.
			
				
				
			// >>> 3. SQL문(편지)의 작성 <<<
			String sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					   + " from tbl_memo "
					   + " order by no desc ";
			// 반드시 "" 안에 SQL문을 적을 때 좌우로 공백을 넣어 한칸씩 띄워쓸 것.
			// SQL문 뒤에 ;을 넣으면 오류이다.!!
			
			
			// >>> 4. 연결한 오라클서버(conn)에 SQL문(편지)을 전달할 객체 PreparedStatement 객체(우편배달부) 생성하기  <<<
			pstmt = conn.prepareStatement(sql);			
			
			
			
			// >>> 5. PreparedStatement 객체(우편배달부)는 작성된 SQL문(편지) 오라클 서버에 보내서 실행이 되도록 해야 한다.  <<<
			rs = pstmt.executeQuery();		// SQL문 실행(select)
			
			/* 
            	SQL문이 DQL(select)이므로 .executeQuery(); 을 사용해야 한다.
            */
			
			System.out.println("-".repeat(70));
			System.out.println("글번호\t글쓴이\t글내용\t작성일자");
			System.out.println("-".repeat(70));
			
			/*
            === 인터페이스 ResultSet 의 주요한 메소드 ===
            1. next()  : select 되어진 결과물에서 커서를 다음으로 옮겨주는 것         리턴타입은 boolean 
            2. first() : select 되어진 결과물에서 커서를 가장 처음으로 옮겨주는 것     리턴타입은 boolean
            3. last()  : select 되어진 결과물에서 커서를 가장 마지막으로 옮겨주는 것    리턴타입은 boolean
            
            == 커서가 위치한 행에서 컬럼의 값을 읽어들이는 메소드 ==
            getInt(숫자) : 컬럼의 타입이 숫자이면서 정수로 읽어들이때
                          파라미터 숫자는 컬럼의 위치값 
                          
            getInt(문자) : 컬럼의 타입이 숫자이면서 정수로 읽어들이때
                          파라미터 문자는 컬럼명 또는 alias명 
                           
            getLong(숫자) : 컬럼의 타입이 숫자이면서 정수로 읽어들이때
                           파라미터 숫자는 컬럼의 위치값 
                          
            getLong(문자) : 컬럼의 타입이 숫자이면서 정수로 읽어들이때
                           파라미터 문자는 컬럼명 또는 alias명                
            
            getFloat(숫자) : 컬럼의 타입이 숫자이면서 실수로 읽어들이때
                            파라미터 숫자는 컬럼의 위치값 
                          
            getFloat(문자) : 컬럼의 타입이 숫자이면서 실수로 읽어들이때
                            파라미터 문자는 컬럼명 또는 alias명 
                               
            getDouble(숫자) : 컬럼의 타입이 숫자이면서 실수로 읽어들이때
                             파라미터 숫자는 컬럼의 위치값 
                          
            getDouble(문자) : 컬럼의 타입이 숫자이면서 실수로 읽어들이때
                             파라미터 문자는 컬럼명 또는 alias명    
                                 
            getString(숫자) : 컬럼의 타입이 문자열로 읽어들이때
                             파라미터 숫자는 컬럼의 위치값 
                          
            getString(문자) : 컬럼의 타입이 문자열로 읽어들이때
                             파라미터 문자는 컬럼명 또는 alias명                                                        
         */
			
			StringBuilder sb = new StringBuilder();
			
			while( rs.next() ) {
				/*
	               rs.next() 는 select 되어진 결과물에서 위치(행의 위치)를 다음으로 옮긴 후 
	               행이 존재하면 true 를 리턴해주고, 행이 없으면 false 를 리턴해주는 메소드이다.
	            */
				
				int no = rs.getInt("no");	// "no" 은 select 해온 컬럼명이다.
		//또는	int no = rs.getInt("NO");	// 대소문자 상관없음. "NO" 은 select 해온 컬럼명이다.
		//또는	int no = rs.getInt(1);		// 1 은 select 해온 컬럼명의 위치값으로써 1번째 컬럼을 의미한다.
				
				String name = rs.getString("name");		// "name" 은 select 해온 컬럼명이다.
		//또는	String name = rs.getString("2");		// 2 는 select 해온 컬럼명의 위치값으로써 2번째 컬럼을 의미한다.
				
				String msg = rs.getString("msg");		// "msg" 은 select 해온 컬럼명이다.
		//또는	String msg = rs.getString("3");			// 3 은 select 해온 컬럼명의 위치값으로써 3번째 컬럼을 의미한다.
				
				String writeday = rs.getString("writeday");		// "writeday" 은 select 해온 컬럼명이다.
		//또는	String writeday = rs.getString("4");			// 4 는 select 해온 컬럼명의 위치값으로써 4번째 컬럼을 의미한다.
				
				sb.append(no);
				sb.append("\t" + name);
				sb.append("\t" + msg);
				sb.append("\t" + writeday + "\n");
				
				
			} // end of while( rs.next() )
			
			System.out.println(sb.toString());
			
			////////////////////////////////////////////////////////////
			
			String no = "";			
			
			
			while(true) {
				
				System.out.print("▶ 수정할 글번호 : ");
				no = sc.nextLine();
				
				sql = " select name, msg "
						+ " from tbl_memo "
						+ " where no = to_number(?) ";
						
				// 새로운 sql문 이므로 새로운 우편배달부 생성해야함.
				pstmt.close();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, no);
				
				rs.close();
		
				try {
					rs = pstmt.executeQuery();	// sql문을 실행	
					
					break;
				} catch(SQLException e) {
					if(e.getErrorCode() == 1722) {
						System.out.println(">> [경고] 수정할 글번호는 숫자만 가능합니다.!!\n");
					}
					
				} // end of try_catch(SQLException e)

			} // end of while(true)
			
			if(rs.next()) {		// 수정해야할 글번호가 존재하는 경우 -> select 되어진 컬럼이 1개(no가 primary key)라서 if(rs.next()) 사용가능
				
				String before_name = rs.getString(1);
				String before_msg = rs.getString(2);
				
				System.out.println("\n==== 수정하기 전 내용 ====");
				System.out.println("\n▶ 글쓴이 : " + before_name);
				System.out.println("\n▶ 글내용 : " + before_msg);
				
				
				System.out.println("\n==== 글 수정하기 ====");
				System.out.print("▶ 글쓴이(변경하지 않으려면 그냥 엔터) : ");
				String new_name = sc.nextLine();
				/*
				 	"".isBlank(); 는 true
				 	"    ".isBlank() 는 true
				 	
				 	"".isEmpty(); 는 true
				 	"    ".isEmpty() 는 false
				 */
				
				if(new_name.isEmpty()) {	// 그냥 엔터를 눌렀다면,
					new_name = before_name;
				}
				
				System.out.print("▶ 글내용(변경하지 않으려면 그냥 엔터) : ");
				String new_msg = sc.nextLine();
				
				if(new_msg.isEmpty()) {	// 그냥 엔터를 눌렀다면,
					new_msg = before_msg;
				}
				
				sql = " update tbl_memo set name = ?, msg = ?, updateday = sysdate "
					+ " where no = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, new_name);
				pstmt.setString(2, new_msg);
				pstmt.setString(3, no);
				
				// sql문을 실행
				int n = pstmt.executeUpdate();
				
				if(n == 1) {
					// n == 1 이라는 것은 update 구문이 성공되었다는 것을 의미함
					
					String yn = "";
					
					do {
						//////////////////////반복시작///////////////////////
						System.out.print("▶ 정말로 수정하시겠습니까?[Y/N] : ");
						yn = sc.nextLine();
						
						if("y".equalsIgnoreCase(yn)) {
							conn.commit();	// 커밋
							System.out.println(">> 데이터 수정 성공!! <<");
							
							System.out.println("\n=== 수정한 후 내용 ===");
							
							sql = " select name, msg "
								+ " from tbl_memo "
								+ " where no = ? ";
									
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, no);
							
							// sql문 실행
							rs = pstmt.executeQuery();
							
							// !!!!!!!! 중요 !!!!!!!!! // 
							rs.next();
							
							String name = rs.getString(1);
							String msg = rs.getString(2);
							
							System.out.println("▶ 글쓴이 : " + name);
							System.out.println("▶ 글내용 : " + msg);
							
							
						}
						else if("n".equalsIgnoreCase(yn)) {
							conn.rollback();	// 롤백
							System.out.println(">> 데이터 수정 취소!! <<");	
						}
						else {
							System.out.println(">> Y 또는 N 만 입력하세요.!! <<\n");
						}
						//////////////////////반복끝///////////////////////////
					} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));	// y 나 n 이라면 빠져나오고 아니면 반복
					
				}
						
			}
			else {	// 수정해야할 글번호가 DB에 존재하지 않는 경우
				System.out.println(">>> 글번호 "+ no +" 은 존재하지 않습니다.");
			}
			
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			// >>> 6. 사용하였던 자원을 반납하기 <<<
			// 반납의 순서는 생성순의 역순으로 한다.
			try {
				
				if(rs != null) {
					rs.close();
					rs = null;
				}
				
				if(pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				
				if(conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
		sc.close();
		System.out.println("~~~ 프로그램 종료 ~~~");
		
	} // end of main()

}
