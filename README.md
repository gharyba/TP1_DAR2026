# TP1 — Développement d'Applications Réparties

**Faculté des Sciences de Sfax — LSI3 — 2026–2027**
**Activité 1-1 : première connexion client/serveur en TCP**
**Auteur : Gharbi Youssef**

## 1. Présentation du TP

Ce projet contient deux applications Java distinctes. Le serveur attend une connexion TCP sur le port 5000 ; le client s'y connecte. Cette activité vérifie l'établissement de la connexion, sans échange de données.

## 2. Objectifs

- Créer un serveur avec `ServerSocket` et `accept()`.
- Créer un client avec `Socket` et tester `localhost`, puis une adresse IPv4.
- Vérifier la fermeture des ressources et les erreurs de connexion.

## 3. Environnement de développement

Windows, Visual Studio Code, OpenJDK Temurin 21 et Git. Versions relevées : `java` et `javac` 21.0.12.1, Git 2.56.0.windows.2. Aucun framework ni gestionnaire de dépendances n'est nécessaire.

## 4. Structure du projet

```text
TP1-DAR2026/
├── src/
│   ├── serverPackage/Server.java
│   ├── clientPackage/Client.java
│   └── App.java                # Fichier initial de VS Code, hors TP
├── .vscode/settings.json
├── .gitignore
└── README.md
```

Les fichiers `.class` sont produits dans `bin/`, ignoré par Git. Le dossier `gharbi_youssef_LSI3_DevAppReparties`, conforme au modèle de nom de l'énoncé, existe à côté du dépôt ; il conserve le brouillon initial du client et un ancien `Serveur.class`. Les sources finales sont dans `TP1-DAR2026/src`.

## 5. Préparation de Git

Depuis le dossier `TP1-DAR2026` :

```powershell
git --version
git status
git remote -v
```

Le dépôt local existait avec un commit initial. Aucun dépôt distant n'était configuré lors de l'audit. Le `.gitignore` exclut les classes compilées, `bin/`, `out/` et les journaux. La publication et le lien ENT restent à effectuer par l'étudiant.

## 6. Préparation de Java

```powershell
java --version
javac --version
javac -encoding UTF-8 -d bin src/serverPackage/Server.java src/clientPackage/Client.java
```

La compilation finale des deux classes a réussi sans erreur avec JDK 21. Dans VS Code, ouvrir `TP1-DAR2026` ; `.vscode/settings.json` définit `src` comme dossier source et `bin` comme sortie.

## 7. Création du serveur TCP

`serverPackage.Server` ouvre un `ServerSocket` sur le port 5000, affiche un message d'attente, puis appelle `accept()`. Après la connexion, il affiche l'adresse du client. Les blocs `try` avec ressources ferment le socket client et le socket serveur. Les erreurs sont affichées sur la sortie d'erreur.

## 8. Création du client TCP

`clientPackage.Client` affiche sa tentative de connexion, ouvre un `Socket`, se connecte au port 5000 et affiche le succès. Il utilise `localhost` par défaut ; un argument permet une autre adresse. Le socket est fermé automatiquement. Le délai de connexion est de trois secondes.

## 9. Exécution des applications

Compiler avec la commande de la section 6, puis ouvrir **deux terminaux PowerShell** dans `TP1-DAR2026`.

Terminal 1 :

```powershell
java -cp bin serverPackage.Server
```

Attendre le message du serveur. Terminal 2 :

```powershell
java -cp bin clientPackage.Client
```

Le serveur traite une connexion, puis s'arrête. Le relancer pour chaque nouvel essai.

## 10. Tests avec localhost

Résultat réellement vérifié sur la même machine : avant le client, le serveur a affiché `Serveur en attente d'un client sur le port 5000...`. Le client a affiché `Connexion au serveur localhost:5000...`, puis `Connexion au serveur réussie !`. Le serveur a affiché `Client connecté : 127.0.0.1` et s'est terminé. Le client a quitté avec le code 0.

## 11. Tests avec une adresse IP

Relever l'adresse IPv4 de l'interface réseau active :

```powershell
ipconfig
```

Lors du test, l'interface Ethernet active avait l'adresse `192.168.1.178`. Après avoir relancé le serveur :

```powershell
java -cp bin clientPackage.Client 192.168.1.178
```

Le client a affiché le message de réussite et le serveur `Client connecté : 192.168.1.178`. C'est un test **sur le même ordinateur**, via son IPv4 locale, et non un test entre deux ordinateurs. Cette adresse peut changer : refaire `ipconfig` avant un nouvel essai. Pour tester depuis un autre ordinateur du même réseau, lancer le serveur sur le premier, utiliser son IPv4 sur le second et vérifier que le pare-feu autorise le port 5000.

## 12. Résultats et interprétations

| Essai | Résultat observé |
|---|---|
| Compilation des deux classes | Réussie, sans erreur. |
| Serveur seul | Message d'attente affiché. |
| Client avec `localhost` | Connexion réussie ; serveur : `127.0.0.1`. |
| Client avec l'IPv4 locale | Connexion réussie ; serveur : `192.168.1.178`. |
| Client sans serveur | `Erreur de connexion : Connection refused: getsockopt`, code 1. |
| Deux serveurs sur le port 5000 | Le second affiche `Erreur du serveur : Address already in use: bind`, code 1. |

`localhost` désigne la machine elle-même, ici via `127.0.0.1`. Une IPv4 comme `192.168.1.178` désigne une interface réseau de cette machine et peut servir à joindre le serveur depuis le réseau local si la configuration le permet. Le port TCP identifie l'application. Le serveur doit être lancé avant le client pour recevoir la connexion ; sinon le client obtient une erreur de connexion refusée.

## 13. Captures d'écran

Captures **à réaliser** ; aucune capture n'a été fabriquée ou ajoutée :

1. `captures/01-git-java.png` : `git --version`, `java --version`, `javac --version` et `git status`.
2. `captures/02-structure.png` : explorateur VS Code montrant les deux packages et les deux sources.
3. `captures/03-localhost.png` : deux terminaux montrant l'attente, la connexion avec `localhost` et la détection du client.
4. `captures/04-ipv4.png` : `ipconfig`, puis les deux terminaux montrant l'essai avec l'IPv4 active.
5. `captures/05-erreurs.png` : facultatif, client sans serveur ou second serveur sur le même port.

Placer les images dans `captures/`, puis remplacer cet emplacement par les liens des fichiers réellement créés :

```markdown
![Git et Java](captures/01-git-java.png)
![Structure Java](captures/02-structure.png)
![Test localhost](captures/03-localhost.png)
![Test IPv4](captures/04-ipv4.png)
```

## 14. Conclusion

Les deux sources compilent et établissent une connexion TCP sur le port 5000. Les essais `localhost` et IPv4 locale ont réussi ; les erreurs de serveur absent et de port occupé ont été observées. Il reste à produire les captures, publier le dépôt après validation et déposer son lien sur l'ENT.
