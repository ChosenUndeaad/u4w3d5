package catalogo.entity;


import javax.persistence.*;

@Entity
@DiscriminatorValue("RIVISTA")
public class Rivista extends ElementoCatalogo {

    @Enumerated(EnumType.STRING)
    private Periodicita periodicita;

    // Getter e Setter

    public Periodicita getPeriodicita() {
        return periodicita;
    }
    public void setPeriodicita(Periodicita periodicita) {
        this.periodicita = periodicita;
    }
}