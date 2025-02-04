package com.irp.InterstellarRoutePlanner.mvcRest.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.CostToGate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Route;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.TransportType;
import com.irp.InterstellarRoutePlanner.mvcRest.Repo.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class IRPServiceImpl implements IRPService {
    
    @Autowired
    RouteRepository routeRepository;
    
    private final static int MAX_ROUTE_DEPTH = 5;
    
    @Override
    public Optional<CostToGate> getCostTransportToGate(int distance, int passengerNum, int daysParked) {
        if ( passengerNum > 5 || passengerNum <= 0 ) {
            return Optional.empty();
        }
        
        var costForHSTC = HSTC_COST_PER_AU * distance;
        
        if ( passengerNum < 5 ) {
            var costForPersonalTransport = (PT_COST_PER_AU * distance) + (daysParked * PARKING_COST_PER_DAY);
            
            if ( costForHSTC > costForPersonalTransport ) {
                return Optional.of( new CostToGate( TransportType.PersonalTransport, costForPersonalTransport ) );
            }
        }
        
        return Optional.of( new CostToGate( TransportType.HSTC, costForHSTC ) );
    }
    
    @Override
    public Optional<Gate> getGate(String gateId) {
        return routeRepository.findById( gateId );
    }
    
    @Override
    public List<Gate> getGates() {
        return routeRepository.findAll();
    }
    
    @Override
    public Optional<Route> getRoute(String startGateId, String destinationGateId) {
        
        var startGate = getGate( startGateId ).orElseThrow();
        var route = new Route( startGate );
        Map<String, Integer> connections;
        try {
            connections = startGate.createConnectionList();
        } catch ( JsonProcessingException e ) {
            return Optional.empty();
        }
        
        if ( connections.containsKey( destinationGateId ) ) {
            return Optional.of( new Route( route, getGate( destinationGateId ).orElseThrow(), connections.get( destinationGateId ) ) );
        }
        
        List<Route> possibleRoutes = new ArrayList<>();
        
        try ( ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor() ) {
            List<Future<Optional<Route>>> callbacks = new ArrayList<>();
            for ( var connection : connections.keySet() ) {
                callbacks.add( executorService.submit( () -> getNextGate( startGateId, connection, destinationGateId, new Route( route, getGate( connection ).orElseThrow(), connections.get( connection ) ), 0 ) ) );
            }
            
            for ( var callback : callbacks ) {
                callback.get().ifPresent( possibleRoutes::add );
            }
            
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        
        int cost = Integer.MAX_VALUE;
        Route bestRoute = null;
        
        for ( var routeOption : possibleRoutes ) {
            if ( routeOption.getCost() < cost ) {
                bestRoute = routeOption;
                cost = bestRoute.getCost();
            }
        }
        
        return Optional.ofNullable( bestRoute );
    }
    
    private Optional<Route> getNextGate(String startGateId, String currentGateId, String destinationGateId, Route currentRoute, int depth) {
        
        if ( depth == MAX_ROUTE_DEPTH ) {
            return Optional.empty();
        }
        
        var currentGate = getGate( currentGateId ).orElseThrow();
        Map<String, Integer> connections = null;
        try {
            connections = currentGate.createConnectionList();
        } catch ( JsonProcessingException e ) {
            return Optional.empty();
        }
        
        if ( !connections.containsKey( destinationGateId ) ) {
            for ( var connection : connections.keySet() ) {
                if ( !startGateId.equals( connection ) ) {
                    Route continuedRoute = new Route( currentRoute, getGate( destinationGateId ).orElseThrow(), connections.get( connection ) );
                    return getNextGate( startGateId, connection, destinationGateId, continuedRoute, depth + 1 );
                }
            }
        }
        
        return Optional.of( new Route( currentRoute, getGate( destinationGateId ).orElseThrow(), connections.get( destinationGateId ) ) );
    }
}
