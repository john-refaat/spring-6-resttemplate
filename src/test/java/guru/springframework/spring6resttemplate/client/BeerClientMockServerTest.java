package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPage;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * @author john
 * @since 18/08/2024
 */
@Slf4j
@RestClientTest
public class BeerClientMockServerTest {

    public static final String ROOT_URL = "http://localhost:8080/api/v1/";
    public static final String BEER_URL = ROOT_URL + "beer";

    BeerClient beerClient;

    @Mock
    BeerRestTemplateBuilder beerRestTemplateBuilder;

    MockRestServiceServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(ROOT_URL));
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("restadmin", "password"));
        Mockito.when(beerRestTemplateBuilder.build()).thenReturn(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        beerClient = new BeerClientImpl(beerRestTemplateBuilder);
    }


    @Test
    void listBeers() throws JsonProcessingException {
        // Mock response
        //Given
        BeerDTOPage stella = new BeerDTOPage(1, 10, 12, 120,
                List.of(BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                        .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build()));

        // When
        mockServer.expect(ExpectedCount.once(),
                        requestTo(BEER_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Basic cmVzdGFkbWluOnBhc3N3b3Jk"))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
                        body(objectMapper.writeValueAsString(stella)));
        BeerDTOPage beerDTOPage = beerClient.listBeers();

        //Then
        mockServer.verify();
        assertThat(beerDTOPage).isNotNull();
        assertThat(beerDTOPage.getPageNumber()).isEqualTo(1);
        assertThat(beerDTOPage.getTotalElements()).isEqualTo(120);
        assertThat(beerDTOPage.getBeers()).hasSize(1);
        assertThat(beerDTOPage.getBeers().getFirst()).isNotNull();
        assertThat(beerDTOPage.getBeers().getFirst().getBeerName()).isEqualTo("Stella");
        log.info(String.valueOf(beerDTOPage));
    }

    @Test
    void listBeersWithName() throws JsonProcessingException {
        //Given
        BeerDTOPage stella = new BeerDTOPage(1, 10, 1, 1,
                List.of(BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                        .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build()));

        URI uri = UriComponentsBuilder.fromHttpUrl(BEER_URL).queryParam("beerName", "Stella").build().toUri();

        //When
        mockServer.expect(requestTo(uri)).andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Basic cmVzdGFkbWluOnBhc3N3b3Jk"))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(stella)));
        BeerDTOPage beerDTOPage = beerClient.listBeers(Map.of("beerName", "Stella"));

        //Then
        mockServer.verify();
        assertThat(beerDTOPage).isNotNull();
        assertThat(beerDTOPage.getPageNumber()).isEqualTo(1);
        assertThat(beerDTOPage.getTotalElements()).isEqualTo(1);
        assertThat(beerDTOPage.getBeers()).hasSize(1);
        assertThat(beerDTOPage.getBeers().getFirst()).isNotNull();
        assertThat(beerDTOPage.getBeers().getFirst().getBeerName()).isEqualTo("Stella");
        log.info(String.valueOf(beerDTOPage));
    }


    @Test
    void getBeerWithId() throws JsonProcessingException {
        // Mock response
        // Given
        BeerDTO stella = BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).build();
        // When
        mockServer.expect(ExpectedCount.once(),
                        requestTo(BEER_URL + "/" + stella.getId().toString()))
                .andExpect(header("Authorization", "Basic cmVzdGFkbWluOnBhc3N3b3Jk"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
                        body(objectMapper.writeValueAsString(stella)));
        BeerDTO beerDTO = beerClient.getBeerById(stella.getId().toString());

        //Then
        mockServer.verify();
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getId()).isEqualTo(stella.getId());
        assertThat(beerDTO.getBeerName()).isEqualTo("Stella");
        log.info(String.valueOf(beerDTO));
    }

    @Test
    void createBeer() throws JsonProcessingException {
        // Mock response
        // Given
        BeerDTO stella = BeerDTO.builder().beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).build();
        BeerDTO createdBeer = BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();
        // When
        mockServer.expect(ExpectedCount.once(),
                        requestTo(BEER_URL))
                .andExpect(method(HttpMethod.POST)).andExpect(content().json(objectMapper.writeValueAsString(stella)))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).
                        body(objectMapper.writeValueAsString(createdBeer)));
        BeerDTO beerDTO = beerClient.createBeer(stella);

        //Then
        mockServer.verify();
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getId()).isNotNull();
        assertThat(beerDTO.getBeerName()).isNotNull();
        assertThat(beerDTO.getBeerStyle()).isNotNull();
        assertThat(beerDTO.getBeerName()).isEqualTo(createdBeer.getBeerName());
        assertThat(beerDTO.getBeerStyle()).isEqualTo(createdBeer.getBeerStyle());
        assertThat(beerDTO.getPrice()).isEqualTo(createdBeer.getPrice());
        assertThat(beerDTO.getUpc()).isEqualTo(createdBeer.getUpc());
        log.info(String.valueOf(beerDTO));
    }

    @Test
    void updateBeer() throws JsonProcessingException {
        // Mock response
        // Given
        BeerDTO stella = BeerDTO.builder().id(UUID.randomUUID()).beerName("Stella").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(12.12)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();
        BeerDTO updatedBeer = BeerDTO.builder().id(stella.getId()).beerName("Stella updated").beerStyle(BeerStyle.WHEAT).price(BigDecimal.valueOf(14.00)).upc("98765432")
                .version(0).quantityOnHand(200).createdDate(stella.getCreatedDate()).updateDate(stella.getUpdateDate()).build();
        // When
        mockServer.expect(ExpectedCount.once(),
                        requestToUriTemplate(BEER_URL + "/{beerId}", stella.getId().toString()))
                .andExpect(method(HttpMethod.PUT)).andExpect(content().json(objectMapper.writeValueAsString(updatedBeer)))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
                        body(objectMapper.writeValueAsString(updatedBeer)));
        mockServer.expect(ExpectedCount.once(),
                requestToUriTemplate(BEER_URL + "/{beerId}", stella.getId().toString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
                        body(objectMapper.writeValueAsString(updatedBeer)));

        BeerDTO beerDTO = beerClient.updateBeer(stella.getId().toString(), updatedBeer);
        //Then
        mockServer.verify();
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getId()).isEqualTo(updatedBeer.getId());
        assertThat(beerDTO.getBeerName()).isEqualTo(updatedBeer.getBeerName());
        assertThat(beerDTO.getBeerStyle()).isEqualTo(updatedBeer.getBeerStyle());
        assertThat(beerDTO.getPrice()).isEqualTo(updatedBeer.getPrice());
        assertThat(beerDTO.getUpc()).isEqualTo(updatedBeer.getUpc());
        log.info(String.valueOf(beerDTO));
    }

    @Test
    void deleteBeer() {
        // Given
        String beerId =  UUID.randomUUID().toString();
        log.info("Beer Id: {}", beerId);
        // When
        mockServer.expect(ExpectedCount.once(),
                        requestToUriTemplate(BEER_URL + "/{beerId}", beerId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        beerClient.deleteBeer(beerId);

        // Then
        mockServer.verify();
    }

    @Test
    void deleteBeerNotFound() {
        // Given
        String beerId =  UUID.randomUUID().toString();
        // When
        mockServer.expect(ExpectedCount.once(),
                        requestToUriTemplate(BEER_URL + "/{beerId}", beerId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withResourceNotFound());
        Assertions.assertThrows(HttpClientErrorException.class,
                () ->beerClient.deleteBeer(beerId));

        // Then
        mockServer.verify();

    }
}
