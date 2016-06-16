package vaint.wyt.db;


import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


/**
 * 服务器开启与关闭的监听器<BR/><BR/>Tomcat关闭时，需要删除JDBC的驱动，否则会报错
 * @author Vaint
 *@E-mail vaintwyt@163.com
 *
 */
public class ApplicationListener implements ServletContextListener {
	static Logger logger = Logger.getLogger(ApplicationListener.class);
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("服务器关闭");
		Enumeration<Driver> drivers = DriverManager.getDrivers();     
        Driver driver = null;
        // clear drivers
        while(drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            } catch (SQLException ex) {
               ex.printStackTrace();
            }
//            try {
//				AbandonedConnectionCleanupThread.shutdown();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
        }
	}

	public void contextInitialized(ServletContextEvent arg0) {
	}
}
