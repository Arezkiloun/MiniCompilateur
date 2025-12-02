import java.util.List;

// Analyse syntaxique pour mini compilateur
public class AnalyseurSyntaxiqueSwitch {

    // Liste des tokens du programme
    private List<Token> tokens;
    // Position actuelle dans la liste
    private int index;
    // Vrai si erreur syntaxique trouvée
    private boolean erreur;

    // Constructeur de l analyseur syntaxique
    public AnalyseurSyntaxiqueSwitch(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.erreur = false;
    }

    // Donne le token courant
    private Token courant() {
        if (index < tokens.size()) return tokens.get(index);
        return null;
    }

    // Passe au token suivant
    private void avancer() {
        if (index < tokens.size()) index++;
    }

    // Teste un lexème précis
    private boolean testLexeme(String lex) {
        Token t = courant();
        return t != null && t.lexeme.equals(lex);
    }

    // Teste un type précis
    private boolean testType(String type) {
        Token t = courant();
        return t != null && t.type.equals(type);
    }

    // Consomme un lexème attendu
    private void attendreLexeme(String lex) {
        Token t = courant();
        if (t == null || !t.lexeme.equals(lex)) {
            System.out.println("Erreur syntaxique : '" + lex + "' attendu, trouvé " +
                               (t == null ? "EOF" : "'" + t.lexeme + "'") +
                               " (ligne " + (t == null ? "?" : t.ligne) + ")");
            erreur = true;
        } else {
            avancer();
        }
    }

    // Consomme un type attendu
    private void attendreType(String type, String role) {
        Token t = courant();
        if (t == null || !t.type.equals(type)) {
            System.out.println("Erreur syntaxique : " + role + " (" + type + ") attendu, trouvé " +
                               (t == null ? "EOF" : "'" + t.lexeme + "'") +
                               " (ligne " + (t == null ? "?" : t.ligne) + ")");
            erreur = true;
        } else {
            avancer();
        }
    }

    // Test pour type simple
    private boolean typeSimple() {
        return testLexeme("int") || testLexeme("char")
            || testLexeme("float") || testLexeme("double")
            || testLexeme("boolean");
    }

    // Test début déclaration classe
    private boolean debutClasse() {
        if (testLexeme("class")) return true;
        if (testLexeme("public") || testLexeme("private") || testLexeme("protected")) {
            if (index + 1 < tokens.size()) {
                Token t2 = tokens.get(index + 1);
                return t2.lexeme.equals("class");
            }
        }
        return false;
    }

    // Test début déclaration méthode
    private boolean debutMethode() {
        if (testLexeme("public") || testLexeme("private") || testLexeme("protected")
            || typeSimple() || testLexeme("void")) {
            int j = index;
            int limite = Math.min(tokens.size(), index + 5);
            while (j < limite) {
                String lex = tokens.get(j).lexeme;
                if (lex.equals("(")) return true;
                if (lex.equals("{") || lex.equals(";")) break;
                j++;
            }
        }
        return false;
    }

    // Test début déclaration variable ou affectation
    private boolean debutDeclOuAffect() {
        if (typeSimple()) return true;
        if (testType("Identificateur")) return true;
        return false;
    }

    // Point d entrée de l analyse
    public void analyserProgramme() {

        while (courant() != null) {
            declarationOuInstruction();
        }

        if (!erreur)
            System.out.println("\nANALYSE SYNTAXIQUE SWITCH/CASE BIEN FAITE");
        else
            System.out.println("\nERREUR(S) SYNTAXIQUE(S) RENCONTREE(S)");
    }

    // Choix entre déclaration ou instruction
    private void declarationOuInstruction() {
        if (debutClasse()) {
            declarationClasse();
        } else if (debutMethode()) {
            declarationMethode();
        } else if (debutDeclOuAffect()) {
            declarationOuAffectation();
        } else {
            instruction();
        }
    }

    // Déclaration de classe simplifiée
    private void declarationClasse() {
        if (testLexeme("public") || testLexeme("private") || testLexeme("protected")) {
            avancer();
        }

        attendreLexeme("class");
        attendreType("Identificateur", "nom de classe");
        attendreLexeme("{");

        while (courant() != null && !testLexeme("}")) {
            declarationOuInstruction();
        }

        attendreLexeme("}");
    }

    // Déclaration méthode simplifiée
    private void declarationMethode() {
        if (testLexeme("public") || testLexeme("private") || testLexeme("protected")) {
            avancer();
        }
        if (testLexeme("static")) {
            avancer();
        }

        if (typeSimple() || testLexeme("void")) {
            avancer();
        } else {
            System.out.println("Erreur syntaxique : type retour ou void attendu (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
        }

        attendreType("Identificateur", "nom de methode");

        attendreLexeme("(");
        while (courant() != null && !testLexeme(")")) {
            avancer();
        }
        attendreLexeme(")");

        attendreLexeme("{");
        while (courant() != null && !testLexeme("}")) {
            declarationOuInstruction();
        }
        attendreLexeme("}");
    }

    // Déclaration variable ou affectation
    private void declarationOuAffectation() {

        if (typeSimple()) {
            avancer();
            attendreType("Identificateur", "nom variable");
            if (testLexeme("=")) {
                avancer();
                expression();
            }
            attendreLexeme(";");
            return;
        }

        if (testType("Identificateur")) {
            avancer();

            if (testLexeme("=")) {
                avancer();
                expression();
                attendreLexeme(";");
            } else {
                while (courant() != null && !testLexeme(";") && !testLexeme("}")) {
                    avancer();
                }
                if (testLexeme(";")) avancer();
            }
        }
    }

    // Instruction générale
    private void instruction() {
        if (testLexeme("switch")) {
            switchInstr();
        } else {
            while (courant() != null && !testLexeme(";") && !testLexeme("}")) {
                avancer();
            }
            if (testLexeme(";")) avancer();
        }
    }

    // Analyse d un switch complet
    private void switchInstr() {
        attendreLexeme("switch");
        attendreLexeme("(");
        expression();
        attendreLexeme(")");
        attendreLexeme("{");

        if (!testLexeme("case") && !testLexeme("default") && !testLexeme("}")) {
            System.out.println("Erreur syntaxique : case ou default attendu (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
        }

        while (testLexeme("case")) {
            caseInstr();
        }

        if (testLexeme("default")) {
            defaultInstr();
        }

        attendreLexeme("}");
    }

    // Analyse d un bloc case
    private void caseInstr() {
        attendreLexeme("case");

        if (testType("Nombre") || testType("Constante caractere")) {
            avancer();
        } else {
            System.out.println("Erreur syntaxique : valeur case incorrecte (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
        }

        attendreLexeme(":");

        while (courant() != null &&
               !testLexeme("case") &&
               !testLexeme("default") &&
               !testLexeme("}")) {

            if (testLexeme("break")) {
                avancer();
                if (testLexeme(";")) {
                    avancer();
                } else {
                    System.out.println("Erreur syntaxique : ; attendu après break (ligne " +
                                       (courant() == null ? "?" : courant().ligne) + ")");
                    erreur = true;
                }
            } else {
                declarationOuInstruction();
            }
        }
    }
    

    // Analyse du bloc default
    private void defaultInstr() {
        attendreLexeme("default");
        attendreLexeme(":");

        while (courant() != null && !testLexeme("}")) {
            if (testLexeme("break")) {
                avancer();
                if (testLexeme(";")) {
                    avancer();
                } else {
                    System.out.println("Erreur syntaxique : ; attendu après break (ligne " +
                                       (courant() == null ? "?" : courant().ligne) + ")");
                    erreur = true;
                }
            } else {
                declarationOuInstruction();
            }
        }
    }
     // Test mot clé booleen
    private boolean motBooleen() {
        Token t = courant();
        if (t == null) return false;
        // true ou false de type Mot-cle
        return t.type.equals("Mot-cle") &&
               (t.lexeme.equals("true") || t.lexeme.equals("false"));
    }

    // Analyse expression simple gauche droite
private void expression() {
    // premier operande
    if (testType("Identificateur") ||
        testType("Nombre") ||
        testType("Constante caractere") ||
        motBooleen()) {
        avancer();
    } else {
        System.out.println("Erreur syntaxique : expression invalide (ligne " +
                           (courant() == null ? "?" : courant().ligne) + ")");
        erreur = true;
        return;
    }

    // operateur puis autre operande
    while (testType("Operateur")) {
        avancer();
        if (testType("Identificateur") ||
            testType("Nombre") ||
            testType("Constante caractere") ||
            motBooleen()) {
            avancer();
        } else {
            System.out.println("Erreur syntaxique : operande manquant après operateur (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
            break;
        }
    }
}

}
