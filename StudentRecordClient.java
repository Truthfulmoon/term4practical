/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package za.ac.cput.studentclient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRecordClient implements ActionListener{
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static JFrame frame;
    private static JTextField nameField;
    private static JTextField idField;
    private static JTextField scoreField;
    private static JTextField searchField;
    private static JTextArea recordsTextArea;
    private static JButton addButton;
    private static JButton retrieveButton;
    private static JButton searchButton;
    private static JButton exitButton;
    private static Socket socket;

    // Connect to the server, create io streams, and call the method that defines the gui
    public StudentRecordClient(){
        try{
            socket = new Socket("127.0.0.1",6666);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }catch(IOException ex){
            JOptionPane.showMessageDialog(null, "Cannot connect to server" + ex.getMessage());
            System.exit(1);
        }
        createAndShowGUI();
    }//end constructor

//------------------------------------------------------------------------------

    //Create the swing-based gui
    private void createAndShowGUI() {
        frame = new JFrame("Student Record Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Score:"));
        scoreField = new JTextField();
        inputPanel.add(scoreField);
        searchField = new JTextField();
        inputPanel.add(searchField);
        searchButton = new JButton("Search");
        inputPanel.add(searchButton);
        searchButton.addActionListener(this);

        frame.add(inputPanel, BorderLayout.NORTH);

        addButton = new JButton("Add Record");
        addButton.addActionListener(this);
        retrieveButton = new JButton("Retrieve Records");
        retrieveButton.addActionListener(this);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(exitButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        recordsTextArea = new JTextArea(10,10);
        recordsTextArea.setEditable(false);
        frame.add(new JScrollPane(recordsTextArea), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

//------------------------------------------------------------------------------

    private static void addStudentRecord() {
        try{
            String name = nameField.getText().trim();
            int id = Integer.parseInt(idField.getText().trim());
            double score = Double.parseDouble(scoreField.getText().trim());

            out.writeObject("ADD");
            out.writeObject(new Student(name, id, score));
            out.flush();

            nameField.setText("");
            idField.setText("");
            scoreField.setText("");
            nameField.requestFocus();

        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(frame, "Score must be a number.");
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error sending record: " + ex.getMessage());
        }
    }//end addStudentRecord()

//------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static void retrieveStudentRecords() {
        try{
            out.writeObject("Retrieve");
            out.flush();
            List<Student> list = (ArrayList<Student>) in.readObject();
            displayStudentRecords(list);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving records: " + ex.getMessage());
        }
    }//end retrieveStudentRecords()

//------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static void searchStudentRecords() {
        try{
            out.writeObject("SEARCH");
            out.writeObject(searchField.getText().trim());
            out.flush();
            List<Student> list = (ArrayList<Student>) in.readObject();
            displayStudentRecords(list);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(frame, "Error searching: " + ex.getMessage());
        }
    }//end searchStudentRecords()

//------------------------------------------------------------------------------

    private static void displayStudentRecords(List<Student> studentList) {
        recordsTextArea.setText("");
        if (studentList.isEmpty()){
            recordsTextArea.append("No records found.\n");
            return;
        }
        for (Student s : studentList){
            recordsTextArea.append(s.toString() + "\n");
        }
    }//end displayStudentRecords()

//------------------------------------------------------------------------------

    private static void closeConnection() {
        try{
            out.writeObject("EXIT");
            out.flush();
            String reply = (String) in.readObject();
            System.out.println("Server:" + reply);
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException | ClassNotFoundException ex){
            System.out.println("Error closing " + ex.getMessage());
        }
        System.exit(0);
    }//end closeConnection()

//------------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e){
        Object src = e.getSource();
        if (src == addButton) {
            addStudentRecord();
        } else if (src == retrieveButton) {
            retrieveStudentRecords();
        } else if (src == searchButton) {
            searchStudentRecords();
        } else if (src == exitButton) {
            closeConnection();
            System.exit(0);
        }
    }//end actionPerformed

//------------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRecordClient());
    }//end main
}//end class