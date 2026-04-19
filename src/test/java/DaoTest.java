import Branch.AuthService;
import Client.Controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class DaoTest extends Application {
    public void start(Stage stage) throws Exception {
        AuthService.registerNewUser("Bui", "Ha", "buihalinh@gmail.com", "0835361207", "060108");
        AuthService.registerNewUser("Admin", "123", "admin@gmail.com", "0123456789", "000000");
        SceneManager.setStage(stage);
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }
}
