package bank.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    @Bean
    public DataSource getDataSource() {
        {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
            dataSourceBuilder.url(  "jdbc:mysql://" +
                    System.getenv("MYSQL_DB_HOST") + ":" +
                    System.getenv("MYSQL_DB_PORT") + "/" +
                    System.getenv("MYSQL_DATABASE")
            );
            dataSourceBuilder.username("root");
            dataSourceBuilder.password(System.getenv("MYSQL_ROOT_PASSWORD"));
            return dataSourceBuilder.build();
        }
    }

}

