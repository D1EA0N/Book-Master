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
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author anonu
 */
public class ViewRecords extends javax.swing.JFrame {
    DefaultTableModel model;
    /** Creates new form ViewRecords */
    public ViewRecords() {
        initComponents();
        setIssueBookDetailsToTable();
    }
    
    public void setIssueBookDetailsToTable() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_db", "root", "Dean@1020");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM issue_book");

            while (rs.next()) {
                String id = rs.getString("id");
                String bookName = rs.getString("book_name");
                String StudentName = rs.getString("student_name");
                String issueDate = rs.getString("issue_date");
                String dueDate = rs.getString("due_date");
                String status = rs.getString("status");

                Object[] obj = {id, bookName, StudentName, issueDate, dueDate, status};
                model = (DefaultTableModel) tbl_issuebookDetails.getModel();
                model.addRow(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    public void clearTable() {
        DefaultTableModel model = (DefaultTableModel) tbl_issuebookDetails.getModel();
        model.setRowCount(0);
    }
    
    public void search() {
        Date uFromDate = fromdate.getDate();
        Date uToDate = todate.getDate();

        if (uFromDate == null || uToDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both From and To dates");
            return;
        }

        long l1 = uFromDate.getTime();
        long l2 = uToDate.getTime();

        // Check if From date is greater than To date
        if (l1 > l2) {
            JOptionPane.showMessageDialog(this, "Invalid date range: From date cannot be later than To date");
            return;
        }

        java.sql.Date fromDate = new java.sql.Date(l1);
        java.sql.Date toDate = new java.sql.Date(l2);

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM issue_book WHERE issue_date BETWEEN ? AND ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDate(1, fromDate);
            pst.setDate(2, toDate);

            ResultSet rs = pst.executeQuery();

            boolean recordFound = false;

            while (rs.next()) {
                recordFound = true;
                String id = rs.getString("id");
                String bookName = rs.getString("book_name");
                String studentName = rs.getString("student_name");
                String issueDate = rs.getString("issue_date");
                String dueDate = rs.getString("due_date");
                String status = rs.getString("status");

                Object[] obj = {id, bookName, studentName, issueDate, dueDate, status};
                model = (DefaultTableModel) tbl_issuebookDetails.getModel();
                model.addRow(obj);
            }

            if (!recordFound) {
                JOptionPane.showMessageDialog(this, "No Record found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel_exit1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        fromdate = new com.toedter.calendar.JDateChooser();
        todate = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btn_search = new rojerusan.RSMaterialButtonRectangle();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_issuebookDetails = new rojeru_san.complementos.RSTableMetro();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(238, 235, 221));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(99, 0, 0));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/AddNewBookIcons/icons8_Literature_100px_1.png"))); // NOI18N
        jLabel9.setText(" View Book Records");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 30, -1, -1));

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
        jPanel1.add(jLabel_exit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 0, -1, -1));

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

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, 330, -1));
        jPanel1.add(fromdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 180, 340, 40));
        jPanel1.add(todate, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 250, 340, 40));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("To Date:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 250, 140, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("From Date:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 180, 140, 40));

        btn_search.setBackground(new java.awt.Color(27, 23, 23));
        btn_search.setText("Search");
        btn_search.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_searchMouseClicked(evt);
            }
        });
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });
        jPanel1.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 200, 190, -1));

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
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 111, 37));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 360));

        jScrollPane2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        tbl_issuebookDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Book Name", "Student", "Issue Date", "Due Date ", "Status"
            }
        ));
        tbl_issuebookDetails.setColorBackgoundHead(new java.awt.Color(99, 0, 0));
        tbl_issuebookDetails.setColorFilasBackgound2(new java.awt.Color(238, 235, 221));
        tbl_issuebookDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_issuebookDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_issuebookDetails.setColorSelBackgound(new java.awt.Color(0, 102, 255));
        tbl_issuebookDetails.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl_issuebookDetails.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tbl_issuebookDetails.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tbl_issuebookDetails.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        tbl_issuebookDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_issuebookDetails.setRowHeight(40);
        tbl_issuebookDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_issuebookDetailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_issuebookDetails);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 380, 1150, 360));

        jPanel2.setBackground(new java.awt.Color(238, 235, 221));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 1370, 410));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_searchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_searchMouseClicked

    }//GEN-LAST:event_btn_searchMouseClicked

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
       if(fromdate.getDate()!= null && todate.getDate() !=null){
            clearTable();
            search();
       }else{
            JOptionPane.showMessageDialog(this,"Please Select A Date");
        }
    }//GEN-LAST:event_btn_searchActionPerformed

    private void tbl_issuebookDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_issuebookDetailsMouseClicked

    }//GEN-LAST:event_tbl_issuebookDetailsMouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        HomePage homepage = new HomePage();
        homepage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel_exit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_exit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel_exit1MouseClicked

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        tbl_issuebookDetails.clearSelection();
        fromdate.setDate(null);
        todate.setDate(null);
        clearTable();
        setIssueBookDetailsToTable();
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        tbl_issuebookDetails.clearSelection();
        fromdate.setDate(null);
        todate.setDate(null);
        clearTable();
        setIssueBookDetailsToTable();
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
            java.util.logging.Logger.getLogger(ViewRecords.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewRecords.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewRecords.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewRecords.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewRecords().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojerusan.RSMaterialButtonRectangle btn_search;
    private com.toedter.calendar.JDateChooser fromdate;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_exit1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private rojeru_san.complementos.RSTableMetro tbl_issuebookDetails;
    private com.toedter.calendar.JDateChooser todate;
    // End of variables declaration//GEN-END:variables

}