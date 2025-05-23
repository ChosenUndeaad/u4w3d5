package catalogo.entity;

import catalogo.entity.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;

import java.util.List;

public class CatalogoService {

    private EntityManager em;

    public CatalogoService(EntityManager em) {
        this.em = em;
    }

    // Aggiunta elemento catalogo (libro o rivista)
    public void aggiungiElemento(ElementoCatalogo elemento) {
        em.getTransaction().begin();
        em.persist(elemento);
        em.getTransaction().commit();
    }

    // Rimozione elemento dato ISBN
    public boolean rimuoviElemento(String isbn) {
        em.getTransaction().begin();
        ElementoCatalogo elemento = em.find(ElementoCatalogo.class, isbn);
        if (elemento != null) {
            em.remove(elemento);
            em.getTransaction().commit();
            return true;
        }
        em.getTransaction().rollback();
        return false;
    }

    // Ricerca per ISBN
    public ElementoCatalogo cercaPerIsbn(String isbn) {
        return em.find(ElementoCatalogo.class, isbn);
    }

    // Ricerca per anno pubblicazione
    public List<ElementoCatalogo> cercaPerAnno(int anno) {
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class);
        query.setParameter("anno", anno);
        return query.getResultList();
    }

    // Ricerca per autore (solo libri)
    public List<Libro> cercaPerAutore(String autore) {
        TypedQuery<Libro> query = em.createQuery(
                "SELECT l FROM Libro l WHERE LOWER(l.autore) LIKE LOWER(:autore)", Libro.class);
        query.setParameter("autore", "%" + autore + "%");
        return query.getResultList();
    }

    // Ricerca per titolo o parte di esso (sia libri che riviste)
    public List<ElementoCatalogo> cercaPerTitolo(String titolo) {
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE LOWER(e.titolo) LIKE LOWER(:titolo)", ElementoCatalogo.class);
        query.setParameter("titolo", "%" + titolo + "%");
        return query.getResultList();
    }

    // Ricerca degli elementi attualmente in prestito dato numero tessera utente
    public List<Prestito> cercaPrestitiAttiviPerTessera(String numeroTessera) {
        TypedQuery<Prestito> query = em.createQuery(
                "SELECT p FROM Prestito p WHERE p.utente.numeroTessera = :tessera AND p.dataRestituzioneEffettiva IS NULL",
                Prestito.class);
        query.setParameter("tessera", numeroTessera);
        return query.getResultList();
    }

    // Ricerca prestiti scaduti e non ancora restituiti
    public List<Prestito> cercaPrestitiScadutiNonRestituiti() {
        TypedQuery<Prestito> query = em.createQuery(
                "SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL AND p.dataRestituzionePrevista < CURRENT_DATE",
                Prestito.class);
        return query.getResultList();
    }
}
