package kr.co.peopleinsoft.cmmn.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BidHeader implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String resultCode;
	private String resultMsg;
}