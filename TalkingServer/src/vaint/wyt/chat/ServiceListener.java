package vaint.wyt.chat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet容器的监听器<BR/>
 * 用于开启处理聊天信息的服务线程
 * @author Vaint
 *@E-mail vaintwyt@163.com
 *
 */
public class ServiceListener implements ServletContextListener {
	ChatServerThread serverThread = null;
	public void contextDestroyed(ServletContextEvent arg0) {
		if(serverThread != null && !serverThread.isInterrupted())
			serverThread.closeServer();
	}

	public void contextInitialized(ServletContextEvent event) {
		//开启聊天通信线程
		if(serverThread == null)
		{
			serverThread = new ChatServerThread();
			serverThread.start();
		}
		
	}

}
