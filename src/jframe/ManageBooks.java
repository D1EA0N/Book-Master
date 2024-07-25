/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author anonu
 */
public class ManageBooks extends javax.swing.JFrame {
    
    String book_name, author, category;   
    int book_id, quantity;
    DefaultTableModel model;
    /**
     * Creates new form ManageBooks
     */
    public ManageBooks() {
        initComponents();
        setBookDetails();
        txt_bookid.setText(generateBookId());  // Set the generated book ID
    }
    
    public boolean validateInput(){
       try {
            // Ensure book_id and quantity fields are not empty and contain valid integers
//            if (txt_bookid.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please enter the book ID");
//                return false;
//            }
//            book_id = Integer.parseInt(txt_bookid.getText());

            // Ensure book_name and author fields are not empty
            if (txt_bookname.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the book name");
                return false; 
            }
            book_name = txt_bookname.getText();

            if (txt_authorname.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the author name");
                return false; 
            }
            author = txt_authorname.getText();
            
            if (txt_quantity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the quantity");
                return false; 
            }
            quantity = Integer.parseInt(txt_quantity.getText());   
            
            if (txt_category.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the book category");
                return false; 
            }
            category = txt_category.getText();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for book ID and quantity");
            return false;
        }
       return true;
    }
    
// To set book details into the table
    public void setBookDetails(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_db", "root", "Dean@1020");        
            
            String query = "SELECT * FROM book_details WHERE status = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "available");
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                String book_id = rs.getString("book_id");
                String book_name = rs.getString("book_name");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");
                
                Object[] obj = {book_id, book_name, author, quantity, category};
                model = (DefaultTableModel)tbl_bookDetails.getModel();
                model.addRow(obj);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // Method to generate the new book ID
    private String generateBookId() {
        String book_id = "";
        try {
            Connection conn = DBConnection.getConnection();

            // Retrieve the maximum current book ID from the database
            String sqlMaxId = "SELECT MAX(CAST(SUBSTRING(book_id, 2) AS UNSIGNED)) AS max_id FROM book_details";
            PreparedStatement pstMaxId = conn.prepareStatement(sqlMaxId);
            ResultSet rs = pstMaxId.executeQuery();

            int newIdNumber = 1;  // Default to 001 if no books exist
            if (rs.next() && rs.getString("max_id") != null) {
                newIdNumber = rs.getInt("max_id") + 1;
            }

            // Generate new book ID with prefix "B" and zero-padding
            book_id = "B" + String.format("%03d", newIdNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return book_id;
    }
    
    //to add book details in the table
    public boolean addBook() {
        String book_name = txt_bookname.getText();
        String author = txt_authorname.getText();
        int quantity = Integer.parseInt(txt_quantity.getText());
        String category = txt_category.getText();
        boolean isAdded = false;

        String book_id = generateBookId();

        String sqlInsert = "INSERT INTO book_details (book_id, book_name, author, quantity, category, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstInsert = conn.prepareStatement(sqlInsert)) {

            pstInsert.setString(1, book_id);
            pstInsert.setString(2, book_name);
            pstInsert.setString(3, author);
            pstInsert.setInt(4, quantity);
            pstInsert.setString(5, category);
            pstInsert.setString(6, "available");

            int rowCount = pstInsert.executeUpdate();
            isAdded = rowCount > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isAdded;
    }
    
    //to clear table
    public void clearTable(){
        DefaultTableModel model = (DefaultTableModel)tbl_bookDetails.getModel();
        model.setRowCount(0);
    }
    
    //to update book details 
    public boolean updateBook(){
        String book_id = txt_bookid.getText();
        book_name = txt_bookname.getText();
        author = txt_authorname.getText();
        quantity = Integer.parseInt(txt_quantity.getText());
        boolean isUpdated = false;
        
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE book_details SET book_name = ?, author = ?, quantity = ? WHERE book_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, book_name);
            pst.setString(2, author);
            pst.setInt(3, quantity);
            pst.setString(4, book_id);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                isUpdated = true;
            }else{
                isUpdated = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isUpdated;
    }
    
    //to delete book details in the table 
    public boolean deleteBook(){
        String book_id = txt_bookid.getText();
        boolean isDeleted = false;
        
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE book_details SET status = ? WHERE book_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, "unavailable");
            pst.setString(2, book_id);
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                isDeleted = true;
            }else{
                isDeleted = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isDeleted;
    }
    
    private void importCSV() {
        // Open a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            readCSV(selectedFile);
        }
    }

    private void readCSV(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true; // Flag to check if it's the first line
            
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection(); // Establish database connection
                conn.setAutoCommit(false); // Disable auto-commit for batch processing
                String sql = "INSERT INTO book_details (book_id, book_name, author, quantity, category) VALUES (?, ?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false; // Skip the first line (header)
                        continue;
                    }
                                    
                    // Parse the CSV line
                    String[] data = line.split(",");
                    if (data.length == 6) {
                        try {
                            // Clean and parse the data
                            String book_id = cleanString(data[0]);
                            String book_name = cleanString(data[1]);
                            String author = cleanString(data[2]);
                            int quantity = Integer.parseInt(cleanString(data[3]));
                            String category = cleanString(data[4]);

                            // Set parameters for the prepared statement
                            pst.setString(1, book_id);
                            pst.setString(2, book_name);
                            pst.setString(3, author);
                            pst.setInt(4, quantity);
                            pst.setString(5, category);

                            try {
                                // Add to batch
                                pst.addBatch();
                            } catch (SQLException ex) {
                                Logger.getLogger(ManageBooks.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            // Handle invalid number format in CSV
                        }
                    } else {
                        System.out.println("Invalid data format: " + line);
                        // Handle invalid CSV line format
                    }
                }
                // Execute batch update and commit the transaction
                pst.executeBatch();
                conn.commit();

                JOptionPane.showMessageDialog(this, "CSV Import Successful!");
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback in case of error
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                // Close resources
                if (pst != null) {
                    try {
                        pst.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String cleanString(String input) {
        // Remove leading and trailing quotes and whitespace
        if (input != null) {
            input = input.trim();
            if (input.startsWith("\"") && input.endsWith("\"")) {
                input = input.substring(1, input.length() - 1);
            }
        }
        return input;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_bookid = new app.bolivia.swing.JCTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_bookname = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_authorname = new app.bolivia.swing.JCTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_quantity = new app.bolivia.swing.JCTextField();
        btn_import = new rojerusan.RSMaterialButtonRectangle();
        btn_add = new rojerusan.RSMaterialButtonRectangle();
        btn_update = new rojerusan.RSMaterialButtonRectangle();
        btn_delete1 = new rojerusan.RSMaterialButtonRectangle();
        txt_category = new app.bolivia.swing.JCTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel_exit1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_bookDetails = new rojeru_san.complementos.RSTableMetro();
        jLabel15 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(129, 0, 0));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Rewind_48px.png"))); // NOI18N
        jLabel1.setText("Back");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 111, 37));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Book ID:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, -1, -1));

        txt_bookid.setBackground(new java.awt.Color(238, 235, 221));
        txt_bookid.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_bookid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_bookid.setEnabled(false);
        txt_bookid.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_bookid.setPlaceholder("Auto-generated ID");
        txt_bookid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bookidFocusLost(evt);
            }
        });
        jPanel1.add(txt_bookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 380, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Contact_26px.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Moleskine_26px.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Book Name:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 170, -1, -1));

        txt_bookname.setBackground(new java.awt.Color(238, 235, 221));
        txt_bookname.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_bookname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_bookname.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_bookname.setPlaceholder("Enter Book Name");
        txt_bookname.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_booknameFocusLost(evt);
            }
        });
        jPanel1.add(txt_bookname, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 380, 50));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Collaborator_Male_26px.png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 340, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Author Name:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 290, -1, -1));

        txt_authorname.setBackground(new java.awt.Color(238, 235, 221));
        txt_authorname.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_authorname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_authorname.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_authorname.setPlaceholder("Enter Author Name");
        txt_authorname.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_authornameFocusLost(evt);
            }
        });
        jPanel1.add(txt_authorname, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 330, 380, 50));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Unit_26px.png"))); // NOI18N
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 460, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Quantity:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 410, -1, -1));

        txt_quantity.setBackground(new java.awt.Color(238, 235, 221));
        txt_quantity.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_quantity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_quantity.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_quantity.setPlaceholder("Enter quantity");
        txt_quantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_quantityFocusLost(evt);
            }
        });
        jPanel1.add(txt_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 450, 380, 50));

        btn_import.setBackground(new java.awt.Color(27, 23, 23));
        btn_import.setText("Import CSV");
        btn_import.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_importActionPerformed(evt);
            }
        });
        jPanel1.add(btn_import, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 720, 130, 40));

        btn_add.setBackground(new java.awt.Color(27, 23, 23));
        btn_add.setText("Add");
        btn_add.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_addMouseClicked(evt);
            }
        });
        btn_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addActionPerformed(evt);
            }
        });
        jPanel1.add(btn_add, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 640, 130, -1));

        btn_update.setBackground(new java.awt.Color(27, 23, 23));
        btn_update.setText("Update");
        btn_update.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });
        jPanel1.add(btn_update, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 640, 130, -1));

        btn_delete1.setBackground(new java.awt.Color(27, 23, 23));
        btn_delete1.setText("Delete");
        btn_delete1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_delete1ActionPerformed(evt);
            }
        });
        jPanel1.add(btn_delete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 640, 130, -1));

        txt_category.setBackground(new java.awt.Color(238, 235, 221));
        txt_category.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_category.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_category.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_category.setPlaceholder("Enter Category (e.g. fiction, non-fiction, history, etc...)");
        txt_category.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_categoryFocusLost(evt);
            }
        });
        jPanel1.add(txt_category, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 570, 380, 50));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Contact_26px.png"))); // NOI18N
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 580, -1, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Category:");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 530, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 900));

        jPanel3.setBackground(new java.awt.Color(238, 235, 221));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel3MouseClicked(evt);
            }
        });
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel_exit1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel_exit1.setText("X");
        jLabel_exit1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_exit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_exit1MouseClicked(evt);
            }
        });
        jPanel3.add(jLabel_exit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 0, -1, -1));

        jScrollPane2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        tbl_bookDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Book ID", "Name", "Author", "Quantity", "Category"
            }
        ));
        tbl_bookDetails.setColorBackgoundHead(new java.awt.Color(99, 0, 0));
        tbl_bookDetails.setColorFilasBackgound2(new java.awt.Color(238, 235, 221));
        tbl_bookDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_bookDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_bookDetails.setColorSelBackgound(new java.awt.Color(153, 153, 153));
        tbl_bookDetails.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        tbl_bookDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_bookDetails.setRowHeight(100);
        tbl_bookDetails.setSelectionBackground(new java.awt.Color(153, 153, 153));
        tbl_bookDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_bookDetailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_bookDetails);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 670, 570));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 102, 51));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Books_52px_1.png"))); // NOI18N
        jLabel15.setText("  Manage Books");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        jPanel4.setBackground(new java.awt.Color(99, 0, 0));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, 380, 10));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 0, 780, 900));

        setSize(new java.awt.Dimension(1366, 768));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        HomePage homepage = new HomePage();
        homepage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void txt_bookidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookidFocusLost

    }//GEN-LAST:event_txt_bookidFocusLost

    private void txt_booknameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_booknameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_booknameFocusLost

    private void txt_authornameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_authornameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_authornameFocusLost

    private void txt_quantityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_quantityFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quantityFocusLost

    private void btn_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_importActionPerformed
        importCSV();
        clearTable();
        setBookDetails();
    }//GEN-LAST:event_btn_importActionPerformed

    private void btn_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addActionPerformed
        // Check if a row is selected in the table
        int selectedRow = tbl_bookDetails.getSelectedRow();  // Replace 'yourTable' with the actual name of your JTable

        if (selectedRow != -1) { // A row is selected
            JOptionPane.showMessageDialog(this, "Please deselect the row before adding a new book.");
            return;  // Exit the method to prevent adding a new book
        }
        if(validateInput()){
            if(addBook() == true){
                JOptionPane.showMessageDialog(this, "Book Details Added");
                clearTable();
                setBookDetails();
                txt_bookid.setText(generateBookId());  // Set the generated book ID
            }else{
                JOptionPane.showMessageDialog(this, "Addition of book details failed");
            }
        }
    }//GEN-LAST:event_btn_addActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed

        if(validateInput()){
            if(updateBook() == true){
                JOptionPane.showMessageDialog(this, "Book Details Updated");
                clearTable();
                setBookDetails();
            }else{
                JOptionPane.showMessageDialog(this, "Updation of book details failed");
            }
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void btn_addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_addMouseClicked

    }//GEN-LAST:event_btn_addMouseClicked

    private void tbl_bookDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_bookDetailsMouseClicked
        int rowNo = tbl_bookDetails.getSelectedRow();
        TableModel model = tbl_bookDetails.getModel();

        txt_bookid.setText(model.getValueAt(rowNo, 0).toString());
        txt_bookname.setText(model.getValueAt(rowNo, 1).toString());
        txt_authorname.setText(model.getValueAt(rowNo, 2).toString());
        txt_quantity.setText(model.getValueAt(rowNo, 3).toString());
        txt_category.setText(model.getValueAt(rowNo, 4).toString());
    }//GEN-LAST:event_tbl_bookDetailsMouseClicked

    private void jLabel_exit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_exit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel_exit1MouseClicked

    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseClicked
        tbl_bookDetails.clearSelection();
        TableModel model = tbl_bookDetails.getModel();

        txt_bookid.setText(generateBookId());  // Set the generated book ID
        txt_bookname.setText("");
        txt_authorname.setText("");
        txt_quantity.setText("");
        txt_category.setText("");
    }//GEN-LAST:event_jPanel3MouseClicked

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        tbl_bookDetails.clearSelection();
        TableModel model = tbl_bookDetails.getModel();

        txt_bookid.setText(generateBookId());  // Set the generated book ID
        txt_bookname.setText("");
        txt_authorname.setText("");
        txt_quantity.setText("");
        txt_category.setText("");
    }//GEN-LAST:event_jPanel1MouseClicked

    private void btn_delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_delete1ActionPerformed
        if(validateInput()){
            if(deleteBook() == true){
                JOptionPane.showMessageDialog(this, "Book Details Deleted");
                clearTable();
                setBookDetails();

   //           CLEAR TEXTBOXES
                txt_bookid.setText("");
                txt_bookname.setText("");
                txt_authorname.setText("");
                txt_quantity.setText("");
                txt_category.setText("");
            }else{
                JOptionPane.showMessageDialog(this, "Deletion of book details failed");
            }
        }
    }//GEN-LAST:event_btn_delete1ActionPerformed

    private void txt_categoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_categoryFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_categoryFocusLost
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageBooks().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSMaterialButtonRectangle btn_add;
    private rojerusan.RSMaterialButtonRectangle btn_delete1;
    private rojerusan.RSMaterialButtonRectangle btn_import;
    private rojerusan.RSMaterialButtonRectangle btn_update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_exit1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private rojeru_san.complementos.RSTableMetro tbl_bookDetails;
    private app.bolivia.swing.JCTextField txt_authorname;
    private app.bolivia.swing.JCTextField txt_bookid;
    private app.bolivia.swing.JCTextField txt_bookname;
    private app.bolivia.swing.JCTextField txt_category;
    private app.bolivia.swing.JCTextField txt_quantity;
    // End of variables declaration//GEN-END:variables

}
