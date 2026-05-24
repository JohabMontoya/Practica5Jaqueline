package com.example.practica5jaqueline;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;

import java.io.File;
public class SoundButton extends Button {


    private AudioClip sonidoHover;
    private AudioClip sonidoClic;

    public SoundButton() {
        this("");
    }

    public SoundButton(String texto) {
        super(texto);
        setFocusTraversable(false);
        addEventHandler(MouseEvent.MOUSE_ENTERED, e -> reproducir(sonidoHover));
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> reproducir(sonidoClic));
    }

    private void reproducir(AudioClip clip) {
        if (clip != null) {
            clip.play();
        }
    }

    public void establecerSonidoHover(File f) {
        this.sonidoHover = (f != null && f.exists())
                ? new AudioClip(f.toURI().toString())
                : null;
    }

    public void establecerSonidoClic(File f) {
        this.sonidoClic = (f != null && f.exists())
                ? new AudioClip(f.toURI().toString())
                : null;
    }
}
