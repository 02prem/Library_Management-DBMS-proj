import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
// import org.apache.commons.lang.StringUtils;

public class JdbcDemo {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/library_1?publicKeyretrieval=true&useSSL=false";
    static final String USER = "root";
    static final String PASS = "bks140875";

    static String man_id;
    static String man_name;
    static int books_available;

    public static void main(String[] args) throws IOException {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            InputStreamReader in = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(in);
            System.out.print("\nWant to continue? [Y/N]:");
            String input = br.readLine();
            if (input.equals("N") || input.equals("n")) {
                System.exit(0);
            }
            clear();

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            println("\n                               Welcome To Library !!\n");
            
            println("Login To Continue\n");

            choice(stmt, br);

            br.close();
            in.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    static void choice(Statement stmt, BufferedReader br) throws IOException, SQLException {
        println("Login As - ");
        println("1. Student");
        println("2. Library Manager");
        println("3. Exit");

        print("Enter Your Choice: ");
        int input = Integer.parseInt(br.readLine());
        clear();

        if (input == 1)
            student_choice(stmt, br);
        else if (input == 2)
            manager_choice(stmt, br);
        else
            System.exit(0);
        choice(stmt, br);
    }

    static void student_choice(Statement stmt, BufferedReader br) throws IOException, SQLException {
        System.out.println("\n\n");

        println("Choice from the available options- ");
        println("1. List of available books");
        println("2. Return the issued book");
        println("0. Exit");
        print("Enter your choice: ");
        int input = Integer.parseInt(br.readLine());
        clear();

        if (input == 1)
            list_of_books(stmt, br);
        else if(input == 2)
            return_a_book(stmt, br);
        else if (input == 0)
            return;
        student_choice(stmt, br);
    }

    static void list_of_books(Statement stmt, BufferedReader br) throws IOException {
        clear();
        String books = "select * from book";
        ResultSet rs = executeSqlCommand(books, stmt);

        try {
            println("List of Available books:\n");
            String format = "|%1$-10s|%2$-25s|%3$-15s|\n";
            System.out.println("+----------------------------------------------------+");
            System.out.format(format, " Book ID", " Book Name", " Author");
            System.out.println("+----------------------------------------------------+");

            // println("List of Available books:\n");
            int f = 0;
            while (rs.next()) {
                // Retrieve by column name
                String id = rs.getString("book_id");
                String name = rs.getString("book_name");
                String author = rs.getString("book_name");
                String student_rno = rs.getString("student_rno");
                String admin_roll_number = rs.getString("admin_roll_number");

                if (student_rno == null || admin_roll_number == null) {
                    f = 1;
                    // Display values
                    System.out.format(format, " "+id, " "+name, " "+author);
                }
            }
            if(f == 1)
                System.out.println("+----------------------------------------------------+");

            if (f == 0) {
                println("Sorry, but no books are available");
                books_available = 0;
            } else {
                books_available = 1;
            }

            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static boolean verify_manager(Statement stmt, BufferedReader br) throws IOException, SQLException {
        System.out.println("\n\n");
        print("Enter ID: ");
        String id = br.readLine();
        print("Enter Password: ");
        String password = br.readLine();

        clear();

        String name = null;

        String managers = "select * from library_manager";
        ResultSet rs = executeSqlCommand(managers, stmt);

        int f = 0;
        while (rs.next()) {
            // Retrieve by column name
            String possible_id = rs.getString("admin_number");
            String given_name = rs.getString("admin_name");
            String possible_password = rs.getString("password_admin");

            if (possible_id.equals(id) && password.equals(possible_password)) {
                f = 1;
                name = given_name;
                break;
            }
        }

        if (f == 1) {
            man_id = id;
            man_name = name;
            return true;
        } else
            return false;
    }

    static void manager_choice(Statement stmt, BufferedReader br) throws IOException, SQLException {
        if (verify_manager(stmt, br)) {
            manager_choices(stmt, br);
        }

        else {
            System.out.println("\n\n");
            println("ERROR: Entered details were incorrect.\n\n");
            choice(stmt, br);
            return;
        }
    }

    static void manager_choices(Statement stmt, BufferedReader br) throws IOException, SQLException {
        System.out.println("\n\n\n\n\n\n");

        println("Choice from the available options- ");
        println("1.  List of available books");
        println("2.  Issue a book");
        // println("3.  Return a book");
        println("3.  Add a student");
        println("4.  Add a library manager");
        println("5.  Add a book");
        println("6.  Delete a book");
        println("7.  Delete a student account");
        println("8.  Delete your account");
        println("9.  List of all books");
        println("10. List of all students");
        println("11. List of students who have issued a book");
        println("0.  Log Out");
        print("\n");
        print("Enter your choice: ");
        int input = Integer.parseInt(br.readLine());
        print("\n");
        clear();

        if (input == 1)
            list_of_books(stmt, br);
        else if (input == 2)
            issue_a_book(stmt, br);
        // else if (input == 3)
        //     return_a_book(stmt, br);
        else if (input == 3)
            add_a_student(stmt, br);
        else if (input == 4)
            add_a_manager(stmt, br);
        else if (input == 5)
            add_a_book(stmt, br);
        else if (input == 6)
            delete_a_book(stmt, br);
        else if (input == 7)
            delete_a_student(stmt, br);
        else if (input == 8) {
            delete_a_manager(stmt, br);
            return;
        } else if (input == 9)
            list_of_all_books(stmt, br);
        else if(input == 10)
            list_of_all_students(stmt, br);
        else if(input == 11)
            students_issued(stmt, br);
        else if (input == 0)
            return;
        
        manager_choices(stmt, br);
    }

    static void students_issued(Statement stmt, BufferedReader br) throws IOException
    {
        clear();
        String students = "select * from student where book_id IS NOT NULL";
        ResultSet rs = executeSqlCommand(students, stmt);
        
        try{
            println("List of Students:\n");
            int f = 0;

            String format = "|%1$-15s|%2$-25s|%3$-10s|\n";
            System.out.println("+----------------------------------------------------+");
            System.out.format(format, " Roll Number", " Name", " Book ID");
            System.out.println("+----------------------------------------------------+");
            while(rs.next())
            {
                f = 1;
                String roll_no = rs.getString("student_roll_number");
                String name = rs.getString("student_full_name");
                String id = rs.getString("book_id");

                System.out.format(format, " "+roll_no, " "+name, " "+id);
            }
            if(f == 1)
            System.out.println("+----------------------------------------------------+");

            if(f == 0)
                println("**No students have issued any book**");

            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void list_of_all_students(Statement stmt, BufferedReader br) throws IOException {
        clear();
        String students = "select * from student";
        ResultSet rs = executeSqlCommand(students, stmt);

        try{
            println("List of Students:\n");
            int f = 0;

            String format = "|%1$-15s|%2$-25s|%3$-10s|\n";
            System.out.println("+----------------------------------------------------+");
            System.out.format(format, " Roll Number", " Name", " Book ID");
            System.out.println("+----------------------------------------------------+");
            while(rs.next())
            {
                f = 1;
                String roll_no = rs.getString("student_roll_number");
                String name = rs.getString("student_full_name");
                String id = rs.getString("book_id");

                System.out.format(format, " "+roll_no, " "+name, " "+id);
            }
            if(f == 1)
            System.out.println("+----------------------------------------------------+");

            if(f == 0)
                println("**No student found**");

            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void list_of_all_books(Statement stmt, BufferedReader br) throws IOException {
        clear();
        String books = "select * from book";
        ResultSet rs = executeSqlCommand(books, stmt);

        try {
            println("List of Available books:\n");
            int f = 0;
            
            String format = "|%1$-10s|%2$-25s|%3$-15s|%4$-15s|%5$-15s|\n";
            System.out.println("+------------------------------------------------------------------------------------+");
            System.out.format(format, " Book ID", " Book Name", " Author", " Student No.", " Admin No.");
            System.out.println("+------------------------------------------------------------------------------------+");
            
            // prem
            // ----

            while (rs.next()) {
                f = 1;
                // Retrieve by column name
                String id = rs.getString("book_id");
                String name = rs.getString("book_name");
                String author = rs.getString("book_author");
                String student_rno = rs.getString("student_rno");
                String admin_roll_number = rs.getString("admin_roll_number");

                // String format = "|%1$-10s|%2$-15s|%3$-15s|%4$-15s|%4$-10s|\n";
                System.out.format(format, " "+id, " "+name, " "+author, " "+student_rno, " "+admin_roll_number);
                // System.out.format(format,StringUtils.center("Real",10),StringUtils.center("",10),StringUtils.center("Gagnon",20);
                
            }
            if(f == 1)
                System.out.println("+------------------------------------------------------------------------------------+");

            if (f == 0) {
                println("Sorry, but no books were found");
            }

            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void delete_a_manager(Statement stmt, BufferedReader br) {
        try {
            print("\n");
            print("All details regarding your account will be deleted. Are you sure? [N/Y]: ");
            String sure = br.readLine();

            if (sure.equals("Y") || sure.equals("y")) {
                String managers = "DELETE FROM library_manager where admin_number = \'" + man_id + "\'";
                int result = updateSqlCommand(managers, stmt);

                if (result != 0)
                    println("Your account has been deleted successfully!");
                else
                    println("Something went wrong!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delete_a_student(Statement stmt, BufferedReader br) {
        try {
            print("Enter the ID of the student: ");
            String id = br.readLine();
            print("\n");
            print("All details regarding the account will be deleted. Are you sure? [N/Y]: ");
            String sure = br.readLine();
            clear();

            if (sure.equals("Y") || sure.equals("y")) {
                String managers = "DELETE FROM student where student_roll_number = \'" + id + "\'";
                int result = updateSqlCommand(managers, stmt);

                if (result != 0)
                    println("Student account having id="+id+" has been deleted successfully!");
                else
                    println("Something went wrong!");
            } else
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delete_a_book(Statement stmt, BufferedReader br) {
        try {
            print("Enter the ID of the book: ");
            String id = br.readLine();

            String x = "select book_id from student where book_id= \'" + id + "\'";
            ResultSet y = executeSqlCommand(x, stmt);

            if(y.next())
            {
                println("Cannot delete this book as it is already been issued !");
                // return;
            }
            else
            {
                clear();

                String managers = "DELETE FROM book where book_id = \'" + id + "\'";
                int result = updateSqlCommand(managers, stmt);

                if (result != 0)
                    println("Book Deleted!");
                else
                    println("Something went wrong!");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void issue_a_book(Statement stmt, BufferedReader br) throws IOException, SQLException {
        try {
            list_of_books(stmt, br);
            if (books_available == 0) {
                println("Sorry, but no books are available");
            } else {

                print("Enter the ID of the book: ");
                String id = br.readLine();

                print("Enter the id of the student: ");
                String student_id = br.readLine();

                clear();

                String managers = "UPDATE book SET student_rno = \'" + student_id + "\', admin_roll_number = \'"
                        + man_id + "\' where book_id = \'" + id + "\'";
                int result = updateSqlCommand(managers, stmt);

                if (result != 0)
                {
                    update_student(stmt, br, id, student_id);
                    println("Book Issued!");
                }
                else
                    println("Something went wrong!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void update_student(Statement stmt, BufferedReader br, String bookid, String studentid) throws IOException, SQLException
    {
        try{
            String student = "UPDATE student SET book_id=\'" + bookid + "\' where student_roll_number=\'"+ studentid+ "\'";
            int result = updateSqlCommand(student, stmt);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void return_a_book(Statement stmt, BufferedReader br) throws IOException {
        try {
            print("Enter the ID of the book: ");
            String id = br.readLine();

            clear();

            String managers = "UPDATE book SET student_rno = null, admin_roll_number = null where book_id = \'" + id + "\'";
            String student = "UPDATE student SET book_id=null where book_id = \'" + id + "\'";
            int result = updateSqlCommand(managers, stmt);

            if (result != 0)
            {
                int result2 = updateSqlCommand(student, stmt);
                println("Thank you for returning the book!");
            }
            else
                println("Something went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void add_a_student(Statement stmt, BufferedReader br) throws IOException {
        try {
            print("Enter the ID of the student: ");
            String id = br.readLine();
            print("Enter the name of the student: ");
            String name = br.readLine();

            clear();

            String managers = "insert into student(student_roll_number,student_full_name,book_id) VALUES(\'" + id
                    + "\',\'" + name + "\',NULL)";
            int result = updateSqlCommand(managers, stmt);

            if (result != 0)
                println("Student Info Added");
            else
                println("Something went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void add_a_book(Statement stmt, BufferedReader br) throws IOException {
        try {
            print("Enter the ID of book: ");
            String id = br.readLine();
            print("Enter the name of the book: ");
            String name = br.readLine();
            print("Enter the author of the book: ");
            String author = br.readLine();

            clear();

            String managers = "insert into book(book_id,book_name,book_author,student_rno,admin_roll_number)" +
                    " VALUES(\'" + id + "\',\'" + name + "\',\'" + author + "\',NULL,NULL)";
            int result = updateSqlCommand(managers, stmt);

            if (result != 0)
                println("Booked Added");
            else
                println("Something went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void add_a_manager(Statement stmt, BufferedReader br) throws IOException {
        try {
            // print("Enter the ID of the manager: ");
            // String id = br.readLine();
            print("Enter the name of the manager: ");
            String name = br.readLine();
            print("Enter the password: ");
            String password = br.readLine();

            clear();

            String managers = "insert into library_manager(admin_name,password_admin) VALUES(\'" + name + "\',\'" + password + "\')";
            int result = updateSqlCommand(managers, stmt);

            if (result != 0)
                println("Library Manager Info Added");
            else
                println("Something went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

    }

    static ResultSet executeSqlCommand(String sql, Statement stmt) {
        try {
            // STEP 3: Query to database
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
            // STEP 4: Extract data from result set

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static int updateSqlCommand(String sql, Statement stmt) throws SQLException {
        int result = stmt.executeUpdate(sql);
        return result;
    }

    static void println(String s) {
        System.out.println(s);
    }

    static void print(String s) {
        System.out.print(s);
    }

}
