# Rapport Devoir 1

## Contexte

Dans le cadre de l'UV SR03 (Architecture des applications Internet),
une application de chat sera développée tout le long du semestre. L'objectif est de mettre en
application les concepts étudiés en cours et utilisés en TD sur un projet plus concrêt.
Le projet se décompose en trois devoirs :

- Communication réseau
- Sécurité
- Implémentation d’une application web FullStack

### Le projet

Le devoir 1 correspondant à la partie "communication réseau" du projet, a pour objectif de développer une
application de chat Client/Serveur via les sockets qui permet d’organiser une
discussion publique entre un ensemble de participants.

### Concepts utilisés

- Afin d'effectuer un communication client/Serveur, les sockets sont utilisés.
- La programmation objet, au coeur de cette application sera le paradigme utilisé
    - Il sera possible de retrouver l'utilisation de l'héritage et la surcharge de certaines méthodes
    - Chaque classe joue sont rôle (Client, Server, threads de communication) joue sont rôle et communique avec les
      autres classes
    - JAVA sera le langage orienté objet utilisé pour cette application notamment car il possède un ensemble de classes
      permettant l'utilisation et la gestion des sockets
- Le multi-threading sera utilisé. Il permet de lancer un ensemble de threads, processus fils executables simultanément
  au sein d'un programme unique. C'est grâce à ce concept que plusieurs clients pourront se connecter et communiquer
  ensemble.

### Objectifs fixés

L'objectif de cette application est de permettre à plusieurs clients de se connecter à un serveur unique et de
communiquer entre
eux comme au sein d'un groupe de discussion. Il devra être possible de choisir un pseudo qui apparaitra lorsque le
client communique
et les clients seront notifiés lors de l'arrivée ou le départ d'un client.

### Cas d'utilisation


## Conception et Développement

### Architecture de l'application

**L’application est composée de deux classes principales :**

1. La classe `Server` permet aux clients de s'y connecter pour pouvoir accéder au fil de discussion.

    - La classe `Server` possède plusieurs attributs :
        - Un ServerSocket qui permet aux clients de se connecter au serveur
        - Un Socket qui correspond au socket du client qui se connecte au serveur
        - Un entier qui sert de compteur pour le nombre de clients connectés
        - Un dictionnaire qui regroupe les pseudos des clients actuellement connectés ainsi que leur canal de sortie qui
          sert à leur envoyer des informations

    - La classe `Server` possède plusieurs méthodes :
        - isExisting permet de savoir si un pseudo existe parmi les pseudos actuellement utilisés ou non. Elle retourne
          donc un booléen et prend un pseudo en argument
        - addToConnectedClients permet d’ajouter un client au dictionnaire
        - removeFromConnectedClients permet de supprimer un client du dictionnaire
        - sendMessagesToClients permet d’envoyer des messages à tous les clients connectés et notamment les messages de
          connection et de déconnection

2. La classe `Client` permet de se connecter au `Server`, d'envoyer ainsi que de recevoir les messages des autres
   clients connectés.
    - La classe `Client` possède plusieurs attributs :
        - Un Socket qui permet la communication avec le serveur.
        - Un attribut booléen qui indique si la connection au serveur est active ou non.
    - La classe `Client` ne possède pas de méthodes particulières si ce n'est le `main`.

**Les deux classes présentées ci-dessus utilisent des Threads afin d'effectuer des executions en parallèle**
Les threads ont été redéfinis dans des classes qui sont spécifiques à leur utilisation. Chacune de ces classes hérite de
la classe mère thread et possède une surcharge de la methode run() qui est appelée lors du lancement du thread.

Pour chaque connection client/serveur, trois threads sont mis en place :

- Un premier thread est généré côté serveur afin de réceptionner les messages envoyé par un client qui lui est attribué.
  Il y a donc un thread de reception par client.
  Le thread est défini dans la classe `ServerMessageReceptor`.
- Un deuxième permet l'envoi de messages (lus sur le terminal) au serveur
  Le thread est défini dans la classe `ClientMessageSender`.
- Un troisième permet la reception des messages du serveur au client. Ces messages comprennent ceux envoyés par les
  autres clients.
  Le thread est défini dans la classe `ClientMessageReceptor`.

![](C:\Users\lv230\IdeaProjects\SR03-Devoir1\src\main\SR03Devoir1.png)
_Expliquer et justifier votre conception et le développement (ex. Le choix des
structures des données JAVA, surcharges des méthodes). Vous pouvez vous appuyer sur
des bouts de code et des schémas._

### Langues utilisées

## Scénarios d'utilisation

### Récupérer et lancer l'application
Expliquer comment récupérer (ex. code sur git avec un commit),
installer et lancer votre application.

### Cas d'utilisations pratiques

Montrer tous les cas d’utilisations pratiques (via votre application sous forme d’un démonstrateur)

#### Démarrer le chat

Afin de démarrer le chat, il faut tout d'abord lancer la classe `Server` puis lancer la classe `Client`.
Il sera demandé sur le terminal du client, d'entrer un pseudo. Ensuite, celui-ci sera connecté au fil de discussion.

#### Ajouter des utilisateurs

Pour ajouter un utilisateur il suffit de relancer un instance de la classe Client. Celui-ci se connectera aprés
avoir choisi un pseudo

#### Démarrer la discussion

Aprés la connection, le client peut écrire un message sur le terminal qui sera envoyé à tous les utilisateurs.
De même, il recevra les messages écrit par les autres clients.

#### Quitter le chat

Pour quitter le chat, il faut que le client tape "exit". La déconnection s'effectuera et les autres clients seront
notifiés.

### Cas particuliers

#### Départ brutal

Si un client, tente de se connecter avant que le server ne tourne, un message informant
que le serveur a un problème sera renvoyé au client et son programme se terminera.

#### Client interrompu

Si le client a un problème, les autres utilisateurs seront notifiés et le serveur supprimera le client de sa base de
données.

#### Serveur interrompu

Si le serveur est interrompu, les clients sont notifiés et sont déconnectés.

# Trucs lambdas à ajouter qq part :

### Traitement des exceptions

Les exceptions connues et voulues (comme la fermeture de certains sockets liés à la déconnection) sont traitées de façon
à éviter un arret total du programme.
De plus les clients et le serveur sont notifiés quand cela est necessaire.

PARLER DE LA MAJ DES EXCEPTIONS VERS DES VRAIES CLASSES PLUS TARD ???

### Utilisation d'une variable pour connaitre l'état de la connection

Dans les classes principales, une variable booléenne a été ajoutée afin de connaitre l'état de connection
entre le serveur et le client. Si la variable est à false la connection n'est plus active, sinon la connection
est active et la variable est à true. Utiliser une variable comme ceci permet aux threads de sortir de se terminer sans
qu'il y ait
d'erreur. En effet, le programme principal attend que les threads se terminent avant de supprimer la connection pour
éviter les erreurs.

Questions du prof :
• Comment garantir qu’un pseudonyme est unique ? Implémenter la solution proposée.
• Comment vous faites pour gérer le cas d’une déconnexion de client sans que le serveur
soit prévenu et vice-versa ? Implémenter la solution proposée.

