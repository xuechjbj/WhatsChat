package xue.apps.chat.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "READERS")
public class MessageReader {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	
	@Column(name="MSG_ID")
	private long msgId;
	
	@Column(name="READER_ID")
	private long readerId;
	
	@Column(name="CHAT_ID")
	private long chatId;
	
	protected MessageReader(){}

	public MessageReader(long uid, long mid, long cid){
		readerId = uid;
		msgId = mid;
		chatId = cid;
	}
	
	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public long getChatId() {
		return chatId;
	}
	
	public long getReaderId() {
		return readerId;
	}

	public void setReaderId(long readerId) {
		this.readerId = readerId;
	}
	
	
}
