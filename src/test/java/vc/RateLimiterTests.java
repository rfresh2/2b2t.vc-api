package vc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimiterTests {

    @Autowired
    protected MockMvc mockMvc;

    // only succeeds when we have rate limiter somewhat high

//    @RepeatedTest(4)
//    public void whenGetPlaytimeAllMonthRepeatedly_thenRateLimited(RepetitionInfo repetitionInfo) throws Exception {
//        ResultMatcher result = repetitionInfo.getCurrentRepetition() == 1 ? status().isOk() :
//                status().isTooManyRequests();
//        mockMvc.perform(MockMvcRequestBuilders.get("/playtime/top/month"))
//                .andExpect(result);
//    }
}
