public class TestSwitch {

    public static void main(String[] args) {

        int x = 4;
        boolean actif = true;
        int resultat = 0;

        x = x + 1;
        actif = false;

        switch (x) {
            case 1:
                System.out.println("un");
                resultat = 10;
                break;

            case 2:
                System.out.println("deux");
                resultat = 20;
                actif = true;
                break;

            case 3:
                System.out.println("trois");
                break;

            case 5:
                System.out.println("cinq");
                resultat = 50;
                break;

            default:
                System.out.println("valeur inconnue");
                break;
        }

        System.out.println("Résultat final = " + resultat);
    }
}
