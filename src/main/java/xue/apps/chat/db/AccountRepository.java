package xue.apps.chat.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

//@Repository
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>{

	
	/**
     * Finds an account by using the username as a search criteria.
     * @param username
     * @return  A list of persons whose last name is an exact match with the given last name.
     *          If no persons is found, this method returns an empty list.
     */
    @Query("select a from Account a where a.userName = :username")
    List<Account> findAccountByName(@Param("username") String username);
    
    @Query("select count(*) from Account a where a.userName = :username")
    int getCountOfAccountByName(@Param("username") String username);

}
