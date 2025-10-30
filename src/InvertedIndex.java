import java.util.*;

/**
 * En kompakt invertert indeks som kobler ord til dokumenter og frekvens.
 * Støtter AND/OR/NOT og rangering etter ordfrekvens.
 */

public class InvertedIndex {
    private final Map<String, Map<String, Integer>> indeks = new HashMap<>();

    // Legger til et dokument og innhold i indeksen
    public void leggTilIIndeks(String dokNavn, String innhold) {
        if (dokNavn == null || innhold == null) {
            throw new IllegalArgumentException("Dokumentnavn og innhold kan ikke være null");
        }
        String[] ord = innhold.toLowerCase()
            .replaceAll("[^a-zA-Z0-9 ]", "")
            .split("\\s+");
        for (String enkeltOrd : ord) {
            if (!enkeltOrd.isEmpty()) {
                indeks.computeIfAbsent(enkeltOrd, k -> new HashMap<>()) 
                     .merge(dokNavn, 1, Integer::sum);
            }
        }
    }

    // Skriver ut hele indeksen med ordfrekvenser
    public void skrivUtIndeks() {
        System.out.println("=== Invertert indeks ===");
        List<String> ordListe = new ArrayList<>(indeks.keySet());
        Collections.sort(ordListe);
        for (String ord : ordListe) {
            System.out.println("\"" + ord + "\" → " + indeks.get(ord));
        }
        System.out.println();
    }

    // Hjelpemetode for å hente dokumenter som inneholder et ord
    private Set<String> dokumenterFor(String ord) {
        Map<String, Integer> dokMap = indeks.get(ord.toLowerCase());
        if (dokMap != null) {
            return new HashSet<>(dokMap.keySet());
        } else {
            return new HashSet<>();
        }
    }

    // AND-søk: dokumenter som inneholder begge ordene
    public Set<String> ogSok(String ord1, String ord2) {
        Set<String> resultat = this.dokumenterFor(ord1);
        resultat.retainAll(this.dokumenterFor(ord2));
        return resultat;
    }

    // OR-søk: dokumenter som inneholder minst ett av ordene
    public Set<String> ellerSok(String ord1, String ord2) {
        Set<String> resultat = this.dokumenterFor(ord1);
        resultat.addAll(this.dokumenterFor(ord2));
        return resultat;
    }

    // NOT-søk: dokumenter som inneholder første ord, men ikke det andre
    public Set<String> ikkeSok(String ord1, String ord2) {
        Set<String> resultat = this.dokumenterFor(ord1);
        resultat.removeAll(this.dokumenterFor(ord2));
        return resultat;
    }

    // Enkelt søk: returnerer dokumenter som inneholder ordet man leter etter
    public Set<String> sok(String ord) {
        return indeks.containsKey(ord.toLowerCase())
                ? new HashSet<>(indeks.get(ord.toLowerCase()).keySet())
                : new HashSet<>();
    }

    // Rangert søk: dokumenter sortert etter antall forekomster
    public List<Map.Entry<String, Integer>> sokRangert(String ord) {
        Map<String, Integer> dokumenter = indeks.get(ord.toLowerCase());
        if (dokumenter == null || dokumenter.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<Map.Entry<String, Integer>> sortertResultat = new ArrayList<>(dokumenter.entrySet());
            sortertResultat.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
            return sortertResultat;
        }
    }

    // Henter ordfrekvens for et ord i et dokument
    public int hentFrekvens(String ord, String dokNavn) {
        Map<String, Integer> dokMap = indeks.get(ord.toLowerCase());
        if (dokMap != null && dokMap.containsKey(dokNavn)) {
            return dokMap.get(dokNavn);
        } else {
            return 0;
        }
    }

    // Tømmer indeksen
    public void tomIndeks() {
        indeks.clear();
    }
}