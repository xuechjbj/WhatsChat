package xue.apps.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

	@RequestMapping("/")
    public String index() {
        return "index.html";
    }
}
