package com.irp.InterstellarRoutePlanner.mvcRest.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GateTest {
    
    @Test
    void TestConnectionJsonConversion() {
        
        Map<String, Integer> expectedConnections = Map.of(
                "RAN", 100,
                "PRX", 90,
                "SIR", 100,
                "ARC", 200,
                "ALD", 250 );
        final Gate testGate = new Gate( "SOL", "Sol", "[{ \"id\": \"RAN\", \"hu\": \"100\" }, { \"id\": \"PRX\", \"hu\": \"90\" }, { \"id\": " +
                                                              "\"SIR\", \"hu\": \"100\" }, { \"id\": \"ARC\", \"hu\": \"200\" }, { \"id\": \"ALD\", \"hu\": \"250\" }]" );
        
        try {
            var connections = testGate.createConnectionList();
            assertThat( connections.size() ).isEqualTo( 5 );
            
            int index = 0;
            for ( var connection : connections.keySet() ) {
                assertThat( expectedConnections.containsKey( connection ) ).isTrue();
            }
            
        } catch ( JsonProcessingException e ) {
            fail( "Exception thrown when processing Json: " + e.getMessage() );
        }
    }
}