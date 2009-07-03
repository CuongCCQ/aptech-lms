/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Main.java
 *
 * Created on Jun 29, 2009, 5:41:30 PM
 */

package student;


import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Kusanagi
 */
public class Main extends javax.swing.JFrame {
    static ArrayList listStudent;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Statement stmt = null;
    String fullname_txt = null;
    String address_txt = null;
    String birthday_txt = null;
    String classname_txt = null;
    String email_txt = null;
    int studentID_txt = 0;
    int index = 0;

    private boolean checkData(){

        // TODO add your handling code here:
        email_txt = email.getText();
        boolean check1 = checkEmail(email_txt);
        boolean check2 = checkBlank(email_txt);

        fullname_txt = fullname.getText();
        boolean check3 = checkBlank(fullname_txt);

        classname_txt = classname.getText();
        boolean check4 = checkBlank(classname_txt);

       address_txt = address.getText();
        boolean check5 = checkBlank(address_txt);

        String[] birthday_arr;
        birthday_txt = birthday.getText();
        birthday_arr = birthday_txt.split("/");
        boolean check6 = true;
        if(birthday_arr[0].equalsIgnoreCase("00") || birthday_arr[1].equalsIgnoreCase("00") || birthday_arr[2].equalsIgnoreCase("0000"))
        {
            check6 = false;
            birthday_txt = birthday_arr[0] + "/" + birthday_arr[1] + "/" + birthday_arr[2];
        }

        if(check1 && check2 && check3 && check4 && check5 && check6){
            return true;
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid !");
            return false;
        }
    }

    private boolean checkUnique(String str){
        boolean result = true;
        for(int i=0;i<listStudent.size();i++){
            Student stu = (Student) listStudent.get(i);
            if(str.equals(stu.getFullname())){
                result = false;
                break;
            }
        }
        return result;
    }

    private void insertData() {
        boolean check = checkData();
        if(check == true)
        {
            if(checkUnique(fullname_txt) == true)
            {

                // TODO add your handling code here:
                String sql = "INSERT INTO dbo.student(fullname,address,birthday,email,class) VALUES(?,?,?,?,?)";
                pstmt = null;

                try {
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, fullname_txt);
                    pstmt.setString(2, address_txt);
                    pstmt.setString(3, birthday_txt);
                    pstmt.setString(4, email_txt);
                    pstmt.setString(5, classname_txt);

                    int rowCount = pstmt.executeUpdate();
                    if(rowCount>0)
                      JOptionPane.showMessageDialog(this, "Data inserted !");

                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(this, e);
                    System.out.print(e);
                    System.exit(0);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "This user is not available !");
            }
        }
    }


    private void updateData() {
        boolean check = checkData();
        if(check == true)
        {
            // TODO add your handling code here:
            

            try {
                String sql = "UPDATE dbo.student SET fullname=?,address =?,birthday =?,email=?,class =? WHERE studentID=?";
                pstmt = null;
                pstmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, fullname_txt);
                pstmt.setString(2, address_txt);
                pstmt.setString(3, birthday_txt);
                pstmt.setString(4, email_txt);
                pstmt.setString(5, classname_txt);
                pstmt.setInt(6, studentID_txt);
                
                pstmt.execute();
                JOptionPane.showMessageDialog(this, "Data updated !");
                
                
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(this, e);
                System.out.print(e);
                //System.exit(0);
            }
        }
    }


    private void deleteData() {
        boolean check = checkData();
        if(check == true)
        {
            // TODO add your handling code here:


            try {
                String sql = "DELETE dbo.student WHERE studentID=?";
                pstmt = null;
                pstmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                pstmt.setInt(1, studentID_txt);

                pstmt.execute();
                JOptionPane.showMessageDialog(this, "Data deleted !");
                loadData();
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(this, e);
                System.out.print(e);
                //System.exit(0);
            }
        }
    }


    private void openConnect(){
        String ip = "127.0.0.1";
        String instanceName = "MSSQLSERVER";
        String db = "student";
        String uid = "sa";
        String pwd = "sa";
        String port = "1433";
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://" + ip + "\\" + instanceName + ":" + port +
                     ";DatabaseName=" + db + ";user=" + uid + ";password=" + pwd;

        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, uid, pwd);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
            System.out.print(e);
            System.exit(0);
        }
    }
    /** Creates new form Main */
    public Main() {
        initComponents();
        openConnect();
        
        
        refeshTable();
        actionBtn.setEnabled(true);
        nextBtn.setEnabled(false);
        prevBtn.setEnabled(false);
        firstBtn.setEnabled(false);
        lastBtn.setEnabled(false);


        

    }

    public void refeshTable(){
        listData();
        
        DefaultTableModel model = (DefaultTableModel)StudentList.getModel();
        model.setColumnCount(0);
        model.addColumn("Id");
        model.addColumn("Name");
        model.addColumn("Address");
        model.addColumn("Birthday");
        model.addColumn("Email");
        model.addColumn("Class");
        model.setRowCount(0);
        
        for (int i = 0; i < listStudent.size(); i++)
        {
            Student s = (Student)listStudent.get(i);
            Vector vRow = new Vector();
            vRow.addElement(s.getID());
            vRow.addElement(s.getFullname());
            vRow.addElement(s.getAddress());
            vRow.addElement(s.getBirthday());
            vRow.addElement(s.getEmail());
            vRow.addElement(s.getClassname());
            model.addRow(vRow);
        }
    }

    public void listData(){
        String sql = "SELECT * FROM student";
        try{
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(sql);
            rs.beforeFirst();

            listStudent = new ArrayList();
            listStudent.clear();
            
            while(rs.next()){
                Student s = new Student();
                s.setID(rs.getString(1));
                s.setFullname(rs.getString(2));
                s.setAddress(rs.getString(3));
                s.setBirthday(rs.getString(4));
                s.setEmail(rs.getString(5));
                s.setClassname(rs.getString(6));
                listStudent.add(s);
                /*
                System.out.print(rs.getString(1) +  " | ");
                System.out.print(rs.getString(2) +  " | ");
                System.out.print(rs.getString(3) +  " | ");
                System.out.print(rs.getString(4) +  " | ");
                System.out.print(rs.getString(5) +  " | ");
                System.out.print(rs.getString(6) +  "\n");
                */
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
            System.exit(0);
        }
    }

    public void loadData(){
        String sql = "SELECT * FROM student";
        try{
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

            rs = stmt.executeQuery(sql);
            if(rs.next() == true){
                showResult();
            }
            else{
                JOptionPane.showMessageDialog(null,"No data !");
                System.exit(0);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
            System.exit(0);
        }
    }

    private boolean checkEmail(String email) {
        //Set the email pattern string
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

        //Match the given string with the pattern
        Matcher m = p.matcher(email);

        //check whether match is found
        boolean matchFound = m.matches();

        if (matchFound)
            return true;
        else
            return false;
    }

    private boolean checkBlank(String str) {
        if (str.isEmpty() || str.trim().equals(""))
            return false;
        else
            return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ActionSelect = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fullname = new javax.swing.JTextField();
        address = new javax.swing.JTextField();
        classname = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        birthday = new javax.swing.JFormattedTextField();
        actionBtn = new javax.swing.JButton();
        firstBtn = new javax.swing.JButton();
        lastBtn = new javax.swing.JButton();
        prevBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        addRadio = new javax.swing.JRadioButton();
        editRadio = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        deleteRadio = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        StudentList = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Fullname");

        jLabel2.setText("Address");

        jLabel3.setText("Class");

        jLabel4.setText("Birthday");

        jLabel5.setText("Email");

        classname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classnameActionPerformed(evt);
            }
        });

        try{
            MaskFormatter formatter = new MaskFormatter("##/##/####");
            birthday = new JFormattedTextField(formatter);
            birthday.setValue("00/00/0000");   
        }
        catch(java.text.ParseException e){
            e.printStackTrace();
        }
        birthday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                birthdayActionPerformed(evt);
            }
        });

        actionBtn.setText("Add");
        actionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionBtnActionPerformed(evt);
            }
        });

        firstBtn.setText("<<");
        firstBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstBtnActionPerformed(evt);
            }
        });

        lastBtn.setText(">>");
        lastBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastBtnActionPerformed(evt);
            }
        });

        prevBtn.setText("<");
        prevBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBtnActionPerformed(evt);
            }
        });

        nextBtn.setText(">");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });

        ActionSelect.add(addRadio);
        addRadio.setSelected(true);
        addRadio.setText("Add");
        addRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addRadioMouseReleased(evt);
            }
        });
        addRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRadioActionPerformed(evt);
            }
        });

        ActionSelect.add(editRadio);
        editRadio.setText("Edit");
        editRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                editRadioMouseReleased(evt);
            }
        });
        editRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRadioActionPerformed(evt);
            }
        });

        jButton1.setText("View Table");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ActionSelect.add(deleteRadio);
        deleteRadio.setText("Delete");
        deleteRadio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                deleteRadioMouseReleased(evt);
            }
        });
        deleteRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRadioActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel6.setText("Student Management");

        StudentList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "StudentID", "Fullname", "Address", "Birthday", "Email", "Class"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        StudentList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StudentListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(StudentList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(actionBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(firstBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(prevBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nextBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lastBtn))
                                    .addComponent(birthday, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                    .addComponent(fullname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                    .addComponent(address, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                    .addComponent(classname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                    .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(editRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                                .addComponent(jButton1))))
                    .addComponent(jLabel6))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addRadio)
                            .addComponent(editRadio)
                            .addComponent(deleteRadio)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(fullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(birthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(classname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(actionBtn)
                            .addComponent(firstBtn)
                            .addComponent(lastBtn)
                            .addComponent(prevBtn)
                            .addComponent(nextBtn))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void classnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classnameActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_classnameActionPerformed

    private void birthdayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_birthdayActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_birthdayActionPerformed

    private void actionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionBtnActionPerformed
        if(actionBtn.getText().equals("Add"))
            insertData();
        else if(actionBtn.getText().equals("Edit"))
            updateData();
        else
            deleteData();

        refeshTable();
}//GEN-LAST:event_actionBtnActionPerformed

    private void firstBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstBtnActionPerformed
        // TODO add your handling code here:
        try{
            rs.first();
            showResult();
        }
        catch(SQLException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
        }
}//GEN-LAST:event_firstBtnActionPerformed

    private void lastBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastBtnActionPerformed
        // TODO add your handling code here:
        try{
            rs.last();
            showResult();
        }
        catch(SQLException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
        }
}//GEN-LAST:event_lastBtnActionPerformed

    private void prevBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBtnActionPerformed
        // TODO add your handling code here:
        try{
            rs.previous();
            showResult();
        }
        catch(SQLException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
        }
}//GEN-LAST:event_prevBtnActionPerformed

    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        // TODO add your handling code here:
        try{
            rs.next();
            showResult();
        }
        catch(SQLException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
        }
}//GEN-LAST:event_nextBtnActionPerformed

    private void addRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRadioActionPerformed
        // TODO add your handling code here:
        fullname.setText("");
        email.setText("");
        address.setText("");
        birthday.setText("00/00/0000");
        classname.setText("");

        actionBtn.setText("Add");
        nextBtn.setEnabled(false);
        prevBtn.setEnabled(false);
        firstBtn.setEnabled(false);
        lastBtn.setEnabled(false);
    }//GEN-LAST:event_addRadioActionPerformed

    private void editRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRadioActionPerformed
        // TODO add your handling code here:
        loadData();
        actionBtn.setText("Edit");
        
    }//GEN-LAST:event_editRadioActionPerformed

    private void editRadioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editRadioMouseReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_editRadioMouseReleased

    private void addRadioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addRadioMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_addRadioMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        listData();
        TableStudent tb = new TableStudent();
        tb.setVisible(true);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void deleteRadioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteRadioMouseReleased
        // TODO add your handling code here:
}//GEN-LAST:event_deleteRadioMouseReleased

    private void deleteRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRadioActionPerformed
        // TODO add your handling code here:
        loadData();
        actionBtn.setText("Delete");
}//GEN-LAST:event_deleteRadioActionPerformed

    private void StudentListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StudentListMouseClicked
        // TODO add your handling code here:
        index = StudentList.getSelectedRow();
        String sid = (String)StudentList.getValueAt(index, 0);
        studentID_txt = Integer.parseInt(sid);
        if(actionBtn.getText().equals("Delete") || actionBtn.getText().equals("Edit")){
            showResult();
        }
    }//GEN-LAST:event_StudentListMouseClicked
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup ActionSelect;
    private javax.swing.JTable StudentList;
    private javax.swing.JButton actionBtn;
    private javax.swing.JRadioButton addRadio;
    private javax.swing.JTextField address;
    private javax.swing.JFormattedTextField birthday;
    private javax.swing.JTextField classname;
    private javax.swing.JRadioButton deleteRadio;
    private javax.swing.JRadioButton editRadio;
    private javax.swing.JTextField email;
    private javax.swing.JButton firstBtn;
    private javax.swing.JTextField fullname;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton lastBtn;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton prevBtn;
    // End of variables declaration//GEN-END:variables

    private void showResult() {
        try{
            
            rs.absolute(index + 1);
            fullname.setText(rs.getString("fullname"));
            address.setText(rs.getString("address"));
            classname.setText(rs.getString("class"));
            birthday.setText(rs.getString("birthday"));
            email.setText(rs.getString("email"));
            studentID_txt = rs.getInt("studentID");
            
            if(rs.next() == true){
                nextBtn.setEnabled(true);
                lastBtn.setEnabled(true);
            }
            else{
                nextBtn.setEnabled(false);
                lastBtn.setEnabled(false);
            }
            rs.previous();
            
            if(rs.previous() == true){
                prevBtn.setEnabled(true);
                firstBtn.setEnabled(true);
            }
            else{
                prevBtn.setEnabled(false);
                firstBtn.setEnabled(false);
            }
            rs.next();

        }
        catch(SQLException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

}
