import java.util.HashSet;

// Pokus na optimalizaci matice

public class MaticeSparse {
    int velikost;
    double [][]obsahMatice;

    public MaticeSparse(int n){
        velikost = n;
    }

    public void vyplnMaticiSousednosti(HashSet<Cesta> cesty){
        int pocetCest = cesty.size();
        int ukazovatko = 0;

        obsahMatice = new double[3][pocetCest];

        for(Cesta c : cesty){
            obsahMatice[0][ukazovatko] = c.getMistoA().getID();
            obsahMatice[1][ukazovatko] = c.getMistoB().getID();
            obsahMatice[2][ukazovatko] = c.getVzdalenost();
            ukazovatko++;
        }
    }

    public int getVelikost() {
        return velikost;
    }

    public double[][] getObsahMatice() {
        return obsahMatice;
    }

    public void printMatice(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < velikost; j++) {
                double cislo = obsahMatice[i][j];
                if (cislo == Double.MAX_VALUE) {
                    System.out.print("INF");
                } else {
                    System.out.print(cislo);
                }
                if (j != velikost - 1) {
                    System.out.print(" - ");
                }
            }
            System.out.println();
        }
    }
}
