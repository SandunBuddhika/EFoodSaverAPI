package com.sandun.web.service;

import com.sandun.web.entities.User;
import com.sandun.web.entities.UserType;
import com.sandun.web.util.HibernateUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserService {

    @Inject
    UserTypeService userTypeService;
    SessionFactory sessionFactory;

    public UserService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<Object> insert(com.sandun.web.dto.SignUpAuth user) {
        System.out.println(user);
        List<Object> returnValue = new ArrayList<>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            UserType userType = userTypeService.getByName(user.getType());
            if (userType == null) {
                returnValue.add("false");
                return returnValue;
            }
            User iUser = null;
            String code = UUID.randomUUID().toString().replaceAll("[a-zA-Z-]", "");
            if (code.length() > 6) {
                code = code.substring(0, 5);
            }
            if (user.getImg() != null && !user.getImg().isBlank() && !user.getImg().equals("null")) {
                iUser = new com.sandun.web.entities.User(user.getfName(), user.getlName(), user.getEmailOrCno(), userType, user.getPassword(), user.getImg(), code, 1);
                session.persist(iUser);
            } else {
                iUser = new com.sandun.web.entities.User(user.getfName(), user.getlName(), user.getEmailOrCno(), userType, user.getPassword(), code, 0);
                session.persist(iUser);
            }
            transaction.commit();
            returnValue.add("true");
            returnValue.add(code);
            returnValue.add(iUser);
            return returnValue;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            returnValue.add("false");
            return returnValue;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public User signIn(String eorc, String password, String type) {
        Session session = null;
        try {
            UserType userType = userTypeService.getByName(type);
            if (userType == null) {
                return null;
            }
            session = sessionFactory.openSession();
            return session.createNamedQuery("signInQ", com.sandun.web.entities.User.class).setParameter("eorc", eorc).setParameter("password", password).setParameter("type", type).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<User> getAllUsers() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.createNamedQuery("getAllUsers", User.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public User checkVerification(Long id, String code) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.createNamedQuery("checkVerification", com.sandun.web.entities.User.class).setParameter("id", id).setParameter("code", code).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public User getById(Long id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean update(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
