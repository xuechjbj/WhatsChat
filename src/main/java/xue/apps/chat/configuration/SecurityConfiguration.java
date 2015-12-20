package xue.apps.chat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import xue.apps.chat.domain.WebSessionListener;
import xue.apps.chat.login.AngularjsCsrfHeaderFilter;
import xue.apps.chat.login.LoginFailureHandler;
import xue.apps.chat.login.LoginSuccessHandler;
import xue.apps.chat.login.MyLogoutSuccessHandler;
import xue.apps.chat.login.RestAuthenticationEntryPoint;
import xue.apps.chat.login.UserDetailsServiceAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceAdapter userDetailsService;

	@Autowired
	private LoginFailureHandler loginFailureHandler;

	/**
	 * This section defines the user accounts which can be used for
	 * authentication as well as the roles each user has.
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()// httpBasic().and()
				.antMatchers(HttpMethod.OPTIONS, "/*/**").permitAll()
				.antMatchers("/login", "/signup", "resources/img*/**", "/favicon.ico").permitAll();

		LoginSuccessHandler loginHandler = loginSuccessHandler();
		
		http.formLogin().loginPage("/login")
		       .successHandler(loginHandler)
		       .failureHandler(loginFailureHandler);

		http.logout().deleteCookies("remove").invalidateHttpSession(true).logoutUrl("/logout")
				.logoutSuccessHandler(logoutSuccessHandler());

		http.csrf().requireCsrfProtectionMatcher(
				new AndRequestMatcher(
				        // Apply CSRF protection to all paths that do NOT match the ones below
				 
				        // We disable CSRF at login/logout, but only for OPTIONS methods
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/login*/**", HttpMethod.OPTIONS.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/favicon.png", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/lib/**", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/login", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/listening/**", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/img/**", HttpMethod.GET.toString())),
				        new NegatedRequestMatcher(new AntPathRequestMatcher("/logout*/**", HttpMethod.OPTIONS.toString()))
				    ));
		
		
		
		
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.addFilterAfter(new AngularjsCsrfHeaderFilter(), SessionManagementFilter.class);

		http.userDetailsService(userDetailsService);

		//http.addFilter(restAuthenticationFilter());
		//http.addFilterBefore(new AuthenticationFilter(), BasicAuthenticationFilter.class);

		http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

	
	@Bean
	protected LoginSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler();
	}

	@Bean
	protected MyLogoutSuccessHandler logoutSuccessHandler() {
		return new MyLogoutSuccessHandler();
	}

	// Register HttpSessionEventPublisher
	@Bean
	public static ServletListenerRegistrationBean<WebSessionListener> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<WebSessionListener>(new WebSessionListener());
	}
}
