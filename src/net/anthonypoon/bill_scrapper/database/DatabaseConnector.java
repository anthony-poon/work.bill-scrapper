/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author anthony.poon
 */
public class DatabaseConnector {
    private Properties config = new Properties();
    private InputStream input;
    private Connection connection = null;
    private String url;
    
    public DatabaseConnector() throws IOException, SQLException {
        Path distConfigPath = Paths.get("resource/config/config.dist.properties");
        Path configPath = Paths.get("resource/config/config.properties");
        if (!Files.exists(configPath)) {
            Files.copy(distConfigPath, configPath);
        }
        input = Files.newInputStream(configPath);
        config.load(input);
        url = "jdbc:mysql://"+config.getProperty("host")+":"+config.getProperty("port")+"/"+config.getProperty("database")+"?user="+config.getProperty("username")+"&password="+config.getProperty("password");
        connection = DriverManager.getConnection(url);
    }
    
    public Connection getConnection() {
        return connection;
    }
}
