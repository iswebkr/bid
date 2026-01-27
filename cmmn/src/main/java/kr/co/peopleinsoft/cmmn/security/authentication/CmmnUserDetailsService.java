package kr.co.peopleinsoft.cmmn.security.authentication;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmmnUserDetailsService implements UserDetailsService {

	final ConfigurableEnvironment environment;
	final NamedParameterJdbcTemplate jdbcTemplate;
	final PasswordEncoder passwordEncoder;

	public CmmnUserDetailsService(ConfigurableEnvironment environment, NamedParameterJdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		this.environment = environment;
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * 사용자 정보 조회 (화면에서 입력받은 사용자의 기본정보 조회)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password;
		boolean enabled;
		boolean accountNonExpired;
		boolean credentialsNonExpired;
		boolean accountNonLocked;
		List<GrantedAuthority> authorities = new ArrayList<>();

		UserInfo userInfo;  // 사용자정보 조회
		List<UserAuthoritie> userAuthorities; // 사용자 권한 목록 조회
		try {
			userInfo = getUserInfo(username);
			userAuthorities = getUserAuthorities(username);

			if (!ObjectUtils.isEmpty(userInfo)) {
				password = userInfo.userPassword();
				enabled = "Y".equalsIgnoreCase(userInfo.userUseYn());
				accountNonExpired = !("Y".equalsIgnoreCase(userInfo.userAccountExpiredYn));
				credentialsNonExpired = !("Y".equalsIgnoreCase(userInfo.userCredentialsExpiredYn));
				accountNonLocked = !("Y".equalsIgnoreCase(userInfo.userAccountExpiredYn));

				userAuthorities.forEach(cmmnUserAuthorities -> {
					authorities.add(new SimpleGrantedAuthority(cmmnUserAuthorities.roleId));
				});

				return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			}
			throw new UsernameNotFoundException(String.format("User not found - (%s)", username));
		} catch (Exception e) {
			throw new UsernameNotFoundException(String.format("User not found - (%s)", username));
		}
	}

	/**
	 * Database 에서 사용자 정보를 조회
	 */
	UserInfo getUserInfo(String username) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", username);

		// TODO : 사용자 정보조회 쿼리
		String userQuery = """
				SELECT
					     USERS.USER_ID
				        , USERS.USER_NAME
				        , USERS.USER_PASSWORD
						, USERS.USER_ACCOUNT_EXPIRED_YN
						, USERS.USER_ACCOUNT_LOCKED_YN
						, USERS.USER_CREDENTIALS_EXPIRED_YN
						, USERS.USER_USE_YN
				FROM	TB_USERS USERS WHERE USERS.USER_ID = :userId
			""";

		return jdbcTemplate.queryForObject(userQuery, paramMap, new DataClassRowMapper<>(UserInfo.class));
	}

	/**
	 * Database 에서 사용자가 보유하고 있는 권한 목록 조회
	 */
	List<UserAuthoritie> getUserAuthorities(String username) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", username);

		// TODO : 사용자가 보유하고 있는 권한 목록 조회 쿼리
		String userAuthoritiesQuery = """
			   SELECT
				          USERS.USER_ID
						, ROLES.ROLE_ID
			   FROM		TB_USER_ROLE USER_ROLE
			   LEFT JOIN TB_USERS USERS
			       ON USERS.USER_ID = USER_ROLE.USER_ID
			       AND USERS.USER_USE_YN = 'Y'
			   LEFT JOIN TB_ROLES ROLES
			       ON ROLES.ROLE_ID = USER_ROLE.ROLE_ID
			   WHERE USERS.USER_ID = :userId
			""";

		return jdbcTemplate.query(userAuthoritiesQuery, paramMap, new DataClassRowMapper<>(UserAuthoritie.class));
	}

	/*** 사용자 정보 Record ***/
	public record UserInfo(
		String userId,
		String userName,
		String userPassword,
		String userAccountExpiredYn,
		String userAccountLockedYn,
		String userCredentialsExpiredYn,
		String userUseYn
	) {
		public UserInfo {
			if ("Y".equals(userAccountExpiredYn)) {
				throw new AccountExpiredException(String.format("[%s] 계정이 만료되었습니다.", userName));
			}
			if ("Y".equals(userCredentialsExpiredYn)) {
				throw new CredentialsExpiredException(String.format("[%s] 계정의 자격증명이 만료되었습니다..", userName));
			}
			if ("Y".equals(userAccountLockedYn)) {
				throw new LockedException(String.format("[%s] 계정은 잠겨 있습니다.", userName));
			}
			if ("N".equals(userUseYn)) {
				throw new DisabledException(String.format("[%s] 계정은 더이상 사용되지 않는 계정입니다.", userName));
			}
		}
	}

	/*** 사용자 권한 Record ***/
	public record UserAuthoritie(
		String userId,
		String roleId
	) {
	}
}