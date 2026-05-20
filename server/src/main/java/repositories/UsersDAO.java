package repositories;

import models.Admin;
import models.Member;
import models.User;

import java.util.List;

public interface UsersDAO extends DAO<User> {

    User getByEmail(String email);

    List<Member> getAllMember();

    List<Admin> getAllAdmin();
}
