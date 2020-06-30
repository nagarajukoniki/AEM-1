package com.tda.apac.core.models;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(name = "TDA APAC Href Properties", service = HrefConfiguration.class,immediate = true, configurationPid = "com.tda.apac.core.models.HrefConfiguration")

@Designate(ocd = HrefConfiguration.URLConfiguration.class)
public class HrefConfiguration  {
	
    
    /* OSGI Configurations */
    @ObjectClassDefinition(name="TDA APAC Href Configurations")
    public @interface URLConfiguration {
	    @AttributeDefinition(
		    name = "hrefLanguage",
		    description = "hrefLanguage Configuration",
		    type = AttributeType.STRING
	    )
	    String[] hrefLanguage() default {"en_US=en-hk","zh_TW=zh-Hant-hk","zh_CN=zh-Hans-hk"};
	    
	    @AttributeDefinition(
                name = "navigationRoot",
                description = "navigationRoot Path."
        ) String navigationRoot() default ("/content/tda/retail/intl/hong-kong");
	   
    }
    
    public static  HashMap<String, String> hrefLang=new HashMap<String, String>();
    
    private static final Logger log = LoggerFactory.getLogger(HrefConfiguration.class);
    
    @Reference
    ResourceResolverFactory resolverFactory;      
    /**
     * Service used to get Sling Osgi configurations
     */
    @Reference
    private ConfigurationAdmin configAdmin;
    
    public static String navigationRoot;
    String[] hrefLanguageDefaultValues= {"en_US=en-hk","zh_TW=zh-Hant-hk","zh_CN=zh-Hans-hk"};
    public static Map<String, String> hrefLanguageMap;
    
    Resource resource;
    
	 ResourceResolver resourceResolver = null;  
   
   
	@Activate
	public void activate(URLConfiguration config) {
		try {
			if (configAdmin != null) {
				Configuration configOsgi = configAdmin.getConfiguration("com.tda.apac.core.models.HrefConfiguration");
				if (configOsgi != null && configOsgi.getProperties() != null) {
					navigationRoot = (String) configOsgi.getProperties().get("navigationRoot");
					log.debug("navigationRoot from config file:"+navigationRoot);
						hrefLanguageMap=new HashMap<String, String>();
						String[] hrefValues=(String[]) configOsgi.getProperties().get("hrefLanguage");
						if(hrefValues==null)
							hrefValues=hrefLanguageDefaultValues;
						for(int i=0;i<hrefValues.length;i++){
						String[]   localeMap=hrefValues[i].split("=");
						if(localeMap[0]!=null&& localeMap[1]!=null)
						hrefLanguageMap.put(localeMap[0], localeMap[1]);
					}
					log.debug("hrefLanguageMap from configuration:"+hrefLanguageMap);
				}
			}
			
			
		}catch(Exception e) {
			log.error("Exception in HrefConfiguration class:"+e.getMessage());
		} 
	}
  
 
}
