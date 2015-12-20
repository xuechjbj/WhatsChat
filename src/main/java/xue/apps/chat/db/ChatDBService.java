package xue.apps.chat.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ChatDBService implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(ChatDBService.class);
	private static ChatDBService dbSrv;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private ChatMessageRepository messageRepository;

	@Autowired
	private ChatSessionRepository sessionRepository;

	@Autowired
	private MessageReaderRepository messageReaderRepository;

	@Autowired
	private ChatInvitationRepository chatInvitationRepository;

	// EntityManager entityManager;
	@PersistenceContext
	protected EntityManager entityManager;

	private TypedQuery<ChatMessage> queryLatestMessageInChat;

	public ChatDBService() {
		// entityManager =
		// Persistence.createEntityManagerFactory("ChatDBService").createEntityManager();
	}

	public void save(ChatMessage record) {
		messageRepository.save(record);
	}

	public List<ChatMessage> findMessagesByChatId(long chatId) {
		return messageRepository.findMessagesByChatId(chatId);
	}

	public ChatMessage findLatestMessageInChat(long chatId) {

		if (messageRepository.getCountOfMessages(chatId) > 0) {
			queryLatestMessageInChat = entityManager.createQuery(
					"SELECT msg from ChatMessage msg " + " WHERE msg.chatId = :chatId AND"
							+ " msg.time = (select MAX(m.time) from ChatMessage m where m.chatId = :chatId)",
					ChatMessage.class);

			ChatMessage msg = queryLatestMessageInChat.setParameter("chatId", chatId).getSingleResult();

			return msg;
		}
		return null;
	}

	///////////////////////////////////////////
	public List<Account> findAccountByName(String username) {
		return accountRepo.findAccountByName(username);
	}

	public int getCountOfAccountByName(String name) {
		return accountRepo.getCountOfAccountByName(name);
	}

	public Account createNewAccount(String username, String passwd, String email, String url) {
		if (getCountOfAccountByName(email) == 0) {
			Account account = new Account(username, passwd, email, url);
			Account newOne = accountRepo.save(account);
			return newOne;
		}
		return null;

	}

	public Account findAccountById(long uid) {
		return accountRepo.findOne(uid);
	}

	public String getDispName(long uid) {
		Account a = accountRepo.findOne(uid);
		if (a != null) {
			return a.getDispName();
		}
		return null;
	}

	////////////////////////////////////////////
	public ArrayList<Long> getChatIdListByUid(long uid) {
		return sessionRepository.getChatIdListByUid(uid);
	}

	public ArrayList<Long> getParticipantsByChatId(long chatId) {
		return sessionRepository.getParticipantsByChatId(chatId);
	}

	protected long getMaxChatId(){
		return sessionRepository.getMaxChatId();
	}
	
	public long saveSession(long initiator, long invitee) {
		long maxChatId = getMaxChatId();
		long newChatId = maxChatId + 1;
		long createTime = new Date().getTime();
		
		ChatSessionEntity p1 = new ChatSessionEntity(newChatId, initiator, createTime);
		ChatSessionEntity p2 = new ChatSessionEntity(newChatId, invitee, createTime);
		sessionRepository.save(p1);
		sessionRepository.save(p2);
		
		return newChatId;
	}
	
	////////////////////////////////////////////
	public void save(MessageReader msgreader) {
		messageReaderRepository.save(msgreader);
	}

	public static ChatDBService get() {

		return dbSrv;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		logger.info("XUE: Set spring app context=" + ctx);
		dbSrv = ctx.getBean(ChatDBService.class);
	}

	// public long findReadMsgIdByUId(long uid, long chatId) {
	// return messageReaderRepository.findMaxMsgIdBy(uid, chatId);
	// }

	public long findMaxMagIdByUser(long uid, long chatId) {

		if (messageReaderRepository.getCountUnderUidChatId(uid, chatId) != 0) {
			return messageReaderRepository.findMaxMsgIdBy(uid, chatId);
		}

		return 0;
	}

	///////////////////////////////////
	public int getCountOfInvitations(long initiatorId, long inviteeId) {
		return chatInvitationRepository.getCountOfInvitations(initiatorId, inviteeId);
	}

	public int getCountOfInvitations(long inviteeId) {
		return chatInvitationRepository.getCountOfInvitations(inviteeId);
	}
	
	public List<ChatInvitation> getInvitationsByInviteeId(long inviteeId) {
		if (chatInvitationRepository.getCountOfInvitations(inviteeId) > 0) {
			return chatInvitationRepository.getInvitationsByInviteeId(inviteeId);
		}
		return null;
	}
	
	public List<ChatInvitation> getInvitationsByInviteeIdWithoutResp(long id) {
		if(chatInvitationRepository.getCountOfInvitationsByInviteeIdWithoutResp(id) > 0){
			return chatInvitationRepository.getInvitationsByInviteeIdWithoutResp(id);
		}
		return null;
	}
	
	public List<ChatInvitation> getInvitationsByInitiatorId(long id){
		return chatInvitationRepository.getInvitationsByInitiatorId(id);
	}
	
	public ChatInvitation getInvitationsById(long id){
		return chatInvitationRepository.findOne(id);
	}

	public ChatInvitation saveInvite(long initiatorId, long inviteeId, String greeting) {
		return chatInvitationRepository
				.save(new ChatInvitation(initiatorId, inviteeId, greeting, new Date().getTime()));
	}
	
	public ChatInvitation saveInvite(ChatInvitation invite) {
		return chatInvitationRepository.save(invite);
	}

	public void deleteInvite(ChatInvitation entity){
		chatInvitationRepository.delete(entity);
	}

}
