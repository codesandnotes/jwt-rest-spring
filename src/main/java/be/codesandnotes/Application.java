package be.codesandnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

// SpringBootApplication conveniently declares @Configuration, @EnableAutoConfiguration and @ComponentScan.
@SpringBootApplication(exclude = CassandraDataAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
