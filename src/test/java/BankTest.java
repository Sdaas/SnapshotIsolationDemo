import com.daasworld.Account;
import com.daasworld.Bank;
import com.daasworld.Transaction;
import com.daasworld.TransactionManager;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class BankTest {

    private Bank bank;
    private TransactionManager pool;

    @Before
    public void setup(){
        pool = new TransactionManager();
        bank = new Bank(3, 100, pool);
    }

    @Test
    public void initialHolding(){
        assertEquals("should have a holding of 300",300, bank.holdings());
    }

    @Test
    public void singleTransfer(){
        Account a0 = bank.account(0);
        Account a1 = bank.account(1);
        Account a2 = bank.account(2);

        Transaction t = pool.next();
        assertEquals("should have a balance of 100",100, a0.balance(t));
        assertEquals("should have a balance of 100",100, a1.balance(t));
        assertEquals("should have a balance of 100",100, a2.balance(t));
        pool.end(t);

        bank.transfer(0, 1, 90);

        t = pool.next();
        assertEquals("should have a balance of 10",10, a0.balance(t));
        assertEquals("should have a balance of 190",190, a1.balance(t));
        assertEquals("should have a balance of 100",100, a2.balance(t));
        pool.end(t);
    }
}
