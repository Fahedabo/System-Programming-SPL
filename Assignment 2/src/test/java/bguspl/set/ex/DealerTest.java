package bguspl.set.ex;

import bguspl.set.Config;
import bguspl.set.Env;
import bguspl.set.UserInterface;
import bguspl.set.Util;
import bguspl.set.ex.TableTest.MockLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DealerTest {

    Dealer dealer;
    @Mock
    Util util;
    @Mock
    private UserInterface ui;
    @Mock
    private Table table;
    @Mock
    private Player player;
    @Mock
    private Logger logger;
    @Mock
    private Object config;
    @Mock
    private Env env;

    @BeforeEach
    void setUp() {

        Properties properties = new Properties();
        properties.put("Rows", "3");
        properties.put("Columns", "4");
        properties.put("FeatureSize", "3");
        properties.put("FeatureCount", "4");
        properties.put("TableDelaySeconds", "0");
        properties.put("PlayerKeys1", "81,87,69,82");
        properties.put("PlayerKeys2", "85,73,79,80");
        MockLogger logger = new MockLogger();
        Config config = new Config(logger, properties);
        Player[] ps = { new Player(env, dealer, table, 0, false), new Player(env, dealer, table, 1, false) };
        Integer[] slotToCard = new Integer[config.tableSize];
        Integer[] cardToSlot = new Integer[config.deckSize];
        env = new Env(logger, config, ui, util);
        table = new Table(env, slotToCard, cardToSlot);
        dealer = new Dealer(env, table, ps);

    }

    // 1
    @Test
    void announceWinnersTest() {
        dealer.players[0].setScore(10);
        dealer.players[1].setScore(10);
        dealer.announceWinners();
        int[] a = { 0, 1 };
        verify(ui).announceWinner(a);
        dealer.players[0].setScore(5);
        dealer.players[1].setScore(10);
        dealer.announceWinners();
        int[] b = { 1 };
        verify(ui).announceWinner(b);

    }

    // 2
    @Test
    void placeCardsOnTableTest() {
        dealer.placeCardsOnTable();
        for (int i = 0; i < 12; i++) {
            assertNotNull(table.slotToCard[i]);
        }

    }

    // 3
    @Test
    void removeAllCardsFromTable() {
        dealer.placeCardsOnTable();
        dealer.removeAllCardsFromTable();
        for (int i = 0; i < env.config.tableSize; i++) {
            assertNull(table.slotToCard[i]);

        }

    }
    //4
    @Test
    void SameCardWasPreseedTest(){
        int []a={1,2,3};
        assertEquals(false, dealer.SameCardWasPreseed(a));

    }
   

}
