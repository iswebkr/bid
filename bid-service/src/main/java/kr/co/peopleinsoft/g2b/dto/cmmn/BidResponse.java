package kr.co.peopleinsoft.g2b.dto.cmmn;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BidResponse<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidHeader header;
	private BidBody<T> body;
}