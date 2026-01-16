package com.banking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.banking.model.Account;
import com.banking.util.DBConnection;
import java.sql.ResultSet ;

public class AccountDAO {

    public boolean createAccount(Account account) {

        String sql = "INSERT INTO accounts (account_number, holder_name, pin, balance) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {

            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getHolderName());
            ps.setString(3, account.getPin());
            ps.setDouble(4, account.getBalance());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Account getAccountByNumber(String accountNumber) {

        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, accountNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setHolderName(rs.getString("holder_name"));
                account.setPin(rs.getString("pin"));
                account.setBalance(rs.getDouble("balance"));
                return account;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public Account validateLogin(String accountNumber, String pin) {

        String sql = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, accountNumber);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setHolderName(rs.getString("holder_name"));
                account.setBalance(rs.getDouble("balance"));
                return account;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean deposit(String accountNumber, double amount) {

        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? ";
        String balanceSql = "SELECT balance FROM accounts WHERE account_number = ? for update";
        String insertTxnSql =
                "INSERT INTO transactions (account_number, txn_type, amount, balance_after) VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // 🔴 START TRANSACTION

            // Step 1: Update balance
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setDouble(1, amount);
            updatePs.setString(2, accountNumber);
            updatePs.executeUpdate();

            // Step 2: Fetch updated balance
            PreparedStatement balPs = conn.prepareStatement(balanceSql);
            balPs.setString(1, accountNumber);
            ResultSet rs = balPs.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            double newBalance = rs.getDouble("balance");

            // Step 3: Insert transaction record
            PreparedStatement txnPs = conn.prepareStatement(insertTxnSql);
            txnPs.setString(1, accountNumber);
            txnPs.setString(2, "DEPOSIT");
            txnPs.setDouble(3, amount);
            txnPs.setDouble(4, newBalance);
            txnPs.executeUpdate();

            conn.commit(); // ✅ COMMIT
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // ❌ ROLLBACK
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public boolean withdraw(String accountNumber, double amount) {

        String checkSql = "SELECT balance FROM accounts WHERE account_number = ? for update";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String insertTxnSql =
                "INSERT INTO transactions (account_number, txn_type, amount, balance_after) VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check balance
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, accountNumber);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            double currentBalance = rs.getDouble("balance");
            if (currentBalance < amount) {
                System.out.println("Insufficient balance!");
                conn.rollback();
                return false;
            }

            double newBalance = currentBalance - amount;

            // Step 2: Update balance
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setDouble(1, amount);
            updatePs.setString(2, accountNumber);
            updatePs.executeUpdate();

            // Step 3: Insert transaction record
            PreparedStatement txnPs = conn.prepareStatement(insertTxnSql);
            txnPs.setString(1, accountNumber);
            txnPs.setString(2, "WITHDRAW");
            txnPs.setDouble(3, amount);
            txnPs.setDouble(4, newBalance);
            txnPs.executeUpdate();

            conn.commit(); // ✅ COMMIT TRANSACTION
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // ❌ ROLLBACK ON ERROR
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void getTransactionHistory(String accountNumber) {

        String sql = "SELECT txn_type, amount, balance_after, txn_date " +
                        "FROM transactions " +
                        "WHERE account_number = ? " +
                        "ORDER BY txn_date DESC ,txn_id DESC";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();

            System.out.println("---- Transaction History ----");

            while (rs.next()) {
                System.out.println(
                        rs.getString("txn_type") + " | " +
                                rs.getDouble("amount") + " | " +
                                rs.getDouble("balance_after") + " | " +
                                rs.getTimestamp("txn_date")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}