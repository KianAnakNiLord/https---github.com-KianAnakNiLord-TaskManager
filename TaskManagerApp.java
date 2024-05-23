import javax.swing.SwingUtilities;

public class TaskManagerApp {
    public static void main(String[] args) {
        
        DatabaseUtil.createTables();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
