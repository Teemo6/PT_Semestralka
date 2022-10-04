public class Main {
    private static final String VSTUP_SOUBORU = "data/random.txt";

    private static final VstupDat VSTUP_DAT = VstupDat.getInstance();

    public static void main(String[] args) {
        VSTUP_DAT.vytvorObjekty(VSTUP_SOUBORU);
    }
}
