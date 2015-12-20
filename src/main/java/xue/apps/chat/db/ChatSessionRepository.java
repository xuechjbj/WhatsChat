package xue.apps.chat.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatSessionRepository extends CrudRepository<ChatSessionEntity, Long> {

	//void addChatMessage(ChatMessage record);

	@Query("select a.chatId from ChatSessionEntity a where a.participant = ?1")
	ArrayList<Long> getChatIdListByUid(long uid);

	@Query("select a.participant from ChatSessionEntity a where a.chatId = ?1")
	ArrayList<Long> getParticipantsByChatId(long chatId);

	@Query("select max(a.chatId) from ChatSessionEntity a")
	long getMaxChatId();

	/*private static final int ChatCounts = 6;

	private static HashMap<String, ArrayList<Long> > mChat2UserMap
	             = new HashMap<String, ArrayList<Long> >();

	private static HashMap<Long, ArrayList<String> > mUser2ChatMap
	             = new HashMap<Long, ArrayList<String> >();
	
	private static ArrayList<ChatMessage> mChatMessageList = new ArrayList<ChatMessage>();
	
	static{
		ChatMessage[] r = new ChatMessage[ChatCounts];
		
		String chatid = generateChatId();
		r[0] = new ChatMessage(chatid, 10001, "Hello Bill!", new Date());
		r[1] = new ChatMessage(chatid, 10001, "Hello Bill", new Date());
		r[2] = new ChatMessage(chatid, 10001, "Hello Bill", new Date());
		r[3] = new ChatMessage(chatid, 10002, "Hello Admin, How's going!", new Date());
		r[4] = new ChatMessage(chatid, 10002, "Hello Admin, very excited", new Date());
		r[5] = new ChatMessage(chatid, 10002, "Hello Admin, Look forward to meeting you", new Date());
		
		for(int i=0; i < r.length; i++){
			mChatMessageList.add(r[i]);
		}
		
		mUser2ChatMap.put(new Long(10001), new ArrayList<String>());
		mUser2ChatMap.get(new Long(10001)).add(chatid);
		
		mUser2ChatMap.put(new Long(10002), new ArrayList<String>());
		mUser2ChatMap.get(new Long(10002)).add(chatid);
		
		mChat2UserMap.put(chatid, new ArrayList<Long>());
		mChat2UserMap.get(chatid).add(new Long(10001));
		mChat2UserMap.get(chatid).add(new Long(10002));
		
	}
	
	public static ArrayList<String> getChatIdListByUid(long uid){
		//ArrayList<String> chatIds = new ArrayList<String>();
		
		return mUser2ChatMap.get(uid);
	}
	
	public static ArrayList<Long> getChatPersonList(String cid) {
		//ArrayList<Long> uids = mChatSessionMap.get(cid);

		return mChat2UserMap.get(cid);
	}
	
	public static String generateChatId(){
		return UUID.randomUUID().toString();
	}
	
	public static void addPersonInChat(String cid, long uid){
		ArrayList<Long> uids = mChat2UserMap.get(cid);
		if (uids == null) {
			uids = new ArrayList<Long>();
			uids.add(uid);
			mChat2UserMap.put(cid, uids);
		} else {
			uids.add(uid);
		}

		ArrayList<String> cids = mUser2ChatMap.get(uid);
		if(cids == null){
			cids = new ArrayList<String>();
			cids.add(cid);
			mUser2ChatMap.put(uid, cids);
		}else{
			cids.add(cid);
		}
	}
	
	public static void addChatMessage(ChatMessage r){
		mChatMessageList.add(r);
	}
	
	public static ArrayList<ChatMessage> getRecordsOnId(String cid){
		ArrayList<ChatMessage> rl = new ArrayList<ChatMessage>();
		
		Iterator<ChatMessage> it = mChatMessageList.iterator();
		
		while(it.hasNext()){
			ChatMessage r = it.next();
			if(r.getChatId().equals(cid)){
				rl.add(r);
			}
		}
		
		return rl;
	}*/
}
