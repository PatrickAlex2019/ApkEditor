package com.gmail.heagoo.apkeditor.translate;

import java.io.Serializable;

public class TranslateItem implements Serializable {

    private static final long serialVersionUID = -3101805950698159689L;
    public String name;
    public String originValue;
    public String translatedValue;

    public TranslateItem(String _n, String _o) {
        this.name = _n;
        this.originValue = _o;
    }

    public TranslateItem(String _n, String _o, String _t) {
        this.name = _n;
        this.originValue = _o;
        this.translatedValue = _t;
    }
}
