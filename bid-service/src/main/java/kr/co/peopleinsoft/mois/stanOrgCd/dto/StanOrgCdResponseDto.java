package kr.co.peopleinsoft.mois.stanOrgCd.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class StanOrgCdResponseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private List<StanOrgCdDto> row;
}