package xue.apps.chat.domain;

import java.util.ArrayList;
import java.util.List;

import xue.apps.chat.db.ChatMessage;

public class ChatHistory {

	private long chatId;
	private String chatTitle;
	private List<ChatMessage> Messages = new ArrayList<ChatMessage>();
	private ArrayList<Participant> Participants = new ArrayList<Participant>();
	
	/*public ChatHistory(String title){
		chatCounts = 0;
		chatTitle = title;
	}*/
	
	public ChatHistory() {
		
	}

	public void addChatMessage(ChatMessage m){
		Messages.add(m);
		//chatCounts++;
	}
	
	public void addChatMessages(List<ChatMessage> m) {
		Messages.addAll(m);
	}

	public void markReadDone(long uid, long msgId){
		
	}
	
	public void addParticipant(String imgurl, String imgalt){
		Participant p = new Participant(imgurl, imgalt);
		Participants.add(p);
	}
	
	public void setTitle(String t) {
		chatTitle = t;
	}
	public String getTitle() {
		return chatTitle;
	}
	
	public void setChatId(long id){
		chatId = id;
	}
	public long getChatId(){
		return chatId;
	}
	public List<ChatMessage> getChatMessages(){
		return Messages;
	}
	public ArrayList<Participant> getParticipants(){
		return Participants;
	}
	
	class Participant{
		public String img_url;
		public String img_alt;
		
		public String getImg_url() {
			return img_url;
		}

		public void setImg_url(String img_url) {
			this.img_url = img_url;
		}

		public String getImg_alt() {
			return img_alt;
		}

		public void setImg_alt(String img_alt) {
			this.img_alt = img_alt;
		}

		public Participant(String imgurl, String imgalt){
			img_url = imgurl;
			img_alt = imgalt;
		}
	}

	public ChatMessage getLatestChat() {
		if(Messages.size() == 0) return null;
		
		return Messages.get(Messages.size()-1);	
	}
	
}
