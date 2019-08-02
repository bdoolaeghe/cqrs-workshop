package fr.soat.cqrs.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ComponentScan("fr.soat.cqrs")
@PropertySource("classpath:database.properties")
@EnableTransactionManagement
public class AppConfig {

    @Autowired
    Environment environment;

    private final String HOST = "dbHost";
    private final String PORT = "dbPort";
    private final String USER = "dbuser";
    private final String DRIVER = "driver";
    private final String PASSWORD = "dbpassword";

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(getDbUrl());
        driverManagerDataSource.setUsername(getDbUser());
        driverManagerDataSource.setPassword(getDbPassword());
        driverManagerDataSource.setDriverClassName(getDbDriver());
        return driverManagerDataSource;
    }

    public String getDbDriver() {
        return environment.getProperty(DRIVER);
    }

    public String getDbPassword() {
        return environment.getProperty(PASSWORD);
    }

    public String getDbUser() {
        return environment.getProperty(USER);
    }

    public String getDbUrl() {
        String url = "jdbc:postgresql://" + getDbHost() + ":" + getDbPort() + "/";
        return  url;
    }

    public String getDbPort() {
        return environment.getProperty(PORT);
    }

    public String getDbHost() {
        return environment.getProperty(HOST);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource datasource) throws SQLException {
        return new DataSourceTransactionManager(datasource);
    }
}
