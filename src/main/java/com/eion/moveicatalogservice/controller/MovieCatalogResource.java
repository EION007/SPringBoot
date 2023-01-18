package com.eion.moveicatalogservice.controller;

import com.eion.moveicatalogservice.entity.CatalogItems;
import com.eion.moveicatalogservice.entity.Movie;
import com.eion.moveicatalogservice.entity.Rating;
import com.eion.moveicatalogservice.entity.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog/")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public MovieCatalogResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItems> getCatalog(@PathVariable String userId) {

        UserRating ratings = restTemplate.getForObject
                ("http://rating-data-service/ratingdata/users/"+ userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            // for each move id call info service and get details
            Movie movie = restTemplate.getForObject("http://moveie-info-service/movies/" + rating.getMovieId(), Movie.class);

//                    Movie movie = webClientBuilder.build()
//                            .get()
//                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                            .retrieve()
//                            .bodyToMono(Movie.class)
//                            .block();

                    //put them all together
                    return new CatalogItems(movie.getName(), "Test", rating.getRating());
                })
                .collect(Collectors.toList());




    }
}
