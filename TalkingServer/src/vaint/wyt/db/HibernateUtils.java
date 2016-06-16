package vaint.wyt.db;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public final class HibernateUtils {
	private static SessionFactory sessionFactory;
	// 相当于线程范围内的缓存容器
	private static ThreadLocal<Session> session = new ThreadLocal<Session>();

	private HibernateUtils() {
	}

	static {
		Configuration cfg = new Configuration();
		cfg.configure();// 如果配置文件为hibernate.cfg.xml，则参数为空。否则，要指定
		sessionFactory = cfg.buildSessionFactory();
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Session getSession() {
		return sessionFactory.openSession();
	}

	public static Session getThreadLocalSession() {
		Session s = (Session) session.get();
		if (s == null) {
			s = getSession();
			session.set(s);
		}
		return s;
	}

	public static void closeSession() {
		Session s = (Session) session.get();
		if (s != null) {
			s.close();
			session.set(null);
		}
	}

	public static boolean add(Object obj) {
		Session s = null;
		boolean bRet = false;
		try {
			s = getSession();
			Transaction tx = s.beginTransaction();
			s.save(obj);
			tx.commit();
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (s != null)
				s.close();
		}
		return bRet;
	}

	public static boolean update(Object obj) {
		Session s = null;
		boolean bRet = false;
		try {
			s = getSession();
			Transaction tx = s.beginTransaction();
			s.update(obj);
			tx.commit();
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (s != null)
				s.close();
		}
		return bRet;
	}

	public static boolean delete(Object obj) {
		Session s = null;
		boolean bRet = false;
		try {
			s = getSession();
			Transaction tx = s.beginTransaction();
			s.delete(obj);
			tx.commit();
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (s != null)
				s.close();
		}
		return bRet;
	}

	public static Object get(Class<?> clazz, Serializable id) {
		Session s = null;
		Object obj = null;
		try {
			s = getSession();
			obj = s.get(clazz, id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (s != null)
				s.close();
		}
		return obj;
	}
}
