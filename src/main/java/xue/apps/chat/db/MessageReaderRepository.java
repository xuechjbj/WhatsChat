package xue.apps.chat.db;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MessageReaderRepository extends CrudRepository<MessageReader, Long> {

	@Query("select a.readerId from MessageReader a where a.msgId = ?1")
	List<Long> findReadersByMsgId(long msgId);
	
	@Query("select max(m.time) from ChatMessage m where m.chatId = ?1")
	ChatMessage queryLatestMessageInChat(long chatId);

	@Query("select max(m.msgId) from MessageReader m where m.readerId = ?1 AND m.chatId = ?2")
	long findMaxMsgIdBy(long uid, long chatId);

	@Query("select count(*) from MessageReader m where m.readerId = ?1 AND m.chatId = ?2")
	int getCountUnderUidChatId(long uid, long chatId);
	//List<Long> findMaxMsgIdByUserId(long uid);
	
}
