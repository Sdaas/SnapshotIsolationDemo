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
    public void oneTransaction(){
        Transaction t = pool.next();
        assertEquals("expected transaction id to be 0", 0, t.id());
        assertEquals("expected max committed transaction to be -1", -1, t.max());
        assertEquals("expected no active transactions", 0, t.active().size());
        assertEquals("expected 1 active transaction", 1, pool.active().size());

        pool.end(t);
        assertEquals("expected no active transaction", 0, pool.active().size());
        assertEquals("expected max committed transaction to be 0", 0, pool.max());
    }

    @Test
    public void twoTransactions(){
        Transaction t0 = pool.next();
        Transaction t1 = pool.next();

        assertEquals("expected transaction id to be 1", 1, t1.id());
        assertEquals("expected max committed transaction to be -1", -1, t1.max());
        assertEquals("expected 1 active transactions", 1, t1.active().size());
        assertEquals("expected 2 active transaction", 2, pool.active().size());

        pool.end(t1);
        assertEquals("expected max committed transaction to be 1", 1, pool.max());
        assertEquals("expected 1 active transaction", 1, pool.active().size());

        pool.end(t0);
        assertEquals("expected max committed transaction to be 1", 1, pool.max());
        assertEquals("expected 0 active transaction", 0, pool.active().size());
    }

    @Test
    public void endMultipleTransactions(){
        Transaction t0 = pool.next();
        Transaction t1 = pool.next();
        Transaction t2 = pool.next();

        assertEquals("expected 3 active transaction", 3, pool.active().size());

        pool.end(t1);
        assertEquals("expected max committed transaction to be 1", 1, pool.max());
        assertEquals("expected 2 active transaction", 2, pool.active().size());

        pool.end(t2);
        assertEquals("expected max committed transaction to be 2", 2, pool.max());
        assertEquals("expected 1 active transaction", 1, pool.active().size());

        pool.end(t0);
        assertEquals("expected max committed transaction to be 2", 2, pool.max());
        assertEquals("expected 0 active transaction", 0, pool.active().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void endTransactionMultipleTimes(){
        Transaction t1 = pool.next();
        pool.end(t1);
        pool.end(t1); // should throw IllegalArgumentException
    }
}
