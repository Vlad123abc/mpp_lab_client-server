package services.rest;

import org.example.Cursa;
import org.springframework.web.client.RestClientException;

import java.sql.Timestamp;

public class NewRestClient {
    private final static NewCursaClient cursaClient = new NewCursaClient();

    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(0L);
        Cursa cursa = new Cursa("testDest", timestamp, 10);

        try{
            System.out.println("Adding a new cursa " + cursa);
            show(()-> {
                try
                {
                    System.out.println(cursaClient.create(cursa));
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            });

            System.out.println("\nPrinting all curse ...");
            show(()->{
                Cursa[] res = new Cursa[0];
                try
                {
                    res = cursaClient.getAll();
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                for(Cursa c : res){
                    System.out.println(c.getId()+": " + c.getDestinatie());
                }
            });
        }catch(RestClientException ex){
            System.out.println("Exception ... " + ex.getMessage());
        }

        System.out.println("\nInfo for cursa with id = 1");
        show(()-> {
            try
            {
                System.out.println(cursaClient.getById("1"));
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });

        System.out.println("\nDeleting cursa with id = " + cursa.getId()); //id e null?
        show(()-> {
            try
            {
                cursaClient.delete(String.valueOf(cursa.getId()));
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception"+ e);
        }
    }
}
