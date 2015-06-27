package cn.hoyoung.app.mooc;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import cn.hoyoung.app.mooc.entity.VideoInfo;
/**
 * 爬取所有视频
 * @author hoyoung
 *
 */
public class MoocPageProcessor  implements PageProcessor{
	private static String[] config = { "src/main/resources/spring.xml" };
	private static ApplicationContext context;
	private static SessionFactory sessionFactory;
	private static Session session;
	static {
		context = new FileSystemXmlApplicationContext(config);
		sessionFactory = (SessionFactory) context.getBean("sessionFactory");
		session = sessionFactory.openSession();
	}
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	public void process(Page page) {
		String url = page.getUrl().toString();
		System.out.println("url="+url);
		Selectable selectable = page.getHtml().xpath("//div[@class='js-course-lists']/ul/li");
		List<Selectable> linodes = selectable.nodes();
		for (Selectable li : linodes) {
//			System.out.println(li.toString());
			System.out.println("********************************************************");
			
			Selectable anode = li.xpath("//a");
			//检查是否更新完毕
			if("更新完毕".equals(anode.xpath("//div[@class='tips']/span[@class='l'][1]/text()").toString())){
				String code = li.xpath("//a/@href").toString();
				code = code.substring(code.lastIndexOf("/")+1);
				System.out.println(code);
				String name = anode.xpath("//h5/span/text()").toString();
				System.out.println(name);
				
				VideoInfo videoInfo = new VideoInfo();
				videoInfo.setName(name);
				videoInfo.setCode(code);
				if(session != null){
					session.save(videoInfo);
				}
			}
		}
	}
	public Site getSite() {
		return site;
	}
	public static void main(String[] args) {
		String baseUrl = "http://www.imooc.com/course/list";
		Spider spider = Spider.create(new MoocPageProcessor());
		spider.addUrl(baseUrl);
		for(int i=1;i<=19;i++){
			spider.addUrl(baseUrl+"?page="+i);
		}
		session.getTransaction().begin();
		spider.thread(1).run();
		if(session != null){
			session.getTransaction().commit();
			session.close();
		}
		if(sessionFactory != null){
			sessionFactory.close();
		}
		System.out.println("完成!");
	}
}
