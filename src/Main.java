public class Main {
    public static void main(String[] args) {
        InvertedIndex indeks = new InvertedIndex();

        // Sjekke byggetid
        long tid1 = System.nanoTime();
        indeks.leggTilIIndeks("Dok1", "Java is fun and powerful, Java is also popular");
        indeks.leggTilIIndeks("Dok2", "Java and data science");
        indeks.leggTilIIndeks("Dok3", "Data is everywhere and Java is useful");
        long tid2 = System.nanoTime();
        System.out.println("Byggetid: " + (tid2 - tid1) / 1_000_000.0 + " ms\n");

        // Vis indeks
        indeks.skrivUtIndeks();

        // Søk
        System.out.println("Søk 'java': " + indeks.sok("java"));
        System.out.println("Søk 'java AND data': " + indeks.ogSok("java", "data"));
        System.out.println("Søk 'java OR data': " + indeks.ellerSok("java", "data"));
        System.out.println("Søk 'java NOT data': " + indeks.ikkeSok("java", "data"));
        System.out.println();

        // Total frekvens uten å endre InvertedIndex
        System.out.println("Frekvens totalt av 'java'  : " + totalFrekvens(indeks, "java"));
        System.out.println();

        // Rangering
        System.out.println("Rangert 'java':");
        for (var e : indeks.sokRangert("java")) {
            System.out.println("  " + e.getKey() + ": " + e.getValue() + " forekomster");
        }
        System.out.println();
        indeks.tomIndeks();
        System.out.println("Etter tømming, søk 'java': " + indeks.sok("java"));
    }

    // Summerer frekvensen av et ord over alle dokumenter
    private static int totalFrekvens(InvertedIndex indeks, String ord) {
        int sum = 0;
        for (String dok : indeks.sok(ord)) {
            sum += indeks.hentFrekvens(ord, dok);
        }
        return sum;
    }
}