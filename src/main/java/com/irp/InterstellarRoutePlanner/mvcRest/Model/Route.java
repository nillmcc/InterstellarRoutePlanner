package com.irp.InterstellarRoutePlanner.mvcRest.Model;

import java.util.ArrayList;
import java.util.List;

public class Route {
    List<Gate> route;
    int cost = 0;
    
    public Route(Gate startGate) {
        route = new ArrayList<>( 4 );
        route.add( startGate );
    }
    
    public Route(Route route, Gate nextGate, int cost) {
        this.route = new ArrayList<>();
        this.cost = route.getCost() + cost;
        
        this.route.addAll( route.getRoute() );
        this.route.add( nextGate );
    }
    
    public List<Gate> getRoute() {
        return List.copyOf( route );
    }
    
    public int getCost() {
        return cost;
    }
}
