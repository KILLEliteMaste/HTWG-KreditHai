package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class KreditHaiTest {

    @ParameterizedTest(name = "{index} => {0} + {1} + {2} + {3}")
    @MethodSource("argumentProviderForExceptions")
    void calculateBonitaetExceptions(double kreditSumme, Wohnort wohnort, Beruf beruf, LocalDate kundeSeit, double ruecklagenInEuro) {
        //Setup
        KreditHai kreditHai = new KreditHai();

        //Execute - Verify
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kreditHai.calculateBonitaet(kreditSumme, wohnort, beruf, kundeSeit, ruecklagenInEuro);
        });

        Assertions.assertNotNull(thrown);
        Assertions.assertEquals(IllegalArgumentException.class, thrown.getClass());
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

    @ParameterizedTest(name = "{index} => {0} + {1} + {2} + {3} + {4}")
    @MethodSource("argumentProvider")
    void calculateBonitaet(double kreditSumme, Wohnort wohnort, Beruf beruf, LocalDate kundeSeit, double ruecklagenInEuro, int expectedResult) {
        //Setup
        KreditHai kreditHai = new KreditHai();

        //Execute
        int bonitaet = kreditHai.calculateBonitaet(kreditSumme, wohnort, beruf, kundeSeit, ruecklagenInEuro);

        // Verify
        Assertions.assertEquals(expectedResult, bonitaet);
    }

    private static Stream<Arguments> argumentProvider() {
        return Stream.of(
                Arguments.of(10000.0, Wohnort.KONSTANZ_ALTSTADT, Beruf.ARZT, LocalDate.of(2010, 2, 2), 1000.0, 4),
                Arguments.of(10000.0, Wohnort.KONSTANZ_WOLLMATINGEN, Beruf.SOFTWARE_ENTWICKLER, LocalDate.of(2020, 2, 2), 5000.0, 8),
                Arguments.of(10000.0, Wohnort.BERLIN_KREUZBERG, Beruf.FRISEUR, LocalDate.of(2020, 2, 2), 500.0, 11)
        );
    }
}