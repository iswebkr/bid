package kr.co.peopleinsoft;

import ch.qos.logback.core.model.Model;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController extends CmmnAbstractController {
	@GetMapping({"/", ""})
	public ModelAndView index(Model model) {
		RedirectView rv = new RedirectView();
		rv.setUrl("/swagger-ui/index.html");
		return new ModelAndView(rv);
	}

	@GetMapping("/main")
	public ModelAndView main(Model model) {
		RedirectView rv = new RedirectView();
		rv.setUrl("/swagger-ui/index.html");
		return new ModelAndView(rv);
	}
}