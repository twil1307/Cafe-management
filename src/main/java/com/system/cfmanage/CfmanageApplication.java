package com.system.cfmanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CfmanageApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfmanageApplication.class, args);
	}

	@GetMapping("/api/test")
	public ResponseEntity<String> hello() {
		return new ResponseEntity<String>("Hello world", HttpStatus.OK);
	}
}
