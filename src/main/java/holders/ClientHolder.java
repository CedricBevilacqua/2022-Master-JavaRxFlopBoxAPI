package holders;

import java.util.ArrayList;
import java.util.List;

import processors.Client;

/**
 * Classe permettant de stocker tous les clients enregistrés sur l'API et de les gérer en les manipulant de différentes manières.
 *
 * @author Cédric Bevilacqua
 */
public class ClientHolder {
	
	private static List<Client> clients = new ArrayList<>();
	
	/**
	 * Ajoute des clients à la liste des clients enregistrés dans l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param newClient : Nouvelle représentation d'un client à ajouter
	 */
	public static void AddClient(Client newClient) throws Exception {
		if(SearchId(newClient.getId())) {
			throw new Exception();
		}
		clients.add(newClient);
	}
	
	/**
	 * Liste les clients enregistrés dans l'API par leur identifiant.
	 *
	 * @author Cédric Bevilacqua
	 * @return Tableau de chaines de caractères de l'identifiant de chaque client
	 */
	public static List<String> ListClients() {
		List<String> clientsId = new ArrayList<>();
		for (Client client : clients) {
			clientsId.add(client.getId());
		}
		return clientsId;
	}
	
	/**
	 * Retrouve un client à partir de son identifiant et le renvoit.
	 *
	 * @author Cédric Bevilacqua
	 * @param id : Identifiant du client à récupérer
	 * @return Représentation du client recherché, renvoit NULL si aucun client ne correspond
	 */
	public static Client GetClient(String id) {
		for (Client client : clients) {
			if(client.getId().equals(id)) {
				return client;
			}
	    }
		return null;
	}
	
	/**
	 * Supprime un client de la liste des clients enregistrés dans l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Identifiant du client à supprimer
	 * @param password : Mot de passe du client à supprimer pour valider sa suppression
	 * @return Booleen contenant vrai ou faux selon si le client a été retrouvé et supprimé ou non
	 */
	public static Boolean RemoveClient(String username, String password) {
		for (int i = 0; i < clients.size(); i++) {
			if(clients.get(i).getId().equals(username) && clients.get(i).checkPassword(password)) {
				clients.remove(i);
				return true;
			}
	    }
		return false;
	}
	
	/**
	 * Vérifie si les identifiants correspondent à un client enregistré et permet de valider une connexion.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Identifiant du client
	 * @param password : Mot de passe du client
	 * @return Booleen indiquant si les identifiants donnés correspondent ou non à un clinet enregistré
	 */
	public static boolean Authenticate(String username, String password) {
		for (int i = 0; i < clients.size(); i++) {
			if(clients.get(i).getId().equals(username) && clients.get(i).checkPassword(password)) {
				return true;
			}
	    }
		return false;
	}
	
	/**
	 * Vérifie si un client est présent dans la liste des clients enregistrés dans l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param id : Identifiant du client à rechercher
	 * @return Booleen valant vrai ou faux selon si le client existe ou non dans la liste des clients enregistrés dans l'API
	 */
	private static Boolean SearchId(String id) {
		for (Client client : clients) {
			if(client.getId().equals(id)) {
				return true;
			}
	    }
		return false;
	}
    
}
