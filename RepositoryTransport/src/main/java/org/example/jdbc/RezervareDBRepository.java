package org.example.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Rezervare;
import org.example.repository.RezervareRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RezervareDBRepository implements RezervareRepository
{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public RezervareDBRepository(Properties props)
    {
        logger.info("Initializing RezervareDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Rezervare getById(Long Id)
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        try(PreparedStatement preStmt = con.prepareStatement("select * from Rezervari where id_rezervare = ?"))
        {
            preStmt.setLong(1, Id);
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                if (resultSet.next())
                {
                    String client = resultSet.getString("client");
                    Integer nr_locuri = resultSet.getInt("nr_locuri");
                    Long id_cursa = resultSet.getLong("id_cursa");

                    Rezervare rezervare = new Rezervare(client, nr_locuri, id_cursa);
                    rezervare.setId(Id);

                    logger.traceExit();
                    return rezervare;
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
    public List<Rezervare> getAll()
    {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Rezervare> rezervari = new ArrayList<>();

        try(PreparedStatement preStmt = con.prepareStatement("select * from Rezervari"))
        {
            try(ResultSet resultSet = preStmt.executeQuery())
            {
                while (resultSet.next())
                {
                    Long id_rezervare = resultSet.getLong("id_rezervare");
                    String client = resultSet.getString("client");
                    Integer nr_locuri = resultSet.getInt("nr_locuri");
                    Long id_cursa = resultSet.getLong("id_cursa");

                    Rezervare rezervare = new Rezervare(client, nr_locuri, id_cursa);
                    rezervare.setId(id_rezervare);
                    rezervari.add(rezervare);
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit();
        return rezervari;
    }

    @Override
    public Boolean save(Rezervare rezervare)
    {
        logger.traceEntry("saving tsak {}", rezervare);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into Rezervari(client, nr_locuri, id_cursa) values(?, ?, ?)"))
        {
            preStmt.setString(1, rezervare.getNume_client());
            preStmt.setInt(2, rezervare.getNr_locuri());
            preStmt.setLong(3, rezervare.getId_cursa());
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
        try(PreparedStatement preStmt = con.prepareStatement("delete from Rezervari where id_rezervare = ?"))
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
    public Boolean update(Long id, Rezervare rezervare)
    {
        logger.traceEntry("updating tsak {}", rezervare);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update Rezervari set client = ?, nr_locuri = ?, id_cursa = ? where id_rezervare = ?"))
        {
            preStmt.setString(1, rezervare.getNume_client());
            preStmt.setInt(2, rezervare.getNr_locuri());
            preStmt.setLong(3, rezervare.getId_cursa());
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
    public long getMaxId()
    {
        Long id = 1L;

        for (Rezervare rezervare : this.getAll())
            if (rezervare.getId() > id)
                id = rezervare.getId();

        return id;
    }
}
