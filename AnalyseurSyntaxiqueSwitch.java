import java.util.List;

public class AnalyseurSyntaxiqueSwitch {

    private List<Token> tokens;
    private int i;            // index du token courant
    private boolean erreur;

    public AnalyseurSyntaxiqueSwitch(List<Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
        this.erreur = false;
    }

    // --------- utilitaires de base ---------

    private Token courant() {
        if (i < tokens.size()) return tokens.get(i);
        return null;
    }

    private void avancer() {
        if (i < tokens.size()) i++;
    }

    private boolean testLexeme(String lex) {
        Token t = courant();
        return t != null && t.lexeme.equals(lex);
    }

    private boolean testType(String type) {
        Token t = courant();
        return t != null && t.type.equals(type);
    }

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

    

    // --------- point d'entrée ---------

    public void analyserProgramme() {
        while (courant() != null) {
            instruction();
        }

        if (!erreur) {
            System.out.println("\nANALYSE SYNTAXIQUE SWITCH/CASE BIEN FAITE");
        } else {
            System.out.println("\nERREUR(S) SYNTAXIQUE(S) RENCONTREE(S)");
        }
    }

    // --------- instructions ---------

    private void instruction() {
        // si c'est un switch, on l'analyse
        if (testLexeme("switch")) {
            switchInstr();
        } else {
            // sinon : on saute jusqu'à ';' ou '}'
            while (courant() != null &&
                   !testLexeme(";") &&
                   !testLexeme("}")) {
                avancer();
            }
            if (testLexeme(";")) {
                avancer(); // consomme le ;
            }
        }
    }

    // switch (...) { case ... : ... break; ... default: ... }
    private void switchInstr() {
        // 'switch'
        attendreLexeme("switch");
        // '('
        attendreLexeme("(");
        // expression simple
        expression();
        // ')'
        attendreLexeme(")");
        // '{'
        attendreLexeme("{");

        // au moins un case ou un default ou directement '}'
        if (!testLexeme("case") && !testLexeme("default") && !testLexeme("}")) {
            System.out.println("Erreur syntaxique : 'case' ou 'default' attendu dans le switch (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
        }

        // plusieurs case possibles
        while (testLexeme("case")) {
            caseInstr();
        }

        // default optionnel
        if (testLexeme("default")) {
            defaultInstr();
        }

        // '}'
        attendreLexeme("}");
    }

    // case valeur : instructions...
    private void caseInstr() {
        // 'case'
        attendreLexeme("case");

        // valeur : Nombre ou Constante caractere
        if (testType("Nombre") || testType("Constante caractere")) {
            avancer();
        } else {
            System.out.println("Erreur syntaxique : Nombre ou Constante caractere attendu après 'case' (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
        }

        // ':'
        attendreLexeme(":");

        // instructions du case, jusqu'à 'case', 'default' ou '}'
        while (courant() != null &&
               !testLexeme("case") &&
               !testLexeme("default") &&
               !testLexeme("}")) {

            if (testLexeme("break")) {
                avancer();
                if (testLexeme(";")) {
                    avancer();
                } else {
                    System.out.println("Erreur syntaxique : ';' attendu après 'break' (ligne " +
                                       (courant() == null ? "?" : courant().ligne) + ")");
                    erreur = true;
                }
            } else {
                instruction();
            }
        }
    }

    // default : instructions...
    private void defaultInstr() {
        // 'default'
        attendreLexeme("default");
        // ':'
        attendreLexeme(":");

        // instructions jusqu'à '}'
        while (courant() != null &&
               !testLexeme("}")) {

            if (testLexeme("break")) {
                avancer();
                if (testLexeme(";")) {
                    avancer();
                } else {
                    System.out.println("Erreur syntaxique : ';' attendu après 'break' (ligne " +
                                       (courant() == null ? "?" : courant().ligne) + ")");
                    erreur = true;
                }
            } else {
                instruction();
            }
        }
    }

    
    private void expression() {
        if (testType("Identificateur") ||
            testType("Nombre") ||
            testType("Constante caractere")) {
            avancer();
        } else {
            System.out.println("Erreur syntaxique : expression invalide dans 'switch(...)' (ligne " +
                               (courant() == null ? "?" : courant().ligne) + ")");
            erreur = true;
            return;
        }

        while (testType("Operateur")) {
            avancer();
            if (testType("Identificateur") ||
                testType("Nombre") ||
                testType("Constante caractere")) {
                avancer();
            } else {
                System.out.println("Erreur syntaxique : operande attendu après operateur (ligne " +
                                   (courant() == null ? "?" : courant().ligne) + ")");
                erreur = true;
                break;
            }
        }
    }

    

}