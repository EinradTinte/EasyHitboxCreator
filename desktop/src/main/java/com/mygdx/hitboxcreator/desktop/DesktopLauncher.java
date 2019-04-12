package com.mygdx.hitboxcreator.desktop;

import com.badlogic.gdx.Files.FileType;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.hitboxcreator.App;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new App(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Easy Hitbox Creator");
        configuration.setWindowedMode(App.WIDTH, App.HEIGHT);
        //configuration.width = 640;
        //configuration.height = 480;

        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.setWindowIcon(FileType.Internal,"libgdx" + size + ".png");
        }
        return configuration;
    }
}