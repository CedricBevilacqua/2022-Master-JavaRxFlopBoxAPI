package processors;

/**
 * Représentation d'un client et de ses différentes informations.
 *
 * @author Cédric Bevilacqua
 */
public class Client {
	
	private String identifier;
	private String password;
    
	/**
	 * Constructeur représentant un client précis avec ses caractéristiques.
	 *
	 * @author Cédric Bevilacqua
	 * @param identifier : Identifiant du client
	 * @param password : Mot de passe du client
	 */
    public Client(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
    
    /**
	 * Accesseur GET de l'identifiant de l'utilisateur.
	 *
	 * @author Cédric Bevilacqua
	 * @return Chaine de caractère contenant l'identifiant du client
	 */
    public String getId() {
    	return identifier;
    }

    /**
	 * Vérifie si un mot de passe soumis est valide.
	 *
	 * @author Cédric Bevilacqua
	 * @param password : Mot de passe soumis à vérifier
	 * @return Booleen indiquant si oui ou non le mot de passe est correcte
	 */
	public Boolean checkPassword(String password) {
		if(this.password.equals(password)) {
			return true;
		}
		return false;
	}
    
}
