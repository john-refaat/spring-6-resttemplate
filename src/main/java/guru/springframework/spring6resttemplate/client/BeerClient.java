package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPage;

import java.util.Map;

/**
 * @author john
 * @since 13/08/2024
 */
public interface BeerClient {
    BeerDTOPage listBeers();
    BeerDTOPage listBeers(Map<String, String> parameters);
    BeerDTO getBeerById(String beerId);
    BeerDTO createBeer(BeerDTO newBeer);
    BeerDTO updateBeer(String beerId, BeerDTO beer);
    void deleteBeer(String beerId);
}
