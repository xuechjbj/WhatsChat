package xue.apps.chat.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import xue.apps.chat.domain.ChatSessionSummary;
import xue.apps.chat.domain.LoginUser;
import xue.apps.chat.domain.LoginUsersManager;


public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);
	
    public LoginSuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) throws ServletException, IOException {
        
    	UserDetailsAdapter user = null;
    	
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated()) {
			user = (UserDetailsAdapter)(authentication.getPrincipal());
		}
    	
		if(user == null){
			throw new BadCredentialsException("Invalid User authentication");
		}
		
    	HttpSession session = request.getSession();
    	session.setMaxInactiveInterval(60 * 60 * 24); //1 day
        
    	CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrf != null) {
			Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
			String token = csrf.getToken();
			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie("XSRF-TOKEN", token);
				cookie.setPath("/");
				response.addCookie(cookie);
				logger.info("Login csrf="+token);
			}			
		}
 
        
		logger.info("XUE: user:"+user.getDispname()+" (id:"+user.getUid()+") login now");
        //response.sendRedirect(request.getContextPath()+"/contact/"+user.getUid());
		
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		writer.print(getUserContact(user));
        writer.flush();
    }

    protected String getUserContact(UserDetailsAdapter userinfo) {

		LoginUser user = LoginUsersManager.getLoginByUserId(userinfo.getUid());
		if (user == null) {
			user = new LoginUser(userinfo.getUid());
			//user.setName(userinfo.getDispname());
			LoginUsersManager.addLoginUser(user);
			user.loadChatSession();
		}

		logger.info("XUE: Show the Contact page for " + user.getName());

		ChatSessionInfo csi = new ChatSessionInfo();
		csi.contacts = user.getChatSessionsSummary();
		csi.loginid = user.getId();
		csi.username = user.getName();
		

		ObjectMapper mapper = new ObjectMapper();
		String contactsJson = "";
		try {
			contactsJson = mapper.writeValueAsString(csi);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		logger.info(contactsJson);

		// logger.info("XUE: Show the Contact count = " + contacts.size());
		return contactsJson;
	}
	
	class ChatSessionInfo{
		long loginid;
		String username;
		ArrayList<ChatSessionSummary> contacts;
		
		public long getLoginid() {
			return loginid;
		}
		public String getUsername(){
		    return username;
		}
		
		public ArrayList<ChatSessionSummary> getContacts() {
			return contacts;
		}
	}
}
