package bguspl.set.ex;

import bguspl.set.Config;
import bguspl.set.Env;
import bguspl.set.UserInterface;
import bguspl.set.Util;
import bguspl.set.ex.TableTest.MockLogger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerTest {

    Player player;
    @Mock
    Util util;
    @Mock
    private UserInterface ui;
    @Mock
    private Table table;
    @Mock
    private Dealer dealer;
    @Mock
    private Logger logger;
    @Mock
    private Env env;

    private Integer[] slotToCard;
    private Integer[] cardToSlot;

    void assertInvariants() {
        assertTrue(player.id >= 0);
        assertTrue(player.score() >= 0);
    }

    @BeforeEach
    void setUp() {
        // purposely do not find the configuration files (use defaults here).
        Properties properties = new Properties();
        properties.put("PenaltyFreezeSeconds", "1.5");

        properties.put("PointFreezeSeconds", "1");
        MockLogger logger = new MockLogger();
        Config config = new Config(logger, properties);
        slotToCard = new Integer[config.tableSize];
        cardToSlot = new Integer[config.deckSize];
       // table = new Table(env, slotToCard, cardToSlot);

        env = new Env(logger, config, ui, util);
        player = new Player(env, dealer, table, 0, false);
        assertInvariants();
    }

    @AfterEach
    void tearDown() {
        assertInvariants();
    }

    @Test
    void point() {

        // force table.countCards to return 3
        when(table.countCards()).thenReturn(3); // this part is just for demonstration

        // calculate the expected score for later
        int expectedScore = player.score() + 1;

        // call the method we are testing
        player.point();

        // check that the score was increased correctly
        assertEquals(expectedScore, player.score());

        // check that ui.setScore was called with the player's id and the correct score
        verify(ui).setScore(eq(player.id), eq(expectedScore));
    }

    //// 1
    @Test
    void SetPenaltyTest(){
        player.SetPenalty(true);
        assertEquals(true, player.returnedFromPenalty);
        player.SetPenalty(false);
        assertEquals(false, player.returnedFromPenalty);

    }


    // 2
    @Test
    void SetPointTest(){
        player.setPoint(-1);
        assertEquals(-1, player.givePoint);
        player.setPoint(1);
        assertEquals(1, player.givePoint);

    }

    // 3

    @Test
    void playertermTest() {
        player.terminate();
        assertEquals(true, player.terminate);

    }
    //4
    @Test
    void ResetTokenTest(){
        player.SetTokens(71, 4, 5);
        player.ResetTokens();
        assertEquals(0, player.Token.size());

    }
    //5
    @Test
    void setScoreTest(){
        player.setScore(8);
        assertEquals(8, player.score());
        player.setScore(100);
        assertEquals(100, player.score());
    }
    //6

    @Test
    void FreezPoint() {
        player.point();
        // verify(ui).setFreeze(0, 2000);
        verify(ui).setScore(0, player.score());
        verify(ui).setFreeze(0, 1000);
        // verify(ui).setFreeze(0, 0);
    }
    //7
    @Test
    void FreezPenalty() {
        player.penalty();
        //verify(ui).setFreeze(0, 3000);
        //verify(ui).setFreeze(0, 2000);
        verify(ui).setFreeze(0, 1500);
        verify(ui).setFreeze(0, 500);
        verify(ui).setFreeze(0, 0);

    }


}