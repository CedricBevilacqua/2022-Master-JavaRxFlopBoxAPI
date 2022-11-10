package processors;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Représentation d'un fichier présent sur le serveur FTP et de ses caractéristiques.
 *
 * @author Cédric Bevilacqua
 */
public class FTPElement {
	
	private String name;
	private String type;
	private String size;
	private String date;
	
	/**
	 * Constructeur initialisant l'élément à partir des informations d'un objet FTPFile retourné par FTPClient.
	 *
	 * @author Cédric Bevilacqua
	 * @param file : Représentation d'un fichier du serveur FTP retourné par FTPClient
	 * @throws ParseException 
	 */
	public FTPElement(FTPFile file, String modificationTime) throws ParseException {
		name = file.getName();
		if (file.isDirectory()) {
	        type = "Directory";
	        date = null;
	    } else {
	    	type = "File";
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		    Date convertedModificationTime = dateFormat.parse(modificationTime);
		    date = Long.toString(convertedModificationTime.getTime()/1000);
	    }
	}
	
	/**
	 * Accesseurs GET du nom du fichier.
	 *
	 * @author Cédric Bevilacqua
	 * @return Nom du fichier sous forme d'une chaine de caractères
	 */
	public String GetName() {
		return name;
	}
	
	/**
	 * Accesseur GET du type du fichier (fichier ou dossier).
	 *
	 * @author Cédric Bevilacqua
	 * @return Type du fichier sous forme d'une chaine de caractères
	 */
	public String GetType() {
		return type;
	}
	
	/**
	 * Accesseur GET de la taille du fichier en nombre d'octets.
	 *
	 * @author Cédric Bevilacqua
	 * @return Taille du fichier sous forme d'une chaine de caractères
	 */
	public String GetSize() {
		return size;
	}
	
	/**
	 * Accesseur GET de la date de modification du fichier.
	 *
	 * @author Cédric Bevilacqua
	 * @return : Date de modification du fichier sous forme d'une chaine de caractères
	 */
	public String GetDate() {
		return date;
	}
	
}
