package pack;
import java.sql.*;
import java.util.*;
import java.io.*;
class TransactionHistory {
    Connection connection;

   TransactionHistory(Connection connection) {
       this.connection = connection;
   }

   void recordTransaction(long accountNumber, String transactionType, double amount) {
       String query = "INSERT INTO Transaction (account_number, transaction_type, amount) VALUES (?, ?, ?)";
       try (PreparedStatement ps = connection.prepareStatement(query)) {
           ps.setLong(1, accountNumber);
           ps.setString(2, transactionType);
           ps.setDouble(3, amount);
           ps.executeUpdate();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

  public  void getHistory(long accountNumber) {
    history.clear();
    String query = "SELECT transaction_type, amount, transaction_date FROM Transaction WHERE account_number = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setLong(1, accountNumber);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String type = rs.getString("transaction_type");
            double amount = rs.getDouble("amount");
            Timestamp date = rs.getTimestamp("transaction_date");
            concate(type, amount,date);
           
        }
       
    } catch (SQLException e) {
        e.printStackTrace();
    }

   
}
Stacks history = new Stacks(100);
 void concate(String transaction, double amount,Timestamp date) {
       String ans = transaction + " Rs." + amount+" on "+date;
      history. push(ans);
   }

   void displayHistory() {
    history.display();  
}

List<String> getAllTransactions() {
    ArrayList<String> allTransactions = new ArrayList<>();
    for (int i = history.top; i >= 0; i--) {
        allTransactions.add(history.a[i]);
    }
    return allTransactions;
}

   Stacks getHistroy(){
       return history;
   }

  public  class Stacks{
    int top,cap;
   String a[];

    Stacks(int size){
        this.cap=size;
        a=new String[size];
        top=-1;
    }
    void push(String data){
        if (top==cap-1){
        System.out.println("Transactions exceeded limits of 100 ");
        clear();
        history.display();
    }
        else{
            top++;
            a[top]=data;
        }
    }

    String pop() {
        if (top == -1) {
            System.out.println("No transactions yet");
            return null;
        } else {
            String data = a[top];
            top--;
            return data;
        }
    }

    void display(){
        for (int i  = top; i >=0 ; i--) {
            System.out.println(a[i]);
        }
        if(top==-1)
        System.out.println(" No transactions yet ");
        
    }
    void clear(){
        top=-1;
    }
    
}}
