package xue.apps.chat.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SESSIONS")
public class ChatSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@Column(name="CHAT_ID")
	private long chatId;

	@Column(name="CREATE_TIME")
	private long createTime;
	
	@Column(name="PARTICIPANT_ID")
	private long participant;
	
	public ChatSessionEntity(long chatId, long participant, long createTime){
		this.chatId = chatId;
		this.participant = participant;
		this.createTime = createTime;
	}
	
	protected ChatSessionEntity(){}
	
	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		this.chatId = chatId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getParticipants() {
		return participant;
	}

	public void setParticipants(long participant) {
		this.participant = participant;
	}
	
}
