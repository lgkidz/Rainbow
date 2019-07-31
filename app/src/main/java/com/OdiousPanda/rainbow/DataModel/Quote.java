package com.OdiousPanda.rainbow.DataModel;

public class Quote {
    private String main;
    private String sub;
    private String att;

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setDefaultQuote() {
        this.main = "If you see these lines.";
        this.sub = "Something went wrong... :|";
    }
}
