import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

/**
 * Instance třídy {@code VstupDat} představuje jedináčka, který umí přečíst vstupní soubor a vybrat potřebná data
 * @author Štěpán Faragula, Mikuláš Mach 04-10-2022
 * @version 1.01
 */
public class VstupDat{
    /** Instance jedináčka */
    private static final VstupDat INSTANCE = new VstupDat();

    /** Načtené hodnoty */
    private List<Sklad> sklady;
    private List<Oaza> oazy;
    private List<Cesta> cesty;
    private List<Velbloud> velbloudi;
    private List<Pozadavek> pozadavky;

    public static VstupDat getInstance(){
        return INSTANCE;
    }

    public void nactiData(String vstupniSoubor) {
        sklady = new ArrayList<>();
        oazy = new ArrayList<>();
        cesty = new ArrayList<>();
        velbloudi = new ArrayList<>();
        pozadavky = new ArrayList<>();

        int vnorenyKomentar = 0;

        Path filePath = Paths.get("data/parser.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> integers = new ArrayList<>();
        while (scanner.hasNext()) {

            if(vnorenyKomentar == 0){
                if (scanner.hasNextInt()) {
                    int a = scanner.nextInt();
                    System.out.println(a);
                    integers.add(a);
                    continue;
                }
            }

            String ahoj = scanner.next();

            if(ahoj.contains("\uD83D\uDC2A")){
                int count = ahoj.length() - ahoj.replaceAll("\uD83D\uDC2A","").length();
                vnorenyKomentar += count;
            }

            if(ahoj.contains("\uD83C\uDFDC") && vnorenyKomentar > 0){
                int count = ahoj.length() - ahoj.replaceAll("\uD83C\uDFDC","").length();
                vnorenyKomentar -= count;
            }
        }
        //integers.forEach(System.out::println);
     }
}