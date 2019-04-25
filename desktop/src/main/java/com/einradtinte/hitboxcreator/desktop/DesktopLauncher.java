package com.einradtinte.hitboxcreator.desktop;

import com.badlogic.gdx.Files.FileType;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.lml.actions.GlobalActions;

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
        configuration.setWindowListener(new Lwjgl3WindowListener() {
            @Override
            public void created(Lwjgl3Window lwjgl3Window) {

            }

            @Override
            public void iconified(boolean b) {

            }

            @Override
            public void maximized(boolean b) {

            }

            @Override
            public void focusLost() {

            }

            @Override
            public void focusGained() {

            }

            @Override
            public boolean closeRequested() {
                if (App.inst().getModelService().hasProjectChanged()) {
                    GlobalActions.showUnsavedChangesDlg(App.inst().getI18NBundle().format("dlgTextUnsavedClose"), "exit");
                    return false;
                }
                return true;
            }

            @Override
            public void filesDropped(String[] strings) {

            }

            @Override
            public void refreshRequested() {

            }
        });
        //configuration.width = 640;
        //configuration.height = 480;

        for (int size : new int[] { 128, 64, 32, 16 }) {
            configuration.setWindowIcon(FileType.Internal,"libgdx" + size + ".png");
        }
        return configuration;
    }
}