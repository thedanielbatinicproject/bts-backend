package online.beneaththestars.btsbackend;

import ch.qos.logback.core.net.SyslogOutputStream;
import online.beneaththestars.btsbackend.repo.PlayerRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import online.beneaththestars.btsbackend.models.Player;
import org.springframework.data.repository.support.Repositories;

@SpringBootApplication
public class BtsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BtsBackendApplication.class, args);
    }
}