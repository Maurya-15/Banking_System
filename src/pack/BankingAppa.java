package pack;
import java.sql.*;
import java.util.*;

class BankingApp {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/bank";
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, username, password);
        Scanner scanner = new Scanner(System.in);
        User user = new User(connection, scanner);
        Accounts accounts = new Accounts(connection, scanner);
        AccountManager accountManager = new AccountManager(connection, scanner);
        Admin admin = new Admin(connection, scanner);
        String email;
        long account_number;

        while (true) {
            System.out.println();
            System.out.println("********************  WELCOME TO BANKING SYSTEM ********************");
            System.out.println();
            System.out.println("1. Admin ");
            System.out.println("2. User ");
            System.out.println("3. Exit ");
            System.out.print("Enter your choice : ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            boolean flag = true;
            System.out.print("Enter password : ");
            String ps = scanner.next();
            if (choice == 1) {  // Admin Panel
                do {
                    scanner.nextLine();
                    if (ps.equals("admin")) {
                        System.out.println();
                        System.out.println("Admin Panel");
                        System.out.println("1. Add New User");
                        System.out.println("2. Delete User");
                        System.out.println("3. Update User");
                        System.out.println("4. Generate Reports");
                        System.out.println("5. Exit");
                        System.out.print("Enter your choice: ");
                        int choice1 = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice1) {
                            case 1:
                                user.register();
                                break;
                            case 2:
                                System.out.print("Enter Email: ");
                                String emailToDelete = scanner.nextLine();
                                if (user.user_exist(emailToDelete)) {
                                    admin.deleteUser(emailToDelete);
                                    
                                    System.out.println("User deleted successfully!");
                                    return;
                                } else {
                                    System.out.println("User does not exist!");
                                }
                                break;
                            case 3:
                                boolean flag1 = true;
                                do {
                                    System.out.println("\n 1. Update email");
                                    System.out.println("2. Update name");
                                    System.out.println("3. Exit");
                                    System.out.print("Enter your choice: ");
                                    int choice2 = scanner.nextInt();
                                    scanner.nextLine();

                                    switch (choice2) {
                                        case 1:
                                            System.out.print("Enter Email: ");
                                            email = scanner.nextLine();
                                            if (user.user_exist(email)) {
                                                admin.updateEmail(email);
                                            } else {
                                                System.out.println("User does not exist for this email address");
                                            }
                                            break;
                                        case 2:
                                            System.out.print("Enter Email: ");
                                            email = scanner.nextLine();
                                            if (user.user_exist(email)) {
                                                admin.updateName(email);
                                            } else {
                                                System.out.println("User does not exist for this email address");
                                            }
                                            break;
                                        case 3:
                                            flag1 = false;
                                            break;
                                        default:
                                            System.out.println("Invalid choice!");
                                    }
                                } while (flag1);
                                break;

                            case 4:
                                System.out.println("1. Generate Transaction History");
                                System.out.println("2. Generate Passbook");
                                System.out.println("3. Exit");
                                System.out.print("Enter your choice: ");
                                int choice3 = scanner.nextInt();
                                scanner.nextLine();

                                switch (choice3) {
                                    case 1:
                                        System.out.print("Enter Account Number: ");
                                        account_number = scanner.nextLong();

                                        String sql="select email from Account where account_number=?";
                                        PreparedStatement pstmt = connection.prepareStatement(sql);
                                        pstmt.setLong(1, account_number);
                                        ResultSet rs = pstmt.executeQuery();
                                        if(rs.next()){
                                            email=rs.getString("email");
                                            if (accounts.account_exist(email)) {
                                                accountManager.transcation_history(account_number);}
                                            
                                        }
                                        else{
                                            System.out.println("Account does not exist!");
                                        }
                                        break;

                                    case 2:
                                     System.out.print("Enter Account Number: ");
                                     account_number = scanner.nextLong();
                                   accountManager.generatePassbook(account_number);
                                   break;

                                    case 3:
                                        break;
                                    default:
                                        System.out.println("Invalid choice!");
                                }
                                break;

                            case 5:
                                flag = false;
                                break;

                            default:
                                System.out.println("Invalid choice!");
                        }
                    } else {
                        System.out.println("Invalid password!");
                    }
                } while (flag);
            } else if (choice == 2) {  // User Panel
                System.out.println("************** User Panel ************");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                String ch1 = scanner.next();

                switch (ch1) {
                    case "1":
                        user.register();
                        break;
                    case "2":
                        email = user.login();
                        if (email != null) {
                            System.out.println();
                            System.out.println("User Logged In!");
                            if (!accounts.account_exist(email)) {
                                System.out.println();
                                System.out.println("1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                if (scanner.nextInt() == 1) {
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account Created Successfully");
                                    System.out.println("Your Account Number is: " + account_number);
                                } else {
                                    break;
                                }
                            }

                            account_number = accounts.getAccount_number(email);
                            int choice2 = 0;
                            while (choice2 != 9) {
                                System.out.println();
                                System.out.println("********** Welcome User **********");
                                System.out.println();
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Transaction history");
                                System.out.println("6. Generate Passbook");
                                System.out.println("7. Delete Transactions");
                                System.out.println("8. Delete Account");
                                System.out.println("9. Log Out");
                                System.out.print("Enter your choice: ");
                                choice2 = scanner.nextInt();

                                switch (choice2) {
                                    case 1:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(account_number);
                                        break;
                                    case 4:
                                        accountManager.CheckBalance(account_number);
                                        break;
                                    case 5:
                                        accountManager.transcation_history(account_number);
                                        break;
                                    case 6:
                                        accountManager.generatePassbook(account_number);
                                        break;
                                    case 7:
                                        accountManager.callProcedure(account_number);
                                        break;
                                    case 8:
                                        accountManager.deleteAccount(account_number);
                                        break;
                                    case 9:
                                        System.out.println("Logging out...");
                                        break;
                                    default:
                                        System.out.println("Invalid choice!");
                                }
                            }
                        } else {
                            System.out.println("Incorrect Email or Password!");
                        }
                        break;
                    case "3":
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM!!!");
                        System.out.println("Exiting System!");
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                }
            } else if (choice == 3) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Enter a valid choice.");
            }
        }
    }
}
