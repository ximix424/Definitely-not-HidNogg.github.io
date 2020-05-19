package sample.controllers;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.Main;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class KeyController extends Controller {

    private KeyObject keyObject, previousKeyObject;
    private Canvas canvas;

    private long time;

    private static KeyController instance;

    public static KeyController getInstance() {
        if (instance == null) {
            System.out.println("Key Controller instantiated");
            instance = new KeyController();
        }
        return instance;
    }

    private KeyController() {

        keyObject = new KeyObject();
        previousKeyObject = new KeyObject();
        this.canvas = Main.canvas;

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!keyObject.keys.containsKey(keyEvent.getCode())) {
                    keyObject.keys.put(keyEvent.getCode(), 0L);
                }else{
                    keyObject.keys.compute(keyEvent.getCode(), (key, oldValue) -> {
                        if(keyEvent.getCode() != null){
                            return oldValue+time;
                        }else{
                            return 0L;
                        }
                    });
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                keyObject.keys.remove(keyEvent.getCode());
            }
        });
    }

    @Override
    public void update(long diffMillis) {
        time = diffMillis;

        previousKeyObject = keyObject;          // Derzeitiges KeyObject abspeichern
        keyObject = new KeyObject();            // Neues KeyObject erzeugen

        keyObject.getKeyHashMap().putAll(previousKeyObject.getKeyHashMap());    // Alle vorherigen Keys in das neue KeyObject kopieren
    }

    public boolean isKeyPressed(KeyCode keyCode)
    {
        if (!keyObject.getKeyHashMap().containsKey(keyCode))
        {
            return false;
        }

        if (isSinglePressKey(keyCode))
        {
            return !previousKeyObject.getKeyHashMap().containsKey(keyCode);
        }
        return true;
    }

    // Keys which are only allowed to activate once per press
    private boolean isSinglePressKey(KeyCode keyCode) {
        return switch (keyCode) {
            case W, S, UP, DOWN -> true;
            default -> false;
        };
    }

    public static class KeyObject {

        private final ConcurrentHashMap<KeyCode, Long> keys;

        public KeyObject() {
            keys = new ConcurrentHashMap<>();
        }

        public ConcurrentHashMap<KeyCode, Long> getKeyHashMap() {
            return keys;
        }

        public void print(){
           keys.forEach((key, value) -> System.out.println("Key: "+key+" Value: "+value));
        }

    }

    public boolean isKeyReleased(KeyCode keyCode){
        return !keyObject.getKeyHashMap().containsKey(keyCode) && previousKeyObject.getKeyHashMap().containsKey(keyCode);
    }

    public long getKeyPressedTime(KeyCode keyCode){
        if(keyObject.getKeyHashMap().containsKey(keyCode)){
            return keyObject.getKeyHashMap().get(keyCode);
        }
        return 0;

    }

}
