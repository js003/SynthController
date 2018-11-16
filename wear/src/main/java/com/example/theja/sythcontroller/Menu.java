package com.example.theja.sythcontroller;

public class Menu {
    private String text;
    private int icon;

    public Menu(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return this.text;
    }

    public int getIcon() {
        return this.icon;
    }

}
