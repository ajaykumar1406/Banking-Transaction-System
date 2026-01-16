package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.model.Account;

public class BankingService {

    private AccountDAO accountDAO;

    public BankingService() {
        this.accountDAO = new AccountDAO();
    }

    public boolean createAccount(Account account) {
        return accountDAO.createAccount(account);
    }

    public Account login(String accountNumber, String pin) {
        return accountDAO.validateLogin(accountNumber, pin);
    }

    public Account getAccount(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    public boolean deposit(String accountNumber, double amount) {
        return accountDAO.deposit(accountNumber, amount);
    }

    public boolean withdraw(String accountNumber, double amount) {
        return accountDAO.withdraw(accountNumber, amount);
    }

    public void showTransactionHistory(String accountNumber) {
        accountDAO.getTransactionHistory(accountNumber);
    }
}