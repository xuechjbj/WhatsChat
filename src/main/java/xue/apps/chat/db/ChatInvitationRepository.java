package xue.apps.chat.db;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ChatInvitationRepository  extends CrudRepository<ChatInvitation, Long>{

	@Query("select count(*) from ChatInvitation a where a.initiator = ?1 AND a.invitee = ?2")
	int getCountOfInvitations(long initiatorId, long inviteeId);
	
	@Query("select count(*) from ChatInvitation a where a.invitee = ?1")
	int getCountOfInvitations(long inviteeId);

	@Query("select a from ChatInvitation a where a.invitee = ?1")
	List<ChatInvitation> getInvitationsByInviteeId(long inviteeId);
	
	@Query("select count(*) from ChatInvitation a where a.invitee = ?1 and a.response = 0")
	int getCountOfInvitationsByInviteeIdWithoutResp(long inviteeId);
	
	@Query("select a from ChatInvitation a where a.invitee = ?1 and a.response != 0")
	List<ChatInvitation> getInvitationsByInviteeIdWithoutResp(long inviteeId);
	
	@Query("select a from ChatInvitation a where a.initiator = ?1")
	List<ChatInvitation> getInvitationsByInitiatorId(long initiatorId);
}
