package com.example.LoanCalculator;

public class LoanController {
    public String calculateLoan(double amount, double rate, int years) {
        double interest = amount * rate * years / 100;
        return "Total repayment: $" + (amount + interest);
    }

    public static void main(String[] args) {
        LoanController controller = new LoanController();
        System.out.println(controller.calculateLoan(10000, 5, 2));
    }
}