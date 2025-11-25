public class Token {
    public String lexeme;   
    public String type;    
    public int ligne;      
    public Token(String lexeme, String type, int ligne) {
        this.lexeme = lexeme;
        this.type = type;
        this.ligne = ligne;
    }

    @Override
    public String toString() {
        return lexeme + " [" + type + "] (ligne " + ligne + ")";
    }
}
