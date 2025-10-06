package com.javatraining.notification_mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // ✅ Enables scheduling support
@SpringBootApplication
public class NotificationMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationMgmtApplication.class, args);
	}

}
