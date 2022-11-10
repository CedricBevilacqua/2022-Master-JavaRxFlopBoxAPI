package processors;

/**
 * Représentation d'un serveur FTP enregistré avec ses différentes informations de connexion.
 *
 * @author Cédric Bevilacqua
 */
public class FTPServer {
	
	private String name;
	private String host;
	private int port;
	private Boolean passive;
    
	/**
	 * Constructeur générant une représentation d'un serveur FTP à partir de différentes informatinos.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur
	 * @param host : Adresse du serveur
	 * @param port : Numéro de port du serveur
	 * @param mode : Mode de connexion actif ou passif au serveur
	 */
    public FTPServer(String name, String host, String port, String mode) {
        this.name = name;
        this.host = host;
        this.port = Integer.parseInt(port);
        if(mode.equals("passive")) {
        	this.passive = true;
        } else if(mode.equals("active")) {
        	this.passive = false;
        }
    }
    
    /**
	 * Accesseur GET renvoyant le nom du serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @return Chaine de caractères contenant le nom du serveur
	 */
    public String getName() {
    	return name;
    }
    
    /**
	 * Accesseur GET renvoyant l'adresse du serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @return Chaine de caractères contenant l'adresse du serveur
	 */
    public String getHost() {
    	return host;
    }
    
    /**
	 * Accesseur GET renvoyant le numéro de port du serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @return Entier contenant le numéro de port du serveur
	 */
    public int getPort() {
    	return port;
    }
    
    /**
	 * Accesseur GET renvoyant le mode de connexion au serveur choisi.
	 *
	 * @author Cédric Bevilacqua
	 * @return Booleen contenant vrai si le mode est configuré sur passif et faux si le mode est configuré sur actif
	 */
    public Boolean getMode() {
    	return passive;
    }
    
}
