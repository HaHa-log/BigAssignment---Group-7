package repositories;

import models.Admin;
import models.User;
import java.util.List;

public interface UsersDAO extends DAO<User> {

    User getByEmail(String email);

    List<User> getAllMember();

    List<Admin> getAllAdmin();
}