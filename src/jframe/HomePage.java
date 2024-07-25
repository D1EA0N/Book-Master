/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import rojeru_san.complementos.RSTableMetro;

/**
 *
 * @author anonu
 */

public class HomePage extends javax.swing.JFrame {
    DefaultTableModel model;
    /**
     * Creates new form HomePage
     */
    
    Color mouseEnterColor = new Color(129,0,0);
    Color mouseExitColor = new Color(27,23,23);
    public HomePage() {
        initComponents();
        showPieChart();
        setStudentDetailsToTable();
        setBookDetailsToTable();
        setDatatoCards();       
    }
    
    public void setStudentDetailsToTable(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_db", "root", "Dean@1020");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM student_details");

            while(rs.next()){

                int studentId = rs.getInt("student_id");
                String studentName = rs.getString("name");
                String course = rs.getString("course");

                Object[] obj = {studentId,studentName,course};
                model = (DefaultTableModel) tbl_studentDetails.getModel();
                model.addRow(obj);
            }
            tbl_studentDetails.addMouseMotionListener((MouseMotionListener) new TooltipMouseListener(tbl_studentDetails));
        }catch(Exception e){
               e.getMessage();
        }                                                                                                                                                                                                                                                                                                                                                       
    }
     
    //Book Details into the table
    public void setBookDetailsToTable(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_db", "root", "Dean@1020");
            
            String query = "SELECT * FROM book_details WHERE status = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "available");
            
            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                String bookId = rs.getString("book_id");
                String bookName = rs.getString("book_name");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");

                Object[] obj = {bookId,bookName,author,quantity, category};
                model = (DefaultTableModel) tbl_bookDetails.getModel();
                model.addRow(obj);
            }
            // Register the mouse motion listener for tooltips
            tbl_bookDetails.addMouseMotionListener((MouseMotionListener) new TooltipMouseListener(tbl_bookDetails));
        }catch(Exception e){
            e.getMessage();
        }                                                                                                                                                                                                                                                                                                                                                     
    }
     
    public void setDatatoCards() {
        PreparedStatement pst = null;
        ResultSet rs = null;

        long l = System.currentTimeMillis();
        Date todaysDate = new Date(l);

        try {
            Connection con = DBConnection.getConnection();

            // For the number of available books
            pst = con.prepareStatement("SELECT * FROM book_details WHERE status = ?");
            pst.setString(1, "available");
            rs = pst.executeQuery();
            rs.last();
            lbl_noOfBooks.setText(Integer.toString(rs.getRow()));

            // For the number of active students
            pst = con.prepareStatement("SELECT * FROM student_details WHERE status = ?");
            pst.setString(1, "active");
            rs = pst.executeQuery();
            rs.last();
            lbl_noOfStudents.setText(Integer.toString(rs.getRow()));

            // For the number of pending issued books
            pst = con.prepareStatement("SELECT * FROM issue_book WHERE status = ?");
            pst.setString(1, "pending");
            rs = pst.executeQuery();
            rs.last();
            lbl_noOfIssuedBooks.setText(Integer.toString(rs.getRow()));

            // For the number of defaulters
            pst = con.prepareStatement("SELECT * FROM issue_book WHERE due_date < ? AND status = ?");
            pst.setDate(1, new java.sql.Date(todaysDate.getTime()));
            pst.setString(2, "pending");
            rs = pst.executeQuery();
            rs.last();
            lbl_defaulterLists.setText(Integer.toString(rs.getRow()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void showPieChart() {    
        // Create dataset
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT book_name, count(*) as issue_count FROM issue_book GROUP BY book_name ORDER BY issue_count DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String bookName = rs.getString("book_name");
                double issueCount = rs.getDouble("issue_count");
                sortedEntries.add(new AbstractMap.SimpleEntry<>(bookName, issueCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Limit to top 10 entries
        int limit = Math.min(6, sortedEntries.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Double> entry = sortedEntries.get(i);
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        // Create chart
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Top 6 Issued Books",
            pieDataset,
            true, 
            true, 
            false
        );

        PiePlot piePlot = (PiePlot) pieChart.getPlot();

        // Change pie chart block colors (make sure these match your dataset keys)
        piePlot.setSectionPaint("IPhone 5s", new Color(255, 255, 102));
        piePlot.setSectionPaint("SamSung Grand", new Color(102, 255, 102));
        piePlot.setSectionPaint("MotoG", new Color(255, 102, 153));
        piePlot.setSectionPaint("Nokia Lumia", new Color(0, 204, 204));
        piePlot.setSectionPaint("Nokia Luumia", new Color(0, 0, 204));

        piePlot.setBackgroundPaint(Color.white);

        // Create ChartPanel to display chart (graph)
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        panelPieChart.removeAll();
        panelPieChart.add(pieChartPanel, BorderLayout.CENTER);
        panelPieChart.validate();
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
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lbl_logout = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnl_mnbooks = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pnl_mnstudent = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pnl_issuebook = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        pnl_return = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        pnl_view = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        pnl_viewissue = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        pnl_defaulter = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        lbl_noOfBooks = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        lbl_noOfStudents = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        lbl_noOfIssuedBooks = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        lbl_defaulterLists = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_studentDetails = new rojeru_san.complementos.RSTableMetro();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_bookDetails = new rojeru_san.complementos.RSTableMetro();
        panelPieChart = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 51));
        setMinimumSize(new java.awt.Dimension(1440, 900));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1440, 900));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(99, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menu.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 60));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("X");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 0, 20, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1482, 60));

        jPanel2.setBackground(new java.awt.Color(27, 23, 23));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(99, 0, 0));
        jPanel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_logout.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_logout.setForeground(new java.awt.Color(255, 255, 255));
        lbl_logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Exit_26px_2.png"))); // NOI18N
        lbl_logout.setText("  Logout");
        lbl_logout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_logoutMouseClicked(evt);
            }
        });
        jPanel3.add(lbl_logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel8.setBackground(new java.awt.Color(129, 0, 0));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel8.setText("Issue Book");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel15.setBackground(new java.awt.Color(129, 0, 0));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel15.setText("View Records");
        jPanel15.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel16.setBackground(new java.awt.Color(129, 0, 0));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel16.setText("Issue Book");
        jPanel16.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel15.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel3.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 620, 330, 60));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 640, 320, 60));

        jPanel4.setBackground(new java.awt.Color(129, 0, 0));
        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Home_26px_2.png"))); // NOI18N
        jLabel4.setText("  Home Page");
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 320, 60));

        pnl_mnbooks.setBackground(new java.awt.Color(27, 23, 23));
        pnl_mnbooks.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_mnbooks.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Book_26px.png"))); // NOI18N
        jLabel6.setText("Manage Books");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel6MouseExited(evt);
            }
        });
        pnl_mnbooks.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel2.add(pnl_mnbooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 320, 60));

        pnl_mnstudent.setBackground(new java.awt.Color(27, 23, 23));
        pnl_mnstudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_mnstudent.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Read_Online_26px.png"))); // NOI18N
        jLabel7.setText("Manage Students");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel7MouseExited(evt);
            }
        });
        pnl_mnstudent.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel2.add(pnl_mnstudent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 320, 60));

        pnl_issuebook.setBackground(new java.awt.Color(27, 23, 23));
        pnl_issuebook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_issuebook.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Sell_26px.png"))); // NOI18N
        jLabel9.setText("Issue Book");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel9MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel9MouseExited(evt);
            }
        });
        pnl_issuebook.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel10.setBackground(new java.awt.Color(129, 0, 0));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel10.setText("Issue Book");
        jPanel10.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        pnl_issuebook.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel2.add(pnl_issuebook, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 320, 60));

        pnl_return.setBackground(new java.awt.Color(27, 23, 23));
        pnl_return.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_return.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 204, 204));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Return_Purchase_26px.png"))); // NOI18N
        jLabel11.setText("Return Book");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel11MouseExited(evt);
            }
        });
        pnl_return.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel12.setBackground(new java.awt.Color(129, 0, 0));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel12.setText("Issue Book");
        jPanel12.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        pnl_return.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel2.add(pnl_return, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 320, 60));

        pnl_view.setBackground(new java.awt.Color(27, 23, 23));
        pnl_view.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_view.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(204, 204, 204));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_View_Details_26px.png"))); // NOI18N
        jLabel17.setText("View Records");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel17MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel17MouseExited(evt);
            }
        });
        pnl_view.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel18.setBackground(new java.awt.Color(129, 0, 0));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel18.setText("Issue Book");
        jPanel18.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        pnl_view.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel19.setBackground(new java.awt.Color(129, 0, 0));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel19.setText("View Records");
        jPanel19.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel20.setBackground(new java.awt.Color(129, 0, 0));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel20.setText("Issue Book");
        jPanel20.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel19.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        pnl_view.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 620, 330, 60));

        jPanel2.add(pnl_view, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 320, 60));

        pnl_viewissue.setBackground(new java.awt.Color(27, 23, 23));
        pnl_viewissue.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_viewissue.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(204, 204, 204));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Books_26px.png"))); // NOI18N
        jLabel25.setText("View Issued Books");
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel25MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel25MouseExited(evt);
            }
        });
        pnl_viewissue.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel26.setBackground(new java.awt.Color(129, 0, 0));
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel26.setText("Issue Book");
        jPanel26.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        pnl_viewissue.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel27.setBackground(new java.awt.Color(129, 0, 0));
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel27.setText("View Records");
        jPanel27.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel28.setBackground(new java.awt.Color(129, 0, 0));
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel28.setText("Issue Book");
        jPanel28.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel27.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        pnl_viewissue.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 620, 330, 60));

        jPanel2.add(pnl_viewissue, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 320, 60));

        pnl_defaulter.setBackground(new java.awt.Color(27, 23, 23));
        pnl_defaulter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnl_defaulter.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(204, 204, 204));
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Conference_26px.png"))); // NOI18N
        jLabel29.setText("Defaulter List");
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel29MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel29MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel29MouseExited(evt);
            }
        });
        pnl_defaulter.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel30.setBackground(new java.awt.Color(129, 0, 0));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel30.setText("Issue Book");
        jPanel30.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        pnl_defaulter.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        jPanel31.setBackground(new java.awt.Color(129, 0, 0));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel31.setText("View Records");
        jPanel31.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel32.setBackground(new java.awt.Color(129, 0, 0));
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Library_32px.png"))); // NOI18N
        jLabel32.setText("Issue Book");
        jPanel32.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel31.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 330, 60));

        pnl_defaulter.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 620, 330, 60));

        jPanel2.add(pnl_defaulter, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 570, 320, 60));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Features");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 110, 50));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 320, 930));

        jPanel13.setBackground(new java.awt.Color(238, 235, 221));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(204, 0, 0)));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_noOfBooks.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        lbl_noOfBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N
        lbl_noOfBooks.setText("0");
        jPanel14.add(lbl_noOfBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 160, 80));

        jPanel13.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 200, 140));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Student Details");
        jPanel13.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, 30));

        jPanel21.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(204, 0, 0)));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_noOfStudents.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        lbl_noOfStudents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_People_50px.png"))); // NOI18N
        lbl_noOfStudents.setText("0");
        jPanel21.add(lbl_noOfStudents, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 160, 80));

        jPanel13.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 50, 210, 140));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setText("Number of Students");
        jPanel13.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, -1, -1));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setText("Issued Books");
        jPanel13.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, -1, -1));

        jPanel22.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(204, 0, 0)));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_noOfIssuedBooks.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        lbl_noOfIssuedBooks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_Sell_50px.png"))); // NOI18N
        lbl_noOfIssuedBooks.setText("0");
        jPanel22.add(lbl_noOfIssuedBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 160, 80));

        jPanel13.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 50, 210, 140));

        jPanel23.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(204, 0, 0)));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_defaulterLists.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        lbl_defaulterLists.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/adminIcons/icons8_List_of_Thumbnails_50px.png"))); // NOI18N
        lbl_defaulterLists.setText("0");
        jPanel23.add(lbl_defaulterLists, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 160, 80));

        jPanel13.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 50, 210, 140));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setText("Defaulter List");
        jPanel13.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, -1, -1));

        jScrollPane1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        tbl_studentDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Name", "Course"
            }
        ));
        tbl_studentDetails.setColorBackgoundHead(new java.awt.Color(99, 0, 0));
        tbl_studentDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_studentDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_studentDetails.setColorSelBackgound(new java.awt.Color(0, 102, 255));
        tbl_studentDetails.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tbl_studentDetails.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        tbl_studentDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_studentDetails.setRowHeight(40);
        jScrollPane1.setViewportView(tbl_studentDetails);

        jPanel13.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 700, 220));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setText("Number of Books");
        jPanel13.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel37.setText("Book Details");
        jPanel13.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jScrollPane2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        tbl_bookDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Book ID", "Name", "Author", "Quantity", "Category"
            }
        ));
        tbl_bookDetails.setColorBackgoundHead(new java.awt.Color(99, 0, 0));
        tbl_bookDetails.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tbl_bookDetails.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tbl_bookDetails.setColorSelBackgound(new java.awt.Color(0, 102, 255));
        tbl_bookDetails.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteFilas(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteFilasSelect(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        tbl_bookDetails.setFuenteHead(new java.awt.Font("Yu Gothic UI Semibold", 1, 20)); // NOI18N
        tbl_bookDetails.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_bookDetails.setRowHeight(40);
        jScrollPane2.setViewportView(tbl_bookDetails);

        jPanel13.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 700, 220));

        panelPieChart.setLayout(new java.awt.BorderLayout());
        jPanel13.add(panelPieChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 300, 270, 340));

        getContentPane().add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 1160, 710));

        setLocation(new java.awt.Point(0, 0));
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        ManageBooks manage = new ManageBooks();
        manage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseEntered
        pnl_mnbooks.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel6MouseEntered

    private void jLabel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseExited
        pnl_mnbooks.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel6MouseExited

    private void jLabel7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseEntered
        pnl_mnstudent.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel7MouseEntered

    private void jLabel7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseExited
        pnl_mnstudent.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel7MouseExited

    private void jLabel9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseEntered
        pnl_issuebook.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel9MouseEntered

    private void jLabel9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseExited
         pnl_issuebook.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel9MouseExited

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        IssueBook issue = new IssueBook();
        issue.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        ManageStudents stud = new ManageStudents();
        stud.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseEntered
            pnl_return.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel11MouseEntered

    private void jLabel11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseExited
            pnl_return.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel11MouseExited

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        ReturnBook returnbook = new ReturnBook();
        returnbook.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel17MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseEntered
        pnl_view.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel17MouseEntered

    private void jLabel17MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseExited
        pnl_view.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel17MouseExited

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        ViewRecords view = new ViewRecords();
        view.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jLabel25MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseEntered
        pnl_viewissue.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel25MouseEntered

    private void jLabel25MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseExited
        pnl_viewissue.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel25MouseExited

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        IssuedBook issued = new IssuedBook();
        issued.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel29MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseEntered
        pnl_defaulter.setBackground(mouseEnterColor);
    }//GEN-LAST:event_jLabel29MouseEntered

    private void jLabel29MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseExited
        pnl_defaulter.setBackground(mouseExitColor);
    }//GEN-LAST:event_jLabel29MouseExited

    private void jLabel29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseClicked
        DefaulterList defaulter = new DefaulterList();
        defaulter.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel29MouseClicked

    private void lbl_logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_logoutMouseClicked
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_lbl_logoutMouseClicked

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
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_defaulterLists;
    private javax.swing.JLabel lbl_logout;
    private javax.swing.JLabel lbl_noOfBooks;
    private javax.swing.JLabel lbl_noOfIssuedBooks;
    private javax.swing.JLabel lbl_noOfStudents;
    private javax.swing.JPanel panelPieChart;
    private javax.swing.JPanel pnl_defaulter;
    private javax.swing.JPanel pnl_issuebook;
    private javax.swing.JPanel pnl_mnbooks;
    private javax.swing.JPanel pnl_mnstudent;
    private javax.swing.JPanel pnl_return;
    private javax.swing.JPanel pnl_view;
    private javax.swing.JPanel pnl_viewissue;
    private rojeru_san.complementos.RSTableMetro tbl_bookDetails;
    private rojeru_san.complementos.RSTableMetro tbl_studentDetails;
    // End of variables declaration//GEN-END:variables
}
