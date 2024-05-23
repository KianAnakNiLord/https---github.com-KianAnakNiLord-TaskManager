import javax.swing.SwingUtilities;

public class TaskManagerApp {
    public static void main(String[] args) {
        // Ensure database tables are created
        DatabaseUtil.createTables();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
