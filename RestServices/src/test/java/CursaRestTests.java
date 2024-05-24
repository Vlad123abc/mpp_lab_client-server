import org.junit.jupiter.api.Test;
import services.rest.NewCursaClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CursaRestTests {
    @Test
    public void test() throws Exception {
        NewCursaClient client = new NewCursaClient();

        Long size = (long) client.getAll().length;


        assertEquals("cluj", client.getById("1").getDestinatie());
    }
}
