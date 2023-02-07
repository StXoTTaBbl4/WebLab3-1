package com.example.demo;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Model;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.persistence.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Model
@ApplicationScoped
public class EntriesBean implements Serializable {

    private Entry entry;
    private List<Entry> entries;

    private Session session;
    private Transaction transaction;

    public EntriesBean() {
        entry = new Entry();
        entries = new ArrayList<>();

        connection();
        loadEntries();

        System.out.println("DB connection established");
    }

    private void connection() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.getTransaction();
    }

    private void loadEntries() {
        try {
            transaction.begin();
            Query query = session.createQuery( " FROM Entry", Entry.class);
            entries = query.getResultList();
            transaction.commit();
            //System.out.println("Reading student records..." + "Length of elements: " + entries.size() + " Hit 1 elem: " + entries.get(0).getHitResult());
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    public void addEntry() {
        System.out.println("Add method invoked");
        try {
            System.out.println(entry.getxValue());
            System.out.println(entry.getyValue());
            System.out.println(entry.getrValue());
            transaction.begin();
            entry.checkHit();
            session.persist(entry);
            entries.add(entry);
            entry = new Entry();
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
  
    }
    public void addEntryWithParameters(){
        System.out.println("canvas click!");
        if(entry==null) entry=new Entry();
        try {
            Map<String, String> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            transaction.begin();
            entry.setxValue(Double.parseDouble(paramMap.get("x")));
            entry.setyValue(Double.parseDouble(paramMap.get("y")));
            entry.setrValue(Double.parseDouble(paramMap.get("r")));
            System.out.println(paramMap.get("x"));
            System.out.println(paramMap.get("y"));
            System.out.println(paramMap.get("r"));
            entry.checkHit();
            session.persist(entry);
            entries.add(entry);
            entry=new Entry();
            transaction.commit();
        } catch (RuntimeException exception) {
            System.out.println("error:" + exception.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }
    public String clearEntries() {
        try {
            transaction.begin();
            Query query = session.createQuery("DELETE FROM Entry", Entry.class);
            query.executeUpdate();
            entries.clear();
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
        return "redirect";
    }

    public void rInputChanged(ValueChangeEvent event) {
        FacesMessage message = new FacesMessage("Input Changed", "Value: " + event.getNewValue());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
