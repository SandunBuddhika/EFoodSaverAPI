package com.sandun.web.service;

import com.sandun.web.entities.UserType;
import com.sandun.web.entities.address.City;
import com.sandun.web.entities.address.District;
import com.sandun.web.entities.address.Location;
import com.sandun.web.entities.address.Province;
import com.sandun.web.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class LocationService {
    private SessionFactory sessionFactory;

    public LocationService() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Location getLocation(String city, String district, String province) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            City cityObj = session.createQuery("select c from City c where c.name=:cName", City.class).setParameter("cName", city).getSingleResult();
            District districtObj = session.createQuery("select d from District d where d.name=:dName", District.class).setParameter("dName", district).getSingleResult();
            Province provinceObj = session.createQuery("select p from Province p where p.name=:pName", Province.class).setParameter("pName", province).getSingleResult();
            if (cityObj != null && districtObj != null && provinceObj != null) {
                return session.createNamedQuery("getLocation", Location.class).setParameter("city", cityObj).setParameter("district", districtObj).setParameter("province", provinceObj).uniqueResult();
            } else {
                return null;
            }
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
