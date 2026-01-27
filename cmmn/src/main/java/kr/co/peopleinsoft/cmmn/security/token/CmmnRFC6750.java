package kr.co.peopleinsoft.cmmn.security.token;

import lombok.Getter;

/**
 * Authentication Scheme for HTTP Authorization header
 */
@Getter
public enum CmmnRFC6750 {
	BASIC("Basic "),
	BEARER("Bearer "),
	DIGEST("Digest ");

	private final String value;

	CmmnRFC6750(String value) {
		this.value = value;
	}
}