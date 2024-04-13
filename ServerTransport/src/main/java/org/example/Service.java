package org.example;

import org.example.repository.CursaRepository;
import org.example.repository.RezervareRepository;
import org.example.repository.UtilizatorRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService
{
    private UtilizatorRepository utilizatorRepository;
    private CursaRepository cursaRepository;
    private RezervareRepository rezervareRepository;

    private Map<String, IObserver> loggedClients;

    public Service(UtilizatorRepository utilizatorRepository, CursaRepository cursaRepository, RezervareRepository rezervareRepository)
    {
        this.utilizatorRepository = utilizatorRepository;
        this.cursaRepository = cursaRepository;
        this.rezervareRepository = rezervareRepository;

        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean login(String username, String password, IObserver client)
    {
        for (User user : this.utilizatorRepository.getAll())
        {
            if (Objects.equals(user.getUsername(), username) && Objects.equals(user.getPassword(), password))
            {
                loggedClients.put(user.getId().toString(), client);
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username)
    {
        return this.utilizatorRepository.getByUsername(username);
    }

    public List<Cursa> getAllCurse()
    {
        return this.cursaRepository.getAll();
    }

    public List<LocCursa> genereaza_lista_locuri(Long id_cursa)
    {
        List<LocCursa> locuri = new ArrayList<>();
        Integer loc = 1;

        for (Rezervare rezervare : this.rezervareRepository.getAll())
        {
            if (Objects.equals(rezervare.getId_cursa(), id_cursa))
            {
                for (int i = 0; i < rezervare.getNr_locuri(); ++i)
                {
                    locuri.add(new LocCursa(loc, rezervare.getNume_client()));
                    loc++;
                }
            }
        }

        for (int i = loc; i <= 18; ++i)
        {
            locuri.add(new LocCursa(i, "-"));
        }

        return locuri;
    }

    public Cursa cauta_cursa(String destinatie, Timestamp data)
    {
        for (Cursa cursa : this.cursaRepository.getAll())
            if (Objects.equals(cursa.getDestinatie(), destinatie) && cursa.getPlecare().equals(data))
                return cursa;

        return null;
    }

    public Integer getNrLocuriLibereCursa(Cursa cursa)
    {
        Integer count = 0;

        for (Rezervare rezervare : this.rezervareRepository.getAll())
        {
            if (Objects.equals(rezervare.getId_cursa(), cursa.getId()))
            {
                count += rezervare.getNr_locuri();
            }
        }

        return 18 - count;
    }

    public void rezerva(String nume, Integer nr, Long id_cursa) throws Exception
    {
        Rezervare rezervare = new Rezervare(nume, nr, id_cursa);
        this.rezervareRepository.save(rezervare);

        Cursa cursa = this.getCursaById(id_cursa);
        Integer nr_locuri = cursa.getNr_locuri();
        cursa.setNr_locuri(nr_locuri - nr);
        Boolean updated = this.cursaRepository.update(id_cursa, cursa);

        if (updated)
        {
            this.notifyRezervare(rezervare);
        }
    }

    private Cursa getCursaById(Long id)
    {
        return this.cursaRepository.getById(id);
    }

    private final int defaultThreadsNo = 5;
    private void notifyRezervare(Rezervare rezervare) throws Exception
    {
        Iterable<User> users = this.utilizatorRepository.getAll();

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(User us : users)
        {
            IObserver chatClient = loggedClients.get(us.getId().toString());
            if (chatClient != null)
            {
                executor.execute(() ->
                {
                    try
                    {
                        chatClient.rezervare(rezervare);
                    } catch (Exception e)
                    {
                        System.err.println("Error notifying friend " + e);
                    }
                });
            }
        }

        executor.shutdown();
    }
}
