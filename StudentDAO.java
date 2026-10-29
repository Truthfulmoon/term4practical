/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.serverapp.ServerDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import za.ac.cput.serverapp.DBConnection.DBConnection;
import za.ac.cput.serverapp.StudentDomain.Student;


/**
 *
 * @author rjbur
 */
public class StudentDAO {
    private Connection con;
    private PreparedStatement pstmt;

    public StudentDAO() {

        try {
            this.con=DBConnection.derbyConnnection();
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean insert(Student student){

        boolean success=false;
        String sql="INSERT INTO STUDENTS (STUDENT_NUMBER, STUDENT_NAME, SCORE) VALUES(?,?,?)";

        try{
            pstmt=con.prepareStatement(sql);

            pstmt.setInt(1, student.getStudentId());
            pstmt.setString(2, student.getStudentName());
            pstmt.setDouble(3, student.getStudentScore());
            int rowsAffected=pstmt.executeUpdate();
            success= rowsAffected >0;

        }catch (SQLException sqlException)
        {
            System.out.println("SQL Error: " + sqlException.getMessage());
        }
        finally {
            closeStatement();
        }
        return success;
    }

    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> record= new ArrayList<>();
        String sql = "SELECT STUDENT_NAME, STUDENT_NUMBER, SCORE FROM STUDENTS";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                record.add(new Student(rs.getString("STUDENT_NAME"),
                                       rs.getInt("STUDENT_NUMBER"),
                                       rs.getDouble("SCORE")));
            }
        } catch (SQLException e) {
            System.out.println("Retrieve error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { }
            try { if (ps != null) ps.close(); } catch (SQLException e) { }
        }
        return record;
    }


    private void closeStatement() {
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException exception) {
            System.out.println("Close statement error: " + exception.getMessage());
        }
    }


    public void createTable()
    {
        String sql = "CREATE TABLE STUDENTS ("
                + "STUDENT_NUMBER INT PRIMARY KEY, "
                + "STUDENT_NAME VARCHAR(100) NOT NULL, "
                + "SCORE DOUBLE NOT NULL)";

        Statement stmt = null;

        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("STUDENTS table created.");
        }
        catch (SQLException sqlException)
        {
            System.out.println("Table already exists or could not be created.");
        }
        finally
        {
            try
            {
                if (stmt != null)
                {
                    stmt.close();
                }
            }
            catch (SQLException exception)
            {
                System.out.println("Close statement error: " + exception.getMessage());
            }
        }
    }

    public void closeConnection()
    {
        try
        {
            if (con != null)
            {
                con.close();
            }
        }
        catch (SQLException exception)
        {
            System.out.println("Close connection error: " + exception.getMessage());
        }
    }
}