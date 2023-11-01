package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.io.Serializable;

public class Dao<T, Id extends Serializable> {
    private SessionFactory sessionFactory;

    public void setup(String configFile) throws Exception {
        // code to load Hibernate Session factory
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(configFile) // gets configuration settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);

            throw new Exception("Error creating sessionFactory");
        }
    }

    public void exit() {
        // code to close Hibernate Session factory
        sessionFactory.close();
    }

    public void create(T object) {
        // save the object to the database
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.persist(object);

        transaction.commit();
        session.close();
    }

    public T read(Class<T> T, Id id){
        // read the object of Class [T] with the given id
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        T object = session.get(T, id);

        transaction.commit();
        session.close();

        return object;
    }

    public void update(T object){
        // update the object in the database with the same id as [object]
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.merge(object);

        transaction.commit();
        session.close();
    }

    public void delete(T object){
        // remove the object from the database with the same id as [object]
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.remove(object);

        transaction.commit();
        session.close();
    }

    public void clear(String tableName){
        // clear the database
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE FROM " + tableName);
        query.executeUpdate();
        
        transaction.commit();
        session.close();
    }
}