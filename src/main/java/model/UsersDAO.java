package model;

import Branch.Admin;
import Branch.Member;
import Branch.User;

import java.sql.SQLException;
import java.util.List;

public interface UsersDAO extends DAO<User> {

    User getByEmail(String email);

    List<Member> getAllMember();

    List<Admin> getAllAdmin();
}
