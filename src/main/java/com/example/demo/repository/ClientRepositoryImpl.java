package com.example.demo.repository;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Client;
import com.zaxxer.hikari.HikariDataSource;

@Repository
public class ClientRepositoryImpl implements ClientRepository{
	
	private int batchSize = 0;
	
	private final HikariDataSource dataSource;

	public ClientRepositoryImpl(HikariDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void save() {
		String sql = "INSERT INTO test (name, surname, patronymic, gender) VALUES (?, ?, ?, ?)";
		System.out.println("Insert started");
		try{
			Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement(sql);
			for(int i = 0;i<1000000;i++) {
				Client client = new Client();
				client.setName(generateRandomString(10));
				client.setSurname(generateRandomString(10));
				client.setPatronymic(generateRandomString(10));
				client.setGender(generateRandomString(10));
				ps.setString(1, client.getName());
				ps.setString(2,  client.getSurname());
				ps.setString(3,  client.getPatronymic());
				ps.setString(4,  client.getGender());
				ps.addBatch();
				batchSize++;
				if(batchSize>=50_000) {
					ps.executeBatch();
					connection.commit();
					System.out.println("Inserted 50000 clients");
					batchSize=0;
				}
			}
			System.out.println("Insertion complete");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }
	
}
