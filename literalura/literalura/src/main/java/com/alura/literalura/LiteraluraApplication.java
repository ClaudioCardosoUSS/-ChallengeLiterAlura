package com.alura.literalura;

import com.alura.literalura.controller.MenuController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LiteraluraApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(MenuController menuController, ConfigurableApplicationContext context) {
		return args -> {
			try {
				System.out.println("═══════════════════════════════════");
				System.out.println("    Bem-vindo ao LiterAlura!");
				System.out.println("═══════════════════════════════════");
				menuController.showMenu();
			} catch (Exception e) {
				System.out.println("Erro na execução do programa: " + e.getMessage());
				e.printStackTrace();
				context.close();
				System.exit(1);
			}
		};
	}
}