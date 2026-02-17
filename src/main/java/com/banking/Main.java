package com.banking;

import java.util.Scanner;
import com.banking.model.Account;
import com.banking.service.BankingService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BankingService service = new BankingService();

        System.out.println("===== Welcome to Banking System =====");

        System.out.println("1. Create New Account");
        System.out.println("2. Login");
        System.out.print("Enter your choice: ");
        int firstChoice = sc.nextInt();
        sc.nextLine(); // consume newline

        Account account = null;

        // 🆕 CREATE ACCOUNT
        if (firstChoice == 1) {

            System.out.print("Enter Account Number: ");
            String accNo = sc.nextLine();

            System.out.print("Enter Holder Name: ");
            String name = sc.nextLine();

            System.out.print("Set PIN: ");
            String pin = sc.nextLine();

            System.out.print("Enter Initial Balance: ");
            double balance = sc.nextDouble();
            sc.nextLine();

            Account newAccount = new Account(accNo, name, pin, balance);

            if (service.createAccount(newAccount)) {
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account creation failed!");
                sc.close();
                return;
            }

            // Auto-login after creation
            account = service.login(accNo, pin);
        }

        // 🔐 LOGIN
        else if (firstChoice == 2) {

            System.out.print("Enter Account Number: ");
            String accNo = sc.nextLine();

            System.out.print("Enter PIN: ");
            String pin = sc.nextLine();

            account = service.login(accNo, pin);

            if (account == null) {
                System.out.println("Invalid login. Exiting...");
                sc.close();
                return;
            }
        } else {
            System.out.println("Invalid choice. Exiting...");
            sc.close();
            return;
        }

        System.out.println("Login successful!");
        System.out.println("Welcome, " + account.getHolderName());

        int choice;

        // 📋 MAIN MENU
        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transaction History");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    Account refreshed =
                            service.getAccount(account.getAccountNumber());
                    System.out.println("Current Balance: " +
                            refreshed.getBalance());
                    break;

                case 2:
                    System.out.print("Enter amount to deposit: ");
                    double dep = sc.nextDouble();

                    if (dep>0 && service.deposit(account.getAccountNumber(), dep)) {
                        Account updated =
                                service.getAccount(account.getAccountNumber());
                        System.out.println("Deposit successful!");
                        System.out.println("Current Balance: " +
                                updated.getBalance());
                    } else {
                        System.out.println("Deposit failed!");
                    }
                    break;

                case 3:
                    System.out.print("Enter amount to withdraw: ");
                    double wit = sc.nextDouble();

                    if (wit>0 && service.withdraw(account.getAccountNumber(), wit)) {
                        Account updated =
                                service.getAccount(account.getAccountNumber());
                        System.out.println("Withdrawal successful!");
                        System.out.println("Current Balance: " +
                                updated.getBalance());
                    } else {
                        System.out.println("Withdrawal failed!");
                    }
                    break;

                case 4:
                    service.showTransactionHistory(
                            account.getAccountNumber());
                    break;

                case 5:
                    System.out.println("Thank you for using Banking System.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }

        } while (choice != 5);

        sc.close();
    }
}