package ru.practicum;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class StatsClient {

    private final RestClient restClient;

    public StatsClient(RestClient restClient) {
        this.restClient = restClient;
    }
}
