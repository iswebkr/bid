package kr.co.peopleinsoft.cmmn.security.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;

import java.util.HashMap;
import java.util.Map;

public class CmmnUserDetailsPasswordService implements UserDetailsPasswordService {

	final ConfigurableEnvironment environment;
	final NamedParameterJdbcTemplate jdbcTemplate;
	final CmmnUserDetailsService cmmnUserDetailService;

	public CmmnUserDetailsPasswordService(ConfigurableEnvironment environment, NamedParameterJdbcTemplate jdbcTemplate, CmmnUserDetailsService cmmnUserDetailService) {
		this.environment = environment;
		this.jdbcTemplate = jdbcTemplate;
		this.cmmnUserDetailService = cmmnUserDetailService;
	}

	@Override
	public UserDetails updatePassword(UserDetails user, @Nullable String newPassword) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", user.getUsername());
		paramMap.put("userPassword", newPassword);

		// TODO : 사용자 패스워드 변경  쿼리
		String userPasswordUpdateQuery = """
				UPDATE TB_USERS SET
						USER_PASSWORD = :userPassword
				WHERE USER_ID = :userId
			""";

		jdbcTemplate.update(userPasswordUpdateQuery, paramMap);

		return cmmnUserDetailService.loadUserByUsername(user.getUsername());
	}
}