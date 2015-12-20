package xue.apps.chat.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "INVITATIONS")
public class ChatInvitation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@Column(name = "INITIATOR_ID", nullable = false)
	private long initiator;
	
	@Column(name = "INVITEE_ID", nullable = false)
	private long invitee;
	
	@Column(name = "GREETING")
	private String greeting;
	
	@Column(name = "CREATE_TIME", nullable = false)
	private long create_time;
	
	@Column(name = "RESPONSE")
	private int response;

	public void setId(long id) {
		this.id = id;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	protected ChatInvitation(){}
	
	public ChatInvitation(long initiatorId, long inviteeId, String str, long time) {
		initiator = initiatorId;
		invitee = inviteeId;
		greeting = str;
		create_time = time;
		response = 0;
	}

	public long getInitiator() {
		return initiator;
	}

	public void setInitiator(long initiator) {
		this.initiator = initiator;
	}

	public long getInvitee() {
		return invitee;
	}

	public void setInvitee(long invitee) {
		this.invitee = invitee;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public long getId() {
		return id;
	}

	public String getGreeting() {
		return greeting;
	}
	
	public int getResponse(){
		return response;
	}
	
	public void setResponse(int resp){
		response = resp;
	}
}
