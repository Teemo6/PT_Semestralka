public class Main {
    private static final String VSTUP_SOUBORU = "data2/centre_large.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();
    private static final Simulace SIMULACE = Simulace.getInstance();

    public static void main(String[] args) {
        long a = System.currentTimeMillis();
        VSTUP_DAT.vytvorObjekty(VSTUP_SOUBORU);
        SIMULACE.spustSimulaci(VSTUP_DAT);
        long b = System.currentTimeMillis();
        System.out.println(b - a);

    }
}
