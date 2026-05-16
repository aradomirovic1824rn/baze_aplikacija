package org.baze.neuronauka.dao;

import org.baze.neuronauka.db.DBConnection;
import org.baze.neuronauka.model.SesijaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SesijaDAO {

    // ─────────────────────────────────────────────────────────────
    // POMOĆNA METODA — čita ResultSet red po red i pravi SesijaDTO
    // Koristimo je na više mesta da ne dupliramo kod
    // ─────────────────────────────────────────────────────────────
    private SesijaDTO mapRow(ResultSet rs) throws SQLException {
        SesijaDTO dto = new SesijaDTO();
        dto.setIdSesija(rs.getInt("id_sesija"));
        dto.setDatumSesije(rs.getDate("datum_sesije").toLocalDate());
        dto.setVremePocetka(rs.getTime("vreme_pocetka").toLocalTime());
        dto.setVremeKraja(rs.getTime("vreme_kraja").toLocalTime());
        dto.setTipSesije(rs.getString("tip_sesije"));
        dto.setEksperiment(rs.getString("eksperiment"));
        dto.setLaboratorija(rs.getString("laboratorija"));
        return dto;
    }

    // ─────────────────────────────────────────────────────────────
    // 1. SVE SESIJE — za punjenje kalendara
    //
    // Lanac JOIN-ova:
    //   Sesija
    //     → Izvodjenje       (da dođemo do eksperimenta i laboratorije)
    //     → Eksperiment      (naziv eksperimenta)
    //     → Laboratorija     (naziv laboratorije)
    //
    // Nema parametara → koristimo Statement (ne PreparedStatement)
    // ─────────────────────────────────────────────────────────────
    public List<SesijaDTO> getSveSesjeSaEksperimentima() {
        List<SesijaDTO> lista = new ArrayList<>();

        String sql = """
                SELECT
                    s.id_sesija,
                    s.datum_sesije,
                    s.vreme_pocetka,
                    s.vreme_kraja,
                    s.tip_sesije,
                    e.naziv  AS eksperiment,
                    l.naziv  AS laboratorija
                FROM      Sesija       s
                JOIN      Izvodjenje   iz  ON s.id_izvodjenje    = iz.id_izvodjenje
                JOIN      Eksperiment  e   ON iz.id_eksperimenta = e.id_eksperiment
                JOIN      Laboratorija l   ON iz.id_laboratorija = l.id_laboratorija
                ORDER BY  s.datum_sesije, s.vreme_pocetka
                """;

        try (Connection con = DBConnection.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // 2. SESIJE FILTRIRANE po laboratoriji i/ili eksperimentu
    //
    // Trik: NULL u PreparedStatement-u znači "filter nije odabran"
    // → SQL upit sam odlučuje da ignoriše taj filter
    // → cela logika filtriranja ostaje u SQL-u, ne u Javi
    //
    // Ima parametre → koristimo PreparedStatement
    // ─────────────────────────────────────────────────────────────
    public List<SesijaDTO> getSesijeFiltered(String laboratorija, String eksperiment) {
        List<SesijaDTO> lista = new ArrayList<>();

        String sql = """
                SELECT
                    s.id_sesija,
                    s.datum_sesije,
                    s.vreme_pocetka,
                    s.vreme_kraja,
                    s.tip_sesije,
                    e.naziv  AS eksperiment,
                    l.naziv  AS laboratorija
                FROM      Sesija       s
                JOIN      Izvodjenje   iz  ON s.id_izvodjenje    = iz.id_izvodjenje
                JOIN      Eksperiment  e   ON iz.id_eksperimenta = e.id_eksperiment
                JOIN      Laboratorija l   ON iz.id_laboratorija = l.id_laboratorija
                WHERE
                    (? IS NULL OR l.naziv = ?)
                    AND (? IS NULL OR e.naziv = ?)
                ORDER BY  s.datum_sesije, s.vreme_pocetka
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Ako je null prosleđeno → SQL ignoruje taj filter (? IS NULL je true)
            // Ako je vrednost prosleđena → SQL filtrira po njoj
            ps.setString(1, laboratorija);
            ps.setString(2, laboratorija);
            ps.setString(3, eksperiment);
            ps.setString(4, eksperiment);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // 3. DROPDOWN — svi nazivi laboratorija
    //
    // Nema parametara → Statement
    // ─────────────────────────────────────────────────────────────
    public List<String> getLaboratorije() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT naziv FROM Laboratorija ORDER BY naziv";

        try (Connection con = DBConnection.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(rs.getString("naziv"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // 4. DROPDOWN — svi nazivi eksperimenata
    //
    // Nema parametara → Statement
    // ─────────────────────────────────────────────────────────────
    public List<String> getEksperimenti() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT naziv FROM Eksperiment ORDER BY naziv";

        try (Connection con = DBConnection.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(rs.getString("naziv"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // 5. UPDATE SESIJE — ima parametre → PreparedStatement
    //
    // Baca SQLException ako trigger u bazi (sesija_nooverlap_update)
    // detektuje vremensko preklapanje sa drugom sesijom.
    // Caller (controller) hvata tu grešku i prikazuje je korisniku.
    // ─────────────────────────────────────────────────────────────
    public void updateSesija(int idSesija,
                             java.time.LocalDate datum,
                             java.time.LocalTime pocetakVremena,
                             java.time.LocalTime krajVremena,
                             String tipSesije) throws SQLException {

        String sql = """
                UPDATE Sesija
                SET
                    datum_sesije  = ?,
                    vreme_pocetka = ?,
                    vreme_kraja   = ?,
                    tip_sesije    = ?
                WHERE id_sesija = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(datum));
            ps.setTime(2, java.sql.Time.valueOf(pocetakVremena));
            ps.setTime(3, java.sql.Time.valueOf(krajVremena));
            ps.setString(4, tipSesije);
            ps.setInt(5, idSesija);

            ps.executeUpdate();
            // ako trigger puca, executeUpdate() baca SQLException
            // — ne hvatamo je ovde, propagiramo je ka controlleru
        }
    }
}