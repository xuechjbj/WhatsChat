package xue.apps.chat.domain;

public class ChatMessageFromClient {

	private String sender;
	private String chatId;
	private String message;
	
	public void setSender(String snder){
		sender = snder;
	}
	public String getSender(){
		return sender;
	}
	
	public void setChatId(String c){
		chatId = c;
	}
	public String getChatId(){
		return chatId;
	}
	
	public void setMessage(String m){
		message = m;
	}
	public String getMessage(){
		return message;
	}
}
