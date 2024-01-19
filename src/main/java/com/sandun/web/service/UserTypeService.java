package com.sandun.web.service;

import com.sandun.web.entities.UserType;
import com.sandun.web.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserTypeService {
    SessionFactory sessionFactory;

    public UserTypeService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public UserType getByName(String name) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.createQuery("select ut from UserType ut where ut.name=:name ", UserType.class).setParameter("name", name).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public UserType getById(int id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.find(UserType.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
