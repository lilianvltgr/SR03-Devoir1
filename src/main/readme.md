
Connexion : 
* Un tableau avec les pseudos pour savoir qui est connecté 
  * Stocké côté serveur
  * Dynamique ? avec les vecteurs comme en cpp ? 
  * On supprimer ceux qui se déconnectent et on ajoute les nouveaux
  * On vérifie au choix du pseudo si le pseudo est unique dans le tableau

Fonctions : 
* Une fonction qui permet de choisir le pseudo 
* Une fonction de diffusion à tous (à chaque fois que le serveur reçoit un message)
  * Une fonction qui gère l'affichage 
* Ajout d'une personne dans le tableau
* Suppression d'une personne dans le tableau
Lorsqu'un client envoie un message au server : Contenu + Pseudo 


**!!! Attention**
* Penser à gérer les exceptions 
* Utiliser les threads 
* Tout en anglais


Threads gestion : 
* Un thread est créé à chaque nouvelle connexion 
* Chaque client possède un thread pour ecrire les messages et un thread pour les afficher 

Comment garantir l'utilisation d'un pseudo unique? 
* While tant que le pseudo n'est pas valide on boucle
* Création d'une fonction de choix de pseudo => fonction rappelée à chaque pb 