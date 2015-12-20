package xue.apps.chat.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xue.apps.chat.db.ChatDBService;
import xue.apps.chat.db.ChatMessage;
import xue.apps.chat.db.MessageReader;

public class ChatSession {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatSession.class);
	private final ChatDBService dbSrv;
	
	private long chatId;
	private ArrayList<ContactPerson> participantIds = new ArrayList<ContactPerson>();
	private ChatMessage latestMsg;

	private LoginUser loginUser;

	
	public ChatSession(long id, LoginUser u){
		chatId = id;
		loginUser = u;
		this.dbSrv = ChatDBService.get();
		latestMsg = dbSrv.findLatestMessageInChat(id);
		if(latestMsg == null){
			latestMsg = new ChatMessage(chatId, u.getId(), "", new Date().getTime());
		}
	}
	
	public void addPerson(ContactPerson person){
		participantIds.add(person);
	}
	
	public long getChatId(){
		//Logger logger = LoggerFactory.getLogger(ChatSession.class);
		//logger.info("getChatId="+chatId);
		return chatId;
	}

	public boolean postChatMessage(long uid, String message) {
		Logger logger = LoggerFactory.getLogger(ChatSession.class);
		
		ChatMessage record = new ChatMessage(chatId, uid, message, new Date().getTime());
		dbSrv.save(record);
		//sessionRepository.addChatMessage(record);
		//chatHistory.addChatMessage(record);
		latestMsg = record;
		
		for (int j = 0; j < participantIds.size(); j++) {
			ContactPerson person = participantIds.get(j);
			LoginUser otherLoginUser = person.getLoginUser();
			logger.info("XUE: contact "+person.getName()+", login="+otherLoginUser);
			if(otherLoginUser!=null){
				otherLoginUser.pushChatMessage2User(record);
				;
				otherLoginUser.pushSessionUpdate(new ChatSessionSummary(otherLoginUser.getId(), this));
			}
		}
		
		//Post msg to my other terminal devices
		loginUser.pushChatMessage2User(record);
		loginUser.pushSessionUpdate(new ChatSessionSummary(loginUser.getId(), this));
		return true;
	}


	public ArrayList<ContactPerson> getParticipantIds() {
		//logger.info("getParticipantIds="+chatId);
		return participantIds;
	}
	
	public LoginUser getLoginUser(){
		//logger.info("getLoginUser="+chatId);
		return loginUser;
	}

	public ChatHistory getChatHistory() {
		ChatHistory chatHistory = new ChatHistory();
		
		StringBuffer title = new StringBuffer();
		title.append("Chatting with");
		
		List<ChatMessage> chatRecords = dbSrv.findMessagesByChatId(chatId);
		chatHistory.addChatMessages(chatRecords);
		//latestMsg.updateFrom(chatHistory.getLatestChat());
		
		Iterator<ContactPerson> itc = participantIds.iterator();
		while(itc.hasNext()){
			ContactPerson c = itc.next();
			chatHistory.addParticipant(c.getIconUrl(), c.getName());
			if (title.length() < 28) {
				title.append(" " + c.getName());
			} else {
				title.append("...");
				break;
			}
		}
		chatHistory.setTitle(title.toString());
		//String sid = RequestContextHolder.currentRequestAttributes().getSessionId();
		chatHistory.setChatId(chatId);
		
		return chatHistory;
	}

	public boolean msgReadDone(long uid, long msgId) {
		
		long readMsgId = ChatDBService.get().findMaxMagIdByUser(uid, chatId);
		if (msgId > readMsgId) {
			MessageReader msgreader = new MessageReader(uid, msgId, chatId);
			ChatDBService.get().save(msgreader);

			//summary.updateLatestRead();
			loginUser.pushSessionUpdate(new ChatSessionSummary(loginUser.getId(), this));
		}
		return true;
	}

	public ChatMessage getLatestMsg() {
		// TODO Auto-generated method stub
		return latestMsg;
	}
}
