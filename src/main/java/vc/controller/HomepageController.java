package vc.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Tags({@Tag(name = "Homepage")})
@Controller
public class HomepageController {

    @RequestMapping("/")
    public String homepage(final HttpServletResponse response) {
        return "redirect:/swagger-ui/index.html";
    }
}
