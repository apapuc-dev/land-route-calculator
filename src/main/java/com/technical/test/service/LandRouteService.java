package com.technical.test.service;

import com.technical.test.exception.CountryCodeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface LandRouteService {
    Set<String> getBorders(String country);

    List<String> getRoute(String origin, String destination) throws CountryCodeNotFoundException;
}
