package gui;

import entity.Cow;
import fileio.CowFileIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class CowGUI extends JFrame {
    private JTextField tagField, breedField, ageField, yieldField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public CowGUI() {
        setTitle("Cow Farm Management Workspace");
        setSize(950, 600);
        
        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        setLayout(new BorderLayout(15, 15));

        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(2,4,10,10));
        leftPanel.setPreferredSize(new Dimension(320, 400));

        
        JPanel formContainer = new JPanel(new GridLayout(4, 2, 5, 12));
        formContainer.setBorder(BorderFactory.createTitledBorder(" Livestock Management Profiles "));
        
        formContainer.add(new JLabel("Tag ID (8 Digits):"));
        tagField = new JTextField();
        formContainer.add(tagField);

        formContainer.add(new JLabel("Breed Name:"));
        breedField = new JTextField();
        formContainer.add(breedField);

        formContainer.add(new JLabel("Age (Years):"));
        ageField = new JTextField();
        formContainer.add(ageField);

        formContainer.add(new JLabel("Milk (L):"));
        yieldField = new JTextField();
        formContainer.add(yieldField);

        
        JPanel operationalGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        operationalGrid.setBorder(BorderFactory.createTitledBorder("  Operations "));
        
        JButton addBtn = new JButton("Add Cow");
        JButton updateBtn = new JButton("Update Changes");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton refreshBtn = new JButton("View All ");

        operationalGrid.add(addBtn);
        operationalGrid.add(updateBtn);
        operationalGrid.add(deleteBtn);
        operationalGrid.add(clearBtn);
        operationalGrid.add(refreshBtn);

        leftPanel.add(formContainer);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(operationalGrid);

        
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));

        
        JPanel queryBar = new JPanel(new BorderLayout(5, 5));
        queryBar.setBorder(BorderFactory.createTitledBorder(" Live Search (ID / Breed Name) "));
        searchField = new JTextField();
        JButton searchBtn = new JButton("search");
        queryBar.add(searchField, BorderLayout.CENTER);
        queryBar.add(searchBtn, BorderLayout.EAST);

        
        String[] headerTitles = { " Tag ID", "Breed ", "Age ", "Milk (L)" };
        tableModel = new DefaultTableModel(headerTitles, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; } 
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);

        rightPanel.add(queryBar, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        
        addBtn.addActionListener(e -> processInsert());
        updateBtn.addActionListener(e -> processUpdate());
        deleteBtn.addActionListener(e -> processDelete());
        clearBtn.addActionListener(e -> resetInputForm());
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadDataGrid(); });
        searchBtn.addActionListener(e -> executeQueryFilter());

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                tagField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                breedField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                ageField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                yieldField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
            }
        });

        
        try {
            CowFileIO.createFileIfNotExists();
        } catch (IOException ex) {
            displayAlertError("Initialization Disk Fault: " + ex.getMessage());
        }

        loadDataGrid();
        
        
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    
    private void processInsert() {
        String id = tagField.getText().trim();
        String breed = breedField.getText().trim();
        String age = ageField.getText().trim();
        String yield = yieldField.getText().trim();

        if (!runDataValidationGuard(id, breed, age, yield)) return;

        
        if (CowFileIO.tagIdExists(id)) {
            displayAlertError("Data Integrity Violation: Tag ID '" + id + "' already assigned to another livestock profile.");
            return;
        }

        try {
            CowFileIO.addCow(new Cow(id, breed, age, yield));
            loadDataGrid();
            resetInputForm();
            JOptionPane.showMessageDialog(this, "Profile added successfully.", "Operation Completed", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            displayAlertError("Writing Operation Failure: " + ex.getMessage());
        }
    }

    private void processUpdate() {
        String id = tagField.getText().trim();
        String breed = breedField.getText().trim();
        String age = ageField.getText().trim();
        String yield = yieldField.getText().trim();

        if (!runDataValidationGuard(id, breed, age, yield)) return;

        try {
            if (CowFileIO.updateCow(new Cow(id, breed, age, yield))) {
                loadDataGrid();
                resetInputForm();
                JOptionPane.showMessageDialog(this, "Profile metadata updated safely.", "Operation Completed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                displayAlertError("Target Record Key could not be resolved.");
            }
        } catch (IOException ex) {
            displayAlertError("Modification Engine Failure: " + ex.getMessage());
        }
    }

    private void processDelete() {
        String id = tagField.getText().trim();
        if (id.isEmpty()) {
            displayAlertError("Operational Request Denied: Please highlight or specify a valid record selection target.");
            return;
        }

        
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete record #" + id + "?", "Confirmation Required", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (CowFileIO.deleteCow(id)) {
                    loadDataGrid();
                    resetInputForm();
                    JOptionPane.showMessageDialog(this, "Record successfully dropped from registry.", "Status Update", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    displayAlertError("Target record does not exist.");
                }
            } catch (IOException ex) {
                displayAlertError("Deletion Operations Fault: " + ex.getMessage());
            }
        }
    }

    private void executeQueryFilter() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadDataGrid();
            return;
        }
        renderTableRows(CowFileIO.searchCows(query));
    }

    private void loadDataGrid() {
        renderTableRows(CowFileIO.getAllCows());
    }

    private void renderTableRows(Object[][] structuredData) {
        tableModel.setRowCount(0);
        for (Object[] rowData : structuredData) {
            tableModel.addRow(rowData);
        }
    }

    private void resetInputForm() {
        tagField.setText("");
        breedField.setText("");
        ageField.setText("");
        yieldField.setText("");
        table.clearSelection();
    }

 
    private boolean runDataValidationGuard(String id, String breed, String age, String yield) {
        // Rule B: All fields are required to Create any record in the database.
        if (id.isEmpty() || breed.isEmpty() || age.isEmpty() || yield.isEmpty()) {
            displayAlertError("Validation Error: Blank input parameters rejected. All database column attributes must be filled.");
            return false;
        }

        
        if (!id.matches("^\\d{8}$")) {
            displayAlertError("Validation Error: The Tag ID input length must contain exactly 8 numeric digits (Integers only).");
            return false;
        }

       
        try {
            int parsedAge = Integer.parseInt(age);
            double parsedYield = Double.parseDouble(yield);
            if (parsedAge < 0 || parsedYield < 0.0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            displayAlertError("Validation Error: Age and Production tracking quantities must be logical positive metrics.");
            return false;
        }

        return true;
    }

    private void displayAlertError(String errorInfoDescription) {
        JOptionPane.showMessageDialog(this, errorInfoDescription, "Data Validation Failure", JOptionPane.ERROR_MESSAGE);
    }
}