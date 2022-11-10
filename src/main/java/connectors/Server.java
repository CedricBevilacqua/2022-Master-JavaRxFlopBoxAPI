package connectors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import holders.ClientHolder;
import holders.FTPServerHolder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import processors.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Classe de gestion de toutes les commandes API qui ont un rapport avec les serveurs FTP.
 *
 * @author Cédric Bevilacqua
 */
@Path("servers")
public class Server {
	
	/**
	 * Traite les requêtes de récupération de la liste des fichiers accessibles à la racine du serveur FTP
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json contenant la liste des éléments directement accessibles avec des méta données sur chacun d'eux et un lien hypermédia
	 */
	@GET
    @Path("{name}")
	@Produces("application/json")
    public String infoServer(@PathParam("name") String name, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @HeaderParam("username") String username, @HeaderParam("password") String password) {
		if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
		FTPServer attributedServer = FTPServerHolder.GetServer(name);
		List<FTPElement> ftpElements;
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpElements = ftpConnection.List("/");
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"message\":\"Error while communicating to the FTP server, test the host, credentials and the link\"}";
		}
		String answer = "";
		for(FTPElement ftpElement : ftpElements) {
			answer += "{\"name\":" + "\"" + ftpElement.GetName() + "\", ";
			answer += "\"type\":" + "\"" + ftpElement.GetType() + "\", ";
			answer += "\"size\":" + "\"" + ftpElement.GetSize() + "\", ";
			answer += "\"date\":" + "\"" + ftpElement.GetDate() + "\", ";
			answer += "\"href\":" + "\"/servers/" + name + "/" + ftpElement.GetName() + "\"}, ";
		}
		if(answer.length() > 0) {
			answer = answer.substring(0, answer.length() - 2);
		}
        return "[" + answer + "]";
    }
	
	/**
	 * Traite les requêtes de récupération de la liste des fichiers stockés à un emplacement précis sur un serveur FTP
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param path : Adresse à partir de laquelle afficher la liste des éléments directement accessibles
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json contenant la liste des éléments directement accessibles avec des méta données sur chacun d'eux et un lien hypermédia
	 */
    @GET
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    public String listFiles(@PathParam("name") String name, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
		FTPServer attributedServer = FTPServerHolder.GetServer(name);
		List<FTPElement> ftpElements;
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpElements = ftpConnection.List("/" + path);
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"message\":\"Error while communicating to the FTP server, test the host, credentials and the link\"}";
		}
		String answer = "";
		for(FTPElement ftpElement : ftpElements) {
			answer += "{\"name\":" + "\"" + ftpElement.GetName() + "\", ";
			answer += "\"type\":" + "\"" + ftpElement.GetType() + "\", ";
			answer += "\"size\":" + "\"" + ftpElement.GetSize() + "\", ";
			answer += "\"date\":" + "\"" + ftpElement.GetDate() + "\", ";
			answer += "\"href\":" + "\"/servers/" + name + "/" + path + "/" + ftpElement.GetName() + "\"}, ";
		}
		if(answer.length() > 0) {
			answer = answer.substring(0, answer.length() - 2);
		}
        return "[" + answer + "]";
    }
    
    /**
	 * Traite les requêtes de récupération d'un fichier sur un serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur sur lquel effectuer l'opération
	 * @param path : Chemin du fichier à télécharger
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Flux d'octets du fichier récupéré
	 */
    @GET
    @Path("download/{name}/{path: .*}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("name") String name, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return Response.noContent().build();
    	}
    	File file = new File("tempCache");
    	FTPServer attributedServer = FTPServerHolder.GetServer(name);
    	try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpConnection.GetFile("/" + path);
			ftpConnection.Disconnect();
		} catch (Exception e) {}
    	return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
    			.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
    			.build();
    }
    
    /**
	 * Traite les requêtes de création d'un nouveau dossier sur un serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param body : Corps de la requête, n'est pas utilisé dans ce cas
	 * @param path : Chemin du dossier à créer
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json de succès ou non
	 */
    @POST
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createFolder(MultivaluedMap<String, String> body, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @PathParam("name") String name, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
		FTPServer attributedServer = FTPServerHolder.GetServer(name);
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpConnection.NewFolder(path);
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"success\":\"" + "false" + "\"}";
		}
		return "{\"success\":\"" + "true" + "\", \"href\":\"" + path + "\"}";
    }
    
    /**
	 * Traite les requêtes de transfert de texte dans un fichier sur un serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param content : Contenu texte à insérer dans la nouvelle ressource créée
	 * @param path : Chemin de la ressource à ajouter
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json de succès ou non
	 */
    @POST
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    @Consumes({MediaType.TEXT_PLAIN})
    public String addTextFile(String content, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @PathParam("name") String name, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
		FTPServer attributedServer = FTPServerHolder.GetServer(name);
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpConnection.SendTextFile(content, path);
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"success\":\"" + "false" + "\"}";
		}
		return "{\"success\":\"" + "true" + "\"}";
    }
    
    /**
	 * Traite les requêtes de transfert d'un fichier sur un serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param inputFile : Stream du fichier d'entrée à envoyer sur le serveur
	 * @param fileMetaData : Méta données du fichier d'entrée, n'est pas utilisé dans le cas présent
	 * @param path : Chemin de la ressource à ajouter
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json de succès ou non
	 */
    @POST
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public String addFile(@FormDataParam("file") InputStream inputFile, @FormDataParam("file") FormDataContentDisposition fileMetaData, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @PathParam("name") String name, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
		FTPServer attributedServer = FTPServerHolder.GetServer(name);
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpConnection.SendBinaryFile(inputFile, path);
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"success\":\"" + "false" + "\"}";
		}
		return "{\"success\":\"" + "true" + "\"}";
    }
    
    /**
	 * Traite les requêtes de renommage d'un élément stocké sur le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param body : Corps de la requête contenant le nouveau nom de la ressource
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param path : Chemin de la ressource à renommer
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json de succès ou non
	 */
    @PUT
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    public String renameFile(MultivaluedMap<String, String> body, @PathParam("name") String name, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
    	FTPServer attributedServer = FTPServerHolder.GetServer(name);
		try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			ftpConnection.Rename("/" + path, body.getFirst("newName"));
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"success\":\"" + "false" + "\"}";
		}
		return "{\"success\":\"" + "true" + "\"}";
    }
    
    /**
	 * Traite les requêtes de suppression d'un élément stocké sur un serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param name : Nom du serveur sur lequel effectuer l'opération
	 * @param path : Chemin de la ressource à supprimer
	 * @param FTPUsername : Identifiant de connexion au serveur FTP
	 * @param FTPPassword : Mot de passe de connexion au serveur FTP
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json de succès ou non
	 */
    @DELETE
    @Path("{name}/{path: .*}")
    @Produces("application/json")
    public String removeFile(@PathParam("name") String name, @PathParam("path") String path, @HeaderParam("FTPUsername") String FTPUsername, @HeaderParam("FTPPassword") String FTPPassword, @HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
    	FTPServer attributedServer = FTPServerHolder.GetServer(name);
    	try {
			FTPConnect ftpConnection = new FTPConnect(attributedServer, FTPUsername, FTPPassword);
			if(!ftpConnection.Remove("/" + path)) {
				ftpConnection.Disconnect();
				return "{\"success\":\"" + "false" + "\"}";
			}
			ftpConnection.Disconnect();
		} catch (Exception e) {
			return "{\"success\":\"" + "false" + "\"}";
		}
    	return "{\"success\":\"" + "true" + "\"}";
    }
    
    /**
	 * Traite des requêtes de récupération de la liste des serveurs enregistrés.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @return Json contenant la liste des noms de serveurs enregistrés sur l'API sous la forme d'un tableau
	 */
    @GET
    @Produces("application/json")
    public String listServers(@HeaderParam("username") String username, @HeaderParam("password") String password) {
    	if(!ClientHolder.Authenticate(username, password)) {
    		return "{\"message\":\"Need to authenticate in header of create an account\"}";
    	}
    	String answer = "";
    	for (String name : FTPServerHolder.ListServers()) {
    		answer += ", \"" + name + "\"";
    	}
    	if(answer.length() > 0) {
    		answer = answer.substring(1);
    	}
        return "{\"serverNames\":[" + answer + "]}";
    }
    
    /**
	 * Traite les requêtes d'ajout d'un nouveau serveur à la liste des serveurs de l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @param : body : Corps de la requête contenant les informations du serveur qu'on ajoute
	 * @return Json de succès ou non
	 */
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addServer(@HeaderParam("username") String username, @HeaderParam("password") String password, MultivaluedMap<String, String> body) {
    	Boolean success = false;
    	if(ClientHolder.Authenticate(username, password)) {
    		success = true;
    		FTPServer newServer = new FTPServer(body.getFirst("name"), body.getFirst("host"), body.getFirst("port"), body.getFirst("mode"));
    		try {
    			FTPServerHolder.AddServer(newServer);
    			
    		} catch (Exception e) {
    			success = false;
    		}
    	}
    	return "{\"success\":\"" + success + "\"}";
    }
    
    /**
	 * Traite les requêtes de mise à jour des informations d'un serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @param body : Corps de la requête contenant les nouvelles informations du serveur
	 * @param name : Nom de la ressource serveur à modifier
	 * @return Json de succès ou non
	 */
    @PUT
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeServer(@HeaderParam("username") String username, @HeaderParam("password") String password, MultivaluedMap<String, String> body, @HeaderParam("name") String name) throws Exception {
    	Boolean success = false;
    	if(ClientHolder.Authenticate(username, password)) {
    		FTPServer server = FTPServerHolder.GetServer(name);
        	if(server != null) {
        		FTPServerHolder.RemoveServer(name);
        		FTPServer newServer = new FTPServer(body.getFirst("name"), body.getFirst("host"), body.getFirst("port"), body.getFirst("mode"));
            	try {
        			FTPServerHolder.AddServer(newServer);
        			success = true;
        			
        		} catch (Exception e) {
        			FTPServerHolder.AddServer(server);
        		}
        	}
    	}
    	return "{\"success\":\"" + success + "\"}";
    }
    
    /**
	 * Traite les requêtes de suppression d'un serveur enregistré dans l'API.
	 *
	 * @author Cédric Bevilacqua
	 * @param username : Nom d'utilisateur de la personne effectuant la requête
	 * @param password : Mot de passe de la personne effectuant la requête
	 * @param name : Nom de la ressource serveur à supprimer
	 * @return Json de succès ou non
	 */
    @DELETE
    @Produces("application/json")
    public String removeServer(@HeaderParam("username") String username, @HeaderParam("password") String password, @HeaderParam("name") String name) {
    	Boolean success = false;
    	if(ClientHolder.Authenticate(username, password)) {
    		if(FTPServerHolder.RemoveServer(name)) {
        		success = true;
        	}
    	}
    	return "{\"success\":\"" + success + "\"}";
    }
}
