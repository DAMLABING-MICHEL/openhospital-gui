package org.isf.opd.model;

/**
 * Created by nicosalvato on 2017-01-31.
 * Contact: nicosalvato@gmail.com
 */
public class OpdRow {
    private Opd opd;
    private String patPCode;

    public OpdRow(Opd opd, String patPCode) {
        this.opd = opd;
        this.patPCode = patPCode;
    }

    public void setOpd(Opd opd) {
        this.opd = opd;
    }

    public Opd getOpd() {
        return opd;
    }

    public void setPatPCode(String patPCode) {
        this.patPCode = patPCode;
    }

    public String getPatPCode() {
        return patPCode;
    }
}
