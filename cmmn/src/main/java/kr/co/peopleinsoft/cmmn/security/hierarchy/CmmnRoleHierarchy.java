package kr.co.peopleinsoft.cmmn.security.hierarchy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.hierarchicalroles.CycleInRoleHierarchyException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CmmnRoleHierarchy implements RoleHierarchy {

	static final Logger logger = LoggerFactory.getLogger(CmmnRoleHierarchy.class);

	final ConfigurableEnvironment environment;
	final NamedParameterJdbcTemplate jdbcTemplate;

	Map<String, Set<GrantedAuthority>> rolesReachableInOneStepMap = new HashMap<>();
	Map<String, Set<GrantedAuthority>> rolesReachableInOneOrMoreStepsMap = new HashMap<>();
	List<HierarchyRole> hierarchyRoles = new ArrayList<>();

	public CmmnRoleHierarchy(ConfigurableEnvironment environment, NamedParameterJdbcTemplate jdbcTemplate) {
		this.environment = environment;
		this.jdbcTemplate = jdbcTemplate;
		setHierarchy();
	}

	private void setHierarchy() {
		try {
			// TODO : 역할 계층 쿼리
			String hierarchyQuery = """
				SELECT
					A.PARENT_ROLE_ID PARENT
					, A.CHILD_ROLE_ID CHILD
				FROM TB_ROLES_HIERARCHY A
				""";

			hierarchyRoles = jdbcTemplate.query(hierarchyQuery, new DataClassRowMapper<>(HierarchyRole.class));
			buildRolesReachableInOneStepMap();
			buildRolesReachableInOneOrMoreStepsMap();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
		if (!authorities.isEmpty()) {
			Set<GrantedAuthority> reachableRoles = new HashSet<>();
			Set<String> processedNames = new HashSet<>();

			for (GrantedAuthority authority : authorities) {
				authority.getAuthority();
				if (processedNames.add(authority.getAuthority())) {
					reachableRoles.add(authority);
					Set<GrantedAuthority> lowerRoles = this.rolesReachableInOneOrMoreStepsMap.get(authority.getAuthority());
					if (lowerRoles != null) {
						for (GrantedAuthority role : lowerRoles) {
							if (processedNames.add(role.getAuthority())) {
								reachableRoles.add(role);
							}
						}
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("getReachableGrantedAuthorities() - From the roles {} one can reach {} in zero or more steps.", authorities, reachableRoles);
			}
			return new ArrayList<>(reachableRoles);
		} else {
			return AuthorityUtils.NO_AUTHORITIES;
		}
	}

	void buildRolesReachableInOneStepMap() {
		this.rolesReachableInOneStepMap = new HashMap<>();
		this.hierarchyRoles.forEach(role -> {
			String higherRole = role.parent;
			GrantedAuthority lowerRole = new SimpleGrantedAuthority(role.child);
			Set<GrantedAuthority> rolesReachableInOneStepSet;

			if (!this.rolesReachableInOneStepMap.containsKey(higherRole)) {
				rolesReachableInOneStepSet = new HashSet<>();
				this.rolesReachableInOneStepMap.put(higherRole, rolesReachableInOneStepSet);
			} else {
				rolesReachableInOneStepSet = this.rolesReachableInOneStepMap.get(higherRole);
			}

			rolesReachableInOneStepSet.add(lowerRole);

			if (logger.isDebugEnabled()) {
				logger.debug("buildRolesReachableInOneStepMap() - From role {} one can reach role {} in one step.", higherRole, lowerRole);
			}
		});
	}

	void buildRolesReachableInOneOrMoreStepsMap() {
		for (String roleName : this.rolesReachableInOneStepMap.keySet()) {
			Set<GrantedAuthority> rolesToVisitSet = new HashSet<>(this.rolesReachableInOneStepMap.get(roleName));
			Set<GrantedAuthority> visitedRolesSet = new HashSet<>();

			while (!rolesToVisitSet.isEmpty()) {
				GrantedAuthority lowerRole = rolesToVisitSet.iterator().next();
				rolesToVisitSet.remove(lowerRole);
				if (visitedRolesSet.add(lowerRole) && this.rolesReachableInOneStepMap.containsKey(lowerRole.getAuthority())) {
					if (roleName.equals(lowerRole.getAuthority())) {
						throw new CycleInRoleHierarchyException();
					}
					rolesToVisitSet.addAll(this.rolesReachableInOneStepMap.get(lowerRole.getAuthority()));
				}
			}

			rolesReachableInOneOrMoreStepsMap.put(roleName, visitedRolesSet);

			if (logger.isDebugEnabled()) {
				logger.debug("buildRolesReachableInOneOrMoreStepsMap() - From role {} one can reach {} in one or more steps.", roleName, visitedRolesSet);
			}
		}
	}

	/*** 계층 권한 ***/
	public record HierarchyRole(
		String parent,
		String child
	) {
	}
}