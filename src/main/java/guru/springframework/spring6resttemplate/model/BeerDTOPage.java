package guru.springframework.spring6resttemplate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author john
 * @since 14/08/2024
 */
@Getter
@ToString
@EqualsAndHashCode
public class BeerDTOPage {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int totalElements;
    private List<BeerDTO> beers;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerDTOPage(int pageNumber, int pageSize, int totalPages, int totalElements,
                       @JsonProperty("content") List<BeerDTO> beers) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.beers = beers;
    }
}
