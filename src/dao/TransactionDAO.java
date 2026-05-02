package dao;

import model.Transaction;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Transaction CRUD operations.
 */
public class TransactionDAO {

    // ---------------------------------------------------------------
    // INSERT
    // ---------------------------------------------------------------
    public boolean addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (type, category_id, amount, date, description) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getType());
            ps.setInt   (2, t.getCategoryId());
            ps.setDouble(3, t.getAmount());
            ps.setDate  (4, new java.sql.Date(t.getDate().getTime()));
            ps.setString(5, t.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addTransaction error: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------
    public boolean updateTransaction(Transaction t) {
        String sql = "UPDATE transactions SET type=?, category_id=?, amount=?, date=?, description=? " +
                     "WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getType());
            ps.setInt   (2, t.getCategoryId());
            ps.setDouble(3, t.getAmount());
            ps.setDate  (4, new java.sql.Date(t.getDate().getTime()));
            ps.setString(5, t.getDescription());
            ps.setInt   (6, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateTransaction error: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------
    public boolean deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteTransaction error: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    // SELECT ALL (with category name via JOIN)
    // ---------------------------------------------------------------
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.type, t.category_id, c.name AS category_name, " +
                     "t.amount, t.date, t.description " +
                     "FROM transactions t JOIN categories c ON t.category_id = c.id " +
                     "ORDER BY t.date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId          (rs.getInt   ("id"));
                t.setType        (rs.getString ("type"));
                t.setCategoryId  (rs.getInt   ("category_id"));
                t.setCategoryName(rs.getString ("category_name"));
                t.setAmount      (rs.getDouble ("amount"));
                t.setDate        (rs.getDate   ("date"));
                t.setDescription (rs.getString ("description"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("getAllTransactions error: " + e.getMessage());
        }
        return list;
    }

    // ---------------------------------------------------------------
    // Helper: total income / expense
    // ---------------------------------------------------------------
    public double getTotalByType(String type) {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("getTotalByType error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // Monthly averages (used by forecasting)
    // ---------------------------------------------------------------
    public double getMonthlyAverage(String type) {
        String sql = "SELECT AVG(monthly) FROM (" +
                     "  SELECT SUM(amount) AS monthly " +
                     "  FROM transactions WHERE type=? " +
                     "  GROUP BY YEAR(date), MONTH(date)" +
                     ") sub";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("getMonthlyAverage error: " + e.getMessage());
        }
        return 0;
    }

    // ---------------------------------------------------------------
    // Monthly series (used by trend forecasting) – returns last N months
    // ---------------------------------------------------------------
    public List<Double> getMonthlySeries(String type, int months) {
        List<Double> series = new ArrayList<>();
        String sql = "SELECT SUM(amount) AS total " +
                     "FROM transactions WHERE type=? " +
                     "GROUP BY YEAR(date), MONTH(date) " +
                     "ORDER BY YEAR(date) DESC, MONTH(date) DESC " +
                     "LIMIT ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt   (2, months);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) series.add(rs.getDouble("total"));
        } catch (SQLException e) {
            System.err.println("getMonthlySeries error: " + e.getMessage());
        }
        return series;
    }
}
