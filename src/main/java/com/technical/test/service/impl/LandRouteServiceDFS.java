package com.technical.test.service.impl;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Service
@Profile("dfs")
public class LandRouteServiceDFS extends LandRouteServiceAbstract {

    public LandRouteServiceDFS(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    protected List<String> findRoute(String origin, String destination) {
        return findRouteDFS(new HashSet<>(), origin, destination);
    }

    /**
     * Search and returns the route from any country in {@code startWithCountries} to {@code destination}using a Depth
     * First Search algorithm.
     *
     * @param visitedCountries the set of countries which were already analyzed.
     * @param origin the origin country to start with
     * @param destination the destination country for the searched route
     * @return a list of countries needed to cross in order to reach the {@code destination} country. If there is no land
     * crossing, a null will be returned.
     */
    LinkedList<String> findRouteDFS(Set<String> visitedCountries, String origin, String destination) {

        if (origin.equals(destination)) {
            LinkedList<String> route = new LinkedList<>();

            route.add(destination);
            return route;
        }

        Set<String> borderCountries = bordersByCountry.get(origin);

        for (String borderCountry : borderCountries) {

            if (visitedCountries.contains(borderCountry)) {
                continue;
            }

            visitedCountries.add(borderCountry);

            LinkedList<String> localRoute = findRouteDFS(visitedCountries, borderCountry, destination);

            if (!CollectionUtils.isEmpty(localRoute)) {
                localRoute.addFirst(origin);
                return localRoute;
            }
        }
        return null;
    }
}

