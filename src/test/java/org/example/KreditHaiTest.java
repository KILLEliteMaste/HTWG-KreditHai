package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.stream.Stream;

class KreditHaiTest {

    @Mock
    DatenbankSchnittstelle datenbankSchnittstelle;

    @Mock
    StatistikSchnittstelle statistikSchnittstelle;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(datenbankSchnittstelle.getBonitaet(Plz.PLZ_78467)).thenReturn(2);
        Mockito.when(datenbankSchnittstelle.getBonitaet(Plz.PLZ_10115)).thenReturn(1);
        Mockito.when(datenbankSchnittstelle.getBonitaet(Plz.PLZ_78554)).thenReturn(3);
    }


    @ParameterizedTest(name = "{index} => {0} + {1} + {2} + {3}")
    @MethodSource("argumentProviderForExceptions")
    void calculateBonitaetExceptions(double kreditSumme, Wohnort wohnort, Beruf beruf, LocalDate kundeSeit, double ruecklagenInEuro) {
        //Setup
        KreditHai kreditHai = new KreditHai(datenbankSchnittstelle, statistikSchnittstelle);

        //Execute - Verify
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kreditHai.calculateBonitaet(kreditSumme, wohnort, beruf, kundeSeit, ruecklagenInEuro, Plz.PLZ_78467);
        });

        Assertions.assertNotNull(thrown);
        Assertions.assertEquals(IllegalArgumentException.class, thrown.getClass());
        Mockito.verifyNoInteractions(statistikSchnittstelle);
    }

    private static Stream<Arguments> argumentProviderForExceptions() {
        return Stream.of(
                Arguments.of(-1.0, Wohnort.BERLIN_KREUZBERG, Beruf.ARZT, LocalDate.of(2020, 2, 2), 1000.0),
                Arguments.of(1.0, Wohnort.BERLIN_KREUZBERG, Beruf.ARZT, LocalDate.of(2020, 2, 2), -1000.0),
                Arguments.of(1.0, Wohnort.BERLIN_KREUZBERG, Beruf.ARZT, LocalDate.of(2030, 2, 2), 1000.0),
                Arguments.of(1.0, Wohnort.BERLIN_KREUZBERG, null, LocalDate.of(2020, 2, 2), 1000.0),
                Arguments.of(1.0, null, Beruf.ARZT, LocalDate.of(2020, 2, 2), 1000.0)
        );
    }

    @ParameterizedTest(name = "{index} => {0} + {1} + {2} + {3} + {4} + {5}")
    @MethodSource("argumentProvider")
    void calculateBonitaet(double kreditSumme, Wohnort wohnort, Beruf beruf, LocalDate kundeSeit, double ruecklagenInEuro, int expectedResult, Plz plz) {
        //Setup
        KreditHai kreditHai = new KreditHai(datenbankSchnittstelle, statistikSchnittstelle);

        //Execute
        int bonitaet = kreditHai.calculateBonitaet(kreditSumme, wohnort, beruf, kundeSeit, ruecklagenInEuro, plz);

        // Verify
        Assertions.assertEquals(expectedResult, bonitaet);
        Mockito.verify(statistikSchnittstelle, Mockito.times(1)).setDecision(bonitaet);
    }

    private static Stream<Arguments> argumentProvider() {
        return Stream.of(
                Arguments.of(10000.0, Wohnort.KONSTANZ_ALTSTADT, Beruf.ARZT, LocalDate.of(2010, 2, 2), 1000.0, 5, Plz.PLZ_10115),
                Arguments.of(10000.0, Wohnort.KONSTANZ_WOLLMATINGEN, Beruf.SOFTWARE_ENTWICKLER, LocalDate.of(2020, 2, 2), 5000.0, 10, Plz.PLZ_78467),
                Arguments.of(10000.0, Wohnort.BERLIN_KREUZBERG, Beruf.FRISEUR, LocalDate.of(2020, 2, 2), 500.0, 14, Plz.PLZ_78554)
        );
    }
}