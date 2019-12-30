import com.daasworld.Transaction;
import com.daasworld.TransactionManager;

import static org.junit.Assert.*;
import org.junit.*;

public class TransactionManagerTest {

    TransactionManager pool;

    @Before
    public void setup(){
        pool = new TransactionManager();
    }

    @Test
    public void createTransaction(){
        Transaction t = pool.next();
        assertEquals("expected transaction id to be 0", 0, t.id());
        assertEquals("expected no active transactions", 0, t.active().size());
        assertEquals("expected 1 active transaction", 1, pool.active().size());
    }

    @Test
    public void endTransaction(){
        Transaction t = pool.next();
        pool.end(t);
        assertEquals("expected no active transaction", 0, pool.active().size());
    }

    @Test
    public void createMultipleTransactions(){
        Transaction t1 = pool.next();
        Transaction t2 = pool.next();
        Transaction t3 = pool.next();
        assertEquals("expected transaction id to be 0", 0, t1.id());
        assertEquals("expected transaction id to be 1", 1, t2.id());
        assertEquals("expected transaction id to be 2", 2, t3.id());
        assertEquals("expected 3 active transactions", 3, pool.active().size());
    }

    @Test
    public void endMultipleTransactions(){
        Transaction t1 = pool.next();
        Transaction t2 = pool.next();
        Transaction t3 = pool.next();

        pool.end(t2);
        pool.end(t3);
        pool.end(t1);
        assertEquals("expected 0 active transactions", 0, pool.active().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void endTransactionMultipleTimes(){
        Transaction t1 = pool.next();
        pool.end(t1);
        pool.end(t1); // should throw IllegalArgumentException
    }
}
