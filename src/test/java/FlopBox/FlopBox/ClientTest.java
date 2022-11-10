package FlopBox.FlopBox;

import org.junit.Test;

import processors.Client;

import static org.junit.Assert.assertEquals;

public class ClientTest {
	
    @Test
    public void ConstructorAndGetters() {
        Client client = new Client("ident", "pass");
        assertEquals("ident", client.getId());
        assertEquals(false, client.checkPassword(null));
        assertEquals(false, client.checkPassword(""));
        assertEquals(false, client.checkPassword("pas"));
        assertEquals(false, client.checkPassword("passs"));
        assertEquals(false, client.checkPassword("pasd"));
        assertEquals(false, client.checkPassword("sdsdoisjsojosjds"));
        assertEquals(true, client.checkPassword("pass"));
    }
    
}
