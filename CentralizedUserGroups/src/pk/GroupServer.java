package pk;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupServer extends UnicastRemoteObject implements GroupServerInterface {
    
    public class Group{
        public String grupo = "";
        public String propietario = "";
        public boolean bloqueado = false;
        final Condition condicion = lock.newCondition();
        public LinkedList<GroupMember> lista_miembros = new LinkedList();
        
        public Group(String grupo, String propietario){
            this.grupo = grupo;
            this.propietario = propietario;
        }
    }
    
    public GroupServer() throws RemoteException{
        super();
        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }
    }
    
    private final ReentrantLock lock = new ReentrantLock();
    private LinkedList<Group> lista_grupos = new LinkedList();
    public Group grupo;
    public GroupMember miembro;
    
    @Override
    public boolean createGroup(String galias, String oalias, String ohostname) throws RemoteException{
        lock.lock();
        try{
            if(!isGroup(galias)){
                grupo = new Group(galias,oalias);
                lista_grupos.add(grupo);
                miembro = new GroupMember(oalias, ohostname);
                grupo.lista_miembros.add(miembro);
                return true;
            }
            return false;
        }finally{
            lock.unlock();
        } 
    }

    @Override
    public boolean isGroup(String galias) throws RemoteException{
        lock.lock();
        try{
            for(int i = 0; i < ListGroups().size(); i++){
                if(ListGroups().get(i).equals(galias)){
                    return true;
                }
            }
            return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean removeGroup(String galias, String oalias) throws RemoteException{
        lock.lock();
        try{
            if(Owner(galias).equals(oalias)){
                for (Group group : lista_grupos) {
                    if(group.grupo.equals(galias)){
                        lista_grupos.remove(group);
                        return true;
                    }
                }
            }
            return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean addMember(String galias, String alias, String hostname) throws RemoteException{
        lock.lock();
        try{
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    while(group.bloqueado){
                        try {
                            group.condicion.await();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if(!isMember(galias, alias)){
                        miembro = new GroupMember(alias, hostname);
                        group.lista_miembros.add(miembro);
                        return true;
                    }
                }
            }
        return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean removeMember(String galias, String alias) throws RemoteException{
        lock.lock();
        try{
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    while(group.bloqueado){
                        try {
                            group.condicion.await();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if(!Owner(galias).equals(alias)){
                        for (GroupMember member : group.lista_miembros) {
                            if(member.miembro.equals(alias)){
                                group.lista_miembros.remove(member);
                                return true;
                            }
                        }
                    }
                }
            }
        return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean isMember(String galias, String alias) throws RemoteException{
        lock.lock();
        try{
            for(int i = 0; i < ListMembers(galias).size(); i++){
                if(ListMembers(galias).get(i).equals(alias)){
                    return true;
                }
            }
            return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public String Owner(String galias) throws RemoteException{
        lock.lock();
        try{
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    return group.propietario;
                }
            }
            return null;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean StopMembers(String galias) throws RemoteException{
        lock.lock();
        try{
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    group.bloqueado = true;
                    return true;
                }
            }
            return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean AllowMembers(String galias) throws RemoteException{
        lock.lock();
        try{
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    group.bloqueado = false;
                    group.condicion.signalAll();
                    return true;
                }
            }
            return false;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public LinkedList<String> ListMembers(String galias) throws RemoteException{
        lock.lock();
        try{
            LinkedList<String> local_lista_miembros = new LinkedList<>();
            for (Group group : lista_grupos) {
                if(group.grupo.equals(galias)){
                    for (GroupMember member : group.lista_miembros) {
                        local_lista_miembros.add(member.miembro);
                    }
                }
            }
            if (!local_lista_miembros.isEmpty()){
                return local_lista_miembros; 
            }
            else{
                return null;
            }
        }finally{
            lock.unlock();
        }
    }

    @Override
    public LinkedList<String> ListGroups() throws RemoteException{
        lock.lock();
        try{
            LinkedList<String> local_lista_grupos = new LinkedList<>();
            for (Group group : lista_grupos) {
                local_lista_grupos.add(group.grupo);
            }
            return local_lista_grupos;
        }finally{
            lock.unlock();
        }
    } 
}
