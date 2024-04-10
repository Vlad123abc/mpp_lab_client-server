package org.example.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.User;
import org.example.repository.UtilizatorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtilizatorDBRepository implements UtilizatorRepository
{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public UtilizatorDBRepository(Properties props)
    {
        logger.info("Initializing UtilizatorDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public User getById(Long Id)
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try(PreparedStatement preStmt = con.prepareStatement("select * from User where user_id = ?"))
        {
            preStmt.setLong(1, Id);
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                if (resultSet.next())
                {
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    User user = new User(username, password);
                    user.setId(Id);

                    logger.traceExit();
                    return user;
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        return null;
    }

    @Override
    public List<User> getAll()
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<User> users = new ArrayList<>();

        try(PreparedStatement preStmt = con.prepareStatement("select * from User"))
        {
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                while (resultSet.next())
                {
                    int id_user = resultSet.getInt("user_id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    User user = new User(username, password);
                    user.setId((long) id_user);
                    users.add(user);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return users;
    }

    @Override
    public Boolean save(User user)
    {
        logger.traceEntry("saving tsak {}", user);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into User(username, password) values(?, ?)"))
        {
            preStmt.setString(1, user.getUsername());
            preStmt.setString(2, user.getPassword());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            logger.traceExit();
            return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return false;
    }

    @Override
    public Boolean delete(Long id)
    {
        logger.traceEntry("delete tsak {}", id);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("delete from User where user_id = ?"))
        {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
            return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return false;
    }

    @Override
    public Boolean update(Long id, User user)
    {
        logger.traceEntry("updating tsak {}", user);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update User set username = ?, password = ? where user_id = ?"))
        {
            preStmt.setString(1, user.getUsername());
            preStmt.setString(2, user.getPassword());
            preStmt.setLong(3, id);
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
            return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return false;
    }

    @Override
    public Long getMaxId() // for tests
    {
        Long id = 1L;
        for (User user : this.getAll())
            if (user.getId() > id)
                id = user.getId();
        return id;
    }

    @Override
    public User getByUsername(String username)
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try(PreparedStatement preStmt = con.prepareStatement("select * from User where username = ?"))
        {
            preStmt.setString(1, username);
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                if (resultSet.next())
                {
                    Long id = resultSet.getLong("user_id");
                    String password = resultSet.getString("password");
                    User user = new User(username, password);
                    user.setId(id);

                    logger.traceExit();
                    return user;
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        return null;
    }
}
