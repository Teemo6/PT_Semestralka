public class Main {
    private static final String VSTUP_SOUBORU = "data2/sparse_slightly_medium.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();
    private static final Simulace SIMULACE = Simulace.getInstance();

    public static void main(String[] args) {
        long a, b;
        a = System.currentTimeMillis();

        VSTUP_DAT.vytvorObjekty(VSTUP_SOUBORU);

        b = System.currentTimeMillis();
        System.out.println("\nData nactena v case: " + (b - a) + " ms.");

        SIMULACE.spustSimulaci(VSTUP_DAT);

        b = System.currentTimeMillis();
        System.out.println("\nRuntime: " + (b - a) + " ms.");
    }
}
