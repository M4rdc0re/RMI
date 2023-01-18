package pk;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente implements Remote {
    public static void main(String[] args) {
        try {
            System.setProperty("java.security.policy", "client.policy");
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());

            }

            Registry registry = LocateRegistry.getRegistry("localhost");
            GroupServerInterface grupo = (GroupServerInterface) registry.lookup("GroupServer");
            
            

            System.out.println("Enter your alias");
            Scanner entrada = new Scanner(System.in);
            String alias = entrada.next();
            

            try {
                InetAddress aHost = InetAddress.getLocalHost();

                int m = 0;
                
                
                while (m != 12) {
                    String galias = "";
                    
                    System.out.println();
                    System.out.println("Select a option:");
                    System.out.println("1-Create group");
                    System.out.println("2-Delete group");
                    System.out.println("3-Add yourself as a member of a group");
                    System.out.println("4-Delete yourself as a member of a group");
                    System.out.println("5-Blocking group registrations and de-registrations");
                    System.out.println("6-View members of a group");
                    System.out.println("7-View current groups");
                    System.out.println("8-Checks if a group exists");
                    System.out.println("9-Show owner of a group");
                    System.out.println("10-Check if you are a member of a group");
                    System.out.println("11-Unblock group registrations and de-registrations ");
                    System.out.println("12-Exit");
                    System.out.println();
te
                    Scanner menu = new Scanner(System.in);
                    System.out.print("Option: ");te
                    m = menu.nextInt();
                    System.out.println();
                    
                    if (m != 12 && m !=7){
                        System.out.println("Enter the group name");
                        Scanner g = new Scanner(System.in);
                        galias = g.next();
                        System.out.println();
                    }

                    switch (m) {
                        case 1:
                            if (grupo.createGroup(galias, alias, aHost.getHostName())) {
                                System.out.println("You have created the group correctly");
                            } else {
                                System.out.println("There is already a group with this alias");
                            }

                            break;

                        case 2:
                            if (grupo.removeGroup(galias, alias)) {
                                System.out.println("You have successfully deleted the group");
                            } else {
                                System.out.println("The owner of the group does not match the one entered");
                            }
                            break;

                        case 3:
                            if (grupo.addMember(galias, alias, aHost.getHostName())) {
                                System.out.println("Successfully added");
                            } else {
                                System.out.println("Error");

                            }
                            break;
                        case 4:
                            if (grupo.removeMember(galias, alias)) {
                                System.out.println("Successfully deleted");
                            } else {
                                System.out.println("Error");
                            }
                            break;

                        case 5:
                            if (grupo.StopMembers(galias)) {
                                System.out.println("Blocked attempts to add/remove members from the group");
                            } else {
                                System.out.println("Error");
                            }
                            break;

                        case 6:
                            if (grupo.isGroup(galias)) {
                                System.out.println(grupo.ListMembers(galias));
                            } else {
                                System.out.println("The group does not exist");
                            }
                            break;

                        case 7:
                            System.out.println("These are the current groups:");
                            System.out.println(grupo.ListGroups());
                            break;

                        case 8:
                            if (grupo.isGroup(galias)) {
                                System.out.println("The group exists");
                            } else {
                                System.out.println("The group does not exist");
                            }
                            break;

                        case 9:
                            System.out.println(grupo.Owner(galias));
                            
                            break;

                        case 10:
                            if (grupo.isMember(galias, alias)) {
                                System.out.println("You are a member of the group");
                            } else {
                                System.out.println("Error");
                            }
                            break;
                        case 11:
                            if (grupo.AllowMembers(galias)) {
                                System.out.println("Unblocked attempts to add/remove members from the group");
                            } else {
                                System.out.println("Error");
                            }
                            break;
                        case 12:
                            System.out.println("Exiting...");
                            break;
                            
                        default:
                            System.out.println("This option does not exist");
                    }

                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (RemoteException e) {
            System.err.println("Client exception: " + e.toString());
        } catch (NotBoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}