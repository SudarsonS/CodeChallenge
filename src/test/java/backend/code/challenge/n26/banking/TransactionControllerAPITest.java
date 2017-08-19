package backend.code.challenge.n26.banking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static backend.code.challenge.n26.banking.util.MultiLineString.multiLineString;
import static java.lang.String.valueOf;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerAPITest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldReturnStatusAs204WhenTimeIsBehind60Seconds() throws Exception {
        mvc.perform(post("/transactions")
                .contentType("application/json")
                .content(multiLineString(/*
                    {
                        "amount": 12.3,
                        "timestamp": 1478192204000
                    }
                */)
                ))
                .andExpect(status().is(204));
    }

    @Test
    public void shouldReturnStatusAs400WhenTheBodyIsNotPresent() throws Exception {
        mvc.perform(post("/transactions")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().is(204));
    }

    @Test
    public void shouldReturnStatusAs201ForValidTransaction() throws Exception {
        String content = multiLineString(/*
                    {
                        "amount": 12.3,
                        "timestamp": 1478192204000
                    }
                */);
        String payload = content.replace("1478192204000", valueOf(System.currentTimeMillis()));

        mvc.perform(post("/transactions")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().is(201));
    }

    @Test
    public void shouldReturnStatisticsForTheTransactions() throws Exception {
        String content = multiLineString(/*
                    {
                        "amount": 12.3,
                        "timestamp": 1478192204000
                    }
                */);
        String firstTransaction = content.replace("1478192204000", valueOf(System.currentTimeMillis()));

        mvc.perform(post("/transactions")
                .contentType("application/json")
                .content(firstTransaction))
                .andExpect(status().is(201));

        String secondTransaction = content.replace("1478192204000", valueOf(System.currentTimeMillis()));
        secondTransaction = secondTransaction.replace("12.3", "10");

        mvc.perform(post("/transactions")
                .contentType("application/json")
                .content(secondTransaction))
                .andExpect(status().is(201));

        mvc.perform(get("/statistics"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.sum", is(22.3)))
                .andExpect(jsonPath("$.average", is(11.15)))
                .andExpect(jsonPath("$.minimum", is(10.0)))
                .andExpect(jsonPath("$.maximum", is(12.3)));

    }
}
