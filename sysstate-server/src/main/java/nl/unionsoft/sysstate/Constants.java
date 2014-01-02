package nl.unionsoft.sysstate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Constants {

    public static final int MAX_DAYS_TO_KEEP_STATES_VALUE = 7;

    public static final String DEFAULT_TEMPLATE_VALUE = "base";
    public static final String MAINTENANCE_MODE = "maintenance-mode";
    public static final String MAINTENANCE_MODE_VALUE = "false";

    public static final String SYSSTATE_PLUGIN_NAME = "sysstate";
}
