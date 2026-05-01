package Client;

import Branch.*;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;
import model.ItemsDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        UsersDAO userDb = DaoFactory.createUsersDAO();
        ItemsDAO itemDb = DaoFactory.createItemDAO();
        Item testItem = new Item("Three body problem", 30, "A sci fi book");
        itemDb.save(testItem);
        //run the 1st line or register any user with the email "admin@gmail.com" if there is no admin@gmail.com on ur device
        //Member ownerTest = AuthService.registerNewUser("admin", "123", "admin@gmail.com", "0988172919", "000000");
        //run the 2nd line if there is already admin@gmail.com on ur device
        Member ownerTest = (Member) TempDatabase.getUserByEmail("admin@gmail.com");
        AuctionManager.getInstance().createAuction(ownerTest, testItem, LocalDateTime.now(), null);
        SceneManager.setStage(stage);
        SceneManager.startApp();
    }
}