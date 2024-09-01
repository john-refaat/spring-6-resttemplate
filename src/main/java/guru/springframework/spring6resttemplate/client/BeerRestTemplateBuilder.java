package guru.springframework.spring6resttemplate.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * @author john
 * @since 16/08/2024
 */
@Component
public class BeerRestTemplateBuilder  {

    @Value("${beer.webservice.base.url}")
    private String baseURL;

    @Value("${rest.template.username}")
    private String username;

    @Value("${rest.template.password}")
    private String password;

    private final RestTemplateBuilder restTemplateBuilder;

    public BeerRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public RestTemplate build() {
        return restTemplateBuilder.basicAuthentication(username, password)
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseURL)).build();
    }

}
