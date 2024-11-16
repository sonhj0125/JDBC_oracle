package jdbc.day02;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/*
	== HR에서 예전에 생성해두었던 pcd_tbl_member_test1_insert 프로시저를 사용해본다. ==
	
	=== tbl_member_test1 테이블에 insert 할 수 있는 요일명과 시간을 제한해 두겠습니다. ===
    
    tbl_member_test1 테이블에 insert 할 수 있는 요일명은 월,화,수,목,금 만 가능하며
    또한 월,화,수,목,금 중에 오후 2시 부터 오후 5시 이전까지만(오후 5시 정각은 안돼요) insert 가 가능하도록 하고자 한다.
    만약에 insert 가 불가한 요일명(토,일)이거나 불가한 시간대에 insert 를 시도하면 
    '영업시간(월~금 14:00 ~ 16:59:59 까지) 아니므로 입력불가함!!' 이라는 오류메시지가 뜨도록 한다.
    
    create or replace procedure pcd_tbl_member_test1_insert
    (p_userid   IN  tbl_member_test1.userid%type
    ,p_passwd   IN  tbl_member_test1.passwd%type
    ,p_name     IN  tbl_member_test1.name%type)
    is
      v_passwd_length  number(2);
      v_ch             varchar2(1);
      v_flag_alphabet  number(1) := 0;
      v_flag_number    number(1) := 0;
      v_flag_special   number(1) := 0;
      
      error_insert     exception; 
      error_dayTime    exception;
    begin
       
       -- 입력(insert)이 불가한 요일명과 시간대를 알아봅니다. --
       if( to_char(sysdate, 'd') in('1','7') OR 
           to_number(to_char(sysdate, 'hh24')) < 14 OR
           to_number(to_char(sysdate, 'hh24')) > 16 ) then 
           raise error_dayTime;
       else --  입력(insert)이 가능한 요일명과 시간대 이라면 암호를 검사하겠다.
       
           v_passwd_length := length(p_passwd);
           
           if( v_passwd_length < 5 or v_passwd_length > 20 ) then
               raise error_insert; -- 사용자가 정의하는 예외절(exception)을 구동시켜라.
           else
               for i in 1..v_passwd_length loop
                   v_ch := substr(p_passwd, i, 1);
                   
                   if(v_ch between 'A' and 'Z') OR (v_ch between 'a' and 'z') then -- 영문자 이라면 
                        v_flag_alphabet := 1;
                   elsif(v_ch between '0' and '9') then -- 숫자 이라면
                        v_flag_number := 1;
                   else -- 특수문자이라면      
                        v_flag_special := 1;
                   end if;
                   
               end loop; -- end of for loop
               
               if(v_flag_alphabet * v_flag_number * v_flag_special = 1) then 
                  insert into tbl_member_test1(userid, passwd, name) values(p_userid, p_passwd, p_name);
               else
                  raise error_insert; -- 사용자가 정의하는 예외절(exception)을 구동시켜라.
               end if;
               
           end if;
       
       end if;
       
       exception 
          when error_dayTime then 
               raise_application_error(-20003, '>> 영업시간(월~금 14:00 ~ 16:59:59 까지)이 아니므로 입력불가함!! <<'); 
          
          when error_insert then 
               raise_application_error(-20002, '>> 암호는 최소 5글자 이상이면서 영문자 및 숫자 및 특수기호가 혼합되어져야 합니다. <<'); 
               
    end pcd_tbl_member_test1_insert;
 
*/

public class Procedure_insert_sqlexception_CallableStatement_04 {

	public static void main(String[] args) {
		
		Connection conn = null;
		// Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바의 객체이다. 
  
		CallableStatement cstmt = null;
		// CallableStatement cstmt 은 Connection conn(연결한 DB 서버)에 존재하는 Procedure 를 호출해주는 객체(우편배달부)이다. 
		
		String userid = "";
		
		try {
			
			// >>> 1. 오라클 드라이버 로딩 <<<
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "HR", "gclass");
			
			
			// >>> 3. Connection conn 객체를 사용하여 prepareCall() 메소드를 호출함으로써
	        //        CallableStatement cstmt 객체를 생성한다.
	        //        즉, 우편배달부(택배기사) 객체 만들기
			cstmt = conn.prepareCall("{call pcd_tbl_member_test1_insert(?,?,?)}");
			
		/*
            오라클 서버에 생성한 프로시저 pcd_tbl_member_test1_insert 의 
            매개변수 갯수가 3개 이므로 ? 를 3개 준다.
           
            프로시저의 IN mode 로 되어진 파라미터에 값을 넣어줄때는 
            cstmt.setXXX() 메소드를 사용한다. 
            
         */
			
			Scanner sc = new Scanner(System.in);
			System.out.print("▶ 아이디 : ");
			userid = sc.nextLine();

			System.out.print("▶ 비밀번호 : ");
			String passwd = sc.nextLine();
			
			System.out.print("▶ 성명 : ");
			String name = sc.nextLine();
	
			cstmt.setString(1, userid);	// 숫자 1 은 프로시저 파라미터중 첫번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(2, passwd);	// 숫자 2 은 프로시저 파라미터중 두번째 파라미터인 IN 모드의 ? 를 말한다.
			cstmt.setString(3, name);	// 숫자 3 은 프로시저 파라미터중 세번째 파라미터인 IN 모드의 ? 를 말한다.
			
			
			// >>> 4. CallableStatement cstmt 객체를 사용하여 오라클의 프로시저 실행하기  <<<
			int n = cstmt.executeUpdate();	// 오라클 서버에게 해당 프로시저를 실행하라는 것.
			// 프로시저의 실행은 2가지가 존재. cstmt.executeUpdate(); 또는 cstmt.execute(); 이다.
				
			if(n == 1) {
				System.out.println(">>> 데이터 입력 성공!! <<<");
			}
			
			sc.close();
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			// e.printStackTrace();
			
			if(e.getErrorCode() == 20002 || e.getErrorCode() == 20003) {
				System.out.println(e.getMessage());
			}
			else if(e.getErrorCode() == 1) {
				System.out.println(">>> 아이디 " + userid + "은 현재 사용중이므로 다른 아이디로 입력하세요!! <<<");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			// >>> 5. 사용하였던 자원을 반납하기 <<<
			// 반납의 순서는 생성순의 역순으로 한다.
			try {
				
				if(cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
				
				if(conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		}
		
		System.out.println("~~~ 프로그램 종료 ~~~");
		
		
	} // end of main()

}
