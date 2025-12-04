import java.util.List;

public class MiniCompilateur {

    public static void main(String[] args) {

        System.out.println("MINI COMPILATEUR ");

      
        String chemin = "C:\\Users\\DELL\\OneDrive\\Desktop\\projet compl\\TestSwitch.java";

        

        System.out.println("\n ANALYSE LEXICALEn");
        AnalyseurLexical.analyser(chemin);

        // Récupération des tokens
        List<Token> tokens = AnalyseurLexical.getTokens();

        
        System.out.println("\n ANALYSE SYNTAXIQUE \n");
        AnalyseurSyntaxiqueSwitch a = new AnalyseurSyntaxiqueSwitch(tokens);
        a.analyserProgramme();

        System.out.println("\n FIN DU MINI COMPILATEUR ");
    }
}
