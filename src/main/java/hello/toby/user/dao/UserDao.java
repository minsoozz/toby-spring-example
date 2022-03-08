package hello.toby.user.dao;

import hello.toby.user.domain.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

  public void add(User user) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    Connection c = DriverManager.getConnection(
        "jdbc:mysql://localhost/springbook", "root", "1234");

    PreparedStatement ps = c.prepareStatement(
        "insert into users(id,name,password) values(?,?,?)");
    ps.setString(1, user.getId());
    ps.setString(2, user.getName());
    ps.setString(3, user.getPassword());

    ps.executeUpdate();

    ps.close();
    c.close();
  }

  public User get(String id) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    Connection c = DriverManager.getConnection(
        "jdbc:mysql://localhost/springbook", "root", "1234");

    PreparedStatement ps = c.prepareStatement(
        "select * from users where id = ?");

    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();
    rs.next();
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));

    rs.close();
    ps.close();
    c.close();

    return user;
  }

  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    UserDao dao = new UserDao();

    User user = new User();
    user.setId("minsoo");
    user.setName("김민수");
    user.setPassword("hello");

    dao.add(user);

    System.out.println("user.getId() =" + user.getId());

    User user2 = dao.get(user.getId());

    System.out.println("user2.getName() = " + user2.getName());
    System.out.println("user2.getPassword() = " + user2.getPassword());
    System.out.println("user2.getId() = " + user2.getId());
  }
}