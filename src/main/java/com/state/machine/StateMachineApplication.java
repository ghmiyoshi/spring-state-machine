package com.state.machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan
public class StateMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(StateMachineApplication.class, args);
	}

}
