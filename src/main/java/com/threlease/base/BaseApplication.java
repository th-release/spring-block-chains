package com.threlease.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class BaseApplication {
	public static void main(String[] args) {
		String currentDirectory = System.getProperty("user.dir");

		try {
			String[] data = {"blocks", "wallets", "peers"};

			Path dataDirectory = Paths.get(currentDirectory + File.separator + "data/");
			if (!Files.exists(dataDirectory) && !Files.isDirectory(dataDirectory)) {
				Files.createDirectory(dataDirectory);
			}

			for (String name: data) {
				Path directory = Paths.get(currentDirectory + File.separator + "data/" + name);
				if (!Files.exists(directory) && !Files.isDirectory(directory)) {
					Files.createDirectory(directory);
				}
			}
		} catch (IOException e) {
			System.out.println("Required Directory");
			return;
		}

		SpringApplication.run(BaseApplication.class, args);
	}
}
