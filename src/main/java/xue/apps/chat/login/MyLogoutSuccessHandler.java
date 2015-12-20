package xue.apps.chat.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.util.WebUtils;

public class MyLogoutSuccessHandler implements LogoutSuccessHandler{

	private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		UserDetailsAdapter user = null;
		
		if (authentication != null) {
			user = (UserDetailsAdapter)(authentication.getPrincipal());
			
			logger.info("XUE: user:"+user.getDispname()+" (id:"+user.getUid()+") logout now");
			return;
            
		}
		if ((authentication instanceof AnonymousAuthenticationToken)) {
			logger.error("XUE: AnonymousAuthenticationToken logout now");
		}
	}

}
