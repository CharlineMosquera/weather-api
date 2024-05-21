package com.github.charlinemosquera.weatherapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class WeatherController {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @GetMapping("/weather")
    public WeatherResponse getWeather(@RequestParam String city, @RequestParam(required = false) String country) {
        String url = "http://api.openweathermap.org/data/2.5/weather";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", country != null ? city + "," + country : city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric");

        RestTemplate restTemplate = new RestTemplate();
        OpenWeatherMapResponse response = restTemplate.getForObject(uriBuilder.toUriString(), OpenWeatherMapResponse.class);

        if (response != null && response.getMain() != null && response.getWeather() != null && !response.getWeather().isEmpty()) {
            WeatherResponse weatherResponse = new WeatherResponse();
            weatherResponse.setCity(response.getName());
            weatherResponse.setCountry(response.getSys().getCountry());
            weatherResponse.setTemperature(response.getMain().getTemp());
            weatherResponse.setHumidity(response.getMain().getHumidity());
            weatherResponse.setDescription(response.getWeather().get(0).getDescription());
            return weatherResponse;
        } else {
            throw new RuntimeException("City not found");
        }
    }
}
