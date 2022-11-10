package FlopBox.FlopBox;

import org.junit.Test;

import processors.FTPServer;

import static org.junit.Assert.assertEquals;

public class FTPServerTest {
	
    @Test
    public void ConstructorAndGetters() {
        FTPServer ftpserver = new FTPServer("name", "host", "2020", "passive");
        assertEquals("host",ftpserver.getHost());
        assertEquals("name",ftpserver.getName());
        assertEquals(2020,ftpserver.getPort());
        assertEquals(true,ftpserver.getMode());
        ftpserver = new FTPServer("name", "host", "2020", "active");
        assertEquals(false,ftpserver.getMode());
    }
    
}
