package xue.apps.chat.domain;

import xue.apps.chat.db.ChatDBService;
import xue.apps.chat.db.ChatMessage;

public class LatestChatMessage {

	private String message;
	private String speaker;
	private boolean readDone;

	public LatestChatMessage(long uid, ChatMessage msg) {
		message = msg.getMessage();
		
		speaker = msg.getSpeaker();

		long readDoneMsgId = ChatDBService.get().findMaxMagIdByUser(uid, msg.getChatId());
		
		if(msg.getSpeakerId() == uid){
			readDone = true;
		}
		else if (msg.getMsgId() > readDoneMsgId)
			readDone = false;
		
		else{
			readDone = true;
		}
	}

	public String getMessage() {
		return message;
	}

	public String getSpeaker() {
		return speaker;
	}

	public boolean isReadDone() {
		return readDone;
	}

	public void updateFrom(ChatMessage latestChat) {

	}

}
