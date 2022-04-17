# Land Route Calculator

Your task is to create a simple Spring Boot service, that is able to calculate any possible land
route from one country to another. The objective is to take a list of country data in JSON format
and calculate the route by utilizing individual countries border information.


## Specifications

* Spring Boot, Maven
* Data link: https://raw.githubusercontent.com/mledoze/countries/master/countries.json
* The application exposes REST endpoint */routing/{origin}/{destination}* that
  returns a list of border crossings to get from origin to destination
* Single route is returned if the journey is possible
* Algorithm needs to be efficient
* If there is no land crossing, the endpoint returns *HTTP 400*
* Countries are identified by *cca3* field in country data
* HTTP request sample (land route from Czech Republic to Italy):
    - GET */routing/CZE/ITA HTTP/1.0*:
      ```json
      {
      "route" : [ "CZE" , "AUT" , "ITA" ]
      }
      ```

## Solution

The route is calculated using a recursive backtracking algorithm.

The algorithm starts with 2 sets: 
- **visitedCountries** - a sets of analyzed countries (empty on first call)
- **startWithCountries** - a sets of countries to start the search with (contains origin country on first call).

On each stage a set of unvisited border countries is created and a next call is done with this set, until the 
destination is found and returned.

After reaching the destination it builds the route list in reverse order (from destination to origin), by adding each 
country on the top of the list. 


```java
/**
 * Calculates and returns the route from any country in {@code startWithCountries} to {@code destination}.
 *
 * @param visitedCountries the set of countries which were already analyzed.
 * @param startWithCountries the set of countries to start with to find a route to the {@code destination}
 * @param destination the destination country for the calculated route
 * @return a list of countries needed to cross in order to reach the {@code destination} country. If there is no land
 * crossing, a null will be returned.
 */
private LinkedList<String> findRouteRecursive(Set<String> visitedCountries, Set<String> startWithCountries, String destination) {

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
        .filter(country -> !visitedCountries.contains(country))
        .collect(Collectors.toSet());

    visitedCountries.addAll(newBorders);

    LinkedList<String> localRoute = findRouteRecursive(visitedCountries, newBorders, destination);

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
```


## Usage

1. Run LandRouteCalculatorApplication using Java 11 or higher.

   Or, you can also use Maven:
   ```
   ./mvnw spring-boot:run
   ````

2. Send a GET request to localhost:{port}/routing/{origin}/{destination}
   ```
   curl localhost:8080/routing/NAM/KOR
   ```

   **Example of response**:
   ```json
   {
       "route": ["NAM", "AGO", "COD", "SSD", "SDN", "EGY", "ISR", "SYR", "TUR", "AZE", "RUS", "PRK", "KOR"]
   }
   ```