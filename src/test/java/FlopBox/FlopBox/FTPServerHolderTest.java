package FlopBox.FlopBox;

import org.junit.Test;

import holders.FTPServerHolder;
import processors.FTPServer;

import static org.junit.Assert.assertEquals;

public class FTPServerHolderTest {
	
    @Test
    public void AddAndListServers() throws Exception {
    	assertEquals(0, FTPServerHolder.ListServers().size());
        FTPServerHolder.AddServer(new FTPServer("Serv1", "host", "20", "passive"));
        assertEquals(1, FTPServerHolder.ListServers().size());
        assertEquals("Serv1", FTPServerHolder.ListServers().get(0));
        
        FTPServerHolder.AddServer(new FTPServer("Serv2", "host", "20", "passive"));
        assertEquals(2, FTPServerHolder.ListServers().size());
        assertEquals("Serv1", FTPServerHolder.ListServers().get(0));
        assertEquals("Serv2", FTPServerHolder.ListServers().get(1));
        
        FTPServerHolder.AddServer(new FTPServer("Serv3", "host", "20", "passive"));
        assertEquals(3, FTPServerHolder.ListServers().size());
        assertEquals("Serv1", FTPServerHolder.ListServers().get(0));
        assertEquals("Serv2", FTPServerHolder.ListServers().get(1));
        assertEquals("Serv3", FTPServerHolder.ListServers().get(2));
    }
    
    @Test
    public void GetServer() throws Exception {
        FTPServerHolder.AddServer(new FTPServer("Serv1", "host", "20", "passive"));
        FTPServerHolder.AddServer(new FTPServer("Serv2", "host", "20", "passive"));
        FTPServerHolder.AddServer(new FTPServer("Serv3", "host", "20", "passive"));
        FTPServer ftpserver;
        
        ftpserver = FTPServerHolder.GetServer("Serv1");
        assertEquals("Serv1", ftpserver.getName());
        ftpserver = FTPServerHolder.GetServer("Serv3");
        assertEquals("Serv3", ftpserver.getName());
        ftpserver = FTPServerHolder.GetServer("Serv2");
        assertEquals("Serv2", ftpserver.getName());
        ftpserver = FTPServerHolder.GetServer("Serv1");
        assertEquals("Serv1", ftpserver.getName());
        assertEquals(null, FTPServerHolder.GetServer("Serv"));
    }
    
    @Test
    public void RemoveServers() {        
        assertEquals(true, FTPServerHolder.RemoveServer("Serv2"));
        assertEquals(2, FTPServerHolder.ListServers().size());
        assertEquals("Serv1", FTPServerHolder.ListServers().get(0));
        assertEquals("Serv3", FTPServerHolder.ListServers().get(1));
        
        assertEquals(true, FTPServerHolder.RemoveServer("Serv1"));
        assertEquals(1, FTPServerHolder.ListServers().size());
        assertEquals("Serv3", FTPServerHolder.ListServers().get(0));
        
        assertEquals(false, FTPServerHolder.RemoveServer("Serv1"));
        assertEquals(1, FTPServerHolder.ListServers().size());
        assertEquals("Serv3", FTPServerHolder.ListServers().get(0));
        
        assertEquals(true, FTPServerHolder.RemoveServer("Serv3"));
        assertEquals(0, FTPServerHolder.ListServers().size());
    }
    
}
