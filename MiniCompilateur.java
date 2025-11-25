import java.util.List;

public class MiniCompilateur {

    public static void main(String[] args) {

        System.out.println("=== MINI COMPILATEUR (Lexical + Syntaxique) ===");

      
        String chemin = "C:\\Users\\DELL\\OneDrive\\Desktop\\projet compl\\TestSwitch.java";

        

        System.out.println("\n=== ETAPE 1 : ANALYSE LEXICALE ===\n");
        AnalyseurLexical.analyser(chemin);

        // Récupération des tokens
        List<Token> tokens = AnalyseurLexical.getTokens();

        
        System.out.println("\n=== ETAPE 2 : ANALYSE SYNTAXIQUE (switch/case) ===\n");
        AnalyseurSyntaxiqueSwitch a = new AnalyseurSyntaxiqueSwitch(tokens);
        a.analyserProgramme();

        System.out.println("\n=== FIN DU MINI COMPILATEUR ===");
    }
}
