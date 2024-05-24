package services.rest;

import org.example.Cursa;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class NewCursaClient {
    RestClient restClient = RestClient.builder().
            requestInterceptor(new CustomRestClientInterceptor()).
            build();

    public static final String URL = "http://localhost:8080/transport/curse";

    private <T> T execute(Callable<T> callable) throws Exception {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new Exception(e);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public Cursa[] getAll() throws Exception {
        return execute(() -> restClient.get().uri(URL).retrieve().body(Cursa[].class));
    }

    public Cursa getById(String id) throws Exception {
        return execute(() -> restClient.get(). uri(String.format("%s/%s", URL, id)).retrieve().body( Cursa.class));
    }

    public Cursa create(Cursa cursa) throws Exception {
        return execute(() -> restClient.post().uri(URL).contentType(APPLICATION_JSON).body(cursa).retrieve().body(Cursa.class));
    }

    public Cursa update(Cursa cursa) throws Exception {
        return execute(() -> restClient.put().uri(URL).contentType(APPLICATION_JSON).body(cursa).retrieve().body(Cursa.class));
    }

    public void delete(String id) throws Exception {
        execute(() -> restClient.delete().uri(String.format("%s/%s", URL, id)).retrieve().toBodilessEntity());
    }

    public class CustomRestClientInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending a " + request.getMethod() + " request to "+request.getURI() + " and body ["+new String(body) + "]");
            ClientHttpResponse response=null;
            try {
                response = execution.execute(request, body);
                System.out.println("Got response code " + response.getStatusCode());
            }catch(IOException ex){
                System.err.println("Eroare executie " + ex);
            }
            return response;
        }
    }
}
