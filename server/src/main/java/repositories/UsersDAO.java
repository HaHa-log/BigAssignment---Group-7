package repositories;

import models.Admin;
import models.Member;
import java.util.List;

public interface UsersDAO extends DAO<Member> {

    Member getByEmail(String email);

    List<Member> getAllMember();

    List<Admin> getAllAdmin();
}