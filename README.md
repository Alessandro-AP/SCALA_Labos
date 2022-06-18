# SCALA Labos - Bot-tender Future

## Choix d'implémentation

Dans cette section, nous abordons les différents choix d'implémentation effectués pour les fonctionnalités ajoutées au projet par rapport au précédent labo (branche origin/Labo_3).

### Temps de préparation des produits

TODO

### Traitement des commandes en préparation

TODO

### Envoi des messages sur le chat une fois le traitement fini

Pour le traitement des messages asynchrones, dans notre cas le traitement d'une commande une fois le temps de préparation écoulé, nous avons beaucoup réfléchi à une intégration possible avec une approche classique du langage scala, mais nous avons constaté qu'étant donné la structure du projet, il n'était pas possible de le faire proprement.

Une solution possible était de passer un ou plusieurs paramètres dans nos classes, ou de leur faire renvoyer par des fonctions, mais nous avons trouvé que cela alourdissait trop le code. 
Une autre solution était de déléguer l'écriture du message au service d'analyse, mais cela aurait changé l'objectif spécifique de la classe et nous aurions commencé à mélanger les tâches (bad pattern).

Pour résoudre ce problème, nous avons donc opté pour l'utilisation d'un channel `LinkedTransferQueue` offert par la bibliothèque standard de Java (Java-Scala 1-0). En fait, ce channel nous permet de créer une simple voie de communication entre les deux classes, la communication ayant lieu de manière asynchrone et sans bloquer le thread principal.


![](images/channel_diagram.png)

Dans le **AnalyserService**, une fois le traitement d'une commande terminé avec succès, une fonction callback se chargera d'envoyer le résultat grâce au channel.

Dans les **MessagesRoutes**, il y a une méthode **listener (Event Loop)** qui est déclenchée lorsqu'un événement s'est produit (l'ajout d'un message). Une fois le message reçu, il sera traité et le listener continuera à attendre d'autres messages éventuels. Si plusieurs messages arrivent en même temps, ils seront traités dans l'ordre d'arrivée et surtout sans produire de blocage.

