package kr.co.peopleinsoft.cmmn.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BidHeader implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String resultCode;
	private String resultMsg;
}