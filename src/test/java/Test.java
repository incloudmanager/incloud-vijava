

import java.net.URL;

import net.sf.json.JSONObject;

import com.vmware.vim25.EnvironmentBrowserConfigOptionQuerySpec;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.ws.Argument;
import com.vmware.vim25.ws.WSClient;


public class Test {
	public static void main(String[] args) throws Exception {
		String version = "urn:vim25/6.5";
		String ip = "100.2.12.190";	
		int port = 443;
		String userName = "administrator@vsphere.local";
		String passwd = "123456Aa?";
        String url = (new StringBuilder()).append("https://").append(ip).append(":").append(port).append("/sdk").toString();
        ServiceInstance si = new ServiceInstance(
        		new URL(url), userName, passwd, true);
        WSClient wsc = si.getServerConnection().getVimService().getWsc();
        String methodName = "QueryConfigOptionEx";
        ManagedObjectReference envMor = new ManagedObjectReference();
        envMor.setVal("envbrowser-122");
        envMor.setType("EnvironmentBrowser");
        EnvironmentBrowserConfigOptionQuerySpec spec = new EnvironmentBrowserConfigOptionQuerySpec();
        spec.setGuestId(new String[]{"windows7Guest"});
        Argument[] paras = new Argument[2];
	    paras[0] = new Argument("_this", "ManagedObjectReference", envMor);
	    paras[1] = new Argument("spec", "EnvironmentBrowserConfigOptionQuerySpec", spec);
		JSONObject json = wsc.invokeAsJsonWithVersion(methodName, paras, version);
		System.out.println(json.toString());
	}
}
