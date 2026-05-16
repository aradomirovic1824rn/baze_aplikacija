package org.baze.neuronauka.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class SesijaDTO {

    private int       idSesija;
    private LocalDate datumSesije;
    private LocalTime vremePocetka;
    private LocalTime vremeKraja;
    private String    tipSesije;    // baseline | stimulus | recovery | wash_out
    private String    eksperiment;  // naziv eksperimenta
    private String    laboratorija; // naziv laboratorije

    // ── getteri ──────────────────────────────────────────────────
    public int       getIdSesija()     { return idSesija; }
    public LocalDate getDatumSesije()  { return datumSesije; }
    public LocalTime getVremePocetka() { return vremePocetka; }
    public LocalTime getVremeKraja()   { return vremeKraja; }
    public String    getTipSesije()    { return tipSesije; }
    public String    getEksperiment()  { return eksperiment; }
    public String    getLaboratorija() { return laboratorija; }

    // ── seteri ───────────────────────────────────────────────────
    public void setIdSesija(int v)           { this.idSesija = v; }
    public void setDatumSesije(LocalDate v)  { this.datumSesije = v; }
    public void setVremePocetka(LocalTime v) { this.vremePocetka = v; }
    public void setVremeKraja(LocalTime v)   { this.vremeKraja = v; }
    public void setTipSesije(String v)       { this.tipSesije = v; }
    public void setEksperiment(String v)     { this.eksperiment = v; }
    public void setLaboratorija(String v)    { this.laboratorija = v; }
}