package com.monjeu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Jeu top-down minimal : un joueur se déplace dans un monde plus grand
 * que l'écran grâce à un joystick virtuel. La caméra suit le joueur.
 */
public class MonJeu extends ApplicationAdapter {

    // ----- Constantes du jeu -----
    private static final float MONDE_LARGEUR = 2000f;  // taille du monde (pixels)
    private static final float MONDE_HAUTEUR = 2000f;
    private static final float TAILLE_JOUEUR = 64f;
    private static final float TAILLE_TUILE = 64f;
    private static final float VITESSE = 300f;         // pixels par seconde
    private static final float ZONE_MORTE = 20f;       // rayon (px) ignoré au centre du joystick
    private static final float VITESSE_ROTATION = 15f; // plus grand = rotation plus rapide

    // ----- Rendu du monde -----
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewportMonde;   // adapte le monde à la taille de l'écran
    private Texture texJoueur;
    private Texture texTuile;
    private Sprite joueur;

    // ----- Interface (joystick) -----
    private Stage stage;
    private Texture texFond;
    private Texture texBouton;
    private Touchpad joystick;

    /** Appelée une fois au démarrage : on crée tout ici. */
    @Override
    public void create() {
        batch = new SpriteBatch();

        // Sol : la texture est répétée automatiquement (mode "Repeat")
        texTuile = new Texture("tuile.png");
        texTuile.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Joueur : placé au centre du monde
        texJoueur = new Texture("joueur.png");
        joueur = new Sprite(texJoueur);
        joueur.setSize(TAILLE_JOUEUR, TAILLE_JOUEUR);
        joueur.setOriginCenter(); // la rotation se fera autour du centre
        joueur.setPosition((MONDE_LARGEUR - TAILLE_JOUEUR) / 2f,
                           (MONDE_HAUTEUR - TAILLE_JOUEUR) / 2f);

        // Caméra du monde : montre au minimum 800x480 unités
        camera = new OrthographicCamera();
        viewportMonde = new ExtendViewport(800, 480, camera);

        // Interface : Stage avec ScreenViewport (1 unité = 1 pixel d'écran)
        stage = new Stage(new ScreenViewport());
        joystick = creerJoystick();
        stage.addActor(joystick);

        // Le Stage reçoit les touches tactiles ET les clics de souris
        Gdx.input.setInputProcessor(stage);
    }

    /** Construit le joystick virtuel avec ses deux images. */
    private Touchpad creerJoystick() {
        texFond = new Texture("joystick_fond.png");
        texBouton = new Texture("joystick_bouton.png");

        TouchpadStyle style = new TouchpadStyle();
        style.background = new TextureRegionDrawable(texFond);

        TextureRegionDrawable bouton = new TextureRegionDrawable(texBouton);
        bouton.setMinWidth(80);   // taille du bouton mobile
        bouton.setMinHeight(80);
        style.knob = bouton;

        Touchpad pad = new Touchpad(ZONE_MORTE, style);
        pad.setBounds(40, 40, 200, 200); // x, y, largeur, hauteur : bas à gauche
        return pad;
    }

    /** Appelée à chaque image : mise à jour puis dessin. */
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime(); // temps écoulé depuis la dernière image

        stage.act(delta);
        mettreAJour(delta);
        dessiner();
    }

    /** Logique du jeu : déplacement, rotation, limites, caméra. */
    private void mettreAJour(float delta) {
        // Direction du joystick, entre -1 et 1 (0 dans la zone morte)
        float dirX = joystick.getKnobPercentX();
        float dirY = joystick.getKnobPercentY();

        if (dirX != 0f || dirY != 0f) {
            // Déplacement indépendant du framerate : vitesse * delta
            joueur.translate(dirX * VITESSE * delta, dirY * VITESSE * delta);

            // Rotation : l'image regarde vers le haut, d'où le -90°
            float angleCible = MathUtils.atan2(dirY, dirX) * MathUtils.radiansToDegrees - 90f;
            float angle = MathUtils.lerpAngleDeg(joueur.getRotation(), angleCible,
                                                 Math.min(1f, VITESSE_ROTATION * delta));
            joueur.setRotation(angle);
        }

        // Limites : le joueur reste dans le monde
        joueur.setPosition(
            MathUtils.clamp(joueur.getX(), 0, MONDE_LARGEUR - TAILLE_JOUEUR),
            MathUtils.clamp(joueur.getY(), 0, MONDE_HAUTEUR - TAILLE_JOUEUR));

        // Caméra centrée sur le joueur, sans sortir du monde
        float demiLargeur = camera.viewportWidth / 2f;
        float demiHauteur = camera.viewportHeight / 2f;
        camera.position.x = MathUtils.clamp(joueur.getX() + TAILLE_JOUEUR / 2f,
                                            demiLargeur, MONDE_LARGEUR - demiLargeur);
        camera.position.y = MathUtils.clamp(joueur.getY() + TAILLE_JOUEUR / 2f,
                                            demiHauteur, MONDE_HAUTEUR - demiHauteur);
        camera.update();
    }

    /** Dessin : d'abord le monde (avec caméra), puis l'interface par-dessus. */
    private void dessiner() {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f); // fond sombre

        viewportMonde.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Sol répété : les coordonnées de texture dépassent 1 => la tuile se répète
        batch.draw(texTuile, 0, 0, MONDE_LARGEUR, MONDE_HAUTEUR,
                   0f, 0f, MONDE_LARGEUR / TAILLE_TUILE, MONDE_HAUTEUR / TAILLE_TUILE);
        joueur.draw(batch);
        batch.end();

        stage.draw(); // le joystick, fixe à l'écran
    }

    /** Appelée quand la fenêtre change de taille (ou à la rotation du téléphone). */
    @Override
    public void resize(int largeur, int hauteur) {
        viewportMonde.update(largeur, hauteur);
        stage.getViewport().update(largeur, hauteur, true);
    }

    /** Libère toute la mémoire native (GPU) utilisée. */
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        texJoueur.dispose();
        texTuile.dispose();
        texFond.dispose();
        texBouton.dispose();
    }
}