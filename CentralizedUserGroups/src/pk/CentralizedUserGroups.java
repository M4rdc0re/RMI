package pk;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class CentralizedUserGroups {
    
    public static void main(String[] args) {
        
        try {
            System.setProperty("java.security.policy", "server.policy");
            if (System.getSecurityManager() == null){
                System.setSecurityManager(new SecurityManager());
            }
          
            GroupServer server = new GroupServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/GroupServer", server);
            System.out.println("Server ready");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Server exception: " + e.toString());
        }
    }
}
