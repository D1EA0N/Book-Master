/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author anonu
 */
public class ManageStudents extends javax.swing.JFrame {
    
    String student_name, course;   
    int student_id;
    DefaultTableModel model;
    /**
     * Creates new form ManageBooks
     */
    public ManageStudents() {
        initComponents();
        setStudentsDetails();
    }
    
    public boolean validateInput(){
       try {
            // Ensure student_id field is not empty and contains a valid integer
            if (txt_studentid.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the student ID");
                return false;
            }
            student_id = Integer.parseInt(txt_studentid.getText());

            // Ensure student_name field is not empty
            if (txt_studentname.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the student name");
                return false;
            }
            student_name = txt_studentname.getText();

            // Ensure a course is selected
            if (combo_cname.getSelectedItem() == null || combo_cname.getSelectedItem().toString().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course");
                return false;
            }
            course = combo_cname.getSelectedItem().toString();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for student ID");
            return false;
        }
       return true;
    }
    
// To set book details into the table
    public void setStudentsDetails(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_db", "root", "Dean@1020");
            
            String query = "SELECT * FROM student_details WHERE status = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "active");
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                String student_id = rs.getString("student_id");
                String student_name = rs.getString("name");
                String course = rs.getString("course");
                
                Object[] obj = {student_id, student_name, course};
                model = (DefaultTableModel)tbl_studentDetails.getModel();
                model.addRow(obj);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
//    to add book details in the table
    public boolean addStudent(){
        student_id = Integer.parseInt(txt_studentid.getText());
        student_name = txt_studentname.getText();
        course = combo_cname.getSelectedItem().toString();
        boolean isAdded = false;
        
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO student_details values(?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setInt(1, student_id);
            pst.setString(2, student_name);
            pst.setString(3, course);
            pst.setString(4, "active");
            
            int rowCount = pst.executeUpdate();
            if(rowCount > 0){
                isAdded = true;
            }else{
                isAdded = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return isAdded;
    }
    
//    to clear table
    public void clearTable(){
        DefaultTableModel model = (DefaultTableModel)tbl_studentDetails.getModel();
        model.setRowCount(0);
    }
    
//    to update book details 
    public boolean updateStudent(){
        student_id = Integer.parseInt(txt_studentid.getText());
        student_name = txt_studentname.getText();
        course = combo_cname.getSelectedItem().toString();
        boolean isUpdated = false;
        
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE student_details SET name = ?, course = ? WHERE student_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, student_name);
            pst.setString(2, course);                        
            pst.setInt(3, student_id);
            
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
    
//    to delete book details in the table 
    public boolean deleteStudent(){
        student_id = Integer.parseInt(txt_studentid.getText());
        boolean isDeleted = false;
        
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE student_details SET status = ? WHERE student_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, "inactive");
            pst.setInt(2, student_id);
            
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
        jPanel2 = new javax.swing.JPanel();
        jLabel_exit = new javax.swing.JLabel();
        txt_username2 = new app.bolivia.swing.JCTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_password = new app.bolivia.swing.JCTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_signup = new rojerusan.RSMaterialButtonRectangle();
        btn_relogin = new rojerusan.RSMaterialButtonRectangle();
        jLabel7 = new javax.swing.JLabel();
        txt_repassword = new app.bolivia.swing.JCTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_studentid = new app.bolivia.swing.JCTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_studentname = new app.bolivia.swing.JCTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btn_delete = new rojerusan.RSMaterialButtonRectangle();
        btn_add = new rojerusan.RSMaterialButtonRectangle();
        btn_update = new rojerusan.RSMaterialButtonRectangle();
        combo_cname = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel_exit1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_studentDetails = new rojeru_san.complementos.RSTableMetro();
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

        jPanel2.setBackground(new java.awt.Color(242, 227, 219));

        jLabel_exit.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel_exit.setText("X");
        jLabel_exit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel_exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_exitMouseClicked(evt);
            }
        });

        txt_username2.setBackground(new java.awt.Color(242, 227, 219));
        txt_username2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_username2.setPlaceholder("Enter username");
        txt_username2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_username2FocusLost(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Username");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Password");

        txt_password.setBackground(new java.awt.Color(242, 227, 219));
        txt_password.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_password.setPlaceholder("Enter password");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Already have an account?");

        btn_signup.setBackground(new java.awt.Color(232, 106, 51));
        btn_signup.setForeground(new java.awt.Color(0, 0, 0));
        btn_signup.setText("Signup");
        btn_signup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_signupActionPerformed(evt);
            }
        });

        btn_relogin.setBackground(new java.awt.Color(232, 106, 51));
        btn_relogin.setText("Login here");
        btn_relogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reloginActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Confirm Password");

        txt_repassword.setBackground(new java.awt.Color(242, 227, 219));
        txt_repassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_repassword.setPlaceholder("Re-enter password");

        jLabel8.setFont(new java.awt.Font("SimSun-ExtB", 1, 64)); // NOI18N
        jLabel8.setText("Signup");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(209, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_relogin, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(179, 179, 179))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(166, 166, 166)
                        .addComponent(jLabel_exit)
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(txt_username2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_password, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_repassword, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(201, 201, 201))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(245, 245, 245)
                .addComponent(btn_signup, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_exit)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_username2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txt_repassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btn_signup, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btn_relogin, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 0, 750, 900));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Student ID:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, -1, -1));

        txt_studentid.setBackground(new java.awt.Color(238, 235, 221));
        txt_studentid.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_studentid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_studentid.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_studentid.setPlaceholder("Enter student ID");
        txt_studentid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_studentidFocusLost(evt);
            }
        });
        jPanel1.add(txt_studentid, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 230, 380, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Contact_26px.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 240, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Moleskine_26px.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 360, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Student Name:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 310, -1, -1));

        txt_studentname.setBackground(new java.awt.Color(238, 235, 221));
        txt_studentname.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 0, 0)));
        txt_studentname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_studentname.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        txt_studentname.setPlaceholder("Enter student name");
        txt_studentname.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_studentnameFocusLost(evt);
            }
        });
        jPanel1.add(txt_studentname, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 350, 380, 50));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Collaborator_Male_26px.png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 480, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Course:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 430, -1, -1));

        btn_delete.setBackground(new java.awt.Color(27, 23, 23));
        btn_delete.setText("Delete");
        btn_delete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });
        jPanel1.add(btn_delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 570, 130, -1));

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
        jPanel1.add(btn_add, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 570, 130, -1));

        btn_update.setBackground(new java.awt.Color(27, 23, 23));
        btn_update.setText("Update");
        btn_update.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });
        jPanel1.add(btn_update, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 570, 130, -1));

        combo_cname.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combo_cname.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BSECE", "BSBA-HRM", "BSBA-MM", "BSE-ENGLISH", "BSE-FILIPINO", "BSE-MATHEMATICS", "BSIE", "BSIT", "BSP", "BTLE-HOME ECONOMICS", "BSMA" }));
        jPanel1.add(combo_cname, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 470, 380, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, -1));

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

        tbl_studentDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Name", "Course"
            }
        ));
        tbl_studentDetails.setColorBackgoundHead(new java.awt.Color(99, 0, 0));
        tbl_studentDetails.setColorFilasBackgound2(new java.awt.Color(238, 235, 221));
        tbl_studentDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_studentDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_studentDetails.setColorSelBackgound(new java.awt.Color(153, 153, 153));
        tbl_studentDetails.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        tbl_studentDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_studentDetails.setRowHeight(70);
        tbl_studentDetails.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbl_studentDetailsFocusLost(evt);
            }
        });
        tbl_studentDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_studentDetailsMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tbl_studentDetailsMouseExited(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_studentDetails);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 680, 590));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 102, 51));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Student_Male_100px.png"))); // NOI18N
        jLabel15.setText("  Manage Students");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, -1, -1));

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

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 380, 10));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 0, 820, 900));

        setSize(new java.awt.Dimension(1366, 768));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel_exit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_exit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel_exit1MouseClicked

    private void tbl_studentDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_studentDetailsMouseClicked
        int rowNo = tbl_studentDetails.getSelectedRow();
        TableModel model = tbl_studentDetails.getModel();
        
        txt_studentid.setText(model.getValueAt(rowNo, 0).toString());
        txt_studentname.setText(model.getValueAt(rowNo, 1).toString());
        combo_cname.setSelectedItem(model.getValueAt(rowNo, 2).toString());
    }//GEN-LAST:event_tbl_studentDetailsMouseClicked

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        if(validateInput()){
            if(updateStudent() == true){
                JOptionPane.showMessageDialog(this, "Student Details Updated");
                clearTable();
                setStudentsDetails();
            }else{
                JOptionPane.showMessageDialog(this, "Updation of student details failed");
            }
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    private void btn_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addActionPerformed
        // Check if a row is selected in the table
        int selectedRow = tbl_studentDetails.getSelectedRow();  // Replace 'yourTable' with the actual name of your JTable

        if (selectedRow != -1) { // A row is selected
            JOptionPane.showMessageDialog(this, "Please deselect the row before adding a new book.");
            return;  // Exit the method to prevent adding a new book
        }
        if(validateInput()){
            if(addStudent() == true){
                JOptionPane.showMessageDialog(this, "Student Details Added");
                clearTable();
                setStudentsDetails();
            }else{
                JOptionPane.showMessageDialog(this, "Addition of student details failed");
            }
        }
    }//GEN-LAST:event_btn_addActionPerformed

    private void btn_addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_addMouseClicked

    }//GEN-LAST:event_btn_addMouseClicked

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
       if(validateInput()){
            if(deleteStudent() == true){
                JOptionPane.showMessageDialog(this, "Student Details Deleted");
                clearTable();
                setStudentsDetails();

                //            CLEAR TEXTBOXES
                txt_studentid.setText("");
                txt_studentname.setText("");
                combo_cname.setSelectedItem("");
            }else{
                JOptionPane.showMessageDialog(this, "Deletion of student details failed");
            }
        }
    }//GEN-LAST:event_btn_deleteActionPerformed

    private void txt_studentnameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_studentnameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_studentnameFocusLost

    private void txt_studentidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_studentidFocusLost

    }//GEN-LAST:event_txt_studentidFocusLost

    private void btn_reloginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reloginActionPerformed
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_btn_reloginActionPerformed

    private void btn_signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_signupActionPerformed

    }//GEN-LAST:event_btn_signupActionPerformed

    private void txt_username2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_username2FocusLost

    }//GEN-LAST:event_txt_username2FocusLost

    private void jLabel_exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel_exitMouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        HomePage homepage = new HomePage();
        homepage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void tbl_studentDetailsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_studentDetailsFocusLost
        
    }//GEN-LAST:event_tbl_studentDetailsFocusLost

    private void tbl_studentDetailsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_studentDetailsMouseExited

    }//GEN-LAST:event_tbl_studentDetailsMouseExited

    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseClicked
        tbl_studentDetails.clearSelection();
        TableModel model = tbl_studentDetails.getModel();
        
        txt_studentid.setText("");
        txt_studentname.setText("");
        combo_cname.setSelectedItem("");
    }//GEN-LAST:event_jPanel3MouseClicked

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        tbl_studentDetails.clearSelection();
        TableModel model = tbl_studentDetails.getModel();
        
        txt_studentid.setText("");
        txt_studentname.setText("");
        combo_cname.setSelectedItem("");
    }//GEN-LAST:event_jPanel1MouseClicked
    
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
            java.util.logging.Logger.getLogger(ManageStudents.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageStudents.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageStudents.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageStudents.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageStudents().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSMaterialButtonRectangle btn_add;
    private rojerusan.RSMaterialButtonRectangle btn_delete;
    private rojerusan.RSMaterialButtonRectangle btn_relogin;
    private rojerusan.RSMaterialButtonRectangle btn_signup;
    private rojerusan.RSMaterialButtonRectangle btn_update;
    private javax.swing.JComboBox<String> combo_cname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_exit;
    private javax.swing.JLabel jLabel_exit1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private rojeru_san.complementos.RSTableMetro tbl_studentDetails;
    private app.bolivia.swing.JCTextField txt_password;
    private app.bolivia.swing.JCTextField txt_repassword;
    private app.bolivia.swing.JCTextField txt_studentid;
    private app.bolivia.swing.JCTextField txt_studentname;
    private app.bolivia.swing.JCTextField txt_username2;
    // End of variables declaration//GEN-END:variables

}
