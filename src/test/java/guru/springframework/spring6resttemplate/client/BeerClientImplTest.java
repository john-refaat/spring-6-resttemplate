package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPage;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 13/08/2024
 */
@SpringBootTest
class BeerClientImplTest {

    public static final BeerDTO NEW_BEER = BeerDTO.builder()
            .beerName("Stella")
            .beerStyle(BeerStyle.ALE)
            .upc("1234567890123")
            .price(BigDecimal.valueOf(9.99))
            .build();

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeers() {
        beerClient.listBeers();
    }

    @Test
    void listBeersWithParameterBeerName() {
        BeerDTOPage beerDTOPage = beerClient.listBeers(Map.of("beerName", "ALE"));
        assertNotNull(beerDTOPage);
        assertEquals(636, beerDTOPage.getTotalElements());
        assertEquals(10, beerDTOPage.getPageSize());
        assertEquals(64, beerDTOPage.getTotalPages());
        assertTrue(beerDTOPage.getBeers().stream().anyMatch(
                beerDTO -> beerDTO.getBeerName().toUpperCase().contains("ALE")));
        beerDTOPage.getBeers().forEach(System.out::println);
    }

    @Test
    void listBeersWithParameters() {
        BeerDTOPage beerDTOPage = beerClient.listBeers(Map.of("beerName", "ALE", "beerStyle", "IPA", "pageNumber", "2", "pageSize", "5"));
        assertNotNull(beerDTOPage);
        assertEquals(5, beerDTOPage.getBeers().size());
        // In Spring Data JPA page numbers are 0 indexed.
        assertEquals(1, beerDTOPage.getPageNumber());
        assertEquals(5, beerDTOPage.getPageSize());
        assertTrue(beerDTOPage.getBeers().stream().anyMatch(
                beerDTO -> beerDTO.getBeerName().toUpperCase().contains("ALE")
                        && beerDTO.getBeerStyle().name().equals("IPA")));
        beerDTOPage.getBeers().forEach(System.out::println);
    }

    @Test
    void getBeerById() {
        BeerDTO first = beerClient.listBeers().getBeers().getFirst();
        BeerDTO beer = beerClient.getBeerById(first.getId().toString());
        assertNotNull(beer);
        assertEquals(first.getId(), beer.getId());
        assertEquals(first.getBeerName(), beer.getBeerName());
        assertEquals(first.getBeerStyle(), beer.getBeerStyle());
        assertEquals(first.getUpc(), beer.getUpc());
        System.out.println(beer);
    }

    @Test
    void createBeer() {
        BeerDTO beer = beerClient.createBeer(NEW_BEER);
        assertNotNull(beer);
        assertEquals(NEW_BEER.getBeerName(), beer.getBeerName());
            assertEquals(NEW_BEER.getBeerStyle(), beer.getBeerStyle());
        System.out.println(beer);
    }

    @Test
    void updateBeer() {
        BeerDTO beer = beerClient.createBeer(NEW_BEER);
        System.out.println(beer);
        String newName = beer.getBeerName() + "  updated";
        beer.setBeerName(newName);
        beer.setPrice(beer.getPrice().add(BigDecimal.ONE));
        BeerDTO updatedBeer = beerClient.updateBeer(beer.getId().toString(), beer);
        assertNotNull(updatedBeer);
        assertEquals(beer.getId(), updatedBeer.getId());
        assertEquals(newName, updatedBeer.getBeerName());
        assertEquals(beer.getBeerStyle(), updatedBeer.getBeerStyle());
        assertEquals(beer.getPrice(), updatedBeer.getPrice());
        System.out.println(updatedBeer);
    }

    @Test
    void deleteBeer() {
        BeerDTO createdBeer = beerClient.createBeer(NEW_BEER);
        System.out.println(createdBeer);
        beerClient.deleteBeer(createdBeer.getId().toString());
        assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(createdBeer.getId().toString()));
        System.out.println("Beer deleted: " + createdBeer.getId());
    }
}