package guru.springframework.spring6resttemplate.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * @author john
 * @since 16/08/2024
 */
public class BeerRestTemplateBuilder  {

   /* @Value("${beer.webservice.base.url}")
    private String baseURL;

    @Value("${rest.template.username}")
    private String username;

    @Value("${rest.template.password}")
    private String password;*/

    private final RestTemplateBuilder restTemplateBuilder;
    //private final OAuthClientInterceptor oAuthClientInterceptor;

    public BeerRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public RestTemplate build() {
        return restTemplateBuilder.build();
                //.basicAuthentication(username, password)
              //  .additionalInterceptors(oAuthClientInterceptor)
              //  .uriTemplateHandler(new DefaultUriBuilderFactory(baseURL)).build();
    }

}
