package kr.co.peopleinsoft.biz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CmmnFaviconController {
	@GetMapping("/favicon.ico")
	void disableFavicon() {
	}
}