/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author anonu
 */
public class IssueBook extends javax.swing.JFrame {

    /**
     * Creates new form IssueBook
     */
    public IssueBook() {
        initComponents();
        date_issuedate.setMinSelectableDate(new Date());
        // Add PropertyChangeListener to date_issuedate
        date_issuedate.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    // Get the selected issue date
                    Date issueDate = date_issuedate.getDate();
                    
                    if (issueDate != null) {
                        // Set the minimum selectable date for date_duedate to be the selected issue date
                        date_duedate.setMinSelectableDate(issueDate);
                    }
                }
            }
        });

        // Optionally, set the initial minimum date for date_duedate
        date_duedate.setMinSelectableDate(new Date());
    }
    
    public boolean validateInput(){
      try {
        // Ensure BookId field is not empty and contains a valid integer
        if (txt_issuebookid.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the book ID");
            return false;
        }
       String BookId = txt_issuebookid.getText();

        // Ensure StudentId field is not empty and contains a valid integer
        if (txt_issuestudentid.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the student ID");
            return false;
        }
        int StudentId = Integer.parseInt(txt_issuestudentid.getText());

        // Ensure uissueDate is not null
        Date uissueDate = date_issuedate.getDate();
        if (uissueDate == null) {
            JOptionPane.showMessageDialog(this, "Please select the issue date");
            return false;
        }

        // Ensure udueDate is not null
        Date udueDate = date_duedate.getDate();
        if (udueDate == null) {
            JOptionPane.showMessageDialog(this, "Please select the due date");
            return false;
        }

        long l1 = uissueDate.getTime();
        long l2 = udueDate.getTime();
        
        // Ensure due date is greater than issue date
        if (l1 > l2) {
            JOptionPane.showMessageDialog(this, "Invalid date range: issue date cannot be later than due date");
            return false;
        }

        java.sql.Date sissueDate = new java.sql.Date(l1);
        java.sql.Date sdueDate = new java.sql.Date(l2);

  
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Please enter valid numeric values for book ID and student ID");
        return false;
    }
       return true;
    }
    
//    to fetch book details
    public void getBookDetails(){
        String BookId = txt_issuebookid.getText();

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT * from book_details WHERE book_id = ?");

            pst.setString(1, BookId);
            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                lbl_bookid.setText(rs.getString("book_id"));
                lbl_bookname.setText("<html>" + rs.getString("book_name") + "</html>");
                lbl_author.setText(rs.getString("author"));
                lbl_quantity.setText(rs.getString("quantity"));
                lbl_bookinvalid.setText("");
            }else{
                lbl_bookinvalid.setText("INVALID BOOK ID");
                lbl_bookid.setText("");
                lbl_bookname.setText("");
                lbl_author.setText("");
                lbl_quantity.setText("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

//    to fetch student details
    public void getStudentDetails(){
        int StudentId = Integer.parseInt(txt_issuestudentid.getText());

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT * from student_details WHERE student_id = ?");

            pst.setInt(1, StudentId);
            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                lbl_studentid.setText(rs.getString("student_id"));
                lbl_studentname.setText("<html>" + rs.getString("name") + "</html>");
                lbl_course.setText("<html>" + rs.getString("course") + "</html>");
                lbl_studentinvalid.setText("");
            }else{
                lbl_studentid.setText("");
                lbl_studentname.setText("");
                lbl_course.setText("");
                lbl_studentinvalid.setText("INVALID STUDENT ID");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //inserting issue book details
    public boolean issuedBook(){
        String BookId = lbl_bookid.getText();
        int StudentId = Integer.parseInt(txt_issuestudentid.getText());
        // Remove HTML tags from the JLabel text
        String bookname = lbl_bookname.getText().replaceAll("<[^>]*>", "");
        String studentname = lbl_studentname.getText().replaceAll("<[^>]*>", "");

        Date uissueDate = date_issuedate.getDate();
        Date udueDate = date_duedate.getDate();

        if (uissueDate == null || udueDate == null) {
            // Handle the case where dates are not provided
            JOptionPane.showMessageDialog(this, "Issue date or due date is missing.");
            return false;
        }

        long l1 = uissueDate.getTime();
        long l2 = udueDate.getTime();
        
        java.sql.Date sissueDate = new java.sql.Date(l1);
        java.sql.Date sdueDate = new java.sql.Date(l2);
        boolean isIssued = false;

        try{
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO issue_book (book_id, book_name, student_id, student_name, "
                    + "issue_date, due_date, status) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, BookId);
            pst.setString(2, bookname);
            pst.setInt(3, StudentId);
            pst.setString(4, studentname);
            pst.setDate(5, sissueDate);
            pst.setDate(6, sdueDate);
            pst.setString(7, "pending");

            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                isIssued = true;
            } else {
                isIssued = false;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return isIssued;
    }

//    update bookcount
    public void updateBookCount(){
        String BookId = txt_issuebookid.getText();

        try{
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE book_details SET quantity = quantity - 1 WHERE book_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, BookId);

            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                JOptionPane.showMessageDialog(this, "Book count updated.");
                int initialCount = Integer.parseInt(lbl_quantity.getText());
                lbl_quantity.setText(Integer.toString(initialCount - 1));
            }else{
                JOptionPane.showMessageDialog(this, "Cannot update book count.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

//    Checking duplicate
    public boolean isIssued(){
        boolean isAlready = false;
        String BookId = txt_issuebookid.getText();
        int StudentId = Integer.parseInt(txt_issuestudentid.getText());

        try{
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM issue_book WHERE book_id = ? AND student_id = ? AND status = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, BookId);
            pst.setInt(2, StudentId);
            pst.setString(3, "pending");

            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                isAlready = true;
            }else{
                isAlready = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isAlready;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lbl_bookinvalid = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lbl_quantity = new javax.swing.JLabel();
        lbl_bookid = new javax.swing.JLabel();
        lbl_bookname = new javax.swing.JLabel();
        lbl_author = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lbl_studentid = new javax.swing.JLabel();
        lbl_studentname = new javax.swing.JLabel();
        lbl_course = new javax.swing.JLabel();
        lbl_studentinvalid = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel_exit1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_issuebookid = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        txt_issuestudentid = new app.bolivia.swing.JCTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        btn_issuebook = new rojerusan.RSMaterialButtonRectangle();
        date_issuedate = new com.toedter.calendar.JDateChooser();
        date_duedate = new com.toedter.calendar.JDateChooser();

        jPanel7.setPreferredSize(new java.awt.Dimension(400, 5));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(99, 0, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(1440, 900));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Rewind_48px.png"))); // NOI18N
        jLabel2.setText("Back");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 111, 37));

        jPanel3.setBackground(new java.awt.Color(27, 23, 23));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 900));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Author:");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, -1, -1));

        lbl_bookinvalid.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_bookinvalid.setForeground(new java.awt.Color(204, 0, 0));
        lbl_bookinvalid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lbl_bookinvalid, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 690, 380, 40));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Book Name:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Book ID:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        lbl_quantity.setBackground(new java.awt.Color(238, 235, 221));
        lbl_quantity.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_quantity.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 540, 170, 30));

        lbl_bookid.setBackground(new java.awt.Color(238, 235, 221));
        lbl_bookid.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_bookid.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_bookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 170, 30));

        lbl_bookname.setBackground(new java.awt.Color(238, 235, 221));
        lbl_bookname.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_bookname.setForeground(new java.awt.Color(255, 255, 255));
        lbl_bookname.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_bookname.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel3.add(lbl_bookname, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 370, 180, 110));

        lbl_author.setBackground(new java.awt.Color(238, 235, 221));
        lbl_author.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_author.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(lbl_author, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 480, 170, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Literature_100px_1.png"))); // NOI18N
        jLabel9.setText("Book Details");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        jPanel9.setPreferredSize(new java.awt.Dimension(400, 5));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 330, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Quantity:");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 540, -1, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, -1));

        jPanel5.setBackground(new java.awt.Color(27, 23, 23));
        jPanel5.setPreferredSize(new java.awt.Dimension(400, 900));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Student_Registration_100px_2.png"))); // NOI18N
        jLabel4.setText("Student Details");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        jPanel6.setPreferredSize(new java.awt.Dimension(400, 5));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 330, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Course:");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 460, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Student Name:");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Student ID:");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));

        lbl_studentid.setBackground(new java.awt.Color(238, 235, 221));
        lbl_studentid.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_studentid.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.add(lbl_studentid, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 170, 30));

        lbl_studentname.setBackground(new java.awt.Color(238, 235, 221));
        lbl_studentname.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_studentname.setForeground(new java.awt.Color(255, 255, 255));
        lbl_studentname.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_studentname.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(lbl_studentname, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 370, 200, 80));

        lbl_course.setBackground(new java.awt.Color(238, 235, 221));
        lbl_course.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_course.setForeground(new java.awt.Color(255, 255, 255));
        lbl_course.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_course.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel5.add(lbl_course, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 460, 200, 60));

        lbl_studentinvalid.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_studentinvalid.setForeground(new java.awt.Color(204, 0, 0));
        lbl_studentinvalid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel5.add(lbl_studentinvalid, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 690, 390, 40));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 390, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Books_52px_1.png"))); // NOI18N
        jLabel1.setText("Issue Book");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 110, -1, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 5));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 170, 330, -1));

        jLabel_exit1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel_exit1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel_exit1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_exit1.setText("X");
        jLabel_exit1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_exit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_exit1MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel_exit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 0, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Book ID:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 270, -1, -1));

        txt_issuebookid.setBackground(new java.awt.Color(238, 235, 221));
        txt_issuebookid.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_issuebookid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_issuebookid.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_issuebookid.setPlaceholder("Enter book ID");
        txt_issuebookid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_issuebookidFocusLost(evt);
            }
        });
        txt_issuebookid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_issuebookidActionPerformed(evt);
            }
        });
        jPanel2.add(txt_issuebookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 260, 380, 50));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Issue Date:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 450, -1, -1));

        txt_issuestudentid.setBackground(new java.awt.Color(238, 235, 221));
        txt_issuestudentid.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_issuestudentid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_issuestudentid.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_issuestudentid.setPlaceholder("Enter student ID");
        txt_issuestudentid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_issuestudentidFocusLost(evt);
            }
        });
        jPanel2.add(txt_issuestudentid, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 350, 380, 50));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Student ID:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 360, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Due Date:");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 540, -1, -1));

        btn_issuebook.setBackground(new java.awt.Color(27, 23, 23));
        btn_issuebook.setText("Issue Book");
        btn_issuebook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_issuebook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_issuebookMouseClicked(evt);
            }
        });
        btn_issuebook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_issuebookActionPerformed(evt);
            }
        });
        jPanel2.add(btn_issuebook, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 630, 350, -1));
        jPanel2.add(date_issuedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 440, 380, 50));
        jPanel2.add(date_duedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 530, 380, 50));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        setBounds(0, 0, 1366, 768);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        HomePage homepage = new HomePage();
        homepage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel_exit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_exit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel_exit1MouseClicked

    private void txt_issuebookidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_issuebookidFocusLost
        if(!txt_issuebookid.getText().equals("")){
            getBookDetails();
        }else{
            lbl_bookid.setText("");
            lbl_bookname.setText("");
            lbl_author.setText("");
            lbl_quantity.setText("");
        }
    }//GEN-LAST:event_txt_issuebookidFocusLost

    private void txt_issuestudentidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_issuestudentidFocusLost
        if(!txt_issuestudentid.getText().equals("")){
            getStudentDetails();
        }else{
            lbl_studentid.setText("");
            lbl_studentname.setText("");
            lbl_course.setText("");
        }
    }//GEN-LAST:event_txt_issuestudentidFocusLost

    private void btn_issuebookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_issuebookMouseClicked

    }//GEN-LAST:event_btn_issuebookMouseClicked

    private void btn_issuebookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_issuebookActionPerformed
        if(validateInput()){
            if(lbl_quantity.getText().equals("0")){
                JOptionPane.showMessageDialog(this, "Book is not available");
            }
            else{
                if(isIssued() == false){
                    if(issuedBook() == true){
                        JOptionPane.showMessageDialog(this, "Book issued successfully.");
                        updateBookCount();
                    }else{
                        JOptionPane.showMessageDialog(this, "Cannot issue the book.");
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "This student already have this book.");
                }
            }
        }
    }//GEN-LAST:event_btn_issuebookActionPerformed

    private void txt_issuebookidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_issuebookidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_issuebookidActionPerformed

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        lbl_bookid.setText("");
        lbl_bookname.setText("");
        lbl_author.setText("");
        lbl_quantity.setText("");
        
        lbl_studentid.setText("");
        lbl_studentname.setText("");
        lbl_course.setText("");
        
        txt_issuebookid.setText("");
        txt_issuestudentid.setText("");
        date_issuedate.setDate(null);
        date_duedate.setDate(null);
    }//GEN-LAST:event_jPanel2MouseClicked

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
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IssueBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSMaterialButtonRectangle btn_issuebook;
    private com.toedter.calendar.JDateChooser date_duedate;
    private com.toedter.calendar.JDateChooser date_issuedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_exit1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lbl_author;
    private javax.swing.JLabel lbl_bookid;
    private javax.swing.JLabel lbl_bookinvalid;
    private javax.swing.JLabel lbl_bookname;
    private javax.swing.JLabel lbl_course;
    private javax.swing.JLabel lbl_quantity;
    private javax.swing.JLabel lbl_studentid;
    private javax.swing.JLabel lbl_studentinvalid;
    private javax.swing.JLabel lbl_studentname;
    private app.bolivia.swing.JCTextField txt_issuebookid;
    private app.bolivia.swing.JCTextField txt_issuestudentid;
    // End of variables declaration//GEN-END:variables
}
