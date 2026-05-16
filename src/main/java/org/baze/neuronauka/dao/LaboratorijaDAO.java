package org.baze.neuronauka.dao;

import org.baze.neuronauka.db.DBConnection;
import org.baze.neuronauka.model.LaboratorijaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaboratorijaDAO {

    // ─────────────────────────────────────────────────────────────
    // POMOĆNA METODA — mapira jedan red ResultSet-a u DTO
    // ─────────────────────────────────────────────────────────────
    private LaboratorijaDTO mapRow(ResultSet rs) throws SQLException {
        return new LaboratorijaDTO(
                rs.getInt("id_laboratorija"),
                rs.getString("naziv"),
                rs.getString("tip_laboratorije"),
                rs.getString("opis_lokacije")
        );
    }

    // ─────────────────────────────────────────────────────────────
    // 1. SVE LABORATORIJE sortirane po id-u
    //
    // Nema parametara → Statement
    // ─────────────────────────────────────────────────────────────
    public List<LaboratorijaDTO> getSveLaboratorije() {
        List<LaboratorijaDTO> lista = new ArrayList<>();
        String sql = """
                SELECT id_laboratorija, naziv, tip_laboratorije, opis_lokacije
                FROM   Laboratorija
                ORDER BY id_laboratorija
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
    // 2. PROVERA da li laboratorija ima izvođenja
    //
    // Uslov za brisanje: laboratorija ne sme imati nijedno Izvodjenje.
    // Istraživači su vezani za laboratoriju INDIREKTNO:
    //   Istrazivac → Izvodjenje_Izvodjac → Izvodjenje → Laboratorija
    // Dakle, ako postoji Izvodjenje u toj laboratoriji, postoje i
    // istraživači koji su radili/rade u njoj — brisanje je zabranjeno.
    //
    // Ima parametar → PreparedStatement
    // ─────────────────────────────────────────────────────────────
    public boolean imaIzvodjenja(int idLaboratorija) {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM   Izvodjenje
                WHERE  id_laboratorija = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLaboratorija);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // u slučaju greške, budemo konzervativni i dozvolimo UI da prikaže grešku
    }

    // ─────────────────────────────────────────────────────────────
    // 3. BRISANJE laboratorije
    //
    // Poziva se SAMO ako imaIzvodjenja() vrati false.
    // Baca SQLException ako baza ipak odbije (FK constraint) —
    // controller hvata i prikazuje grešku korisniku.
    //
    // Ima parametar → PreparedStatement
    // ─────────────────────────────────────────────────────────────
    public void deleteLaboratorija(int idLaboratorija) throws SQLException {
        String sql = "DELETE FROM Laboratorija WHERE id_laboratorija = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLaboratorija);
            ps.executeUpdate();
        }
        // SQLException se propagira ka controlleru — ne hvatamo je ovde
    }

    // ─────────────────────────────────────────────────────────────
    // 4. IZMENA laboratorije — menjaju se naziv, tip i opis lokacije
    //
    // Baca SQLException ako baza odbije (duplikat naziva i sl.) —
    // controller hvata i prikazuje grešku korisniku.
    //
    // Ima parametre → PreparedStatement
    // ─────────────────────────────────────────────────────────────
    public void updateLaboratorija(int idLaboratorija,
                                   String naziv,
                                   String tipLaboratorije,
                                   String opisLokacije) throws SQLException {
        String sql = """
                UPDATE Laboratorija
                SET
                    naziv            = ?,
                    tip_laboratorije = ?,
                    opis_lokacije    = ?
                WHERE id_laboratorija = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, naziv);
            ps.setString(2, tipLaboratorije);
            ps.setString(3, opisLokacije);
            ps.setInt(4, idLaboratorija);

            ps.executeUpdate();
        }
        // SQLException se propagira ka controlleru
    }
}