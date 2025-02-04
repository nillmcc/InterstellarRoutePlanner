package com.irp.InterstellarRoutePlanner.mvcRest.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.CostToGate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Route;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IRPService {
    
    double HSTC_COST_PER_AU = 0.45;
    double PT_COST_PER_AU = 0.3;
    double PARKING_COST_PER_DAY = 5.0;
    
    Optional<CostToGate> getCostTransportToGate(int distance, int passengerNum, int daysParked);
    
    Optional<Gate> getGate(String gateId);
    
    List<Gate> getGates();
    
    Optional<Route> getRoute(String startGateId, String destinationGateId);
}
