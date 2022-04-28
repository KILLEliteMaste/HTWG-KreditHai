package org.example;

import java.time.LocalDate;
import java.time.Period;

public class KreditHai {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    DatenbankSchnittstelle datenbankSchnittstelle;
    StatistikSchnittstelle statistikSchnittstelle;

    public KreditHai(DatenbankSchnittstelle datenbankSchnittstelle, StatistikSchnittstelle statistikSchnittstelle) {
        this.datenbankSchnittstelle = datenbankSchnittstelle;
        this.statistikSchnittstelle = statistikSchnittstelle;
    }

    /**
     * Minimum 5, maximum 15. Je höher, desto schlechter.
     * Gewährt: 5-7.
     * Berater muss dazu gerufen werden: 8-10.
     * Abgelehnt: 11-15
     *
     * @param kreditSumme
     * @param wohnort
     * @param beruf
     * @param kundeSeit
     * @param ruecklagenInEuro
     * @param plz
     * @return
     */
    public int calculateBonitaet(double kreditSumme, Wohnort wohnort, Beruf beruf, LocalDate kundeSeit, double ruecklagenInEuro, Plz plz) {
        if (kreditSumme <= 0
                || ruecklagenInEuro <= 0
                || wohnort == null
                || beruf == null
                || kundeSeit == null
                || kundeSeit.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }

        int bonitaet = 0;

        Period zugehoerigkeitSeit = Period.between(kundeSeit, LocalDate.now());

        if (zugehoerigkeitSeit.getYears() >= 10) {
            bonitaet += 1;
        } else if (zugehoerigkeitSeit.getYears() >= 5) {
            bonitaet += 2;
        } else {
            bonitaet += 3;
        }

        double prozent = ruecklagenInEuro / kreditSumme;
        if (prozent >= 0.1) {
            bonitaet += 1;
        } else if (prozent >= 0.05) {
            bonitaet += 2;
        } else {
            bonitaet += 3;
        }

        bonitaet += datenbankSchnittstelle.getBonitaet(plz);

        bonitaet += beruf.value;
        bonitaet += wohnort.value;

        statistikSchnittstelle.setDecision(bonitaet);

        return bonitaet;
    }

}

enum Beruf {
    SOFTWARE_ENTWICKLER(2),
    ARZT(1),
    FRISEUR(3);
    final int value;

    Beruf(int value) {
        this.value = value;
    }
}

enum Wohnort {
    BERLIN_KREUZBERG(3),
    KONSTANZ_WOLLMATINGEN(2),
    KONSTANZ_ALTSTADT(1);
    final int value;

    Wohnort(int value) {
        this.value = value;
    }
}

enum Plz {
    PLZ_78467,
    PLZ_78554,
    PLZ_10115
}