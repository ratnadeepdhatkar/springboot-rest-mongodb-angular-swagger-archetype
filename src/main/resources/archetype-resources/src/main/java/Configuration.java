#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * Configuration class
 *
 * Created by talbot on 22.02.15.
 */
@org.springframework.context.annotation.Configuration
@EnableSwagger
@PropertySource("classpath:application.properties")
public class Configuration {

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public MongoFactoryBean mongoFactoryBean(@Value("${symbol_dollar}{app.db.host}") final String hostName) {
        final MongoFactoryBean mongoFactoryBean = new MongoFactoryBean();
        mongoFactoryBean.setHost(hostName);
        return mongoFactoryBean;
    }

    @Bean
    public MongoDbFactory mongoDbFactory(final String databaseName) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(), databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate(@Value("${symbol_dollar}{app.db.name}") final String databaseName) throws Exception {
        return new MongoTemplate(this.mongoDbFactory(databaseName));
    }

    @Bean
    public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).
                apiInfo(this.getApiInfo()).
                includePatterns("/api/.*").
                swaggerGroup("v1")
                ;
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "XML document uploader API",
                "Provides retrieving list of uploaded XML documents, retrieving XML document file by its ID, creating and deleting XML documents",
                "",
                "dmitry@tretyakov.im",
                "",
                ""
        );
    }
}
