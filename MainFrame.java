package com.sms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private final StudentDAO dao = new StudentDAO();
    private final JTextField tfRoll = new JTextField(), tfName = new JTextField(),
            tfEmail = new JTextField(), tfPhone = new JTextField(), tfSearch = new JTextField(20);
    private final JComboBox<String> cbCourse = new JComboBox<>(new String[]{"Computer Science","Mathematics","Physics","Business","Design"});
    private final JComboBox<String> cbYear = new JComboBox<>(new String[]{"1","2","3","4"});
    private final JButton btnSave = new JButton("Add Student"), btnUpdate = new JButton("Update"),
            btnDelete = new JButton("Delete"), btnClear = new JButton("Clear");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Roll No","Name","Email","Course","Year","Phone"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private int selectedId = -1;

    public MainFrame() {
        setTitle("Student Management System");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // Header
        JLabel header = new JLabel("🎓 Student Management System", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setOpaque(true);
        header.setBackground(new Color(79,70,229));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;
        addField(form,g,0,"Roll No:",tfRoll); addField(form,g,1,"Name:",tfName);
        addField(form,g,2,"Email:",tfEmail); addField(form,g,3,"Course:",cbCourse);
        addField(form,g,4,"Year:",cbYear);   addField(form,g,5,"Phone:",tfPhone);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styleBtn(btnSave, new Color(79,70,229)); styleBtn(btnUpdate, new Color(251,191,36));
        styleBtn(btnDelete, new Color(239,68,68)); styleBtn(btnClear, new Color(148,163,184));
        btns.add(btnSave); btns.add(btnUpdate); btns.add(btnDelete); btns.add(btnClear);
        g.gridx=0; g.gridy=6; g.gridwidth=4; form.add(btns, g);

        add(form, BorderLayout.WEST);

        // Search + Table
        JPanel right = new JPanel(new BorderLayout(5,5));
        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sp.add(new JLabel("Search:")); sp.add(tfSearch);
        right.add(sp, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);
        add(right, BorderLayout.CENTER);

        // Listeners
        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());
        tfSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadTable(); }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                selectedId = (int) model.getValueAt(r,0);
                tfRoll.setText((String) model.getValueAt(r,1));
                tfName.setText((String) model.getValueAt(r,2));
                tfEmail.setText((String) model.getValueAt(r,3));
                cbCourse.setSelectedItem(model.getValueAt(r,4));
                cbYear.setSelectedItem(String.valueOf(model.getValueAt(r,5)));
                tfPhone.setText((String) model.getValueAt(r,6));
            }
        });

        loadTable();
    }

    private void addField(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx=0; g.gridy=row; g.gridwidth=1; g.weightx=0; p.add(new JLabel(label), g);
        g.gridx=1; g.weightx=1; field.setPreferredSize(new Dimension(200,28)); p.add(field, g);
    }
    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private Student readForm() {
        if (tfRoll.getText().trim().isEmpty() || tfName.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll No, Name and Email are required.");
            return null;
        }
        Student s = new Student();
        s.setId(selectedId);
        s.setRollNo(tfRoll.getText().trim());
        s.setName(tfName.getText().trim());
        s.setEmail(tfEmail.getText().trim());
        s.setCourse((String) cbCourse.getSelectedItem());
        s.setYear(Integer.parseInt((String) cbYear.getSelectedItem()));
        s.setPhone(tfPhone.getText().trim());
        return s;
    }

    private void save() {
        Student s = readForm(); if (s == null) return;
        try { dao.addStudent(s); clear(); loadTable();
            JOptionPane.showMessageDialog(this, "Student added.");
        } catch (SQLException ex) { err(ex); }
    }
    private void update() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        Student s = readForm(); if (s == null) return;
        try { dao.updateStudent(s); clear(); loadTable();
            JOptionPane.showMessageDialog(this, "Student updated.");
        } catch (SQLException ex) { err(ex); }
    }
    private void delete() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this,"Select a row first."); return; }
        if (JOptionPane.showConfirmDialog(this,"Delete this student?","Confirm",JOptionPane.YES_NO_OPTION)!=0) return;
        try { dao.deleteStudent(selectedId); clear(); loadTable(); }
        catch (SQLException ex) { err(ex); }
    }
    private void clear() {
        selectedId = -1; tfRoll.setText(""); tfName.setText(""); tfEmail.setText(""); tfPhone.setText("");
        cbCourse.setSelectedIndex(0); cbYear.setSelectedIndex(0); table.clearSelection();
    }
    private void loadTable() {
        try {
            List<Student> list = dao.getAllStudents(tfSearch.getText().trim());
            model.setRowCount(0);
            for (Student s : list)
                model.addRow(new Object[]{s.getId(),s.getRollNo(),s.getName(),s.getEmail(),s.getCourse(),s.getYear(),s.getPhone()});
        } catch (SQLException ex) { err(ex); }
    }
    private void err(Exception ex) {
        JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
