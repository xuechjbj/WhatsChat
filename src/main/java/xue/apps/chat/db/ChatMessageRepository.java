package xue.apps.chat.db;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long>{


	@Query("select m from ChatMessage m where m.chatId = :chatId")
    public List<ChatMessage> findMessagesByChatId(@Param("chatId") long chatId);

	@Query("select count(*) from ChatMessage m where m.chatId = :chatId")
	public int getCountOfMessages(@Param("chatId") long chatId);
	//@Query("SELECT max(t.id) FROM #{#entityName} t where m.chatId = :chatId")
	//public ChatMessage findLatestMsg(long chatId);
	
	//ChatMessage findTopByChatIdByOrderByTimeDesc(long chatId);
}
