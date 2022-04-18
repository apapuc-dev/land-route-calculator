package com.technical.test.controller;

import com.technical.test.exception.CountryCodeNotFoundException;
import com.technical.test.service.LandRouteService;
import com.technical.test.service.impl.LandRouteServiceAbstract;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class LandRouteController {

    private final LandRouteService landRouteService;

    public LandRouteController(LandRouteServiceAbstract landRouteService) {
        this.landRouteService = landRouteService;
    }

    @GetMapping("/routing/{origin}/{destination}")
    ResponseEntity<Map<String, List<String>>> getRoute(@PathVariable String origin, @PathVariable String destination)
            throws CountryCodeNotFoundException {

        List<String> route = landRouteService.getRoute(origin, destination);

        if (Objects.isNull(route) || route.size() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(Collections.singletonMap("route", route));
    }

}
