package jdbc.day01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DML_insert_PreparedStatement_01 {

	public static void main(String[] args) {
		
		Connection conn = null;	
		// Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바의 객체이다. Connection conn 가 오라클 데이터베이스 서버
		
		PreparedStatement pstmt = null;	
		// PreparedStatement pstmt 은 Connection conn(연결한 DB서버)에 전송할 SQL문(편지)을 전송(전달)을 해주는 객체(우편배달부)이다.
		
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
			System.out.print("▶ 글쓴이 : ");
			String name = sc.nextLine();
			
			System.out.print("▶ 글내용 : ");
			String msg = sc.nextLine();
			
			/*
			 	String sql = " insert into tbl_memo(no, name, msg)"
					   	   + " values(seq_memo.nextval, '"+name+"', '"+msg+"')";
				// 위와 같이 변수의 값을 직접 SQL문에 대입시켜버리면, 외부에서 볼 때 입력한 데이터값이 보여지므로 보안상 위험하다.
				// 그래서 아래처럼 위치홀더(?)를 사용합니다.!!!!
			*/
			
			String sql = " insert into tbl_memo(no, name, msg) "
				   	   + " values(seq_memo.nextval, ?, ?) ";		
			// ? : "위치홀더" 라고 부른다. 숫자, 날짜, 문자 관계없이 무조건 ? 넣기
			// sql문 뒤에 ;을 넣으면 오류이다.!! java.sql.SQLSyntaxErrorException: ORA-00933: SQL 명령어가 올바르게 종료되지 않았습니다
			
			
			// >>> 4. 연결한 오라클서버(conn)에 SQL문(편지)을 전달할 객체 PreparedStatement 객체(우편배달부) 생성하기  <<<
			pstmt = conn.prepareStatement(sql);			
			// 맵핑
			pstmt.setString(1, name);	// 1 은 String sql 에서 첫번째 위치홀더(?)를 말한다. 첫번째 위치홀더(?)에 name 을 넣어준다.
			pstmt.setString(2, msg);	// 2 는 String sql 에서 두번째 위치홀더(?)를 말한다. 두번째 위치홀더(?)에 msg 를 넣어준다.
			// 데이터 값인 경우 위치홀더(?) 사용, 컬럼이나 테이블은 x -> 변수처리
			
			
			System.out.println("확인용 sql => " + sql);
			
			
			
			// >>> 5. PreparedStatement 객체(우편배달부)는 작성된 SQL문(편지) 오라클 서버에 보내서 실행이 되도록 해야 한다.  <<<
			int n = pstmt.executeUpdate();		// SQL문 실행
			
			/*
            .executeUpdate(); 은 SQL문이 DML문(insert, update, delete, merge) 이거나 
                              	SQL문이 DDL문(create, drop, alter, truncate) 일 경우에 사용된다. 
                  
            SQL문이 DML문이라면 return 되어지는 값은 적용되어진 행의 개수를 리턴시켜준다.
            예를 들어, insert into ... 하면 1 개행이 입력되므로 리턴값은 1 이 나온다. 
                   	update ... 할 경우에 update 할 대상의 행의 개수가 5 이라면 리턴값은 5 가 나온다. 
                   	delete ... 할 경우에 delete 되어질 대상의 행의 개수가 3 이라면 리턴값은 3 이 나온다.
                  
            SQL문이 DDL문이라면 return 되어지는 값은 무조건 0 이 리턴된다.
              
            .executeQuery(); 은 SQL문이 DQL(select)일 경우에 사용된다.  
            */
			
			System.out.println("확인용 n => " + n);
			
			if(n == 1) {
				
				String yn = "";
				
				do {
					//////////////////////반복시작///////////////////////
					System.out.print("▶ 정말로 입력하시겠습니까?[Y/N] : ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						conn.commit();	// 커밋
						System.out.println(">> 데이터 입력 성공!! <<");
						
					}
					else if("n".equalsIgnoreCase(yn)) {
						conn.rollback();	// 롤백
						System.out.println(">> 데이터 입력 취소!! <<");	
					}
					else {
						System.out.println(">> Y 또는 N 만 입력하세요.!! <<\n");
					}
					//////////////////////반복끝///////////////////////////
				} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));	// y 나 n 이라면 빠져나오고 아니면 반복
				
			}
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			// >>> 6. 사용하였던 자원을 반납하기 <<<
			// 반납의 순서는 생성순의 역순으로 한다.
			try {
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
