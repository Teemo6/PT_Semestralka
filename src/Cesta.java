public class Cesta {
    private AMisto mistoA;
    private AMisto mistoB;

    public Cesta(AMisto mistoA, AMisto mistoB){
        this.mistoA = mistoA;
        this.mistoB = mistoB;
    }

    public double vypoctiVzdalenost(){
        return mistoA.getPozice().computeDistance(mistoB.getPozice());
    }

    public AMisto getMistoA() {
        return mistoA;
    }

    public AMisto getMistoB() {
        return mistoB;
    }

    @Override
    public String toString() {
        return "Cesta{" +
                "mistoA=" + mistoA +
                ", mistoB=" + mistoB +
                '}';
    }
}
