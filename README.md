# Mon Jeu

Petit jeu top-down en Java avec [libGDX](https://libgdx.com). Un joueur se déplace
dans un monde plus grand que l'écran grâce à un joystick virtuel ; la caméra le suit.

## Lancer le jeu

Prérequis : JDK 11 ou plus.

```bash
./gradlew desktop:run
```

Ou ouvrir le dossier dans IntelliJ IDEA (Open → `build.gradle`), puis lancer
`DesktopLauncher` en réglant le **Working directory** sur le dossier `assets/`.

> Si `gradlew` est absent : installer Gradle puis exécuter `gradle wrapper`.

## Contrôles

- **Joystick virtuel** (en bas à gauche) : toucher l'écran (ou cliquer à la souris)
  et glisser pour déplacer le joueur.
- Le joueur se tourne dans la direction du mouvement.
- Pas de clavier : le jeu est pensé pour le tactile.

## Compiler en HTML5 pour GitHub Pages

libGDX utilise GWT pour le web. Le plus simple :

1. Générer un projet avec [gdx-liftoff](https://github.com/libgdx/gdx-liftoff)
   en cochant la plateforme **HTML**.
2. Copier `MonJeu.java` dans le module `core` et les images dans `assets/`.
3. Compiler :
   ```bash
   ./gradlew html:dist
   ```
4. Le site est généré dans `html/build/dist/`.
5. Publier ce dossier sur la branche `gh-pages` de ton dépôt, puis activer
   **Settings → Pages → Branch : gh-pages**.

Le code du jeu n'utilise que des classes compatibles GWT.

## Licence

À choisir (ex. MIT).