package services.rest;

import org.example.jdbc.CursaDBRepository;
import org.example.repository.CursaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class RepositoryConfig {
    @Bean
    public CursaRepository cursaRepository()
    {
        Properties properties = new Properties();
        try
        {
            properties.load(RepositoryConfig.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set");
            properties.list(System.out);
        }
        catch (IOException e)
        {
            System.err.println("Cannot find server.properties! " + e);
        }
        return new CursaDBRepository(properties);
    }
}
