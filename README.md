# BEVILACQUA-FlopBox



## Ce que contient le projet

Le projet réalisé concerne une API "FlopBox" qui permet de se connecter et d'intéragir avec des serveurs FTP qui ont été déclarés sur l'API au travers d'un compte propre à chaque utilisateur.

La vidéo de présentation ainsi qu'un diagramme UML sont disponibles dans le dossier docs à la racine du dépôt.

Voici ce qui a été fait conformément au cahier des charges : 

- Enregistrer un nouveau serveur FTP
- Supprimer un serveur FTP enregistré
- Modifier une association FTP
- Se connecter à un servuer FTP (réalisé automatiquement lors d'une action sur le serveur de manière transparente par l'API, il n'y a donc pas de commande spécifique pour "juste" se connecter sans ne rien faire)
- L'API rejette la connexion si le nom d'utilisateur est inconnu
- L'API rejette la connexion si le mot de passe de l'utilisateur est incorrecte
- Lister le contenu d'un répertoire stocké sur le serveur (ainsi que le contenu à la racine du serveur)
- Récupérer un fichier texte (se fait de la même manière qu'un fichier binaire, rien de particulier n'a été rajouté pour récupérer spécifiquement un fichier texte)
- Récupérer un fichier binaire
- Récupérer le contenu d'un répertoire complet (il n'y a pas de commande le faisant directement, mais cette action est possible par le client en réalisant une suite de requête pour récupérer toute l'arborescence des fichiers et sous répertoire contenu dans un répertoire uns par uns)
- Stocker un fichier texte sur un serveur FTP (on envoi une requête de type TEXT-PLAIN avec du texte à l'intérieur)
- Stocker un fichier binaire sur un serveur FTP
- Stocker le contenu d'un répertoire complet (cette action ne peut pas être faite directement par une seule commande mais peut être réalisée par le client par une suite de requêtes stockant uns à uns chaque fichier d'un répertoire après l'avoir créé)
- Renommer un fichier distant
- Créer un répertoire distant
- Renommer un répertoire distant
- Supprimer un répertoire distant (fonctionne uniquement si le répertoire est vide, mais on peut aussi supprimer des fichiers distants, une suite de requête pourra donc supprimer le contenu du répertoire pour ensuite supprimer le répertoire lui même)
- Couper proprement la connexion (cette action n'est pas faite par le client mais gérée par l'API comme pour la connexion, après chaque action, la connexion est coupée proprement)
- Spécifier le port du serveur FTP associé à l'API
- Configurer l'URL du serveur FTP associé à l'API
- L'API supporte les modes ACTIF et PASSIF (ce mode est renseigné lors de la configuration du serveur avec l'URL et le port)
- La plateforme supporte la connexion de plusieurs clients simultanés (aucun thread n'est utilisé, mais les requêtes pourront être exécuté l'une à la suite de l'autre car elles ne sont pas liées entre elles)
- Le code de la plateforme compile correctement avec Maven
- Le code est documenté
- Le code est testé avec des tests unitaires sous JUnit (il ne l'est pas à 100% mais il y a une couverture du code tout à fait honorable couvrant les points les plus critiques)
- Le code suit les principes de conception objet
- Le code s'exécute (preuve dans la vidéo jointe dans la documentation)

Le cahier des charges a donc été totalement respecté.

## Organisation du projet

Le projet a été architecturé sous 3 package différents : 
- **connectors** : Contient toutes les classes Jersey et JAX-RS, gérant toutes les requêtes et réponses API
    - **Authentication** : Gère toutes les requêtes concernant la ressource */auths* impliquant la création, modification et suppression des comptes utilisateurs en appelant les différentes méthodes des classes contenues dans *holders* ou *processors*.
    - **Server** : Gère toutes les requêtes concernant la ressource */server* qui concernent à la fois toutes les déclarations, modifications et suppression de serveurs mais également toutes les opérations sur les serveur. A chaque fois des méthodes contenues dans *holders* ou *processors* sont appelées.
- **holders** : Contient toutes les classes (statiques) contenant les données de l'API par composition avec des classes représentant les clients ou les serveurs déclarés dans l'API avec des méthodes permettant de manipuler rapidement ces informations.
    - **ClientHolder** : Contient des classes représentant des clients par composition ainsi que des méthodes permettant de les ajouter, supprimer, les lister, obtenir des clients à partir d'un identifiant ou encore authentifier un utilisateur à partir d'un simple nom d'utilisateur et un mot de passe.
    - **FTPServerHolder** : Contient des classes représentant des serveurs par composition ainsi que des méthodes permettant de les ajouter, supprimer, les lister ou obtenir un serveur particulier à partir de son nom.
- **processors** : Contient toutes les classes représentant de l'information ainsi que toutes les méthodes d'action et de traitement sur les données ainsi que les classes de gestion de la partie connexion FTP.
    - **Client** : Chaque instance de cette classe représente un client. Elle contient toutes ses informations sous la forme d'attributs ainsi que des accesseurs. Une méthode a été implémentée permettant de vérifier si un mot de passe soumis en paramètre est valide.
    - **FTPServer** : Chaque instance de cette classe représente un serveur. Elle contient toutes ses informations sous la forme d'attributs et d'accesseurs.
    - **FTPElement** : Chaque instance de cette classe représente un élément stocké sur le serveur FTP. Elle contient de nombreux attributs et accesseurs qui sont les métadonnées d'un fichier ou un dossier présent sur un serveur FTP. Elle est essentiellement utilisée pour lister des fichiers du serveur et lire et convertir les informations contenues dans les *FTPFile* générées par *FTPClient*.

## Requêtes et réponses

Ici sont détaillées les requêtes possibles sur l'API ainsi que les réponses renvoyées.

Toutes les requêtes indiquées ici sont écrites en cURL.

Postman a aussi été utilisé lors de la création de ce projet. Egalement, un mini serveur FTP écrit en Python et utilisant la librairie indiquée dans le sujet a été utilisé pour mener les tests, ce script Python est aussi disponible dans le dossier *docs*.

### Authentification

Commandes de gestion des comptes utilisateurs.</br>
Ressource : */auths*

#### Créer un compte

Permet de créer un nouveau compte utilisateur, une requête **POST** prenant en **body** le nom d'utilisateur et le mot de passe du compte.

```
curl --location --request POST 'http://localhost:8080/v1/auths' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'username=user' --data-urlencode 'password=pass'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Lister les comptes

Permet de lister tous les comptes utilisateurs enregistrés, une requête **GET** ne prenant **aucun paramètre**.

```
curl --location --request GET 'http://localhost:8080/v1/auths'
```

En réponse, on obtient un JSON contenant un tableau de tous les noms d'utilisateurs des comptes enregistrés.

#### Modifier un compte

Permet de changer les informations d'un compte enregistré, une requête **PUT** prenant en **body** les anciens identifiants puis les nouveaux.

```
curl --location --request PUT 'http://localhost:8080/v1/auths' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'username=user' --data-urlencode 'password=pass' --data-urlencode 'newUsername=user2' --data-urlencode 'newPassword=pass2'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Supprimer un compte

Permet de supprimer un compte utilisateur, une requête **DELETE** avec un **header** contenant l'identifiant et le mot de passe du compte.

```
curl --location --request DELETE 'http://localhost:8080/v1/auths' --header 'username: user' --header 'password: pass'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

### Serveurs

Commandes de gestion des déclarations des serveurs FTP.</br>
Ressource : */servers*

#### Ajouter un serveur

Déclare un serveur FTP, une requête **POST** prenant en **header** les identifiants du client et en **body** le nom du serveur, son adresse, son port de connexion ainsi que le mode de connexion (passive / active) souhaité.

```
curl --location --request POST 'http://localhost:8080/v1/servers' --header 'username: user' --header 'password: pass' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'name=MyServ' --data-urlencode 'host=localhost' --data-urlencode 'port=21' --data-urlencode 'mode=passive'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Voir la liste des serveurs

Affiche la liste des serveurs enregistrés, une requête **GET** prenant en **header** les identifiants du client.

```
curl --location --request GET 'http://localhost:8080/v1/servers' --header 'username: user' --header 'password: pass'
```

En réponse, on obtient un JSON contenant un tableau de tous les serveurs FTP déclarés sur l'API.

#### Modifier un serveur

Modifie les paramètres d'un serveur, une requête **PUT** prenant en **header** les identifiants du client ainsi que le nom du serveur à modifier et en **body** les nouveaux paramètres du serveur (nom, adresse, port, mode).

```
curl --location --request PUT 'http://localhost:8080/v1/servers' --header 'username: toto' --header 'password: toto' --header 'name: Perso' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'name=Perso2' --data-urlencode 'host=localhost' --data-urlencode 'port=2122' --data-urlencode 'mode=active'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Supprimer un serveur

Supprime un serveur enregistré, une requête **DELETE** prenant en **header** les identifiants du client ainsi que le nom du serveur.

```
curl --location --request DELETE 'http://localhost:8080/v1/servers' --header 'username: toto' --header 'password: toto' --header 'name: Perso2'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

### Actions sur un serveur FTP

Commandes d'action sur les fichiers et dossiers d'un serveur déclaré sur l'API.</br>
Ressource : */servers*

#### Voir le contenu d'un dossier ou de la racine

Obtient le contenu des fichiers situés à une adresse précise d'un serveur FTP enregistré sur l'API, une requête **GET** prenant en **header** les identifiants du client ainsi que du serveur.

```
curl --location --request GET 'http://localhost:8080/v1/servers/Perso/...' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass'
```

En réponse, on obtient un JSON contenant un tableau avec une suite de JSON contenant des méta données sur chaque fichier et dossier accessible directement depuis l'adresse indiquée dans l'URI.

#### Envoyer un fichier binaire sur le serveur

Envoi un fichier binaire sur un serveur FTP enregistré dans l'API, une requête **POST** prenant en **header** les identifiants du client et du serveur et un champ *file* contenant le flux du fichiers à transmettre.

```
curl -X POST -F 'file=@/home/myfile' http://localhost:8080/v1/servers/Perso/MyFile --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Envoyer un fichier texte sur le serveur

Envoi du texte brute sur un serveur FTP enregistré dans l'API, une requête **POST** prenant en *header* les identifiants du client et du serveur et du texte brut de type *TEXT-PLAIN*.

```
curl --location --request POST 'http://localhost:8080/v1/servers/Perso/textFile.txt' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass' --header 'Content-Type: text/plain' --data-raw 'Ceci est un text !'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Créer un dossier

Crée un dossier sur un serveur FTP enregistré, une requête **POST** prenant en **header** les identifiants du client et du serveur.

```
curl --location --request POST 'http://localhost:8080/v1/servers/Perso/newfolder' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass' --header 'Content-Type: application/x-www-form-urlencoded'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Renommer un dossier

Renomme un dossier sur un serveur FTP enregistré, une requête **PUT** prenant en **header** les identifiants du client et du serveur et un **body** prenant le nouveau nom du dossier.

```
curl --location --request PUT 'http://localhost:8080/v1/servers/Perso/newfolder' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass' --data-urlencode 'newName=newFolder2'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Supprimer un élément

Supprime un fichier ou un dossier vide sur un serveur FTP enregistré, une requête **DELETE** prenant en **header** les identifiants du client et du serveur.

```
curl --location --request DELETE 'http://localhost:8080/v1/servers/Perso/tutu' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass'
```

En réponse, on obtient un JSON indiquant du succès ou non de l'opération.

#### Télécharger un fichier

Télécharge un fichier present sur un serveur FTP, une requête **GET** prenant en **header** les identifiants du client et du serveur ainsi que pour la commande cURL un champ *output* indiquant où enregistrer le fichier.

```
curl --location --request GET 'http://localhost:8080/v1/servers/download/Perso/MyFile' --header 'username: user' --header 'password: pass' --header 'FTPUsername: byvoid' --header 'FTPPassword: pass' --output testFile
```

En réponse, on obtient un flux d'octets du fichier présent sur le serveur FTP.