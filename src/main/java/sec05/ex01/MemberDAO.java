package sec05.ex01;

import java.nio.file.spi.FileSystemProvider;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;




public class MemberDAO {
	

	
	private Connection conn;
//	private Statement stmt;  //정적 SQL 문을 실행하고 생성된 결과를 반환하는 데 사용되는 개체입니다.
	
	// Statement 인터페이스가 DBMS에 전달하는 SQL문은 단순한 문자열이므로 DBMS는 이 문자열을 DBMS가 이해할 수 있도록 컴파일하고 실행함
	// 반면에 PreparedStatement 인터페이스는 컴파일된 SQL문을 DBMS에 전달하여 성능을 향상시킴
	// 또한 실행하려는 SQL문에 '?'를 넣을 수 있습니다. Statement보다 SQL문 작성하기가 더 간단함.
	private PreparedStatement pstmt;
	
	private DataSource dataFactory;  //연결을 위한 팩토리입니다. DriverManager 기능의 대안 
	                                 //JNDI(Java Naming and Directory) API를 기반으로 하는 이름 지정 서비스에 등록됩니다.
	
	//생
	public MemberDAO() {
		System.out.println("MemberDAO 객체 생성");
		
		 try {
				//1. 연결을 하기 위한 컨텍스트(pro07)인식을 위한 Context 객체
				Context ctx=new InitialContext();
				Context envContext=(Context) ctx.lookup("java:/comp/env");
				dataFactory=(DataSource) envContext.lookup("jdbc/oracle");
				////이 코드는 JNDI를 사용하여 "java:/comp/env" 컨텍스트에서 "jdbc/oracle"라는 이름으로 등록된 
				//데이터 소스를 얻어와서 dataFactory 변수에 할당하는 것입니다. 
				//이렇게 얻어온 데이터 소스를 사용하여 데이터베이스 연결을 설정하고 관리할 수 있습니다.
						
				
		 }catch(Exception e) {
			 System.out.println("MemberDAO 객체에서 DB 연결 관련 에러");
		 }
		
	}
	
	//메
	
	//연결 후 회원 목록을 가져와라는 메서드
	public List<MemberVO> listMembers(){
		
		List<MemberVO> list = new ArrayList<MemberVO>();
		
		try {		
			
			//connDB();
			//2. 연결
			conn=dataFactory.getConnection();
			
			//3. 연결 객체가(conn) sql을 돌려야함, sql을 돌리기 위해서는 sql 관련 문구를 처리하는 Statement Interface 사용
			
			
			//4. SQL 작성
			
			String query ="select * from T_MEMBER";
			System.out.println("실행한 sql : " + query);
			//ResultSet rs =stmt.executeQuery(query);  //ResultSet : DB에서 가져온 데이터를 읽음
			pstmt=conn.prepareStatement(query); //매개변수화된 SQL 문을 데이터베이스로 전송하기 위한 객체를 생성합니다.
			
			ResultSet rs=pstmt.executeQuery();
			
			//rs.next(); //Moves the cursor forward one row from its current position
			
			while(rs.next()) {
				// 결과테이블(ResultSet)의 칼럼 인식 후 결과값 가져오기
				String id=rs.getString("id");
				System.out.println("나온 id" + id);
				String pwd=rs.getString("pwd");
				String name=rs.getString("name");
				String email=rs.getString("email");
				Date joinDate=rs.getDate("joinDate");
				
				//MemberVO 객체를 만들어서 그 객체에 resultset의 결과를 set해야함
				MemberVO vo =new MemberVO();
				vo.setId(id);
				vo.setPwd(pwd);
				vo.setName(name);
				vo.setEmail(email);
				vo.setJoinDate(joinDate);
				
				list.add(vo);
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			
		}catch(Exception e) {
			System.out.println("연결시 에러");
		}
		
		
		return list;
		
	}
	
	
	
	//회원을 추가하라는 메서드
	public void addMember(MemberVO memberVO) {
		try {
			conn=dataFactory.getConnection();
			
			String id=memberVO.getId();
			String pwd = memberVO.getPwd();
			String name = memberVO.getName();
			String email = memberVO.getEmail();
			
			String query="insert into t_member(id,pwd,name,email) VALUES(?,?,?,?)";
			System.out.println("회원 추가 sql문 : " + query);
			
			pstmt=conn.prepareStatement(query);
			
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			
			
			pstmt.executeUpdate();  // 추가시 executeUpdate
			
			pstmt.close();
		} catch (Exception e) {
			System.out.println("회원추가시 에러");
		}
	}
	
	
	
	//회원 삭제 코드
	public void delMember(String id) {
		System.out.println("삭제하고자 하는 id + "  +  id);
		try {
			conn = dataFactory.getConnection();
			String query = "delete from t_member" + " where id=?";
			System.out.println("prepareStatememt:" + query);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//회원 가입 여부 확인 코드
	public boolean isExisted(MemberVO memberVO){
		boolean result = false;
		String id = memberVO.getId();
		String pwd = memberVO.getPwd();
		try {
			
			
			
			conn = dataFactory.getConnection();
			
			String query="select   decode(count(*), 1, 'true','false' ) as result from t_member where id=? and pwd=?";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			ResultSet rs = pstmt.executeQuery();
			
			rs.next();
			
			result= Boolean.parseBoolean(rs.getString("result"));
			System.out.println("result=" + result);
			
			
		}catch(Exception e) {
			System.out.println("회원 가입 여부 조회시 에러");
		}
		
		
		
		return result;
	}

}
