package xue.apps.chat.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import xue.apps.chat.db.Account;
import xue.apps.chat.db.ChatDBService;
import xue.apps.chat.db.ChatInvitation;
import xue.apps.chat.db.ChatMessage;

public class LoginUser{

	private static final Logger logger = LoggerFactory.getLogger(LoginUser.class);

	private long mUserId;
	private String mUsername;

	private ArrayList<ChatSession> mChatSessions = new ArrayList<ChatSession>();
	private ArrayList<NotifyClientChannel> mNotiChannel = new ArrayList<NotifyClientChannel>();

	public LoginUser(long id) {
		mUserId = id;

		Account a = ChatDBService.get().findAccountById(id);
		mUsername = a.getDispName();
	}

	public void loadChatSession() {
		logger.info("Load contact for " + mUsername);

		ArrayList<Long> chatList = ChatDBService.get().getChatIdListByUid(mUserId);

		if(chatList == null) return;
		
		for (int i = 0; i < chatList.size(); i++) {
			long cid = chatList.get(i);
			ChatSession cs = new ChatSession(cid, this);

			ArrayList<Long> accounts = ChatDBService.get().getParticipantsByChatId(cid);
			for (int j = 0; j < accounts.size(); j++) {
				Account a = ChatDBService.get().findAccountById(accounts.get(j));
				if (a.getId() != mUserId) {
					ContactPerson person = new ContactPerson(a.getId(), a.getDispName(), a.getPortraitUrl());
					cs.addPerson(person);
				}
			}

			mChatSessions.add(cs);
		}
	}

	public void addNewSession(ChatSession cs){
		mChatSessions.add(cs);
	}
	
	public ChatSession getChatSessionById(long cid) {
		for (int i = 0; i < mChatSessions.size(); i++) {
			if (mChatSessions.get(i).getChatId() == cid)
				return mChatSessions.get(i);
		}
		return null;
	}

	public ArrayList<ChatSessionSummary> getChatSessionsSummary() {
		ArrayList<ChatSessionSummary> l = new ArrayList<ChatSessionSummary>();
		
		mChatSessions.stream().forEach(cs -> l.add(new ChatSessionSummary(mUserId, cs)));

		return l;
	}

	public void addListeningPort(String sid, SseEmitter emitter) {
		NotifyClientChannel ch = new NotifyClientChannel(sid, emitter);
		
		emitter.onCompletion(new Runnable() {
	        @Override
	        public void run() {
	        	logger.info("XUE:onCompletion on session ");
	        	//mNotiChannel.remove(this);
	        }
	    });
		
		mNotiChannel.add(ch);
		logger.info("XUE: addListeningPort for Login user " + mUsername+". Now he has "+
				mNotiChannel.size()+" channels. sid="+sid);
	}

	public void removeSession(String sid) {
		ArrayList<NotifyClientChannel> toBeRemoved = new ArrayList<NotifyClientChannel>();
		
		mNotiChannel.stream().filter(c -> c.mSessionId.equals(sid)).forEach(c -> toBeRemoved.add(c));

		if (toBeRemoved.size() == 0) {
			logger.error("XUE: removeSession can't find the session id in the Login user " + mUsername);
			return;
		}

		if (mNotiChannel.size() == toBeRemoved.size()) {
			// The last session of login user was quit. It means the user logout
			// now
			LoginUsersManager.removeLoginuser(mUserId);
			notifyOtherPersons(false);
		}

		logger.info("XUE: Total remove " + toBeRemoved.size() + " on sid:" + sid);
		mNotiChannel.removeAll(toBeRemoved);
		logger.info("XUE: "+mUsername +" Left " + mNotiChannel.size() + " channel.");
		for(int i=0; i<mNotiChannel.size(); i++){
			logger.info("XUE: The Left session id is "+mNotiChannel.get(i).mSessionId);
		}
	}

	public int getSessionCount() {
		return mNotiChannel.size();
	}

	public boolean checkSessionId(String sid) {
		
		Optional<NotifyClientChannel> channel = mNotiChannel.stream()
			      .filter(c -> c.mSessionId.equals(sid))
			      .findFirst();
		if(channel.isPresent())
			return true;

		return false;
	}

	public void notifyOtherPersons(boolean login) {
		logger.info(getName()+ " notify other person \"i am "+ (login?"login\"":"logout\""));
		for (int i = 0; i < mChatSessions.size(); i++) {
			ArrayList<ContactPerson> participants = mChatSessions.get(i).getParticipantIds();

			for (int j = 0; j < participants.size(); j++) {
				ContactPerson person = participants.get(j);

				//person.updateStatus();
				LoginUser loginuer = person.getLoginUser();
				if (loginuer != null) {
					loginuer.notifyLogin(login, this);
				}
			}
		}
	}

	protected void notifyLogin(boolean bLogin, LoginUser loginuser) {
		for (int i = 0; i < mChatSessions.size(); i++) {
			ArrayList<ContactPerson> participants = mChatSessions.get(i).getParticipantIds();

			for (int j = 0; j < participants.size(); j++) {
				ContactPerson person = participants.get(j);
				if (person.getPersonId() == loginuser.getId()) {

					logger.info("XUE: notify " + mUsername + " that " + 
					            loginuser.getName() + " login=" + bLogin);

					if (bLogin) {
						person.setLoginUser(loginuser);
					} else {
						person.setLoginUser(null);
					}
					pushSessionUpdate(new ChatSessionSummary(person.getPersonId(), mChatSessions.get(i)));
				}
			}
		}
	}

	protected void notifyClient(String msg) {
		ArrayList<NotifyClientChannel> toBeRemoved = new ArrayList<NotifyClientChannel>();

		//logger.info("XUE: user " + uid + " has " + mNotiChannel.size() + " channels");
		Iterator<NotifyClientChannel> it = mNotiChannel.iterator();

		while (it.hasNext()) {
			NotifyClientChannel channel = it.next();
			if (channel.mListeningPort != null) {
				try {
					channel.mListeningPort.send(msg);
				} catch (IOException e) {
					logger.error("Push msg error");
					toBeRemoved.add(channel);
					
				}
			} else {
				logger.error("XUE:user " + mUsername + " has NULL channels");
			}
		}
		
		if(toBeRemoved.size() != 0){
			logger.error("XUE:user " + mUsername + " will remove "+toBeRemoved.size()+" channels.");
			
			it = toBeRemoved.iterator();
			while (it.hasNext()) {
				NotifyClientChannel channel = it.next();
				removeSession(channel.mSessionId);
			}
		}
	}

	protected void deleteChannel(NotifyClientChannel c){
		logger.error("XUE:deleteChannel " + mUsername + " has NULL channels");
		removeSession(c.mSessionId);
	}
	
	public String getName() {
		return mUsername;
	}

	public long getId() {
		return mUserId;
	}

	public void pushChatMessage2User(ChatMessage chat) {
		
		logger.info("XUE: send msg:"+chat+" to "+ mUsername);
		
		notifyClient(NotifyClientMessage.obtainPushMsg(chat));
	}

	public void pushSessionUpdate(ChatSessionSummary chatSessionSummary) {
		notifyClient(NotifyClientMessage.obtainSessionSummaryMsg(chatSessionSummary));
	}

	public void notifyInvite(ChatInvitation invitation) {
		notifyClient(NotifyClientMessage.obtainInviteMsg(invitation));
	}
	
}
