package com.irp.InterstellarRoutePlanner.mvcRest.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.*;

@Entity
@Getter
public class Gate {
    
    @Id
    @Column(length = 3, columnDefinition = "varchar(3)", updatable = false, nullable = false)
    String id;
    
    @Column(length = 20, columnDefinition = "varchar(20)", updatable = false, nullable = false)
    String name;
    
    @Column
    String connections;
    
    public Gate() {
    }
    
    public Gate(String id, String name, String connections) {
        this.id = id;
        this.name = name;
        this.connections = connections;
    }
    
    @Override
    public boolean equals(Object o) {
        if ( o == null || getClass() != o.getClass() ) return false;
        
        Gate gate = (Gate) o;
        return id.equals( gate.id );
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public String toJson() {
        return "{ \"gateCode\": \"" + id + "\", \"name\": \"" + name + "\", ";
    }
    
    public Map<String, Integer> createConnectionList() throws JsonProcessingException {
        var gateConnections = Arrays.asList( new ObjectMapper().readValue(connections, Connection[].class) );
        
        // TODO Cache the map for future retrieval
        Map<String, Integer> mapOfConnections = new TreeMap<>();
        for(var gateConnection : gateConnections) {
            mapOfConnections.put( gateConnection.id(), gateConnection.hu() );
        }
        return mapOfConnections;
    }
}

record Connection(int hu, String id) {
}
