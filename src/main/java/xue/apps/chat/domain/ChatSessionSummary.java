package xue.apps.chat.domain;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatSessionSummary {

	//private ChatDBService dbSrv;
	
	private long chatId;
	private ArrayList<ContactPerson> participantIds = new ArrayList<ContactPerson>();
	private LatestChatMessage latestMsg;
	
	//private long uid;
	//private ChatSession origin;
	
	public ChatSessionSummary(long uid, ChatSession origin){
		chatId = origin.getChatId();
		//this.uid = uid;
		//this.origin = origin;
		participantIds = origin.getParticipantIds();
		
		latestMsg = new LatestChatMessage(uid, origin.getLatestMsg());
	}

	public long getChatId() {
		return chatId;
	}

	public ArrayList<ContactPerson> getParticipantIds() {
		return participantIds;
	}
	
	public LatestChatMessage getLatestMsg(){
		return latestMsg;
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
