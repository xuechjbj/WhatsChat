package xue.apps.chat.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "MESSAGES")
public class ChatMessage {

	//@Autowired
	//private MessageReaderRepository messageReaderRepo;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long msgId;

	@Column(name = "CHAT_ID", nullable = false)
	private long chatId;

	@Column(name = "SPEAKER_ID", nullable = false)
	private long speakerId;

	@Column(name = "MESSAGE", nullable = false)
	private String message;

	@Column(name = "TIME_STAMP", nullable = false)
	private long time;

	@Transient
	private String speakerUrl;
	@Transient
	private String speaker;
	
	protected ChatMessage(){}
	
	public ChatMessage(long chatId, long spkrId, String msg, long t) {
		this.chatId = chatId;
		message = msg;
		time = t;
		speakerId = spkrId;
	}

	public long getMsgId(){
		return msgId;
	}
	public long getChatId() {
		return chatId;
	}

	public String getSpeaker() {
		if(speaker == null){
			Account account = ChatDBService.get().findAccountById(speakerId);
			speaker = account.getDispName();
		}
		return speaker;
	}
	
	public String getSpeakerUrl() {
		if(speakerUrl == null){
			Account account = ChatDBService.get().findAccountById(speakerId);
			speakerUrl = account.getPortraitUrl();
		}
		return speakerUrl;
	}
	
	public long getSpeakerId() {
		return speakerId;
	}

	public void setMessage(String m) {
		message = m;
	}

	public String getMessage() {
		return message;
	}

	public long getTime() {
		return time;
	}
}
