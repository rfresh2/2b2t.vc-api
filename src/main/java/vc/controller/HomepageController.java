package vc.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tags({@Tag(name = "Homepage")})
@RestController
public class HomepageController {

    @GetMapping("/")
    public void homepage(final HttpServletResponse response) {
        response.setHeader("Location", "/swagger-ui/index.html");
        response.setStatus(302);
    }
}
