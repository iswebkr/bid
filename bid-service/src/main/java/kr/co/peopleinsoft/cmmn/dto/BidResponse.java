package kr.co.peopleinsoft.cmmn.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BidResponse<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidHeader header;
	private BidBody<T> body;
}