package pack;
import java.sql.*;
import java.util.*;
import java.io.*;

class AccountManager {
    Connection con;
    Scanner sc;
    TransactionHistory t;    

    AccountManager(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
        this.t=new TransactionHistory(con);
        Trigger();
    }



    void credit_money(long account_number) throws SQLException {
        sc.nextLine();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        if(amount < 0) {
            System.out.println(" Please Enter correct amount");
            return;
        }

        try {
            con.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                ps.setLong(1, account_number);
                ps.setString(2, security_pin);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement ps1 = con.prepareStatement(credit_query);
                    ps1.setDouble(1, amount);
                    ps1.setLong(2, account_number);
                    int rowsAffected = ps1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs." + amount + " credited Successfully");
                        con.commit();
                        con.setAutoCommit(true);
                        t.recordTransaction(account_number, "Credited", amount);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        con.rollback();
                        con.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.setAutoCommit(true);
    }

    void debit_money(long account_number) throws SQLException {
        sc.nextLine();
        System.out.print("Enter Amount to Debit : ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try {
            con.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                ps.setLong(1, account_number);
                ps.setString(2, security_pin);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double current_balance = rs.getDouble("balance");
                    if (amount <= current_balance) {
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement ps1 = con.prepareStatement(debit_query);
                        ps1.setDouble(1, amount);
                        ps1.setLong(2, account_number);
                        int rowsAffected = ps1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs." + amount + " debited Successfully");
                            con.commit();
                            con.setAutoCommit(true);
                            t.recordTransaction(account_number, "Debited", amount);

                            //t.concate("Debited", amount,date);
                            return;
                        } else {
                            System.out.println("Transaction Failed!");
                            con.rollback();
                            con.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance!");
                    }
                } else {
                    System.out.println("Invalid Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.setAutoCommit(true);
    }

    void transfer_money(long sender_account_number) throws SQLException {
        sc.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = sc.nextLong();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        
        if (sender_account_number == receiver_account_number) {
            System.out.println("Sender and Receiver account numbers cannot be the same.");
            return;
        }
        
        try {
            con.setAutoCommit(false);
            
            // Validate sender's account and pin
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, sender_account_number);
            ps.setString(2, security_pin);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                double current_balance = rs.getDouble("balance");
                if (amount <= current_balance) {
                    // Validate receiver's account exists
                    PreparedStatement psReceiver = con.prepareStatement("SELECT * FROM Accounts WHERE account_number = ?");
                    psReceiver.setLong(1, receiver_account_number);
                    ResultSet rsReceiver = psReceiver.executeQuery();
                    
                    if (rsReceiver.next()) {
                        // Perform Debit from sender
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement debitPreparedStatement = con.prepareStatement(debit_query);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        
                        // Perform Credit to receiver
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement creditPreparedStatement = con.prepareStatement(credit_query);
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        
                        // Execute both queries
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            // Record transaction history for both accounts
                            t.recordTransaction(sender_account_number, "Debited", amount);
                            t.recordTransaction(receiver_account_number, "Credited", amount);
                            
                            con.commit();
                            System.out.println("Transaction Successful! Rs." + amount + " Transferred.");
                        } else {
                            System.out.println("Transaction Failed.");
                            con.rollback();
                        }
                    } else {
                        System.out.println("Receiver account does not exist.");
                        con.rollback();
                    }
                } else {
                    System.out.println("Insufficient Balance.");
                    con.rollback();
                }
            } else {
                System.out.println("Invalid Security Pin.");
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback(); // Rollback in case of any exceptions
        } finally {
            con.setAutoCommit(true); // Ensure auto-commit is turned back on
        }
    }

    public void CheckBalance(long account_number) {
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, account_number);
            ps.setString(2, security_pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Balance: Rs. "+balance );
            } else {
                System.out.println("Invalid Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void deleteAccount(long account_number) throws SQLException {
        // Check if the account exists and get the balance
        String checkBalanceQuery = "SELECT balance FROM Accounts WHERE account_number = ?";
        try (PreparedStatement ps = con.prepareStatement(checkBalanceQuery)) {
            ps.setLong(1, account_number);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance > 0) {
                    System.out.println("Your account has a balance of Rs. " + balance);
                    System.out.println("Please withdraw the remaining balance before deleting the account.");
                    debit_money(account_number); 
                }

                String deleteAccountQuery = "DELETE FROM Accounts WHERE account_number = ?";
                try (PreparedStatement deletePs = con.prepareStatement(deleteAccountQuery)) {
                    deletePs.setLong(1, account_number);
                    int rowsAffected = deletePs.executeUpdate();
    
                    if (rowsAffected > 0) {
                        callProcedure(rowsAffected);
                        System.out.println("Account deleted successfully.");
                    } else {
                        System.out.println("Failed to delete the account.");
                    }
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    


        void Trigger() {
            String deleteAccount = "CREATE TRIGGER IF NOT EXISTS DeleteAccount " +
                    "BEFORE DELETE ON Accounts " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "INSERT INTO accounts_log (account_number, full_name, email, balance, security_pin) " +
                    "VALUES (OLD.account_number, OLD.full_name, OLD.email, OLD.balance, OLD.security_pin); " +
                    "END;";
        
            try (Statement stmt = con.createStatement()) {
                stmt.execute(deleteAccount);
                System.out.println("Account and related Transactions deleted successfully");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Trigger failed");
            }
        }
        


            public void callProcedure(long accountNumber) throws SQLException {
                String procedureCall = "{CALL DeleteTransactionHistory(?)}";
                try (CallableStatement cs = con.prepareCall(procedureCall)) {
                    cs.setLong(1, accountNumber);
                    cs.execute();
                    System.out.println(" Transactions deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Failed to delete Transactions.");
                    throw e; 
                }
            }


    public void transcation_history(long account_number) {
       t.getHistory(account_number);
      t.displayHistory();
       
    }

    public void generatePassbook(long account_number) {
        String query = "SELECT * FROM Accounts WHERE account_number = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, account_number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                double balance = rs.getDouble("balance");

                // Creating the passbook file
                String fileName = "Passbook_" + account_number + ".txt";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                    bw.write("******* Passbook *******\n");
                    bw.write("Account Number: " + account_number + "\n");
                    bw.write("Account Holder: " + fullName + "\n");
                    bw.write("Balance: Rs. " + balance + "\n\n");
                    bw.write("Transaction History:\n");

                    // Adding transaction history to passbook
                    t.getHistory(account_number);
                    for (String transaction : t.getAllTransactions()) {
                        bw.write(transaction + "\n");
                    }

                    System.out.println("Passbook generated successfully: " + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to generate passbook.");
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
