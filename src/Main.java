public class Main {
    private static final String VSTUP_SOUBORU = "data2/sparse_bit_large.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();
    private static final Simulace SIMULACE = Simulace.getInstance();

    public static void main(String[] args) {
        long a = System.currentTimeMillis();

        VSTUP_DAT.vytvorObjekty(VSTUP_SOUBORU);
        System.out.println("Data nactena.");
        SIMULACE.spustSimulaci(VSTUP_DAT);

        long b = System.currentTimeMillis();
        System.out.println("\nRuntime: " + (b - a) + " ms.");
    }
}
