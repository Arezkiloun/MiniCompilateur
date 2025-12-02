// Représente un lexème du programme
public class Token {
    // Texte exactement comme dans fichier
    public String lexeme;
    // Type lexical du lexème
    public String type;
    // Numéro de ligne du lexème
    public int ligne;

    // Création d un nouveau token
    public Token(String lexeme, String type, int ligne) {
        this.lexeme = lexeme;
        this.type = type;
        this.ligne = ligne;
    }

    // Affichage lisible d un token
    public String toString() {
        return lexeme + " [" + type + "] (ligne " + ligne + ")";
    }
}
