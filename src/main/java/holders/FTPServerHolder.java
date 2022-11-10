package holders;

import java.util.ArrayList;
import java.util.List;

import processors.FTPServer;

/**
 * Classe permettant de stocker, manipuler et accéder de différentes manières aux serveurs FTP déclarés sur l'API.
 *
 * @author Cédric Bevilacqua
 */
public class FTPServerHolder {
	
	private static List<FTPServer> servers = new ArrayList<>();
	
	/**
	 * Déclare un nouveau serveur sur l'API et l'enregistre.
	 *
	 * @author Cédric Bevilacqua
	 * @param newServer : Représentation du nouveau serveur à enregistrer
	 */
	public static void AddServer(FTPServer newServer) throws Exception {
		if(SearchName(newServer.getName())) {
			throw new Exception();
		}
		servers.add(newServer);
	}
	
	/**
	 * Liste les serveurs enregistrés sur l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @return Liste de chaine de caractères des seveurs enregistrés
	 */
	public static List<String> ListServers() {
		List<String> serversName = new ArrayList<>();
		for (FTPServer server : servers) {
			serversName.add(server.getName());
		}
		return serversName;
	}
	
	/**
	 * Supprime un serveur de l'API à partir de son nom.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serceur à supprimer
	 * @return Booleen indiquant si le serveur a pu être supprimé ou pas
	 */
	public static Boolean RemoveServer(String name) {
		for (int i = 0; i < servers.size(); i++) {
			if(servers.get(i).getName().equals(name)) {
				servers.remove(i);
				return true;
			}
	    }
		return false;
	}
	
	/**
	 * Récupère un serveur à partir de son nom.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur à récupérer
	 * @return Représentation du serveur correspondant
	 */
	public static FTPServer GetServer(String name) {
		for (FTPServer server : servers) {
			if(server.getName().equals(name)) {
				return server;
			}
	    }
		return null;
	}
    
	/**
	 * Vérifie l'existence d'un serveur à partir de son nom.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur à rechercher
	 * @return Booleen indiquant si le serveur existe ou non dans la liste des serveurs déclarés sur l'API
	 */
	private static Boolean SearchName(String name) {
		for (FTPServer server : servers) {
			if(server.getName().equals(name)) {
				return true;
			}
	    }
		return false;
	}
	
}
