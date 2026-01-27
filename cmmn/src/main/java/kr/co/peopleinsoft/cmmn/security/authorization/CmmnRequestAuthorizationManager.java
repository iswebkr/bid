package kr.co.peopleinsoft.cmmn.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.peopleinsoft.cmmn.security.hierarchy.CmmnRoleHierarchy;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.function.Supplier;

public class CmmnRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	static final Logger logger = LoggerFactory.getLogger(CmmnRequestAuthorizationManager.class);

	final CmmnRoleHierarchy roleHierarchy;
	final NamedParameterJdbcTemplate jdbcTemplate;

	public CmmnRequestAuthorizationManager(CmmnRoleHierarchy roleHierarchy, NamedParameterJdbcTemplate jdbcTemplate) {
		this.roleHierarchy = roleHierarchy;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication, RequestAuthorizationContext object) {
		Authentication auth = authentication.get();
		AuthorizationManager<HttpServletRequest> authorizationManager;
		assert object != null;
		HttpServletRequest request = object.getRequest();
		RequestMatcherDelegatingAuthorizationManager.Builder builder = new RequestMatcherDelegatingAuthorizationManager.Builder();

		if (auth == null || !auth.isAuthenticated()) {
			return new AuthorizationDecision(false);
		}

		// TODO : 리소스-역할 매핑 쿼리
		String resourceRoleQuery = """
			SELECT
			     RESOURCE.RESOURCE_ID
			     , RESOURCE.RESOURCE_NAME
			     , RESOURCE.RESOURCE_PATTERN
			     , RESOURCE.RESOURCE_DESCRIPTION
			     , RESOURCE.RESOURCE_TYPE
			     , RESOURCE.RESOURCE_HTTP_METHOD
			     , RESOURCE.RESOURCE_SORT_ORDER
				 , ROLES.ROLE_ID
				 , ROLES.ROLE_NAME
				 , ROLES.ROLE_DESCRIPTION
			FROM TB_SECURED_RESOURCES_ROLE RESOURCE_ROLE
			LEFT JOIN TB_SECURED_RESOURCES RESOURCE
			    ON RESOURCE.RESOURCE_ID = RESOURCE_ROLE.RESOURCE_ID
			LEFT JOIN TB_ROLES ROLES
				ON ROLES.ROLE_ID = RESOURCE_ROLE.ROLE_ID
			""";

		try {
			List<Resource> resources = jdbcTemplate.query(resourceRoleQuery, new DataClassRowMapper<>(Resource.class));

			resources.forEach(resource -> {
				RequestMatcher matcher;

				if (resource.resourceType().equalsIgnoreCase("regex")) {
					matcher = RegexRequestMatcher.regexMatcher(HttpMethod.valueOf(resource.resourceHttpMethod()), resource.resourcePattern());
				} else if (resource.resourceType().equalsIgnoreCase("ciregex")) {
					matcher = new RegexRequestMatcher(resource.resourcePattern(), resource.resourceHttpMethod(), true);
				} else {
					matcher = PathPatternRequestMatcher.withDefaults().matcher(resource.resourcePattern());
				}

				// String authorities = StringUtils.join(resource.getAuthorities(), ",");
				var authzManager = AuthorityAuthorizationManager.hasAuthority(resource.roleId());

				// Role Hierarchy
				authzManager.setRoleHierarchy(roleHierarchy);

				builder.add(matcher, authzManager);
			});

			builder.add(AnyRequestMatcher.INSTANCE, (authenticationSupplier, requestAuthorizationContext) -> new AuthorizationDecision(true));

			authorizationManager = builder.build();

			return authorizationManager.authorize(authentication, request);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}
		return new AuthorizationDecision(false);
	}

	record Resource(
		String resourceId,
		String resourceName,
		String resourcePattern,
		String resourceDescription,
		String resourceType,
		String resourceHttpMethod,
		int resourceSortOrder,
		String roleId,
		String roleName,
		String roleDescription
	) {
	}
}