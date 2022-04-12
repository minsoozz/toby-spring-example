package hello.toby.user.service;

import hello.toby.user.dao.UserDao;
import hello.toby.user.domain.Level;
import hello.toby.user.domain.User;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserService {

  UserDao userDao;

  private DataSource dataSource;

  private PlatformTransactionManager transactionManager;

  private MailSender mailSender;

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setMailSender(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void upgradeLevels() throws Exception {

    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
  }

  protected void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
  }

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private boolean canUpgradeLevel(User user) {
    Level currentLevel = user.getLevel();
    switch (currentLevel) {
      case BASIC:
        return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
      case SILVER:
        return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
      case GOLD:
        return false;
      default:
        throw new IllegalArgumentException("Unknown Level :" + currentLevel);
    }
  }

  public void add(User user) throws SQLException {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }

  static class TestUserService extends UserService {

    private String id;

    public TestUserService(String id) {
      this.id = id;
    }

    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) {
        throw new TestUserServiceException();
      }
    }
  }

  static class TestUserServiceException extends RuntimeException {

  }
}