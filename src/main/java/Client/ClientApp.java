package Client;

import Branch.*;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.ItemsDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.List;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        UsersDAO userDb = DaoFactory.createUsersDAO();
        ItemsDAO itemDb = DaoFactory.createItemDAO();
        List<Item> items = itemDb.getAll();
        //run the 1st line or register any user with the email "admin@gmail.com" if there is no admin@gmail.com on ur device
        //Member ownerTest = AuthService.registerNewUser("admin", "123", "admin@gmail.com", "0988172919", "000000");
        //run the 2nd line if there is already admin@gmail.com on ur device
        Member ownerTest = (Member) userDb.getByEmail("vuongthuyhang1102@gmail.com");

        for (Item item : items) {
            AuctionManager.getInstance().createAuction(ownerTest, item, LocalDateTime.now(), null);
        }

        SceneManager.setStage(stage);
        SceneManager.startApp();
    }

    public static void main(String[] args) {
        launch(args);
    }
}