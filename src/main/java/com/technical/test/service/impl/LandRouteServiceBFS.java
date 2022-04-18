package com.technical.test.service.impl;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Profile("bfs")
public class LandRouteServiceBFS extends LandRouteServiceAbstract {

    public LandRouteServiceBFS(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    protected List<String> findRoute(String origin, String destination) {
        Set<String> startWithCountries = new HashSet<>();

        startWithCountries.add(origin);
        
        return findRouteBFS(new HashSet<>(), startWithCountries, destination);
    }

    /**
     * Search and returns the route from any country in {@code startWithCountries} to {@code destination} using a
     * Breadth First Search algorithm.
     *
     * @param visitedCountries the set of countries which were already analyzed.
     * @param startWithCountries the set of countries to start with to find a route to the {@code destination}
     * @param destination the destination country for the searched route
     * @return a list of countries needed to cross in order to reach the {@code destination} country. If there is no land
     * crossing, a null will be returned.
     */
    LinkedList<String> findRouteBFS(Set<String> visitedCountries, Set<String> startWithCountries, String destination) {

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
                .filter(Predicate.not(visitedCountries::contains))
                .collect(Collectors.toSet());

        visitedCountries.addAll(newBorders);

        LinkedList<String> localRoute = findRouteBFS(visitedCountries, newBorders, destination);

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

}