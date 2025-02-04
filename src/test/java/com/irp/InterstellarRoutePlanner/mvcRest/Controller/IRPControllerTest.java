package com.irp.InterstellarRoutePlanner.mvcRest.Controller;

import com.irp.InterstellarRoutePlanner.mvcRest.Model.CostToGate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Route;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.TransportType;
import com.irp.InterstellarRoutePlanner.mvcRest.Service.IRPService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IRPController.class)
class IRPControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @MockitoBean
    IRPService service;
    
    @Test
    void testCheckHSTCUsed() throws Exception {
        
        given( service.getCostTransportToGate( eq( 1 ), eq( 5 ), anyInt() ) )
                .willReturn( Optional.of( new CostToGate( TransportType.HSTC, 100 ) ) );
        
        mockMvc.perform( get( IRPController.TRANSPORT_URL + "?passengers={number}", 1, 5 )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.transportType", is( TransportType.HSTC.toString() ) ) )
                .andExpect( status().isOk() );
    }
    
    @Test
    void testGetGates() throws Exception {
        given( service.getGates() ).willReturn( List.of(
                new Gate( "SOL", "Sol", "" ),
                new Gate( "PRX", "Prx", "" ),
                new Gate( "CAS", "Cas", "" )
        ) );
        
        mockMvc.perform( get( IRPController.GATE_URL )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.content.length()", is( 3 ) ) )
                .andExpect( status().isOk() );
    }
    
    @Test
    void testGetGate() throws Exception {
        
        final String testConnectionsString = "testConnections";
        
        given( service.getGate( "SOL" ) ).willReturn( Optional.of( new Gate( "SOL", "Sol", testConnectionsString ) ) );
        
        mockMvc.perform( get( IRPController.GATE_CODE, "SOL" )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.connections", is( testConnectionsString ) ) );
    }
    
    @Test
    void testNoGateExisting() throws Exception {
        
        given( service.getGate( anyString() ) ).willReturn( Optional.empty() );
        
        mockMvc.perform( get( IRPController.GATE_CODE, "" )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
    }
    
    @Test
    void testGetBadRoute() throws Exception {
        given( service.getRoute( anyString(), anyString() ) ).willReturn( Optional.empty() );
        
        mockMvc.perform( get( IRPController.TO_GATE_CODE, "SOL", "PRX" )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
    }
    
    @Test
    void testGetRoute() throws Exception {
        given( service.getRoute( anyString(), anyString() ) ).willReturn( Optional.of(new Route( new Gate("SOL", "", "") ) ) );
        
        mockMvc.perform( get( IRPController.TO_GATE_CODE, "SOL", "PRX" )
                                 .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
    }
}