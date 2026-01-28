package kr.co.peopleinsoft.cmmn.dto;

import lombok.Getter;

@Getter
public enum BidEnum {
	SERIAL_KEY("85a36152c7481157cf73aa74b3afa79c89ad51fa9e5ab91507af68338cc3ad93");
	private final String key;

	BidEnum(String key) {
		this.key = key;
	}
}