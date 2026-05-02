package controller;

import dao.CategoryDAO;
import dao.TransactionDAO;
import model.Category;
import model.Transaction;

import java.util.List;

/**
 * Controller: mediates between Views and DAOs.
 * All business logic / validation lives here.
 */
public class TransactionController {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO    categoryDAO    = new CategoryDAO();

    // ---- Transaction CRUD -------------------------------------------

    public boolean addTransaction(Transaction t) {
        if (t.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        return transactionDAO.addTransaction(t);
    }

    public boolean updateTransaction(Transaction t) {
        if (t.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        return transactionDAO.updateTransaction(t);
    }

    public boolean deleteTransaction(int id) {
        return transactionDAO.deleteTransaction(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    // ---- Summary figures for Dashboard ------------------------------

    public double getTotalIncome()  { return transactionDAO.getTotalByType("Income"); }
    public double getTotalExpense() { return transactionDAO.getTotalByType("Expense"); }
    public double getBalance()      { return getTotalIncome() - getTotalExpense(); }

    // ---- Categories -------------------------------------------------

    public List<Category> getCategories() {
        return categoryDAO.getAllCategories();
    }

    // ---- Forecasting ------------------------------------------------

    /**
     * Basic forecast: average monthly income - average monthly expense.
     * Returns array [avgIncome, avgExpense, predictedBalance].
     */
    public double[] basicForecast() {
        double avgIncome  = transactionDAO.getMonthlyAverage("Income");
        double avgExpense = transactionDAO.getMonthlyAverage("Expense");
        double predicted  = avgIncome - avgExpense;
        return new double[]{avgIncome, avgExpense, predicted};
    }

    /**
     * Trend-based forecast using simple linear regression on monthly totals.
     * Returns the predicted value for (n+1)th month for both income and expense.
     * Returns array [predictedIncome, predictedExpense, predictedBalance].
     */
    public double[] trendForecast() {
        List<Double> incomeSeries  = transactionDAO.getMonthlySeries("Income",  6);
        List<Double> expenseSeries = transactionDAO.getMonthlySeries("Expense", 6);

        double predIncome  = linearExtrapolate(incomeSeries);
        double predExpense = linearExtrapolate(expenseSeries);
        double predBalance = predIncome - predExpense;

        return new double[]{predIncome, predExpense, predBalance};
    }

    /**
     * Simple linear regression extrapolation.
     * x = month index (1, 2, … n), y = amount.
     * Returns predicted y for x = n+1.
     */
    private double linearExtrapolate(List<Double> series) {
        int n = series.size();
        if (n == 0) return 0;
        if (n == 1) return series.get(0);

        // Series comes in DESC order from DB, reverse for chronological
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = series.get(n - 1 - i);   // chronological order
            sumX  += x;
            sumY  += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope     = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        double predicted = slope * (n + 1) + intercept;
        return Math.max(predicted, 0);
    }
}
