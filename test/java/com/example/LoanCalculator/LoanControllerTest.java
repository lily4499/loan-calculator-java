package com.example.LoanCalculator;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoanControllerTest {
    @Test
    public void testCalculateLoan() {
        LoanController controller = new LoanController();
        String result = controller.calculateLoan(10000, 5, 2);
        assertEquals("Total repayment: $11000.0", result);
    }
}