

public class TestSwitch   {

    public static void main(String[] args) {
        int x = 2;
        boolean test = true;

        switch () {
            case 0:
                System.out.println("zero");
                test = false;
                break;
            case 1:
                System.out.println("un");
                break;
            case 2:
                System.out.println("deux");
                break
            default:
                System.out.println("autre");
                break;
        }

        System.out.println("fin");
    }
}
