package pack;
import java.sql.*;
import java.util.*;

public class Admin {
     Connection connection;
    Scanner scanner;

   public Admin(Connection connection, Scanner scanner) {
       this.connection = connection;
       this.scanner = scanner;

   }

   void deleteUser(String email) throws Exception {
    String delete_user_query = "DELETE FROM USER WHERE email = ?";
       try (PreparedStatement ps = connection.prepareStatement(delete_user_query)) {
           ps.setString(1, email);
           ps.executeUpdate();
       } catch (SQLException e) {
           System.out.println("Error deleting user: " + e.getMessage());
       }
       return;
   }

   void updateEmail(String email) throws Exception {
    String update_email_query = "UPDATE USER SET email =? WHERE email =?";
       System.out.print("Enter new email: ");
       String new_email = scanner.nextLine();
       try (PreparedStatement ps = connection.prepareStatement(update_email_query)) {
           ps.setString(1, new_email);
           ps.setString(2, email);
           ps.executeUpdate();
       } catch (SQLException e) {
           System.out.println("Error updating email: " + e.getMessage());
       }
       return;
   }

   void updateName(String email) throws Exception {
    String update_name_query = "UPDATE USER SET user_name =? WHERE email =?";
    System.out.print("Enter new name: ");
    String new_name = scanner.nextLine();
    try (PreparedStatement ps = connection.prepareStatement(update_name_query)) {
        ps.setString(1, new_name);
        ps.setString(2, email);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error updating name: " + e.getMessage());
    }
}



    
}
