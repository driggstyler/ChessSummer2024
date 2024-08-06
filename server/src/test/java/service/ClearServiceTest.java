package service;

import Results.ClearResult;
import Services.ClearService;
import org.junit.jupiter.api.*;

public class ClearServiceTest {

    @BeforeEach
    public void setup() {

    }
    @Test
    @DisplayName("Clear successful")
    public void clearSuccess() {
        //make req
        ClearService clearService = new ClearService();
        //run clearservice
        ClearResult clearResult = clearService.Execute();
        //take result object and make assertion
        Assertions.assertEquals("Clear succeeded.", clearResult.getMessage(), "Database did not clear.");
        //Assert(expected, actual, "message")
    }

    @Test
    @DisplayName("Clear failed")
    public void clearFail() {}
}
