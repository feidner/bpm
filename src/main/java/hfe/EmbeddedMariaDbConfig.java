package hfe;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
class EmbeddedMariaDbConfig {

    @Bean
    MariaDB4jSpringService mariaDB4jSpringService() {
        return new MariaDB4jSpringService();
    }

    @Bean
    @Primary
    DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService,
                          @Value("${app.mariaDB4j.databaseName}") String databaseName,
                          @Value("${spring.datasource.username}") String datasourceUsername,
                          @Value("${spring.datasource.password}") String datasourcePassword,
                          @Value("${spring.datasource.driver-class-name}") String datasourceDriver) throws ManagedProcessException {
        //Create our database with default root user and no password
        mariaDB4jSpringService.getDB().createDB(databaseName);

        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();

        return DataSourceBuilder
                .create()
                .username(datasourceUsername)
                .password(datasourcePassword)
                .url(config.getURL(databaseName))
                .driverClassName(datasourceDriver)
                .build();
    }

    @Bean("hfe")
    DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService,
                          @Value("${spring.datasource.driver-class-name}") String datasourceDriver) throws ManagedProcessException {

        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();

        return DataSourceBuilder
                .create()
                .username("hfe")
                .password("woodo")
                .url(config.getURL("woodo"))
                .driverClassName(datasourceDriver)
                .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("hfe") DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}