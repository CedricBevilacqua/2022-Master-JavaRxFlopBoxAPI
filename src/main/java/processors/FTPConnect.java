package processors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Gère la connexion à un serveur précis et permet d'y effectuer différentes actions.
 *
 * @author Cédric Bevilacqua
 * @see <a href="http://www.codeurjava.com/2015/12/connexion-et-login-au-serveur-ftp-java.html">Connexion</a> 
 * @see <a href="https://www.codejava.net/java-se/ftp/java-ftp-list-files-and-directories-example">Listage</a> 
 */
public class FTPConnect {
	
	private String server;
	private int port;
	private String username;
	private String password;
	private Boolean passiveMode;
	private FTPClient ftpConnection;
	
	/**
	 * Constructeur mettant en place les paramètres puis en démarrant la connexion.
	 *
	 * @author Cédric Bevilacqua
	 * @param server : Représentation d'un serveur FTP avec les informations nécessaire pour le contacter
	 * @param username : Identifiant de connexion au serveur
	 * @param password : Mot de passe de connexion au serveur
	 */
	public FTPConnect(FTPServer server, String username, String password) throws Exception {
		 this.server = server.getHost();
		 this.port = server.getPort();
		 this.username = username;
		 this.password = password;
		 this.passiveMode = server.getMode();
		 if(!Connect()) {
			 throw new Exception();
		 };
	}
	
	/**
	 * Liste les fichiers et dossiers visibles à une adresse indiquée sur le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param adress : Adresse à partir de laquelle lister les éléments visibles à ce niveau de l'arborescence
	 * @return Liste des éléments FTP visibles à l'emplacement indiqués
	 * @throws ParseException 
	 */
	public List<FTPElement> List(String adress) throws IOException, ParseException {
		FTPFile[] files = ftpConnection.listFiles(adress);
		List<FTPElement> elements = new ArrayList<>();
		for (FTPFile file : files) {
			String lastModificationDate = ftpConnection.getModificationTime(adress + "/" + file.getName());
		    FTPElement newElement = new FTPElement(file, lastModificationDate);
		    elements.add(newElement);
		}
		return elements;
	}
	
	/**
	 * Supprime un fichier ou un dossier du serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param adress : Adresse de l'élément à supprimer
	 * @return Booleen indiquant le succès ou non de l'opération
	 */
	public Boolean Remove(String adress) {
		try {
			if(!ftpConnection.deleteFile(adress)) {
				if(!ftpConnection.removeDirectory(adress)) {
					return false;
				}
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Envoi un fichier binaire sur le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param file : Stream d'octets à transférer sur le serveur
	 * @param destination : Emplacement du fichier à enregistrer sur le serveur
	 * @return Booleen indiquant le succès ou non de l'opération
	 */
	public Boolean SendBinaryFile(InputStream file, String destination) throws IOException {
		ftpConnection.setFileTransferMode(ftpConnection.BINARY_FILE_TYPE);
		Boolean answer = ftpConnection.storeFile(destination, file);
		file.close();
		return answer;
	}
	
	/**
	 * Envoi un fichier texte sur le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param fileContent : Contenu texte à transférer sur le serveur
	 * @param destination : Emplacement du fichier à créer sur le serveur
	 */
	public void SendTextFile(String fileContent, String destination) throws IOException {
		ftpConnection.setFileTransferMode(ftpConnection.BINARY_FILE_TYPE);
	    OutputStream streamWriter = ftpConnection.storeFileStream(destination);
	    PrintWriter printer = new PrintWriter(streamWriter, true);
	    printer.print(fileContent);
	    printer.close();
	    streamWriter.close();
	}
	
	/**
	 * Téléchargement d'un fichier du serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @param path : Emplacement du fichier à télécharger
	 */
	public void GetFile(String path) throws IOException {
		FileOutputStream cacheFile = new FileOutputStream("tempCache");
		ftpConnection.setFileType(FTP.BINARY_FILE_TYPE);
	    ftpConnection.retrieveFile(path, cacheFile);
	}
	
	/**
	 * Création d'un dossier sur le serveur.
	 *
	 * @author Cédric Bevilacqua
	 * @param path : Emplacement du dossier à créer
	 */
	public void NewFolder(String path) throws Exception {
		ftpConnection.setFileTransferMode(ftpConnection.BINARY_FILE_TYPE);
	    ftpConnection.makeDirectory(path);
	}
	
	/**
	 * Renomme un élément sur le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 * @param path : Emplacement sur le serveur de l'élément à renommer
	 * @param name : Nouveau nom à donner à l'élément
	 */
	public void Rename(String path, String name) throws Exception {
		String[] splittedPath = path.split("/");
		splittedPath[splittedPath.length - 1].length();
		if(!ftpConnection.rename(path, name)) {
			throw new Exception();
		}
	}
	
	/**
	 * Ferme la connexion avec le serveur FTP.
	 *
	 * @author Cédric Bevilacqua
	 */
	public void Disconnect() throws IOException {
		ftpConnection.logout();
		ftpConnection.disconnect();
	}
	
	/**
	 * Démarre la connexion au serveur FTP et s'identifie auprès de lui.
	 *
	 * @author Cédric Bevilacqua
	 */
	private Boolean Connect() {
		this.ftpConnection = new FTPClient();
		try {
			this.ftpConnection.connect(server, port);
			this.ftpConnection.getReplyStrings();
			this.ftpConnection.getReplyStrings();
		    int reponse = this.ftpConnection.getReplyCode();
		    if(!FTPReply.isPositiveCompletion(reponse)) {
		       return false;
		    }
		    boolean etat = this.ftpConnection.login(username, password);
		    this.ftpConnection.getReplyStrings();
		    if(!etat) {
		        return false;
		    } else {
		    	if(passiveMode) {
		    		ftpConnection.enterLocalPassiveMode();
		    	} else {
		    		ftpConnection.enterLocalActiveMode();
		    	}
		        return true;
		    }
		 } catch (IOException ex) {
			ex.printStackTrace();
		    return false;
		 }
	}
	
}
