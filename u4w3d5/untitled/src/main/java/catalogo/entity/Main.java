package catalogo.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("catalogoPU");
        EntityManager em = emf.createEntityManager();

        CatalogoService service = new CatalogoService(em);

        // Crea un libro
        Libro libro = new Libro();
        libro.setIsbn("1234567890123");
        libro.setTitolo("Java Avanzato");
        libro.setAnnoPubblicazione(2025);
        libro.setNumeroPagine(500);
        libro.setAutore("Giuseppe Verdi");
        libro.setGenere("Programmazione");

        // Crea un utente
        Utente utente = new Utente();
        utente.setNome("Mario");
        utente.setCognome("Rossi");
        utente.setDataNascita(LocalDate.of(1990, 1, 1));
        utente.setNumeroTessera("TESS123");

        // Salva libro e utente
        try {
            em.getTransaction().begin();
            em.persist(utente);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        service.aggiungiElemento(libro);

        // Registra un prestito
        Prestito prestito = new Prestito();
        prestito.setUtente(utente);
        prestito.setElementoPrestato(libro);
        prestito.setDataInizioPrestito(LocalDate.now());
        prestito.setDataRestituzionePrevista(LocalDate.now().plusDays(30));
        prestito.setDataRestituzioneEffettiva(null); // non ancora restituito

        service.registraPrestito(prestito);

        // Recupera i prestiti per utente
        System.out.println("\nPrestiti attivi per utente con tessera TESS123:");
        service.prestitiPerUtente("TESS123").forEach(p ->
                System.out.println("→ " + p.getElementoPrestato().getTitolo() + " - restituzione prevista: " + p.getDataRestituzionePrevista())
        );

        em.close();
        emf.close();
    }
}