package com.fiszki;

public class MainMenuName {
    private String name;
    private int imgResourcedID;

    public static final MainMenuName [] mainmenuelements = {
      new MainMenuName("WŁASNE",R.drawable.wp),
      new MainMenuName("",R.drawable.nointernetconnection)
    };

    public MainMenuName(String name, int imgResourcedID) {
        this.name = name;
        this.imgResourcedID = imgResourcedID;
    }


    public String getName() {
        return name;
    }

    public int getImgResourcedID() {
        return imgResourcedID;
    }



}
