public class Cesta {
    private AMisto mistoA;
    private AMisto mistoB;

    public Cesta(AMisto mistoA, AMisto mistoB){
        this.mistoA = mistoA;
        this.mistoB = mistoB;
    }

    public AMisto getMistoA() {
        return mistoA;
    }

    public AMisto getMistoB() {
        return mistoB;
    }
}
