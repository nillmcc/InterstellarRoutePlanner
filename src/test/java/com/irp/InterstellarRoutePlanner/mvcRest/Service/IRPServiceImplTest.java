package com.irp.InterstellarRoutePlanner.mvcRest.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import com.irp.InterstellarRoutePlanner.mvcRest.Model.TransportType;
import com.irp.InterstellarRoutePlanner.mvcRest.Repo.RouteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class IRPServiceImplTest {
    
    @InjectMocks
    IRPServiceImpl service;
    
    @Mock
    RouteRepository routeRepository;
    
    @BeforeAll
    static void setUp() {
    
    }
    
    @ParameterizedTest
    @CsvSource(textBlock = """
            3, 100, 5, 45.0, HSTC
            5, 250, 12, 112.5, HSTC
            2, 10, 1, 4.5, HSTC
            4, 21675,  10, 6552.5, PersonalTransport
            """)
    void testGetCostForPassengers(int passengerNum, int distance, int daysParked, double expectedCost, String typeExpected) {
        var costToGate = service.getCostTransportToGate( distance, passengerNum, daysParked );
        assertThat( costToGate ).isPresent();
        assertThat( costToGate.get().transportType() ).isEqualTo( TransportType.valueOf( typeExpected ) );
        assertThat( costToGate.get().cost() ).isEqualTo( expectedCost );
    }
    
    @ParameterizedTest
    @CsvSource(textBlock = """
            -3
            0
            6
            7
            """)
    void testGetCostForErroneousPassengers(int numOfPassengers) {
        var costToGate = service.getCostTransportToGate( 1, numOfPassengers, 12 );
        assertThat( costToGate ).isEmpty();
    }
    
    @Test
    void testSimpleRoute() {
        
        given( routeRepository.findById( "SOL" ) ).willReturn( Optional.of( new Gate( "SOL", "Sol", "[{ \"id\": \"RAN\", \"hu\": \"100\" }, { \"id\": \"PRX\", \"hu\": \"90\" }, { \"id\": \"SIR\", \"hu\": \"100\" }, { \"id\": \"ARC\", \"hu\": \"200\" }, { \"id\": \"ALD\", \"hu\": \"250\" }]" ) ) );
        given( routeRepository.findById( "PRX" ) ).willReturn( Optional.of( new Gate( "PRX", "Proxima", "[{ \"id\": \"SOL\", \"hu\": \"90\" }, { \"id\": \"SIR\", \"hu\": \"100\" }, { \"id\": \"ALT\", \"hu\": \"150\" }]" ) ) );
        
        var route = service.getRoute( "SOL", "PRX" );
        assertThat( route.isPresent() ).isTrue();
        assertThat( route.get().getRoute().size() ).isEqualTo( 2 );
    }
    
    @Test
    void testComplexRoute() {
        
        given( routeRepository.findById( "FOM" ) ).willReturn( Optional.of( new Gate( "FOM", "Formalhaut", "[{ \"id\": \"PRX\", \"hu\": \"10\" }, { \"id\": \"DEN\", \"hu\": \"20\" }, { \"id\": \"ALS\", \"hu\": \"9\" }]" ) ) );
        given( routeRepository.findById( "DEN" ) ).willReturn( Optional.of( new Gate( "DEN", "Denebula", "[{ \"id\": \"PRO\", \"hu\": \"5\" }, { \"id\": \"ARC\", \"hu\": \"2\" }, { \"id\": \"FOM\", \"hu\": \"8\" }, { \"id\": \"RAN\", \"hu\": \"100\" }, { \"id\": \"ALD\", \"hu\": \"3\" }]" ) ) );
        given( routeRepository.findById( "RAN" ) ).willReturn( Optional.of( new Gate( "RAN", "Ran", "[{ \"id\": \"SOL\", \"hu\": \"100\" }]" ) ) );
        given( routeRepository.findById( "SOL" ) ).willReturn( Optional.of( new Gate( "SOL", "Sol", "[{ \"id\": \"RAN\", \"hu\": \"100\" }, { \"id\": \"PRX\", \"hu\": \"90\" }, { \"id\": \"SIR\", \"hu\": \"100\" }, { \"id\": \"ARC\", \"hu\": \"200\" }, { \"id\": \"ALD\", \"hu\": \"250\" }]" ) ) );
        given( routeRepository.findById( "ALS" ) ).willReturn( Optional.of( new Gate( "ALS", "Alshain", "[{ \"id\": \"ALT\", \"hu\": \"1\" }, { \"id\": \"ALD\", \"hu\": \"1\" }]" ) ) );
        given( routeRepository.findById( "ALD" ) ).willReturn( Optional.of( new Gate( "ALD", "Alderman", "[{ \"id\": \"SOL\", \"hu\": \"200\" }, { \"id\": \"ALS\", \"hu\": \"160\" }, { \"id\": \"VEG\", \"hu\": \"320\" }]" ) ) );
        given( routeRepository.findById( "ARC" ) ).willReturn( Optional.of( new Gate( "ARC", "Arcturus", "[{ \"id\": \"SOL\", \"hu\": \"500\" }, { \"id\": \"DEN\", \"hu\": \"120\" }]" ) ) );
        given( routeRepository.findById( "PRO" ) ).willReturn( Optional.of( new Gate( "PRO", "Procyon", "[{ \"id\": \"CAS\", \"hu\": \"80\" }]" ) ) );
        given( routeRepository.findById( "CAS" ) ).willReturn( Optional.of( new Gate( "CAS", "Castor", "[{ \"id\": \"SIR\", \"hu\": \"200\" }, { \"id\": \"PRO\", \"hu\": \"120\" }]" ) ) );
        
        var route = service.getRoute( "DEN", "SOL" );
        assertThat( route.isPresent() ).isTrue();
        assertThat( route.get().getRoute().size() ).isEqualTo( 3 );
        assertThat( route.get().getCost() ).isEqualTo( 200 );
    }
}