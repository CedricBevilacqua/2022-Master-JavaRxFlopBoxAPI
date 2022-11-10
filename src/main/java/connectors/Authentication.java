package connectors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import processors.Client;

/**
 * Classe de gestion de toutes les commandes API qui ont un rapport avec la manipulation des comptes clients.
 *
 * @author Cédric Bevilacqua
 */
@Path("auths")
public class Authentication {
	
	/**
	 * Gère les requêtes pour lister les comptes enregistrés sur l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @return Chaine de caractère contenant un Json de réponse qui est un tableau des identifiants enregistrés
	 */
    @GET
    @Produces("application/json")
    public String list() {
    	String answer = "";
    	for (String id : holders.ClientHolder.ListClients()) {
    		answer += ", \"" + id + "\"";
    	}
    	if(answer.length() > 0) {
    		answer = answer.substring(1);
    	}
        return "{\"clientsIds\":[" + answer + "]}";
    }
    
    /**
	 * Gère les requêtes de création de nouveaux comptes clients sur l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param body : Corps de la requête contenant les informations dont l'identifiant et le mot de passe du nouveau  compte
	 * @return Chaine de caractère contenant un Json de réponse qui est une réponse de succès ou non
	 */
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String create(MultivaluedMap<String, String> body) {
    	processors.Client newClient = new processors.Client(body.getFirst("username"), body.getFirst("password"));
    	Boolean success = true;
    	try {
			holders.ClientHolder.AddClient(newClient);
			
		} catch (Exception e) {
			success = false;
		}
    	return "{\"success\":\"" + success + "\"}";
    }
    
    /**
	 * Gère la mise à jour des informations d'identification d'un utilisateur sur l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param body : Corps de la requête contenant les informations dont l'ancien et nouveaux identifiants et mots de passe
	 * @return Chaine de caractère contenant un Json de réponse qui est une réponse de succès ou non
	 */
    @PUT
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change(MultivaluedMap<String, String> body) throws Exception {
    	Boolean success = false;
    	Client client = holders.ClientHolder.GetClient(body.getFirst("username"));
    	if(client != null && client.checkPassword(body.getFirst("password"))) {
    		holders.ClientHolder.RemoveClient(body.getFirst("username"), body.getFirst("password"));
    		processors.Client newClient = new processors.Client(body.getFirst("newUsername"), body.getFirst("newPassword"));
        	try {
    			holders.ClientHolder.AddClient(newClient);
    			success = true;
    			
    		} catch (Exception e) {
    			processors.Client oldClient = new processors.Client(body.getFirst("username"), body.getFirst("password"));
    			holders.ClientHolder.AddClient(oldClient);
    		}
    	}
    	return "{\"success\":\"" + success + "\"}";
    }
    
    /**
	 * Gère la suppression d'un compte client sur l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Nom d'utilisateur du compte à supprimer
	 * @param password : Mot de passe du compte à supprimer pour valider l'opération
	 * mettre à jour le nombre de branches de l'arbre à chaque ligne
	 * @return Chaine de caractère contenant un Json de réponse qui est une réponse de susccès ou non
	 */
    @DELETE
    @Produces("application/json")
    public String remove(@HeaderParam("username") String username, @HeaderParam("password") String password) {
    	Boolean success = false;
    	if(holders.ClientHolder.RemoveClient(username, password)) {
    		success = true;
    	}
    	return "{\"success\":\"" + success + "\"}";
    }
}
