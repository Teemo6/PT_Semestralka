public class Pozadavek implements Comparable<Pozadavek>{
    private final double casPrichodu;
    private final AMisto oaza;
    private final int pozadavekKosu;
    private final double deadline;
    private boolean jeSplnen;

    protected static int pocet;
    protected int ID;

    public Pozadavek(double casPrichodu, AMisto oaza, int pozadavekKosu, double deadline){
        this.casPrichodu = casPrichodu;
        this.oaza = oaza;
        this.pozadavekKosu = pozadavekKosu;
        this.deadline = deadline;

        jeSplnen = false;

        pocet++;
        this.ID = pocet;
    }

    public double getCasPrichodu() {
        return casPrichodu;
    }

    public int getPozadavekKosu() {
        return pozadavekKosu;
    }

    public double getDeadline() {
        return deadline;
    }

    public AMisto getOaza() {
        return oaza;
    }

    public int getID() {
        return ID;
    }

    public void setJeSplnen(boolean jeSplnen) {
        this.jeSplnen = jeSplnen;
    }

    public boolean jeSplnen() {
        return jeSplnen;
    }



    /**
     * Porovnává časy požadavků
     * Primárně podle příchozího času
     * Sekundárně podle Deadline
     * @param p pozadavek
     * @return číslo na porovnání
     */
    @Override
    public int compareTo(Pozadavek p) {
        int thisCasPrichodu = (int)this.getCasPrichodu();
        int thatCasPrichodu = (int)p.getCasPrichodu();
        int thisDeadline = (int)this.getDeadline();
        int thatDeadline = (int)p.getDeadline();

        if (thisCasPrichodu == thatCasPrichodu) {
            return thisDeadline - thatDeadline;
        }
        else {
            return thisCasPrichodu - thatCasPrichodu;
        }
    }
}
