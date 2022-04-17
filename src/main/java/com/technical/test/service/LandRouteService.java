package com.technical.test.service;

import com.technical.test.exception.CountryCodeNotFoundException;
import com.technical.test.model.Country;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LandRouteService {
    public static final String COUNTRIES_URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";
    private final RestTemplate restTemplate;
    private Map<String, Set<String>> bordersByCountry;

    public LandRouteService(RestTemplateBuilder restTemplateBuilder) {

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.setMessageConverters(messageConverters);
    }

    @PostConstruct
    public void initializeBordersByCountryMap() {
        Country[] countries = restTemplate.getForObject(COUNTRIES_URL, Country[].class);

        if (Objects.isNull(countries)) {
            return;
        }
        bordersByCountry = Stream.of(countries)
                .collect(Collectors.toMap(Country::getCca3, Country::getBorders));
    }

    public List<String> getRoute(String origin, String destination) throws CountryCodeNotFoundException {

        if (Objects.isNull(bordersByCountry)) {
            return null;
        }

        validateCountryCode(origin);
        validateCountryCode(destination);

        Set<String> startWithCountries = new HashSet<>();

        startWithCountries.add(origin);

        return findRouteRecursive(new HashSet<>(), startWithCountries, destination);
    }

/**
 * Calculates and returns the route from any country in {@code startWithCountries} to {@code destination}.
 *
 * @param analyzedCountries the set of countries which were already analyzed.
 * @param startWithCountries the set of countries to start with to find a route to the {@code destination}
 * @param destination the destination country for the calculated route
 * @return a list of countries needed to cross in order to reach the {@code destination} country. The returned list includes
 * the destination country, but not the countries in {@code startWithCountries}. If there is no land crossing, a null
 * will be returned.
 */
private LinkedList<String> findRouteRecursive(Set<String> analyzedCountries, Set<String> startWithCountries, String destination) {

    if (CollectionUtils.isEmpty(startWithCountries)) {
        return null;
    }

    if (startWithCountries.contains(destination)) {
        LinkedList<String> route = new LinkedList<>();

        route.add(destination);
        return route;
    }

    Set<String> newBorders = startWithCountries.stream()
            .flatMap(country -> bordersByCountry.get(country).stream())
            .filter(country -> !analyzedCountries.contains(country))
            .collect(Collectors.toSet());

    analyzedCountries.addAll(newBorders);

    LinkedList<String> localRoute = findRouteRecursive(analyzedCountries, newBorders, destination);

    if (!CollectionUtils.isEmpty(localRoute)) {
        String firstCountry = localRoute.getFirst();

        bordersByCountry.get(firstCountry).stream()
                .filter(startWithCountries::contains)
                .findFirst()
                .ifPresent(localRoute::addFirst);

        return localRoute;
    }

    return null;
}

    private void validateCountryCode(String countryCode) throws CountryCodeNotFoundException {
        if (!bordersByCountry.containsKey(countryCode)) {
            throw new CountryCodeNotFoundException(String.format(
                    "Country code [%s] was not found in bordersByCountry map",
                    countryCode));
        }
    }
}

