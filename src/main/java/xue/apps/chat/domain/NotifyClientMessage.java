package xue.apps.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import xue.apps.chat.db.Account;
import xue.apps.chat.db.ChatDBService;
import xue.apps.chat.db.ChatInvitation;
import xue.apps.chat.db.ChatMessage;


public class NotifyClientMessage {

	//JSON Vulnerability Protection
	//https://docs.angularjs.org/api/ng/service/$http
	private final static String ANGULAR_PREFIX = ")]}',\n";
	
	public static String obtainSessionSummaryMsg(ChatSessionSummary chatSessionSummary) {
		return new SessionChangeMsg(chatSessionSummary).toJson();
	}
	
	public static String obtainPushMsg(ChatMessage chat){
		return new PushChatMsg(chat).toJson();
	}
	
	public static String obtainInviteMsg(ChatInvitation invt){
		
		Account initator = ChatDBService.get().findAccountById(invt.getInitiator());
		String initatorName = initator.getDispName();
		String url = initator.getPortraitUrl();
		Account invitee = ChatDBService.get().findAccountById(invt.getInvitee());
		
		ChatInvite chatInvite = new ChatInvite(invt.getId(), invt.getInitiator(), 
				invt.getInvitee(), initatorName, invitee.getDispName(),
				invt.getGreeting(),url,invt.getResponse());
		
		return new InviteMsg(chatInvite).toJson();
	}
	
	static class InviteMsg{
		private int type;
		ChatInvite chatInvite;
		
		InviteMsg(ChatInvite invt){
			type = 3;
			chatInvite = invt;
		}
		public int getType() {
			return type;
		}
		public ChatInvite getChatInvite() {
			return chatInvite;
		}
		
		public String toJson(){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	static class ChatInvite{
		long invtId;
		long initatorId;
		long inviteeId;
		int resp;

		String initiator, invitee;
		String greeting;
		String url;
		
		ChatInvite(long invtId, long initatorId, long inviteeId, 
				String initiator, String invitee,
				String greeting, String url, int resp){
			this.invtId = invtId;
			this.initatorId = initatorId;
			this.inviteeId = inviteeId;
			this.initiator = initiator;
			this.invitee = invitee;
			this.greeting = greeting;
			this.url = url;
			this.resp = resp;
		}

		public long getInitatorId() {
			return initatorId;
		}
		public long getInviteeId() {
			return inviteeId;
		}
		public long getInviteId(){
			return invtId;
		}
		public String getInitiator() {
			return initiator;
		}
		public String getInvitee() {
			return invitee;
		}
		public int getResp(){
			return resp;
		}

		public String getGreeting() {
			return greeting;
		}
		
		public String getPortraitUrl(){
			return url;
		}
	}
	
	static class  SessionChangeMsg{
		private int type;
		ChatSessionSummary session;
		
		SessionChangeMsg(ChatSessionSummary session){
			this.type = 1;
			this.session = session;
		}
		
		public int getType(){
			return type;
		}
		
		public ChatSessionSummary getSession(){
			return session;
		}
		
		public String toJson(){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
    static class PushChatMsg{
		private int type;
		private ChatMessage msg;
		
		PushChatMsg(ChatMessage msg){
			this.type = 2;
			this.msg = msg;
		}
		
		public int getType(){
			return type;
		}
		
		public ChatMessage getMessage(){
			return msg;
		}
		
		public String toJson(){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
}
