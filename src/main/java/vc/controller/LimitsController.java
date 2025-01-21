package vc.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tags({@Tag(name = "Limits")})
@RestController
public class LimitsController {

    public record SessionTimeLimitResponse(int hours) {
        public static final SessionTimeLimitResponse INSTANCE = new SessionTimeLimitResponse(8);
    }

    @GetMapping("/limits/session-time-limit")
    @ApiResponses(value =
        @ApiResponse(
            responseCode = "200",
            description = """
                  Return the current 2b2t game session time limit for non-priority players.
                  
                  When this limit is reached, the player will be kicked from the server.
                  """
        )
    )
    public ResponseEntity<SessionTimeLimitResponse> getNonPrioKickTimeLimit() {
        return ResponseEntity.ok(SessionTimeLimitResponse.INSTANCE);
    }
}
