// SCALA - Labo 2
// Authors : Alessandro Parrino, Daniel Sciarra ◕◡◕
// Date: 16.04.22

package Chat

enum Token:
     case 
     // Terms
     BONJOUR,
     JE,
     ME,
     MON,
     QUEL,
     LE,
     DE,
     APPELLE,
     SVP,
     ASSOIFFE,
     AFFAME,
     SOLDE,
     PRIX,
     // Actions
     ETRE,
     VOULOIR,
     COMMANDER,
     COMBIEN,
     CONNAITRE,
     // Logic Operators
     ET,
     OU,
     // Products
     PRODUCT,
     MARQUE,
     // Util
     POSSESSIF,
     PRONOM,
     PSEUDO,
     NUM,
     EOL,
     UNKNOWN,
     BAD
end Token
