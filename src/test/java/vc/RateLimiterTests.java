package vc;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimiterTests {

    @Autowired
    protected MockMvc mockMvc;

    @RepeatedTest(4)
    public void whenGetPlaytimeAllMonthRepeatedly_thenRateLimited(RepetitionInfo repetitionInfo) throws Exception {
        ResultMatcher result = repetitionInfo.getCurrentRepetition() == 1 ? status().isOk() :
                status().isTooManyRequests();
        mockMvc.perform(MockMvcRequestBuilders.get("/playtime/top/month"))
                .andExpect(result);
    }
}
