package com.technical.test.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LandRouteServiceDFS.class, LandRouteServiceBFS.class})
@ActiveProfiles(profiles = {"bfs", "dfs"})
class LandRouteServiceTest {

    @Autowired
    private LandRouteServiceDFS landRouteServiceDFS;

    @Autowired
    private LandRouteServiceBFS landRouteServiceBFS;

    @Test
    void testFindRouseBFS() {
        String origin = "NAM";
        String destination = "KOR";
        List<String> route = landRouteServiceBFS.findRoute(origin, destination);

        assertRouteIsValid(route, origin, destination);
    }

    @Test
    void testFindRouteDFS() {
        String origin = "NAM";
        String destination = "KOR";
        List<String> route = landRouteServiceDFS.findRoute(origin, destination);

        assertRouteIsValid(route, origin, destination);
    }

    private void assertRouteIsValid(List<String> route, String origin, String destination) {
        assertThat(route).first().isEqualTo(origin);
        assertThat(route).last().isEqualTo(destination);

        for (int i = 0; i < route.size() - 1; i++) {
            Set<String> borderCountries = landRouteServiceDFS.getBorders(route.get(i));

            assertThat(borderCountries)
                    .contains(route.get(i + 1));
        }
    }
}