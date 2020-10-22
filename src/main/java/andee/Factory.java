package andee;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Factory {
    private static SessionFactory factory;
    private Factory() {}
    public static SessionFactory getInstance() {
        if(factory == null) {
            try {
                return factory = new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        } else {
           return factory;
        }
    }


}
