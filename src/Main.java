/**
 * Main-klasse for å demonstrere funksjonaliteten til InvertedIndex
 * (bygging, enkeltsøk, AND/OR/NOT, rangering og tidsmålinger).
 */
public class Main {
    public static void main(String[] args) {
        InvertedIndex indeks = new InvertedIndex();

        // Byggetid (Oppgave 1)
        long tid1 = System.nanoTime();
        indeks.leggTilIIndeks("Dok1", "Java is fun and powerful, Java is also popular");
        indeks.leggTilIIndeks("Dok2", "Java and data science");
        indeks.leggTilIIndeks("Dok3", "Data is everywhere and Java is useful");
        long tid2 = System.nanoTime();
        double byggTidMs = (tid2 - tid1) / 1_000_000.0;
        System.out.println("Byggetid: " + byggTidMs + " ms\n");

        // Vis indeks (Oppgave 1)
        indeks.skrivUtIndeks();

        // Søk (Oppgave 2)
        System.out.println("Søk 'java': " + indeks.sok("java"));
        System.out.println("Søk 'java AND data': " + indeks.ogSok("java", "data"));
        System.out.println("Søk 'java OR data': " + indeks.ellerSok("java", "data"));
        System.out.println("Søk 'java NOT data': " + indeks.ikkeSok("java", "data"));
        System.out.println();

        // Total frekvens (hjelp til Oppgave 3)
        System.out.println("Frekvens totalt av 'java'  : " + totalFrekvens(indeks, "java"));
        System.out.println();

        // Rangering (Oppgave 3)
        System.out.println("Rangert 'java':");
        for (var e : indeks.sokRangert("java")) {
            int f = e.getValue();
            System.out.println("  " + e.getKey() + ": " + f + " " + (f == 1 ? "forekomst" : "forekomster"));
        }
        System.out.println();

        System.out.println("Rangert 'java' + 'data':");
        for (var e : indeks.sokRangert("java", "data")) {
            int f = e.getValue();
            System.out.println("  " + e.getKey() + ": " + f + " " + (f == 1 ? "forekomst" : "forekomster"));
        }
        System.out.println();

        // Tidsmålinger (Oppgave 4)
        long q0 = System.nanoTime();
        indeks.sok("java");
        long q1 = System.nanoTime();
        double sokJavaMs = (q1 - q0) / 1_000_000.0;
        System.out.println("Søketid 'java': " + sokJavaMs + " ms");

        long a0 = System.nanoTime();
        indeks.ogSok("java", "data");
        long a1 = System.nanoTime();
        double sokJavaAndDataMs = (a1 - a0) / 1_000_000.0;
        System.out.println("Søketid 'java AND data': " + sokJavaAndDataMs + " ms\n");

        // Kort refleksjon (til Oppgave 4 i rapport)
        System.out.println("Refleksjon: Indeksbygging flytter arbeidet til forhåndsprosessering og kostet ~"
                + byggTidMs + " ms, mens enkeltsøk tok ~" + sokJavaMs + " ms og kombinerte søk ~"
                + sokJavaAndDataMs + " ms. Dette støtter analysen om at søketid domineres av antall treff.");
        System.out.println();

        // Tømmer indeksen
        indeks.tomIndeks();
        System.out.println("Etter tømming, søk 'java': " + indeks.sok("java"));
    }

    // Summerer frekvens for et ord over alle dokumenter
    private static int totalFrekvens(InvertedIndex indeks, String ord) {
        int sum = 0;
        for (String dok : indeks.sok(ord)) {
            sum += indeks.hentFrekvens(ord, dok);
        }
        return sum;
    }
}