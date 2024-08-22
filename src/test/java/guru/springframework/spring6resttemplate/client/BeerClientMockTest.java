package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.model.BeerStyle.WHEAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * @author john
 * @since 17/08/2024
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class BeerClientMockTest {

    static final String ROOT_URL = "http://localhost:8080";


    BeerClient beerClient;

    @Mock
    RestTemplate restTemplate;

    @Mock
    BeerRestTemplateBuilder restTemplateBuilder;


    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
    }

    @Test
    void listBeers() {
        BeerDTOPage stella = new BeerDTOPage(1, 10, 12, 120,
                List.of(BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                        .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build()));
        Mockito.when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.ofNullable(stella));
        BeerDTOPage beerDTOPage = beerClient.listBeers(Map.of("pageNumber", "1", "pageSize", "4"));
        assertThat(beerDTOPage).isNotNull();
        assertThat(beerDTOPage.getPageNumber()).isEqualTo(1);
        assertThat(beerDTOPage.getTotalElements()).isEqualTo(120);
        assertThat(beerDTOPage.getBeers()).hasSize(1);
        log.info(String.valueOf(beerDTOPage));
    }

    @Test
    void listBeersWithBeerName() {
        BeerDTOPage stella = new BeerDTOPage(1, 10, 1, 1,
                List.of(BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                        .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build()));
        Mockito.when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.ofNullable(stella));
        BeerDTOPage beerDTOPage = beerClient.listBeers(Map.of("beerName", "Stella"));
        assertThat(beerDTOPage).isNotNull();
        assertThat(beerDTOPage.getPageNumber()).isEqualTo(1);
        assertThat(beerDTOPage.getTotalElements()).isEqualTo(1);
        assertThat(beerDTOPage.getBeers()).hasSize(1);
        assertThat(beerDTOPage.getBeers().getFirst().getBeerName()).isEqualTo("Stella");
        log.info(String.valueOf(beerDTOPage));
    }

    @Test
    void getBeerWithId() {
        BeerDTO stella = BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        Mockito.when(restTemplate.getForObject(anyString(), any(), anyString())).thenReturn(stella);
        BeerDTO beer = beerClient.getBeerById(UUID.randomUUID().toString());
        assertThat(beer).isNotNull();
        assertThat(beer.getId()).isNotNull();
        assertThat(beer.getBeerName()).isEqualTo("Stella");
        assertThat(beer.getBeerStyle()).isEqualTo(WHEAT);
        assertThat(beer.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(12.12));
        log.info("Beer: {}", beer);

    }

    @Test
    void createBeer() {
        BeerDTO newBeer = BeerDTO.builder().id(UUID.randomUUID()).beerName("New Beer").beerStyle(WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        Mockito.when(restTemplate.postForObject(anyString(), any(BeerDTO.class), any())).thenReturn(newBeer);
        BeerDTO beer = beerClient.createBeer(newBeer);
        assertThat(beer).isNotNull();
        assertThat(beer.getId()).isNotNull();
        assertThat(beer.getBeerName()).isEqualTo("New Beer");
        log.info("Created Beer: {}", beer);
    }

    @Test
    void updateBeer() {
        BeerDTO beer = BeerDTO.builder().id(UUID.randomUUID()).beerName("Updated Beer").beerStyle(WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        Mockito.when(restTemplate.getForObject(anyString(), any(), anyString())).thenReturn(beer);
        BeerDTO beerDTO = beerClient.updateBeer(beer.getId().toString(), beer);
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getId()).isEqualTo(beer.getId());
        assertThat(beerDTO.getBeerName()).isEqualTo("Updated Beer");
        assertThat(beerDTO.getBeerStyle()).isEqualTo(WHEAT);
        assertThat(beerDTO.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(12.12));
        log.info("Updated Beer: {}", beerDTO);
    }

    @Test
    void deleteBeer() {
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> beerIdCaptor = ArgumentCaptor.forClass(String.class);
        String beerId = UUID.randomUUID().toString();
        beerClient.deleteBeer(beerId);
        Mockito.verify(restTemplate, times(1)).delete(urlCaptor.capture(), beerIdCaptor.capture());
        assertThat(urlCaptor.getValue()).endsWith("beer/{beerId}");
        assertThat(beerIdCaptor.getValue()).isEqualTo(beerId);
        log.info("Deleted Beer with ID: {}", beerId);
    }

    @Test
    void deleteBeerNotFound() {
        String beerId = UUID.randomUUID().toString();
        doThrow(HttpClientErrorException.NotFound.class).when(restTemplate).delete(anyString(), anyString());

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () ->
            beerClient.deleteBeer(beerId)
        );
        log.info("Deleted Beer with ID: {} (not found)", beerId);
    }

}
