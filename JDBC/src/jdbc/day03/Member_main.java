package jdbc.day03;

import java.util.Scanner;

public class Member_main {

	public static void main(String[] args) {
		
		MemberCtrl mctrl = new MemberCtrl();
		Scanner sc = new Scanner(System.in);
		
		mctrl.menu_Start(sc);
		
		sc.close();
		System.out.println("\n~~~~ 프로그램 종료 ~~~~");

	} // end of main()

}
