package cn.hoyoung.app.mooc;

import org.hibernate.Session;

import cn.hoyoung.app.mooc.entity.User;
import cn.hoyoung.app.mooc.util.HibernateUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        User user = new User();
        user.setName("hoyoung");
        session.save(user);
        session.getTransaction().commit();
        HibernateUtil.getSessionFactory().close();
    }
}
