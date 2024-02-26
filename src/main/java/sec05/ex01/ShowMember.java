package sec05.ex01;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/show")
public class ShowMember extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doHandle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String id = "", pwd = "";
		Boolean isLogon = false;
		//getSession(true) : 기존의 세션 객체가 존재하면 반환하고, 없으면 새로 생성
		//getSession(false): 기존의 세션 객체가 존재하면 반환하고, 없으면 null을 반환
		HttpSession session = request.getSession(false);
		isLogon = (Boolean) session.getAttribute("isLogon");
		System.out.println("로그온 여부 : " + isLogon);
		
		System.out.println("session : " + session);
		if (session != null) {
			if (isLogon == true) {
				id = (String) session.getAttribute("login.id");
				pwd = (String) session.getAttribute("login.pwd");
				out.print("<html><body>");
				out.print("아이디: " + id + "<br>");
				out.print("비밀번호: " + pwd + "<br>");
				out.print("</body></html>");
			} else {
				response.sendRedirect("login3.html");
			}
		}else {
			response.sendRedirect("login3.html");
		}
	}

}
