package com.technical.test.service.impl;

import com.technical.test.exception.CountryCodeNotFoundException;
import com.technical.test.model.Country;
import com.technical.test.service.LandRouteService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class LandRouteServiceAbstract implements LandRouteService {
    public static final String COUNTRIES_URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";
    protected final RestTemplate restTemplate;
    protected Map<String, Set<String>> bordersByCountry;

    public LandRouteServiceAbstract(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

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

    @Override
    public Set<String> getBorders(String country) {
        return bordersByCountry.get(country);
    }

    @Override
    public List<String> getRoute(String origin, String destination) throws CountryCodeNotFoundException {

        if (Objects.isNull(bordersByCountry)) {
            return null;
        }

        validateCountryCode(origin);
        validateCountryCode(destination);

        return findRoute(origin, destination);
    }

    protected abstract List<String> findRoute(String origin, String destination);

    private void validateCountryCode(String countryCode) throws CountryCodeNotFoundException {
        if (!bordersByCountry.containsKey(countryCode)) {
            throw new CountryCodeNotFoundException(String.format(
                    "Country code [%s] was not found in bordersByCountry map",
                    countryCode));
        }
    }
}
