package pl.sda.treasury;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreasuryApplication {


    public static final Long currentSchoolClass = Long.valueOf(1);

    public static void main(String[] args) {
        SpringApplication.run(TreasuryApplication.class, args);


    }

}
