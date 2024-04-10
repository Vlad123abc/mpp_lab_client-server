package org.example.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Cursa;
import org.example.repository.CursaRepository;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CursaDBRepository implements CursaRepository
{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CursaDBRepository(Properties props)
    {
        logger.info("Initializing CursaDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Cursa getById(Long Id)
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try(PreparedStatement preStmt = con.prepareStatement("select * from Curse where id_cursa = ?"))
        {
            preStmt.setLong(1, Id);
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                if (resultSet.next())
                {
                    String destinatie = resultSet.getString("destinatie");
                    Timestamp plecare = resultSet.getTimestamp("plecare");
                    Integer nr_locuri = resultSet.getInt("nr_locuri");
                    Cursa cursa = new Cursa(destinatie, plecare, nr_locuri);
                    cursa.setId(Id);

                    logger.traceExit();
                    return cursa;
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
    public List<Cursa> getAll()
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Cursa> curse = new ArrayList<>();

        try(PreparedStatement preStmt = con.prepareStatement("select * from Curse"))
        {
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                while (resultSet.next())
                {
                    Long id_cursa = resultSet.getLong("id_cursa");
                    String destinatie = resultSet.getString("destinatie");
                    Timestamp plecare = resultSet.getTimestamp("plecare");
                    Integer nr_locuri = resultSet.getInt("nr_locuri");
                    Cursa cursa = new Cursa(destinatie, plecare, nr_locuri);
                    cursa.setId(id_cursa);
                    curse.add(cursa);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return curse;
    }

    @Override
    public Boolean save(Cursa cursa)
    {
        logger.traceEntry("saving tsak {}", cursa);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into Curse(destinatie, plecare, nr_locuri) values(?, ?, ?)"))
        {
            preStmt.setString(1, cursa.getDestinatie());
            preStmt.setTimestamp(2, cursa.getPlecare());
            preStmt.setInt(3, cursa.getNr_locuri());
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
        try(PreparedStatement preStmt = con.prepareStatement("delete from Curse where id_cursa = ?"))
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
    public Boolean update(Long id, Cursa cursa)
    {
        logger.traceEntry("updating tsak {}", cursa);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update Curse set destinatie = ?, plecare = ?, nr_locuri = ? where id_cursa = ?"))
        {
            preStmt.setString(1, cursa.getDestinatie());
            preStmt.setTimestamp(2, cursa.getPlecare());
            preStmt.setInt(3, cursa.getNr_locuri());
            preStmt.setLong(4, id);
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
    public Long getMaxId()
    {
        Long id = 1L;

        for (Cursa cursa : this.getAll())
            if (cursa.getId() > id)
                id = cursa.getId();

        return id;
    }
}
