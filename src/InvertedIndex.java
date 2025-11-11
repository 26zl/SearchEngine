import java.util.*;

/**
 * En kompakt invertert indeks som kobler ord til dokumenter og frekvens.
 * Støtter AND/OR/NOT og rangering (enkelt- og multi-ord).
 */
public class InvertedIndex {
    private final Map<String, Map<String, Integer>> indeks = new HashMap<>();

    // Legger til et dokument og innhold i indeksen
    public void leggTilIIndeks(String dokNavn, String innhold) {
        if (dokNavn == null || innhold == null) {
            throw new IllegalArgumentException("Dokumentnavn og innhold kan ikke være null");
        }
        String[] ord = innhold.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-zA-Z0-9 ]", " ")  
                .trim()
                .split("\\s+");
        for (String enkeltOrd : ord) {
            if (!enkeltOrd.isEmpty()) {
                indeks.computeIfAbsent(enkeltOrd, k -> new HashMap<>())
                      .merge(dokNavn, 1, Integer::sum); // Copilot forbedring
            }
        }
    }

    // Skriver ut hele indeksen med ordfrekvenser
    public void skrivUtIndeks() {
        System.out.println("=== Invertert indeks ===");
        List<String> ordListe = new ArrayList<>(indeks.keySet());
        Collections.sort(ordListe);
        for (String ord : ordListe) {
            System.out.println("\"" + ord + "\" \u2192 " + indeks.get(ord));
        }
        System.out.println();
    }

    // Hjelpemetode for å hente dokumenter som inneholder et ord
    private Set<String> dokumenterFor(String ord) {
        Map<String, Integer> dokMap = indeks.get(ord.toLowerCase(Locale.ROOT));
        return (dokMap != null) ? new HashSet<>(dokMap.keySet()) : new HashSet<>();
    }

    // AND-søk: dokumenter som inneholder begge ordene
    public Set<String> ogSok(String ord1, String ord2) {
        Set<String> resultat = dokumenterFor(ord1);
        resultat.retainAll(dokumenterFor(ord2));
        return resultat;
    }

    // OR-søk: dokumenter som inneholder minst ett av ordene
    public Set<String> ellerSok(String ord1, String ord2) {
        Set<String> resultat = dokumenterFor(ord1);
        resultat.addAll(dokumenterFor(ord2));
        return resultat;
    }

    // NOT-søk: dokumenter som inneholder første ord, men ikke det andre
    public Set<String> ikkeSok(String ord1, String ord2) {
        Set<String> resultat = dokumenterFor(ord1);
        resultat.removeAll(dokumenterFor(ord2));
        return resultat;
    }

    // Enkelt søk: returnerer dokumenter som inneholder ordet man leter etter
    public Set<String> sok(String ord) {
        if (ord == null) return new HashSet<>();
        Map<String, Integer> dokMap = indeks.get(ord.toLowerCase(Locale.ROOT));
        return (dokMap == null) ? new HashSet<>() : new HashSet<>(dokMap.keySet()); // Copilot forbedring
    }

    // Rangert søk (ett ord): dokumenter sortert etter antall forekomster
    public List<Map.Entry<String, Integer>> sokRangert(String ord) {
        if (ord == null) return new ArrayList<>();
        return sokRangert(new String[]{ord});
    }

    // Rangert søk (flere ord): summerer frekvens per dokument for alle oppgitte ord (OR-logikk)
    public List<Map.Entry<String, Integer>> sokRangert(String... ord) {
        if (ord == null || ord.length == 0) {
            return new ArrayList<>();
        }
        Map<String, Integer> score = new HashMap<>();
        for (String enkeltOrd : ord) {
            if (enkeltOrd == null) continue;
            String normalisert = enkeltOrd.toLowerCase(Locale.ROOT).trim();
            if (normalisert.isEmpty()) continue;

            Map<String, Integer> dokumenter = indeks.get(normalisert);
            if (dokumenter == null) continue;

            for (Map.Entry<String, Integer> entry : dokumenter.entrySet()) {
                score.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> sortertResultat = new ArrayList<>(score.entrySet());
        sortertResultat.sort(
            Map.Entry.<String, Integer>comparingByValue().reversed()
                     .thenComparing(Map.Entry::getKey)
        );
        return sortertResultat;
    }

    // Henter ordfrekvens for et ord i et dokument
    public int hentFrekvens(String ord, String dokNavn) {
        Map<String, Integer> dokMap = indeks.get(ord.toLowerCase(Locale.ROOT));
        if (dokMap != null && dokMap.containsKey(dokNavn)) {
            return dokMap.get(dokNavn);
        }
        return 0;
    }

    // Tømmer indeksen
    public void tomIndeks() {
        indeks.clear();
    }
}