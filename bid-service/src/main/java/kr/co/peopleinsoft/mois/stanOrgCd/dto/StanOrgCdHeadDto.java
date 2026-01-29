package kr.co.peopleinsoft.mois.stanOrgCd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StanOrgCdHeadDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> head;

}