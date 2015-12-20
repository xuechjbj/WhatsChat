package xue.apps.chat.domain;

import java.util.ArrayList;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSessionListener implements HttpSessionListener {
   
	private static final Logger logger = LoggerFactory.getLogger(WebSessionListener.class);
	
   //Notification that a session was created.
   @Override
   public void sessionCreated(HttpSessionEvent httpSessionCreatedEvent) {
	   
	   String id = httpSessionCreatedEvent.getSession().getId();
	   logger.info("XUE:sessionCreated="+id);
   } 
    
   //Notification that a session is about to be invalidated.
   @Override
   public void sessionDestroyed(HttpSessionEvent httpSessionDestroyedEvent) {
	   String sid = httpSessionDestroyedEvent.getSession().getId();
	   logger.info("XUE:sessionDestroyed="+sid);
	   //LoginUsersManager.removeSession(sid);;
	   
	   LoginUser loginUser = LoginUsersManager.getLoginUserBySid(sid);
	   if(loginUser != null){
		   loginUser.removeSession(sid);
	   }
   }

}