package kr.co.peopleinsoft.g2b.dto.cmmn;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class BidBody<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private List<T> items;
	private int numOfRows;
	private int pageNo;
	private int totalCount;
}