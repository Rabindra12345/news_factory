package com.pironews.piropironews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication
//IF WE EXCLUDE CONFIG CLASS AND TYR TO USE SECURITY CONFIG WITH ALL SECURITY DEPENCENCY ON POM IT WONT WORK AND WILL THROW
//UNSATISFIED BEAN DEPENDENCY ERROR.
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@SpringBootApplication
public class PiropironewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiropironewsApplication.class, args);
	}

}
