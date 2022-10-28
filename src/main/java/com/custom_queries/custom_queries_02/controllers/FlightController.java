package com.custom_queries.custom_queries_02.controllers;

import com.custom_queries.custom_queries_02.entities.Flight;
import com.custom_queries.custom_queries_02.entities.Status;
import com.custom_queries.custom_queries_02.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    FlightRepository flightRepository;

    @PostMapping
    public String createFlights(@RequestParam(required = false) Integer n) {
        Random random = new Random();
        if (n == null) {
            n = 100;
        }
        for (int i = 0; i < n; ++i) {
            Flight flight = new Flight();
            String randomString = random.ints(30, 100, 217)
                    .collect(StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append)
                    .toString();
            flight.setDescription(randomString + " " + randomString + " " + randomString);
            flight.setFromAirport(randomString);
            flight.setToAirport(randomString);
            flight.setStatus(Status.values()[random.nextInt(0, 3)]);
            flightRepository.save(flight);
        }
        return "Created " + n + " flights successfully!";
    }

    @GetMapping
    public Page<Flight> getAllFlights(@RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Sort sorting = Sort.by(Sort.Direction.ASC, "fromAirport");
            Pageable pageable = PageRequest.of(page, size, sorting);
            return flightRepository.findAll(pageable);
        } else return Page.empty();
    }

    @GetMapping("/ontime")
    public List<Flight> getFlightsOnTime() {
        return flightRepository.findByStatus(Status.ONTIME);
    }

    @GetMapping("/status")
    public List<Flight> getFlightsByStatus(@RequestParam String p1, @RequestParam String p2, HttpServletResponse response) {
        List<Flight> flightList = new ArrayList<>();
        p1 = p1.toUpperCase();
        p2 = p2.toUpperCase();
        List<String> statusList = List.of(Status.ONTIME.toString(),
                Status.DELAYED.toString(), Status.CANCELLED.toString());
        if (statusList.contains(p1) && statusList.contains(p2)) {
            flightList.addAll(flightRepository.findTwoStatus(p1, p2));
        } else response.setStatus(400);
        return flightList;
    }
}
