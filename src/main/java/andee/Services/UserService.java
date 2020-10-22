package andee.Services;

import andee.Factory;
import andee.models.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.Random;


public class UserService {
    private static SessionFactory factory = Factory.getInstance();


    public static int randomNum() {
        Random rand = new Random(); //instance of random class
        int upperbound = 25;
        //generate random values from 0-24
        return rand.nextInt(upperbound);
    }
    public static User createUser(String name, String password, String email) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        try {
            tx = session.beginTransaction();
            user = new User();
            user.setName(name);
            String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            user.setPassword(bcryptHashString);
            user.setEmail(email);
            user.setCode(String.valueOf(randomNum()));
            user.setIs_verified(false);
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }
    public static User getUser(String name) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(User.class);
            user = (User) criteria.add(Restrictions.eq("name", name)).uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }

    public static boolean verifyEmail(String name, String code) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(User.class);
            user = (User) criteria.add(Restrictions.eq("name", name)).uniqueResult();
            if(code.equals(user.getCode())) {
                user.setIs_verified(true);
            } else {
                return false;
            }
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public static String saveNewPassword(String name) {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(User.class);
            user = (User) criteria.add(Restrictions.eq("name", name)).uniqueResult();
            if(user != null ){
                String password = generateRandomString();
                String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                user.setPassword(bcryptHashString);
                tx.commit();
                return password;
            }
            return null;
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
