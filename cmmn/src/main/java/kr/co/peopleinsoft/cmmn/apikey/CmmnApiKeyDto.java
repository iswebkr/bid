package kr.co.peopleinsoft.cmmn.apikey;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class CmmnApiKeyDto implements Serializable {
	private String apiKeyHash;
	private String clientName;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
	private Integer rateLimit;
}