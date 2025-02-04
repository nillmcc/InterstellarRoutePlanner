package com.irp.InterstellarRoutePlanner.mvcRest.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.CostToGate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Route;
import com.irp.InterstellarRoutePlanner.mvcRest.Service.IRPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class IRPController {
    
    public final static String GATE_URL = "/gates";
    public final static String GATE_CODE = GATE_URL + "/{gateCode}";
    public final static String TO_GATE_CODE = GATE_CODE + "/to/{targetGateCode}";
    public final static String TRANSPORT_URL = "/transport/{distance}";
    
    IRPService irpService;
    
    @Autowired
    public IRPController(IRPService irpService) {
        this.irpService = irpService;
    }
    
    @GetMapping(TRANSPORT_URL)
    public CostToGate GetCheapestVehicleToGate(@PathVariable("distance") int distanceToGate,
                                               @RequestParam(required = false) Integer passengers,
                                               @RequestParam(required = false) Integer parking) {
        if ( passengers == null ) {
            passengers = 1;
        }
        else if ( passengers > 5 ) {
            throw new RuntimeException( "Too many passengers" );
        }
        if ( parking == null ) {
            parking = 0;
        }
        
        return irpService.getCostTransportToGate( distanceToGate, passengers, parking )
                       .orElseThrow( () -> new RuntimeException( "Invalid Request" ) );
    }
    
    @GetMapping(GATE_URL)
    public ResponseEntity<Page<Gate>> GetGates() {
        return ResponseEntity.ok( new PageImpl<>( irpService.getGates() ) );
    }
    
    @GetMapping(GATE_CODE)
    public Gate GetGate(@PathVariable("gateCode") String gateId) {
        return irpService.getGate( gateId ).orElseThrow( () -> new RuntimeException( "No Gate Found" ) );
    }
    
    @GetMapping(TO_GATE_CODE)
    public ResponseEntity<Page<Gate>> GetRoute(@PathVariable("gateCode") String gateId,
                                               @PathVariable("targetGateCode") String targetGateId) {
        Page<Gate> routePage;
        try {
            Optional<Route> route = irpService.getRoute( gateId, targetGateId );
            if ( route.isPresent() ) {
                routePage = new PageImpl<>( route.get().getRoute() );
            }
            else {
                return new ResponseEntity( HttpStatus.NOT_FOUND );
            }
        } catch ( Exception e ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return ResponseEntity.ok( routePage );
    }
}
