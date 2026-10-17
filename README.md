# TP1 — Les sockets en mode connecté TCP

Faculté des Sciences de Sfax (FSS) · LSI3 · Développement d'applications réparties · 2026–2027
Amal ABID (cours), Zeineb DAOUD (TP) · Auteur : Gharbi Youssef

## Objectifs et environnement

Les trois activités montrent une connexion TCP, un service de multiplication exécuté par le serveur, puis une calculatrice à quatre opérations accessible par IPv4. Le projet utilise Java standard (JDK 8 ou plus), VS Code et Git, sans bibliothèque externe. Vérifié avec Temurin 21.0.12.1 et Git 2.56.0.windows.2.

Un socket TCP représente une connexion entre deux extrémités identifiées par leurs adresses et ports. `ServerSocket` écoute sur un port ; `accept()` attend un client et crée le socket de cette connexion. Le serveur doit donc être démarré avant le client. Les blocs `try` avec ressources ferment les flux et sockets à la fin ou après une erreur.

## Structure et compilation

```text
TP1-DAR2026/
├── src/serverPackage/Server.java   # activité 1-1
├── src/clientPackage/Client.java   # activité 1-1
├── src/activite12/Server.java       # multiplication
├── src/activite12/Client.java
├── src/activite13/Server.java       # quatre opérations
├── src/activite13/Client.java
├── src/App.java                     # exemple initial de VS Code, hors TP
├── captures/                        # captures à réaliser
├── .vscode/settings.json
└── README.md
```

Depuis `TP1-DAR2026` dans PowerShell :

```powershell
java --version
javac --version
javac -encoding UTF-8 -d bin src/serverPackage/Server.java src/clientPackage/Client.java src/activite12/Server.java src/activite12/Client.java src/activite13/Server.java src/activite13/Client.java
```

`bin/` contient les fichiers compilés et est ignoré par Git. Ouvrir deux terminaux dans ce dossier : un pour le serveur et un pour le client. Les serveurs utilisent le port 5000 ; les lancer **séparément**.

## Activité 1-1 — Première connexion client/serveur

`serverPackage.Server` écoute sur le port 5000, accepte un client, affiche son adresse et ferme la connexion. `clientPackage.Client` se connecte à `localhost` par défaut ou à l'hôte passé en argument. Il n'y a pas encore de données applicatives échangées.

```powershell
# Terminal serveur
java -cp bin serverPackage.Server
# Terminal client
java -cp bin clientPackage.Client
```

Test local refait le 17 octobre 2026 : compilation réussie ; le client a affiché la réussite de la connexion et le serveur l'adresse `127.0.0.1`. Le serveur traite un seul client, puis se termine. L'essai historique avec `192.168.1.178`, décrit dans le précédent README, concernait la même machine ; cette adresse n'est plus celle du Wi‑Fi actuel.

## Activité 1-2 — Service de multiplication

`activite12.Client` lit un entier signé au clavier et le transmet avec `DataOutputStream.writeInt()` (4 octets). `activite12.Server` le lit avec `DataInputStream.readInt()`, calcule `x * 5` en `long` pour éviter le débordement d'un `int`, puis répond avec `writeLong()` (8 octets). Le client utilise `readLong()` et affiche la réponse. Les flux tamponnés sont vidés avec `flush()` après chaque envoi. Le client ne calcule jamais le produit.

Une seule connexion transporte plusieurs demandes. La valeur `0` est envoyée comme sentinelle de fin : le serveur ne renvoie **aucune réponse** pour elle et ferme la connexion. Une saisie non numérique est rejetée localement sans interrompre la session. Une coupure imprévue ou une absence du serveur produit un message d'erreur ; le client utilise un délai de connexion de 3 secondes et un délai de lecture de 10 secondes.

```powershell
# Terminal serveur
java -cp bin activite12.Server
# Terminal client, autre terminal
java -cp bin activite12.Client
```

Test réel local du 17 octobre 2026, dans **une seule connexion** :

| Saisie `x` | Résultat observé côté client | État |
|---:|---:|---|
| 5 | 25 | Validé |
| 10 | 50 | Validé |
| -4 | -20 | Validé |
| -10 | -50 | Validé |
| 1 | 5 | Validé |
| `abc` | Saisie invalide ; nouvelle invitation | Validé |
| 2147483647 | 10737418235 | Validé |
| 0 | Arrêt de la communication ; fermeture des deux côtés | Validé |

Le journal du serveur a affiché chaque multiplication, ce qui confirme que le calcul s'effectue bien côté serveur.

## Activité 1-3 — Calculatrice et adresse IPv4

`activite13.Server` écoute par défaut sur `127.0.0.1:5000`. Pour un autre ordinateur, lui passer **une adresse IPv4 appartenant à son interface locale** et un port. Il utilise `InetAddress` pour représenter l'IPv4 et `InetSocketAddress` pour associer adresse et port au `ServerSocket`. `activite13.Client` construit aussi un `InetSocketAddress`, puis connecte son `Socket` avec un délai de 3 secondes. Les deux programmes vérifient la syntaxe IPv4 et la plage des ports (1 à 65535). Le serveur traite une session client, puis se termine.

```powershell
# Test sur la même machine, dans deux terminaux
java -cp bin activite13.Server
java -cp bin activite13.Client

# Exemple pour deux PC : remplacer l'IPv4 par celle relevée sur le PC serveur
java -cp bin activite13.Server 192.168.1.20 5000
java -cp bin activite13.Client 192.168.1.20 5000
```

`localhost`/`127.0.0.1` désigne toujours la machine qui exécute le programme : depuis le PC client, il ne désigne pas le PC serveur. Une adresse IPv4 du réseau local désigne l'interface du serveur. Ne pas recopier l'adresse d'exemple ci-dessus sans la vérifier.

### Opérations et protocole

Le menu propose `1` addition, `2` soustraction, `3` multiplication, `4` division, `0` quitter. L'énoncé demande d'envoyer le choix et `x`, sans préciser de deuxième opérande. **Convention de cette réalisation : la deuxième valeur est toujours 5** (`x + 5`, `x - 5`, `x * 5`, `x / 5`). Il s'agit d'un choix d'implémentation, pas d'une consigne explicite pour les quatre services. La constante `OPERAND` dans le serveur permet de l'adapter si l'enseignante précise une autre convention.

`DataOutputStream` écrit les données binaires ; `DataInputStream` les lit dans le **même ordre**. Pour chaque demande :

1. Le client envoie `writeInt(opération)` puis `writeInt(x)` et appelle `flush()`.
2. Le serveur lit deux `int`, effectue l'opération, envoie un octet d'état `0` puis un `double` avec `writeDouble()` ; le client lit cet octet puis le résultat. L'addition, la soustraction et la multiplication utilisent d'abord `long` côté serveur ; le `double` commun permet aussi les quotients fractionnaires.
3. Pour une opération inconnue reçue, le serveur envoie l'état `1` puis un texte avec `writeUTF()` ; le client sait le lire. Le menu rejette normalement ce choix avant l'envoi.
4. Pour quitter, le client envoie seulement `writeInt(0)` ; le serveur n'attend pas `x` et ne répond pas. Contrairement à l'activité 1-2, `x = 0` reste une valeur valide.

Le serveur effectue tous les calculs. Le client gère le menu, les erreurs de saisie, l'envoi et l'affichage des réponses. La division par zéro ne s'applique pas ici, puisque le diviseur est la constante 5.

### Tests locaux observés

Compilé et exécuté le 17 octobre 2026 avec les dix calculs suivants dans **une seule session** sur `127.0.0.1` :

| Opération | `x` | Résultat reçu |
|---|---:|---:|
| Addition | 10 | 15.0 |
| Soustraction | 10 | 5.0 |
| Multiplication | 10 | 50.0 |
| Division | 10 | 2.0 |
| Addition | -3 | 2.0 |
| Soustraction | -3 | -8.0 |
| Multiplication | -3 | -15.0 |
| Division | 7 | 1.4 |
| Addition | 0 | 5.0 |
| Multiplication | 0 | 0.0 |

Autres essais réellement exécutés : le choix `9` a été refusé ; la saisie `bad` a demandé une nouvelle valeur ; `0` au menu a fermé les deux applications ; une fin de saisie a été détectée comme déconnexion par le serveur. Sans serveur sur le port 5099, le client a indiqué « Connection refused » avec code de sortie 1. Une IPv4 non valide et un port `70000` ont été rejetés avec code 1. Avec l'IPv4 Wi‑Fi relevée par `ipconfig` ce jour-là (`172.17.19.53`), le serveur et le client exécutés **sur ce même PC** ont échangé `division, x = 7 → 1.4`. Cette adresse peut changer.

### Vérification à effectuer entre deux ordinateurs

**PENDING MANUAL VERIFICATION — aucun essai entre deux machines distinctes n'a été réalisé.**

1. Sur le PC serveur, relever l'IPv4 de l'interface reliée au réseau local avec `ipconfig`. Vérifier qu'elle est accessible depuis le PC client. Compiler et lancer `java -cp bin activite13.Server <IPv4_du_PC_serveur> 5000`.
2. Vérifier que le pare-feu Windows autorise le TCP entrant sur le port 5000 pour le profil privé et le réseau local voulu. Créer une règle limitée à ce réseau si nécessaire, après accord du propriétaire du PC ; ne pas désactiver le pare-feu.
3. Sur le second PC du même réseau, compiler le projet. `ping <IPv4_du_PC_serveur>` peut aider au diagnostic, mais une réponse ping ne prouve pas l'accès TCP. Tester `Test-NetConnection <IPv4_du_PC_serveur> -Port 5000` pendant que le serveur écoute.
4. Lancer `java -cp bin activite13.Client <IPv4_du_PC_serveur> 5000`, essayer les quatre opérations, puis `0`. Relever les sorties des **deux** PC et le résultat de `Test-NetConnection`. Si la connexion échoue, contrôler l'IPv4 active, le réseau, le profil pare-feu, le port et l'état du serveur.

## Captures d'écran à réaliser

Le dossier `captures/` est préparé. **Aucune capture n'a encore été ajoutée ; les éléments ci-dessous sont une liste de travail, pas des preuves déjà présentes.** Faire des captures réelles des terminaux et de VS Code, puis ajouter au README des liens Markdown uniquement vers les images qui existent.

| Nom conseillé dans `captures/` | Contenu à montrer |
|---|---|
| `01-environnement.png` | `java --version`, `javac --version`, `git --version`, `git status` |
| `02-activite11.png` | Sources et terminaux du serveur/client 1-1 sur `localhost` |
| `03-activite12-positif.png` | Serveur et client 1-2 avec `5 → 25` |
| `04-activite12-negatif.png` | Serveur et client 1-2 avec `-4 → -20` |
| `05-activite12-session.png` | Plusieurs valeurs dans la même session, puis `0` et fermeture |
| `06-activite13-menu.png` | Menu et structure des sources 1-3 |
| `07-activite13-operations.png` | Les quatre résultats, dont `7 / 5 = 1.4` |
| `08-ipv4.png` | `ipconfig`, liaison sur l'IPv4 actuelle et connexion locale |
| `09-deux-pc.png` | Seulement après essai réel : `Test-NetConnection` et les deux terminaux sur deux PC |
| `10-arret.png` | Option `0` et fermeture propre |

## Difficultés et conclusion

La multiplication d'un grand `int` déborde si elle reste en `int` : le serveur 1-2 utilise `long`. La division peut être fractionnaire : le protocole 1-3 renvoie un `double`. Le code de fin diffère entre les activités : `x = 0` en 1-2, choix du menu `0` en 1-3. Les entrées invalides ne doivent pas casser une session active.

Les trois activités compilent et les essais locaux décrits ci-dessus ont réussi. La connexion réelle entre deux PC, les captures et le dépôt du lien sur l'ENT restent à réaliser.

## Dépôt GitHub

Dépôt existant : [gharyba/TP1_DAR2026](https://github.com/gharyba/TP1_DAR2026). La branche locale `main` a un distant `origin` configuré vers ce dépôt ; l'activité 1-1 y avait déjà été publiée. Les changements des activités 1-2 et 1-3 **n'ont pas été poussés**. Vérifier les différences et les captures avant une nouvelle publication ; déposer ensuite le lien sur l'ENT manuellement.
