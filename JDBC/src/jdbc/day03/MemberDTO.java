package jdbc.day03;

// === DTO(Data Transfer Object, 데이터전송(운반)객체 )
// 쉽게 말해서 DTO는 테이블의 1개 행(ROW)을 말한다. *** 암기
// 어떤 테이블에 데이터를 insert 하고자 할때 DTO에 담아서 보낸다.
// 또한 어떤 테이블에서 데이터를 select 하고자 할때도 DTO에 담아서 읽어온다.

public class MemberDTO {
	
	// field, attribute, property, 속성
	private int userseq; 	// 회원번호
	private String userid;	// 회원아이디
	private String passwd;	// 회원 비밀번호
	private String name;	// 회원명
	private String mobile;	// 연락처
	private int point; 	// 포인트
	private String registerday;	// 가입일자
	private int status;			// status 컬럼의 값이 1이라면, 가입된 상태, 0이면 탈퇴
	
	
	
	// method, operation, 기능
	public int getUserseq() {
		return userseq;
	}
	
	public void setUserseq(int userseq) {
		this.userseq = userseq;
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public int getPoint() {
		return point;
	}
	
	public void setPoint(int point) {
		this.point = point;
	}
	
	public String getRegisterday() {
		return registerday;
	}
	
	public void setRegisterday(String registerday) {
		this.registerday = registerday;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	/////////////////////////////////////////////////////////
	
	@Override		
	public String toString() {
		return "=== 나의 정보 ===\n"
			 + "◇ 성명 : " + name + "\n"
			 + "◇ 연락처  : " + mobile + "\n"
			 + "◇ 포인트 : " + point + "\n"
			 + "◇ 가입일자 : " + registerday.substring(0,10);
	}
	
	
	
	
	
	
	
	
	
	
}
