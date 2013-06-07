package nl.unionsoft.sysstate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Constants {
    public static final int MAX_DAYS_TO_KEEP_STATES_VALUE = 7;
    public static final String MAX_DAYS_TO_KEEP_STATES = "MAX_DAYS_TO_KEEP_STATES";
    public static final String DEFAULT_TEMPLATE = "default-template";
    public static final String DEFAULT_TEMPLATE_VALUE = "base";
    public static final String MAINTENANCE_MODE = "maintenance-mode";
    public static final String MAINTENANCE_MODE_VALUE = "false";

    public static void main(final String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException {

        URI url = new File("C:\\.sysstate\\plugins\\sysstate-bol-0.91-1.jar").toURI();
        URLClassLoader child = new URLClassLoader(new URL[] { url.toURL() }, Constants.class.getClassLoader());
        System.out.println(child);
        System.out.println("1a:" + child.getResourceAsStream("bol-bean-context.xml"));
        System.out.println("1b:" + Constants.class.getResourceAsStream("bol-bean-context.xml"));
        System.out.println("2a:" + Class.forName ("com.bol.sysstate.DatabaseDiscovery", true, child));
        //System.out.println("3:" + Class.forName ("com.bol.sysstate.DatabaseDiscovery"));
        //System.out.println(classToLoad);


        //new ClassPathXml
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        context.setClassLoader(child);
        context.setConfigLocation("classpath:/bol-bean-context.xml");
        context.refresh();
        context.getBean(Class.forName ("com.bol.sysstate.DatabaseDiscovery", true, child));
        System.out.println(context);


    }
}
