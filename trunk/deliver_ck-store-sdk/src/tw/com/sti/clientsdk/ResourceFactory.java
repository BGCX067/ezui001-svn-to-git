package tw.com.sti.clientsdk;

import java.util.ResourceBundle;

public class ResourceFactory {

	public final static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(ResourceFactory.class.getPackage().getName() + ".messages");
    }

}
