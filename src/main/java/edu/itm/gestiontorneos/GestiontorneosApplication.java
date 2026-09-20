package edu.itm.gestiontorneos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })

public class GestiontorneosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestiontorneosApplication.class, args);
	}

}
