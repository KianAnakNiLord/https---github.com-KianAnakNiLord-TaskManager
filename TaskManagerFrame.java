import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class TaskManagerFrame extends JFrame {
    private int userId;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Integer> yearComboBox;
    private JComboBox<Integer> monthComboBox;
    private JComboBox<Integer> dayComboBox;
    private JButton addButton;
    private JButton removeButton;

    public TaskManagerFrame(int userId) {
        this.userId = userId;
        setTitle("Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        loadTasks();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(20, 20, 20));

        JPanel taskListPanel = new JPanel(new BorderLayout());
        taskListPanel.setBackground(new Color(30, 30, 30));
        taskListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Tasks", 0, 0,
                new Font("Arial", Font.BOLD, 16), Color.WHITE));

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));
        taskList.setBackground(new Color(50, 50, 50));
        taskList.setForeground(Color.WHITE);
        taskList.setCellRenderer(new TaskCellRenderer());

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Task selectedTask = taskListModel.get(index);
                        showTaskDetails(selectedTask);
                    }
                }
            }
        });

        JScrollPane taskScrollPane = new JScrollPane(taskList);
        taskScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        taskListPanel.add(taskScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(30, 30, 30));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Add New Task", 0, 0,
                new Font("Arial", Font.BOLD, 16), Color.WHITE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField(20);
        styleTextField(titleField);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        styleTextArea(descriptionArea);

        yearComboBox = new JComboBox<>();
        for (int i = 2020; i <= 2040; i++) {
            yearComboBox.addItem(i);
        }

        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }

        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayComboBox.addItem(i);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(createInputRow("Title:", titleField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(createInputRow("Description:", new JScrollPane(descriptionArea)), gbc);

        JPanel dueDatePanel = new JPanel();
        dueDatePanel.setBackground(new Color(30, 30, 30));
        dueDatePanel.add(new JLabel("Year:"));
        dueDatePanel.add(yearComboBox);
        dueDatePanel.add(new JLabel("Month:"));
        dueDatePanel.add(monthComboBox);
        dueDatePanel.add(new JLabel("Day:"));
        dueDatePanel.add(dayComboBox);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(createInputRow("Due Date:", dueDatePanel), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(20, 20, 20));
        addButton = createStyledButton("Add Task");
        removeButton = createStyledButton("Remove Task");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        mainPanel.add(taskListPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTask();
            }
        });
    }

    private void styleTextField(JTextField textField) {
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        textField.setBackground(new Color(50, 50, 50));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setBackground(new Color(50, 50, 50));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private JPanel createInputRow(String labelText, Component inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 30));
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.WEST);
        panel.add(inputComponent, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(200, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void loadTasks() {
        taskListModel.clear();
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tasks WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task(rs.getString("title"), rs.getString("description"), rs.getString("due_date"));
                taskListModel.addElement(task);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addTask() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        int day = (int) dayComboBox.getSelectedItem();
        String dueDate = LocalDate.of(year, month, day).toString();

        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (user_id, title, description, due_date) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, dueDate);
            stmt.executeUpdate();
            loadTasks();
            clearInputFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearInputFields() {
        titleField.setText("");
        descriptionArea.setText("");
        yearComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
    }

    private void removeTask() {
        Task selectedTask = taskList.getSelectedValue();
        if (selectedTask != null) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE user_id = ? AND title = ?");
                stmt.setInt(1, userId);
                stmt.setString(2, selectedTask.getTitle());
                stmt.executeUpdate();
                loadTasks();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showTaskDetails(Task task) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText("<html><body style='font-family:Arial; color:white; background-color:black;'>" +
                "<h2 style='color:red;'>" + task.getTitle() + "</h2>" +
                "<p><b>Description:</b> " + task.getDescription() + "</p>" +
                "<p><b>Due Date:</b> " + task.getDueDate() + "</p>" +
                "</body></html>");
        textPane.setEditable(false);
    
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(new Color(20, 20, 20));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(20, 20, 20));
        
        JButton editButton = createStyledButton("Edit");
    
        buttonPanel.add(editButton);
    
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        JDialog dialog = new JDialog(this, "Task Details", true);
    
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTask(task);
                dialog.dispose();
            }
        });
    
        dialog.getContentPane().add(detailPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    

    private void editTask(Task task) {
        JDialog dialog = new JDialog(this, "Edit Task", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
    
        JTextField editTitleField = new JTextField(task.getTitle(), 20);
        JTextArea editDescriptionArea = new JTextArea(task.getDescription(), 5, 20);
        editDescriptionArea.setLineWrap(true);
        editDescriptionArea.setWrapStyleWord(true);
    
        JComboBox<Integer> editYearComboBox = new JComboBox<>();
        for (int i = 2020; i <= 2040; i++) {
            editYearComboBox.addItem(i);
        }
        JComboBox<Integer> editMonthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            editMonthComboBox.addItem(i);
        }
        JComboBox<Integer> editDayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            editDayComboBox.addItem(i);
        }
    
        LocalDate dueDate = LocalDate.parse(task.getDueDate());
        editYearComboBox.setSelectedItem(dueDate.getYear());
        editMonthComboBox.setSelectedItem(dueDate.getMonthValue());
        editDayComboBox.setSelectedItem(dueDate.getDayOfMonth());
    
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(new Color(30, 30, 30));
        editPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Edit Task", 0, 0,
                new Font("Arial", Font.BOLD, 16), Color.WHITE));
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        editPanel.add(createInputRow("Title:", editTitleField), gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 1;
        editPanel.add(createInputRow("Description:", new JScrollPane(editDescriptionArea)), gbc);
    
        JPanel dueDatePanel = new JPanel();
        dueDatePanel.setBackground(new Color(30, 30, 30));
        dueDatePanel.add(new JLabel("Year:"));
        dueDatePanel.add(editYearComboBox);
        dueDatePanel.add(new JLabel("Month:"));
        dueDatePanel.add(editMonthComboBox);
        dueDatePanel.add(new JLabel("Day:"));
        dueDatePanel.add(editDayComboBox);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        editPanel.add(createInputRow("Due Date:", dueDatePanel), gbc);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(20, 20, 20));
    
        JButton confirmButton = createStyledButton("Confirm");
        JButton cancelButton = createStyledButton("Cancel");
    
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
    
        dialog.add(editPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTitle = editTitleField.getText();
                String newDescription = editDescriptionArea.getText();
                String newDueDate = LocalDate.of(
                        (int) editYearComboBox.getSelectedItem(),
                        (int) editMonthComboBox.getSelectedItem(),
                        (int) editDayComboBox.getSelectedItem()).toString();
    
                updateTaskInDatabase(task, newTitle, newDescription, newDueDate);
    
                task.setTitle(newTitle);
                task.setDescription(newDescription);
                task.setDueDate(newDueDate);
    
                taskList.repaint(); // Repaint the list to reflect the changes
    
                dialog.dispose();
            }
        });
    
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void updateTaskInDatabase(Task task, String newTitle, String newDescription, String newDueDate) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE tasks SET title = ?, description = ?, due_date = ? WHERE user_id = ? AND title = ?");
            stmt.setString(1, newTitle);
            stmt.setString(2, newDescription);
            stmt.setString(3, newDueDate);
            stmt.setInt(4, userId);
            stmt.setString(5, task.getTitle());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    

    private void updateTaskInDatabase(Task task) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE tasks SET title = ?, description = ?, due_date = ? WHERE user_id = ? AND title = ?");
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getDueDate());
            stmt.setInt(4, userId);
            stmt.setString(5, task.getTitle());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TaskManagerFrame(1).setVisible(true);
        });
    }
}

class Task {
    private String title;
    private String description;
    private String dueDate;
    private boolean completed;

    public Task(String title, String description, String dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return title + " - " + description + " - " + dueDate + (completed ? " [Completed]" : "");
    }
}

class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel dueDateLabel;

    public TaskCellRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(50, 50, 50));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(255, 200, 0));

        descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.WHITE);

        dueDateLabel = new JLabel();
        dueDateLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        dueDateLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(50, 50, 50));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descriptionLabel, BorderLayout.CENTER);
        textPanel.add(dueDateLabel, BorderLayout.SOUTH);

        add(textPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
        titleLabel.setText(task.getTitle());
        descriptionLabel.setText(task.getDescription());
        dueDateLabel.setText(task.getDueDate());

        if (isSelected) {
            setBackground(new Color(70, 70, 70));
        } else {
            setBackground(new Color(50, 50, 50));
        }

        return this;
    }
}