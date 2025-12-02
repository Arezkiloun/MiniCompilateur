import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyseurLexical {

    private static boolean erreur = false;

    // Liste de tous les tokens trouvés
    private static List<Token> tokens = new ArrayList<>();

    
    private static int numLigne = 1;

 
    //  TABLEAUX 

    // Mots-clés 
    private static String[] MOTS_CLES = {
       
        "int", "char", "float", "double", "boolean", "void",
      
        "class", "public", "private", "protected", "static", "return","system","out","println",
       
        "switch", "case", "break", "default",
       
        "if", "else", "while", "for", "do", "true", "false",
        
        "arezki" ,"lounis"
    };

    // Opérateurs 
    private static String[] OPERATEURS = {
        "+", "-", "*", "/", "%", "=", "==",
        "<", ">", "<=", ">=", "!=",
        "++", "--"
    };

    // Séparateurs
    private static String[] SEPARATEURS = {
        "(", ")", "{", "}", ";", ",", ":", ".","[","]"
    };

    // Chiffres
    private static char[] CHIFFRES = {
        '0','1','2','3','4','5','6','7','8','9'
    };

    // Lettres
    private static char[] LETTRES = {
        'a','b','c','d','e','f','g','h','i','j',
        'k','l','m','n','o','p','q','r','s','t',
        'u','v','w','x','y','z',
        'A','B','C','D','E','F','G','H','I','J',
        'K','L','M','N','O','P','Q','R','S','T',
        'U','V','W','X','Y','Z'
    };

     //  ANALYSEUR LEXICAL

    public static void analyser(String chemin) {
       
    tokens.clear();
    erreur = false;
    numLigne = 1;   // <-- réinitialiser le numéro de ligne

    try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {

        int car;
        String lexeme = "";
        boolean ignoreCommentaireLigne = false;
        boolean ignoreCommentaireBloc = false;

        while ((car = br.read()) != -1) {
            char c = (char) car;

            // --------- début commentaires ---------
            if (!ignoreCommentaireLigne && !ignoreCommentaireBloc && c == '/') {
                br.mark(1);
                int next = br.read();
                if (next == '/') {             
                    ignoreCommentaireLigne = true;
                    continue;
                } else if (next == '*') {       
                    ignoreCommentaireBloc = true;
                    continue;
                } else {
                    br.reset();
                }
            }

            // commentaire ligne
            if (ignoreCommentaireLigne) {
                if (c == '\n') {
                    numLigne++;             // <--- compte ligne commentaire
                    ignoreCommentaireLigne = false;
                }
                continue;
            }

            // commentaire bloc
            if (ignoreCommentaireBloc) {
                if (c == '\n') {
                    numLigne++;             // <--- compte ligne commentaire bloc
                }
                if (c == '*') {
                    br.mark(1);
                    int next = br.read();
                    if (next == '/') {
                        ignoreCommentaireBloc = false;
                    } else {
                        br.reset();
                    }
                }
                continue;
            }

            // chaîne 
            if (c == '"') {
                if (lexeme.length() > 0) {
                    afficherLexeme(lexeme);
                    lexeme = "";
                }
                String chaine = "\"";
                while ((car = br.read()) != -1) {
                    char next = (char) car;
                    if (next == '\n') numLigne++;
                    chaine = chaine + next;
                    if (next == '"') break;
                }
                afficherLexeme(chaine);
                continue;
            }

            //  caractère 
            if (c == '\'') {
                if (lexeme.length() > 0) {
                    afficherLexeme(lexeme);
                    lexeme = "";
                }
                String ch = "'";
                int c1 = br.read();
                int c2 = br.read();
                if (c1 != -1) ch = ch + (char)c1;
                if (c2 != -1) ch = ch + (char)c2;
                afficherLexeme(ch);
                continue;
            }

            // espaces = fin de lexème 
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                if (c == '\n') {
                    numLigne++;             // <--- compte ligne normale
                }
                if (lexeme.length() > 0) {
                    afficherLexeme(lexeme);
                    lexeme = "";
                }
                continue;
            }

            // opérateurs 
            if (c == '=' || c == '<' || c == '>' || c == '!' || c == '+' || c == '-') {
                br.mark(1);
                int next = br.read();
                String op2;

                if (next != -1) {
                    op2 = "" + c + (char) next;
                } else {
                    op2 = "" + c;
                }

                if (operateur(op2)) {
                    if (lexeme.length() > 0) {
                        afficherLexeme(lexeme);
                        lexeme = "";
                    }
                    afficherLexeme(op2);
                    continue;
                } else {
                    if (next != -1) {
                        br.reset();
                    }
                }
            }

            //  séparateur 
            String s = "" + c;
            if (separateur(s)) {
                if (lexeme.length() > 0) {
                    afficherLexeme(lexeme);
                    lexeme = "";
                }
                afficherLexeme(s);
                continue;
            }

            if (operateur(s)) {
                if (lexeme.length() > 0) {
                    afficherLexeme(lexeme);
                    lexeme = "";
                }
                afficherLexeme(s);
                continue;
            }

            lexeme = lexeme + c;
        }

        // dernier lexème
        if (lexeme.length() > 0) {
            afficherLexeme(lexeme);
        }

    } catch (IOException e) {
        System.out.println("Erreur lecture fichier : " + e.getMessage());
    }

    if (erreur)
        System.out.println("\nERREUR LEXICALE RENCONTREE");
    else
        System.out.println("\nANALYSE LEXICALE BIEN FAITE");
}

    

    // Comparer deux chaînes 
    private static boolean egal(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;

        int i = 0;
        while (i < a.length()) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    // Chercher une chaîne dans un tableau
    private static boolean dansTableau(String lex, String[] tab) {
        int i = 0;
        while (i < tab.length) {
            if (egal(lex, tab[i])) {
                return true;
            }
            i = i + 1;
        }
        return false;
    }

    // Chercher un caractère dans un tableau
    private static boolean dansTableauChar(char c, char[] tab) {
        int i = 0;
        while (i < tab.length) {
            if (c == tab[i]) {
                return true;
            }
            i = i + 1;
        }
        return false;
    }

    
    //  FONCTIONS LEXICALES
    

    public static boolean motcle(String lex) {
        return dansTableau(lex, MOTS_CLES);
    }

    public static boolean operateur(String lex) {
        return dansTableau(lex, OPERATEURS);
    }

    public static boolean separateur(String lex) {
        return dansTableau(lex, SEPARATEURS);
    }

    private static boolean chiffre(char c) {
        return dansTableauChar(c, CHIFFRES);
    }

    private static boolean lettre(char c) {
        return dansTableauChar(c, LETTRES);
    }

    // nombre 
    public static boolean nombre(String lex) {
        if (lex == null) return false;
        if (lex.length() == 0) return false;

        int i = 0;
        while (i < lex.length()) {
            char c = lex.charAt(i);
            if (!chiffre(c)) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    // identificateur 
    public static boolean identificateur(String lex) {
        if (lex == null) return false;
        if (lex.length() == 0) return false;

        char c0 = lex.charAt(0);
        if (!(lettre(c0) || c0 == '_')) {
            return false;
        }

        int i = 1;
        while (i < lex.length()) {
            char c = lex.charAt(i);
            if (!(lettre(c) || chiffre(c) || c == '_')) {
                return false;
            }
            i = i + 1;
        }

        // mot-clé ≠ identificateur
        if (motcle(lex)) {
            return false;
        }

        return true;
    }

    // constante caractère 
    public static boolean caractere(String lex) {
        if (lex == null) return false;
        if (lex.length() != 3) return false;
        if (lex.charAt(0) == '\'' && lex.charAt(2) == '\'') {
            return true;
        }
        return false;
    }

    // chaîne de caractères 
    public static boolean chaine(String lex) {
        if (lex == null || lex.length() < 2) return false;
        return (lex.charAt(0) == '"' && lex.charAt(lex.length() - 1) == '"');
    }

   
   
    
    

    //  AFFICHAGE D'UN LEXÈME


    private static void afficherLexeme(String lex) {
        if (lex == null || lex.length() == 0) return;

        String type;

        if (motcle(lex))
            type = "Mot-cle";
        else if (nombre(lex))
            type = "Nombre";
        else if (identificateur(lex))
            type = "Identificateur";
        else if (caractere(lex))
            type = "Constante caractere";
        else if (chaine(lex))
            type = "Chaine";
        else if (operateur(lex))
            type = "Operateur";
        else if (separateur(lex))
            type = "Separateur";
        else {
            type = "ERREUR";
            System.out.println(lex + " --> ERREUR LEXICALE");
            erreur = true;
        }

        if (!type.equals("ERREUR")) {
            System.out.println(lex + " --> " + type);
        }

        tokens.add(new Token(lex, type, numLigne));
    }

    
    //  ACCES AUX TOKENS
   

    public static List<Token> getTokens() {
        return tokens;
    }


    
}
