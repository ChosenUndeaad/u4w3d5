package catalogo.entity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class CatalogoService {

    private EntityManager em;

    public CatalogoService(EntityManager em) {
        this.em = em;
    }

    public void aggiungiElemento(ElementoCatalogo elemento) {
        try {
            em.getTransaction().begin();
            em.persist(elemento);
            em.getTransaction().commit();
            System.out.println("Elemento aggiunto: " + elemento.getTitolo());
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Errore durante l'aggiunta dell'elemento:");
            e.printStackTrace();
        }
    }

    public void rimuoviElemento(String isbn) {
        try {
            em.getTransaction().begin();
            ElementoCatalogo elemento = em.find(ElementoCatalogo.class, isbn);
            if (elemento != null) {
                em.remove(elemento);
                System.out.println("Elemento rimosso con ISBN: " + isbn);
            } else {
                System.out.println("Elemento con ISBN " + isbn + " non trovato.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public ElementoCatalogo cercaPerISBN(String isbn) {
        return em.find(ElementoCatalogo.class, isbn);
    }

    public List<ElementoCatalogo> cercaPerAnno(int anno) {
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class);
        query.setParameter("anno", anno);
        return query.getResultList();
    }

    public List<Libro> cercaPerAutore(String autore) {
        TypedQuery<Libro> query = em.createQuery(
                "SELECT l FROM Libro l WHERE l.autore = :autore", Libro.class);
        query.setParameter("autore", autore);
        return query.getResultList();
    }

    public List<ElementoCatalogo> cercaPerTitolo(String titolo) {
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE LOWER(e.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))",
                ElementoCatalogo.class);
        query.setParameter("titolo", titolo);
        return query.getResultList();
    }

    public void registraPrestito(Prestito prestito) {
        try {
            em.getTransaction().begin();
            em.persist(prestito);
            em.getTransaction().commit();
            System.out.println("Prestito registrato per utente: " + prestito.getUtente().getNumeroTessera());
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public List<Prestito> prestitiPerUtente(String numeroTessera) {
        TypedQuery<Prestito> query = em.createQuery(
                "SELECT p FROM Prestito p WHERE p.utente.numeroTessera = :tessera AND p.dataRestituzioneEffettiva IS NULL",
                Prestito.class);
        query.setParameter("tessera", numeroTessera);
        return query.getResultList();
    }

    public List<Prestito> prestitiScadutiNonRestituiti() {
        LocalDate oggi = LocalDate.now();
        TypedQuery<Prestito> query = em.createQuery(
                "SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL AND p.dataRestituzionePrevista < :oggi",
                Prestito.class);
        query.setParameter("oggi", oggi);
        return query.getResultList();
    }
}
