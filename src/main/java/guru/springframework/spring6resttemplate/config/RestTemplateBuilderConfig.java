package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * @author john
 * @since 16/08/2024
 */
//@Configuration
public class RestTemplateBuilderConfig {
    @Value("${beer.webservice.base.url}")
    private String baseURL;

  /*  @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();//configurer.configure(new RestTemplateBuilder());
        return restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(baseURL));
    }*/
}
