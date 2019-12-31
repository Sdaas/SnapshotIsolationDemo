import com.daasworld.Account;
import com.daasworld.Transaction;
import com.daasworld.TransactionManager;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccountTest {
    Account a;

    @Before
    public void setup(){
        TransactionManager pool = new TransactionManager();
        a = new Account(pool, 100);
    }

    @Test
    public void initialBalance(){
        Transaction t = new Transaction(1, new ArrayList<>(), 0);
        assertEquals("initial balance should be 100", 100, a.balance(t));
        assertEquals("should have 1 entries in history", 1, a.history().size());
        assertEquals("last transaction should have been 0", 0, a.lastTransaction().id());
    }

    @Test
    public void singleSetTest(){

        Transaction t = new Transaction(1, new ArrayList<>(), 0 );
        a.update(t,50);
        assertEquals("should have 2 entries in history", 2, a.history().size());
        assertEquals("last transaction should have been 2", 1, a.lastTransaction().id());

        t = new Transaction(2, new ArrayList<>(), 1);
        assertEquals("balance should be 50", 50, a.balance(t));

        t = new Transaction(2, new ArrayList<>(), 0);
        assertEquals("balance should be 100", 100, a.balance(t));


        t = new Transaction(2, Arrays.asList(1L), 0);
        assertEquals("balance should be 100", 100, a.balance(t));
    }

    @Test
    public void multipleSetTest(){

        Transaction t;
        for (int i = 1; i < 6; i++) {
            t = new Transaction(i, new ArrayList<>(), 0);
            a.update(t,100*i);
        }

        t = new Transaction(6, new ArrayList<>(), 5);
        assertEquals("balance should be 500", 500, a.balance(t));
        assertEquals("should have 6 elements in history", 6, a.history().size());
        assertEquals("last transaction should have been 5", 5, a.lastTransaction().id());
    }

    @Test
    public void visibilityTest() {

        long id = 1000;   // current transaction ID. Set it to some huge number ...
        long maxId = 10;  // max committed Txn Id when this txn was created
        List<Long> active = Arrays.asList(2L,5L,6L,7L);

        // An account is created and updated by transactions 0, 2, 3, 5, 7, and 11
        // remember that account creation itself updates the account with tid 0

        Transaction t = new Transaction(2, new ArrayList<>(), 1);
        a.update(t,222);

        t = new Transaction(3, new ArrayList<>(), 2);
        a.update(t,333);

        t = new Transaction(5, new ArrayList<>(), 4);
        a.update(t,555);

        t = new Transaction(7, new ArrayList<>(), 6);
        a.update(t,777);

        t = new Transaction(11, new ArrayList<>(), 10);
        a.update(t,1111);

        // 11 should not be visible ( exceeds the max id of 10)
        // 7 should not be visible (  txn was active when this txn was created )
        // 5 should not be visible (  txn was active when this txn was created )
        // 3 should be visible
        // 2 should NOT be visible  ( transaction had not yet committed when this txn was created )
        // 0 should be visible
        // The most recent one in this is 3 ....

        t = new Transaction(id,active,maxId);
        assertEquals("balance should have been 333", 333, a.balance(t));
    }

}
