package smart.management

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class ManagementServerApplication {

    static void main(String[] args) {
        SpringApplication.run this, args
    }

}
