package com.monjeu.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.monjeu.MonJeu;

/** Point d'entrée de la version bureau (LWJGL3). */
public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Mon Jeu");           // titre de la fenêtre
        config.setWindowedMode(800, 480);     // taille de départ
        config.useVsync(true);                // synchronisation verticale
        config.setForegroundFPS(60);          // limite à 60 images/s

        new Lwjgl3Application(new MonJeu(), config);
    }
}