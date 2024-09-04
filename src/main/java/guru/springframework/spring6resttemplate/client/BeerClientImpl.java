package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author john
 * @since 13/08/2024
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;


    @Override
    public BeerDTOPage listBeers() {
        return listBeers(new HashMap<>());
    }

    @Override
    public BeerDTOPage listBeers(Map<String, String> parameters) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(URI.create("beer"));
        if (parameters.containsKey("beerName"))
            uriComponentsBuilder.queryParam("beerName", parameters.get("beerName"));
        if (parameters.containsKey("beerStyle"))
            uriComponentsBuilder.queryParam("beerStyle", parameters.get("beerStyle"));
        if (parameters.containsKey("pageNumber"))
            uriComponentsBuilder.queryParam("pageNumber", parameters.get("pageNumber"));
        if (parameters.containsKey("pageSize"))
            uriComponentsBuilder.queryParam("pageSize", parameters.get("pageSize"));


        log.info("GET request to: {}", uriComponentsBuilder.toUriString());

        ResponseEntity<BeerDTOPage> pageResponseEntity = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPage.class);
        log.info("Total Pages: {}", pageResponseEntity.getBody().getTotalPages());
        log.info("Total Elements: {}", pageResponseEntity.getBody().getTotalElements());

        return pageResponseEntity.getBody();
    }

    @Override
    public BeerDTO getBeerById(String beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        log.info("GET request to: {}", "beer/" + beerId);
        return restTemplate.getForObject("beer/{beerId}", BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newBeer) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        log.info("POST request to: {}", "beer");
        return restTemplate.postForObject("beer", newBeer, BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(String beerId, BeerDTO beer) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        log.info("PUT request to: {}", "beer/" + beerId);
        restTemplate.put("beer/{beerId}", beer, beerId);
        return getBeerById(beerId);
    }

    @Override
    public void deleteBeer(String beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        log.info("DELETE request to: {}", "beer/" + beerId);
        restTemplate.delete("beer/{beerId}", beerId);
    }


}
