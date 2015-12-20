package xue.apps.chat.login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import xue.apps.chat.db.Account;
import xue.apps.chat.db.AccountRepository;

@Component
public class UserDetailsServiceAdapter implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String _username = username.toLowerCase();
		List<Account> accounts = accountRepo.findAccountByName(_username);
		if (accounts.size() == 0) {
			return null;
		}

		Account account = accounts.get(0);

		return new UserDetailsAdapter(account.getUserName(), account.getPassword(),
				account.getDispName(),
				account.getId());
	}

}
