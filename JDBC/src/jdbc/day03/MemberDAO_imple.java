package jdbc.day03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberDAO_imple implements MemberDAO {

	// field, attribute, property, 속성
	private Connection conn;
	private PreparedStatement pstmt;	// null 이 초기값
	private ResultSet rs;
	
	
	
	// method, operation, 기능
	
	// === 자원 반납을 해주는 메소드 === //
	private void close() {
		
		try {
			
			if(rs != null) 		{rs.close(); 	rs = null;}
			if(pstmt != null) 	{pstmt.close(); pstmt = null;}
			if(conn != null) 	{conn.close(); 	conn = null;}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	} // end of private void close()
	
	
	// === 회원가입(insert) 메소드 === //
	@Override
	public int memberRegister(MemberDTO member) {
		
		int result = 0;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "gclass");
			
			String sql = " insert into tbl_member(userseq, userid, passwd, name, mobile) "
				   	   + " values(userseq.nextval, ?, ?, ?, ?) ";		
			
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, member.getUserid());	
			pstmt.setString(2, member.getPasswd());	
			pstmt.setString(3, member.getName());	
			pstmt.setString(4, member.getMobile());	
			
			result = pstmt.executeUpdate();		// SQL문 실행
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 1) {	// 유니크 제약(userid)에 중복되어지면,
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			close();
		}
		
		return result;
	} // end of public int memberRegister(MemberDTO member)

	
	
	// === 로그인처리(select) 메소드 === //
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO member = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "gclass");
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') AS registerday "
					   + " from tbl_member "
					   + " where status = 1 and userid = ? and passwd = ? ";		
			
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, paraMap.get("userid"));	
			pstmt.setString(2, paraMap.get("passwd"));	
			
			rs = pstmt.executeQuery();	// SQL문 실행
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt("userseq"));
				member.setUserid(rs.getString("userid"));	
				member.setName(rs.getString("name"));
				member.setMobile(rs.getString("mobile"));
				member.setPoint(rs.getInt("point"));
				member.setRegisterday(rs.getString("registerday"));		// alias 명으로 적기
				
			}
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 1) {	// 유니크 제약(userid)에 중복되어지면,
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
			else {
				e.printStackTrace();
			}
			
		} finally {
			close();
		}
		return member;
	} // end of public MemberDTO login(Map<String, String> paraMap)


	
	// === 회원탈퇴하기(delete) 메소드 === //
	@Override
	public int memberDelete(int userseq) {
		
		int result = 0;
			
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "gclass");
			
			String sql = " update tbl_member set status = 0"
				   	   + " where userseq = ? ";		
			
			pstmt = conn.prepareStatement(sql);			
			pstmt.setInt(1, userseq);	
			
			result = pstmt.executeUpdate();		// SQL문 실행
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			close();		// 자원 반납
		}
			
		return result;
		
	} // end of public int memberDelete(int userseq)

	
	// === 모든 회원 조회(select) 하기 메소드 === //
	@Override
	public List<MemberDTO> showAllMember() {
		
		List<MemberDTO> memberList = new ArrayList<>();
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "gclass");
			
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') AS registerday "
					   + " 	, status "
					   + " from tbl_member "
					   + " order by userseq asc ";		
			
			pstmt = conn.prepareStatement(sql);			
				
			rs = pstmt.executeQuery();	// SQL문 실행
			
			while(rs.next()) {
				MemberDTO member = new MemberDTO();
				
				member.setUserseq(rs.getInt("userseq"));
				member.setUserid(rs.getString("userid"));	
				member.setName(rs.getString("name"));
				member.setMobile(rs.getString("mobile"));
				member.setPoint(rs.getInt("point"));
				member.setRegisterday(rs.getString("registerday"));		// alias 명으로 적기
				member.setStatus(rs.getInt("status"));
				
				memberList.add(member);
			} // end of while(rs.next())
			
		} catch (ClassNotFoundException e) {
			
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		} catch (SQLException e) {
			
				e.printStackTrace();
			
		} finally {
			close();
		}
		
		return memberList;
		
	} // end of public List<MemberDTO> showAllMember()
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
