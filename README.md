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

The route is searched using two different algorithms:
- Depth First Search (DFS)
- Breadth First Search (BFS) 

By default, the application uses the DFS implementation, which is faster than BFS. For running the application with the 
BFS implementation, see the Usage section below.


```java
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
```


## Usage

1. Run LandRouteCalculatorApplication using Java 11 or higher.

   Or, you can also use Maven. For DFS algorithm:
   ```
   ./mvnw spring-boot:run
   ```
   For BFS:
   ```
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=bfs
   ```

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