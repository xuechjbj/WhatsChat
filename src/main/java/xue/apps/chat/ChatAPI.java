package xue.apps.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import xue.apps.chat.db.Account;
import xue.apps.chat.db.ChatDBService;
import xue.apps.chat.db.ChatInvitation;
import xue.apps.chat.domain.ChatHistory;
import xue.apps.chat.domain.ChatMessageFromClient;
import xue.apps.chat.domain.ChatReadUpdateFromClient;
import xue.apps.chat.domain.ChatRequestFromClient;
import xue.apps.chat.domain.ChatSession;
import xue.apps.chat.domain.ChatSessionSummary;
import xue.apps.chat.domain.LoginUser;
import xue.apps.chat.domain.LoginUsersManager;
import xue.apps.chat.login.UserDetailsAdapter;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
public class ChatAPI {

	private static final Logger logger = LoggerFactory.getLogger(ChatAPI.class);

	@RequestMapping(value = "/launchchat", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> launchChatSession(@RequestBody ChatRequestFromClient chatrequest) {
		//logger.info(chatrequest.getChatsessionid() + ":" + chatrequest.getSender());

		long cid = Long.parseLong(chatrequest.getChatsessionid());

		String uidstr = chatrequest.getSender();

		long uid = Long.parseLong(uidstr);
		LoginUser user = LoginUsersManager.getLoginByUserId(uid);
		if (user == null)
			return null;

		ChatSession cs = user.getChatSessionById(cid);
		if (cs == null) {
			return null;
		}
		ChatHistory chatHistory = cs.getChatHistory();

		ObjectMapper mapper = new ObjectMapper();
		String chatHistoryJson = "";
		try {
			chatHistoryJson = mapper.writeValueAsString(chatHistory);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(chatHistoryJson);

		return new ResponseEntity<String>(chatHistoryJson, HttpStatus.OK);
	}

	@RequestMapping(value = "/postchat", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public boolean handleChatMessage(@RequestBody ChatMessageFromClient chat) {
		//logger.info(chat.getChatId() + ":" + chat.getSender() + ":" + chat.getMessage());

		long cid = Long.parseLong(chat.getChatId());
		String uidstr = chat.getSender();

		long uid = Long.parseLong(uidstr);

		LoginUser user = LoginUsersManager.getLoginByUserId(uid);
		if (user == null)
			return false;

		ChatSession cs = user.getChatSessionById(cid);
		if (cs == null) {
			return false;
		}

		return cs.postChatMessage(uid, chat.getMessage());
	}

	@RequestMapping(value = "/updateReadStatus", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public boolean updateReadStatus(@RequestBody ChatReadUpdateFromClient reader) {

		long cid = reader.getChatId();

		long uid = reader.getUid();

		// long uid = Long.parseLong(uidstr);

		LoginUser user = LoginUsersManager.getLoginByUserId(uid);
		if (user == null)
			return false;

		ChatSession cs = user.getChatSessionById(cid);
		if (cs == null) {
			return false;
		}

		cs.msgReadDone(uid, reader.getMsgId());

		return true;
	}
	
	@RequestMapping(value = "/listening/{loginid}")
	@ResponseBody
	public SseEmitter subscribeListening(@PathVariable("loginid") String loginid, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// String uidstr = Utils.decodeUserId(loginid);
		long uid = Long.parseLong(loginid);

		LoginUser loginUser = LoginUsersManager.getLoginByUserId(uid);
		if (loginUser != null) {
			logger.info("XUE:" + loginUser.getName() + " is subscribing Listening");

			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/event-stream");
			response.setCharacterEncoding("utf-8");

			String sid = RequestContextHolder.currentRequestAttributes().getSessionId();
			SseEmitter emitter = new SseEmitter((long) (1000 * 60 * 60 * 24));
			loginUser.addListeningPort(sid, emitter);
			loginUser.notifyOtherPersons(true);

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					checkIfNewInvites(loginUser.getId());
				}

			}, 1000);

			return emitter;
		}
		return null;
	}

	@RequestMapping(value = "/inviteChat", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public int handelInviteChat(@RequestBody final MultiValueMap<String, String> requestParamMap) {
		String username = requestParamMap.getFirst("invitedname");
		// String password = requestParamMap.getFirst("password");
		String greeting = requestParamMap.getFirst("greeting");

		UserDetailsAdapter initiator = getCurrentAuthorizedUser();
		if(initiator == null){
			return -1;
		}
		
		if (ChatDBService.get().getCountOfAccountByName(username) != 1) {
			logger.error(initiator.getUsername() + " invitation error: no account " + username);
			return -2;
		}

		Account invitee = ChatDBService.get().findAccountByName(username).get(0);
		long inviteeId = invitee.getId();
		
		long initiatorId = initiator.getUid();
		
		if(inviteeId == initiatorId){
			return -3;
		}
		
		if (ChatDBService.get().getCountOfInvitations(initiatorId, inviteeId) != 0) {
			logger.error(initiator.getUsername() + " invitation error: duplicate inviting " + username);
			return -4;
		}
		// Account account =
		// ChatDBService.get().findAccountByName(username).get(0);

		ChatInvitation invitation = ChatDBService.get().saveInvite(initiatorId, inviteeId, greeting);

		LoginUser login = LoginUsersManager.getLoginByUserId(inviteeId);
		if (login != null) {
			login.notifyInvite(invitation);
		}

		return 0;
	}

	private void checkIfNewInvites(long id) {
		List<ChatInvitation> invites = ChatDBService.get().getInvitationsByInviteeIdWithoutResp(id);
		List<ChatInvitation> invites1 = ChatDBService.get().getInvitationsByInitiatorId(id);
		
		if (invites == null || invites.size() == 0) {
			if (invites1 == null || invites1.size() == 0) {
				return;
			}
			invites = invites1;
		}
		else if(invites1 != null && invites1.size() > 0){
			invites.addAll(invites1);
		}
		
		LoginUser login = LoginUsersManager.getLoginByUserId(id);
		if (login != null) {
			for (int i = 0; i < invites.size(); i++) {
				login.notifyInvite(invites.get(i));
			}
		}
	}

	@RequestMapping(value = "/invitesResp", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public int handelInviteReso(@RequestBody final MultiValueMap<String, String> requestParamMap) {
		String invite = requestParamMap.getFirst("inviteId");
		String response = requestParamMap.getFirst("response");

		UserDetailsAdapter user = getCurrentAuthorizedUser();
		if(user == null){
			return -1;
		}
		
		long inviteId = Long.parseLong(invite);
		ChatInvitation invt = ChatDBService.get().getInvitationsById(inviteId);
		if (invt == null)
			return -2;

		int resp = Integer.parseInt(response);
		
		if(resp == 0){
			ChatDBService.get().deleteInvite(invt);
			return 0;
		}
		
		if ((resp == 1 || resp == -1) && user.getUid() == invt.getInvitee()) {
			long newChatId = 0;
			invt.setResponse(resp);
			ChatDBService.get().saveInvite(invt);
			
			if (resp == 1) {
				newChatId = ChatDBService.get().saveSession(invt.getInitiator(), invt.getInvitee());
				//ChatDBService.get().removeInvite()
			}

			LoginUser login = LoginUsersManager.getLoginByUserId(invt.getInitiator());
			if (login != null) {
				if (resp == 1) {
					ChatSession cs = new ChatSession(newChatId, login);
					login.addNewSession(cs);

					ChatSessionSummary css = new ChatSessionSummary(user.getUid(), cs);
					login.pushSessionUpdate(css);
				}
				logger.info("The invitee "+user.getDispname() + 
						" accept invitation from initiator "+login.getName());
				login.notifyInvite(invt);
			}
		}
		
		else if(resp == -2 && user.getUid() == invt.getInitiator()){
			ChatDBService.get().deleteInvite(invt);
			LoginUser login = LoginUsersManager.getLoginByUserId(invt.getInitiator());
			if (login != null) {
				logger.info("Initiator "+user.getDispname()+ " canceled the invite to "+
			        login.getName());
				login.notifyInvite(invt);
			}
		}
		
		else {
			logger.error("Illeagal invite response from "+user.getDispname()+" id="+user.getUid());
			return -3;
		}

		return 0;
	}
	
	@RequestMapping(value = "/contact/{userid}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> getUserContact(@PathVariable("userid") long userid) {

		String sid = RequestContextHolder.currentRequestAttributes().getSessionId();

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!(userDetails instanceof UserDetailsAdapter)) {
			logger.error("It is NOT UserDetailsAdapter");
			return null;
		}

		UserDetailsAdapter userinfo = (UserDetailsAdapter) userDetails;

		LoginUser user = LoginUsersManager.getLoginByUserId(userinfo.getUid());
		if (user == null) {
			user = new LoginUser(userinfo.getUid());
			//user.setName(userinfo.getDispname());
			LoginUsersManager.addLoginUser(user);
			user.loadChatSession();
		}

		logger.info("XUE: Show the Contact page for " + user.getName() + ", sid=" + sid);

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
		return new ResponseEntity<String>(contactsJson, HttpStatus.OK);
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
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST, 
			consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseBody
	public boolean userSignup(@RequestBody final MultiValueMap<String, String> requestParamMap) {
		
		String username = requestParamMap.getFirst("username");
		String password = requestParamMap.getFirst("password");
		String email = requestParamMap.getFirst("email");
		String url = "";
		
		if(ChatDBService.get().createNewAccount(username, password, email, url) == null){
			return false;
		}
		
		return true;
	}
	
	protected UserDetailsAdapter getCurrentAuthorizedUser(){
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!(userDetails instanceof UserDetailsAdapter)) {
			logger.error("It is NOT UserDetailsAdapter");
			return null;
		}

		return (UserDetailsAdapter) userDetails;
	}
}
