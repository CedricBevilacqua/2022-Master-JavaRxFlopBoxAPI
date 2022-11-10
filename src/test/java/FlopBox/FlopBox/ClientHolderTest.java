package FlopBox.FlopBox;

import org.junit.Test;

import holders.ClientHolder;
import processors.Client;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

public class ClientHolderTest {
	
    @Test
    public void TestClass() throws Exception {
    	assertEquals(0, ClientHolder.ListClients().size());
    	
        ClientHolder.AddClient(new Client("Ident1", "pass1"));
        assertEquals(1, ClientHolder.ListClients().size());
        assertEquals("Ident1", ClientHolder.ListClients().get(0));
        
        ClientHolder.AddClient(new Client("Ident2", "pass2"));
        assertEquals(2, ClientHolder.ListClients().size());
        assertEquals("Ident1", ClientHolder.ListClients().get(0));
        assertEquals("Ident2", ClientHolder.ListClients().get(1));
        
        ClientHolder.AddClient(new Client("Ident3", "pass3"));
        assertEquals(3, ClientHolder.ListClients().size());
        assertEquals("Ident1", ClientHolder.ListClients().get(0));
        assertEquals("Ident2", ClientHolder.ListClients().get(1));
        assertEquals("Ident3", ClientHolder.ListClients().get(2));
        
        Client client;
        
        client = ClientHolder.GetClient("Ident1");
        assertEquals("Ident1", client.getId());
        client = ClientHolder.GetClient("Ident3");
        assertEquals("Ident3", client.getId());
        client = ClientHolder.GetClient("Ident2");
        assertEquals("Ident2", client.getId());
        client = ClientHolder.GetClient("Ident1");
        assertEquals("Ident1", client.getId());
        assertEquals(null, ClientHolder.GetClient("Ident"));
        
        assertEquals(false, ClientHolder.RemoveClient("zofdjkzonfj", "pass1"));
        assertEquals(3, ClientHolder.ListClients().size());
        assertEquals(false, ClientHolder.RemoveClient("Ident2", "pass1"));
        assertEquals(3, ClientHolder.ListClients().size());
        assertEquals(false, ClientHolder.RemoveClient("Ident1", "fsdfokdsnfos"));
        assertEquals(3, ClientHolder.ListClients().size());
        assertEquals(false, ClientHolder.RemoveClient("Ident1", "pass2"));
        assertEquals(3, ClientHolder.ListClients().size());
    	
        assertEquals(true, ClientHolder.RemoveClient("Ident2", "pass2"));
        assertEquals(2, ClientHolder.ListClients().size());
        assertEquals("Ident1", ClientHolder.ListClients().get(0));
        assertEquals("Ident3", ClientHolder.ListClients().get(1));
        
        assertEquals(true, ClientHolder.RemoveClient("Ident1", "pass1"));
        assertEquals(1, ClientHolder.ListClients().size());
        assertEquals("Ident3", ClientHolder.ListClients().get(0));
        
        assertEquals(false, ClientHolder.RemoveClient("Ident1", "pass1"));
        assertEquals(1, ClientHolder.ListClients().size());
        assertEquals("Ident3", ClientHolder.ListClients().get(0));
        
        assertEquals(true, ClientHolder.RemoveClient("Ident3", "pass3"));
        assertEquals(0, ClientHolder.ListClients().size());
    }
    
}
