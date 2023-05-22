package bguspl.set.ex;

import bguspl.set.Env;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {
    Thread TimerThread;
    private long Timer;
    private  boolean TimeToReset;
    private  List<Integer> EmptySlots;
    public LinkedBlockingQueue<Offer> PlayerSet;
    protected volatile boolean Press[][];
    private volatile List<Integer> CardsIdWhichDeleted;
    private Thread[] playeer;
    public volatile boolean canPress;
    public volatile boolean ok;

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;
    public final Player[] players;

    /**
     * The list of card ids that are left in the dealer's deck.
     */
    private final List<Integer> deck;

    /**
     * True iff game should be terminated.
     */
    public volatile boolean terminate;

    /**
     * The time when the dealer needs to reshuffle the deck due to turn timeout.
     */
    private long reshuffleTime = Long.MAX_VALUE;

    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
        EmptySlots = IntStream.range(0, env.config.tableSize).boxed().collect(Collectors.toList());
        TimerThread = new Thread();
        Timer = env.config.turnTimeoutMillis;
        TimeToReset = false;
        PlayerSet = new LinkedBlockingQueue<Offer>();
        Press = new boolean[players.length][env.config.tableSize];
        for (int j = 0; j < players.length; j++) {
            for (int i = 0; i < env.config.tableSize; i++) {
                Press[j][i] = false;
            }

        }
        CardsIdWhichDeleted = new ArrayList<Integer>();
        playeer = new Thread[players.length];
        canPress = true;
        ok = true;

    }

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        TimerThread = new Thread(() -> {
            reshuffleTime=System.currentTimeMillis()+ env.config.turnTimeoutMillis;
            Timer = reshuffleTime-System.currentTimeMillis();;

            while (!terminate) {
                try {
                    synchronized (this) {
                        System.out.println("wait");
                        wait();
                    }
                } catch (InterruptedException e) {
                    // TODO: handle exception
                }

                while (Timer > 5000) {
                    env.ui.setCountdown(Timer, false);
                    Timer = Timer - 1000;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                    }

                    // Timer = reshuffleTime-System.currentTimeMillis();;
                }
                while (Timer<=5000&&Timer >= 0) {
                    env.ui.setCountdown(Timer, true);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                    }
                    Timer = Timer - 10;

                }
                synchronized (this) {
                    notifyAll();
                }

            }
        });
        TimerThread.start();
        for (int i = 0; i < players.length; i++) {// initilaize threads
            playeer[i] = new Thread(players[i], env.config.playerNames[i]);
            playeer[i].start();
        }
        ThreadStart(false);
        env.logger.info("Thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            Collections.shuffle(deck);
            placeCardsOnTable();
            updateTimerDisplay(true);
            if(env.config.hints)
                table.hints();
            ThreadStart(true);
            timerLoop();
            ThreadStart(false);
            removeAllCardsFromTable();
            for(Player player:players){
                player.SetPenalty(false);
            }
        }
        terminate();
        TimerThread.stop();
        announceWinners();
        env.logger.info("Thread " + Thread.currentThread().getName() + " terminated.");
    }

    private void ThreadStart(boolean f) {
        for (int i = 0; i < players.length; i++) {
            players[i].setStart(f);
        }
    }
    private void update(){//just if there is legal set
        TimeToReset=false;
        Timer=env.config.turnTimeoutMillis;
    }

    /**
     * The inner loop of the dealer thread that runs as long as the countdown did
     * not time out.
     */
    private void timerLoop() {
        while (!terminate & Timer > 0 ) {
           // System.out.println(TimerThread.getState());
            synchronized (this) {
                try {
                    wait(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateTimerDisplay(false);
            removeCardsFromTable();
            placeCardsOnTable();
        }
    }

    /**
     * Called when the game should be terminated.
     */
    public void terminate() {
        // TODO implement
        for (int i = players.length-1; i >=0 ; i--) {
            players[i].terminate();
        }
        terminate = true;
        System.out.println("dealer is terminated");

    }

    /**
     * Check if the game should be terminated or the game end conditions are met.
     *
     * @return true iff the game should be finished.
     */
    private boolean shouldFinish() {
        return terminate || env.util.findSets(deck, 1).size() == 0;
    }

    /**
     * Checks cards should be removed from the table and removes them.
     */
    private void removeCardsFromTable() {
        // TODO implement
        if (!PlayerSet.isEmpty()) {
            Offer offer = new Offer(new ArrayList<>(), 0);
            try {
                offer = PlayerSet.take();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int playerId = offer.GetId();
            int[] Setarr = ChangeToArr(offer.GetToken());
            int count = 0;
            int[] cards = new int[env.config.featureSize];
            for (int i = 0; i < env.config.featureSize; i++) {
                if (table.slotToCard[Setarr[i]] != null) {
                    cards[i] = table.slotToCard[Setarr[i]];
                    count++;
                }
            }
            if (count != env.config.featureSize) {
                return;
            }
            if (env.util.testSet(cards)) {
                ok = false;

                RemoveSameToken(Setarr);
                boolean ans = SameCardWasPreseed(cards);
                if (ans) {
                    for (int i = 0; i < env.config.featureSize; i++) {
                        Press[playerId][Setarr[i]] = false;
                    }
                }
                boolean givePoint = false;
                for (int i = 0; i < env.config.featureSize && !ans; i++) {
                    CardsIdWhichDeleted.add(cards[i]);
                    if (table.cardToSlot[cards[i]] != null)
                        synchronized (table.cardToSlot[cards[i]]) {
                            table.removeCard(Setarr[i]);
                        }
                    table.removeToken(playerId, Setarr[i]);
                    EmptySlots.add(Setarr[i]);
                    givePoint = true;
                }
                if (givePoint) {
                    update();
                    players[playerId].setPoint(1);
                }
                ok = true;

            } else {
                players[playerId].setPoint(-1);
            }
            players[playerId].playerThread.interrupt();
        }

    }

    /**
     * Check if any cards can be removed from the deck and placed on the table.
     * 
     * @pre - there is Emptyslots in the array and cards to fill them
     * @post- all the slots filled by cards from the deck
     */
    public void placeCardsOnTable() {
        // TODO implement
            synchronized (table) {
                Collections.shuffle(EmptySlots);
                while (!(EmptySlots.isEmpty())) {
                    int lastslot = EmptySlots.remove(EmptySlots.size() - 1);
                    if (!deck.isEmpty()) {
                        table.placeCard(deck.remove(deck.size() - 1), lastslot);
                    }
                }
                canPress = true;
            }


    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */

    private void updateTimerDisplay(boolean reset) {
        // // TODO implement
        if (TimeToReset) {
           Timer = env.config.turnTimeoutMillis;
            TimeToReset = false;
        }
        synchronized (this) {

            notifyAll();
        }

    }

    /**
     * Returns all the cards from the table to the deck.
     * 
     * @pre - there are legal set on the table or on the deck and the timer reaches
     *      zero
     *
     * @post- all the empty slots filled by number of cards in the deck its okay if
     *        there some empty slots because the deck cards finished
     */
    public void removeAllCardsFromTable() {
        // TODO implement
        TimeToReset = true;
        canPress = false;
        synchronized (table) {
            for (int deletedSlot = 0; deletedSlot < env.config.tableSize; deletedSlot++) {
                EmptySlots.add(deletedSlot);
                for (int playerId = 0; playerId < players.length; playerId++) {
                    if (Press[playerId][deletedSlot] == true) {
                        Press[playerId][deletedSlot] = false;
                        table.removeToken(playerId, deletedSlot);
                    }
                    players[playerId].ResetTokens();
                }
            }
            Collections.shuffle(EmptySlots);
            for (int k = 0; k < env.config.tableSize && !EmptySlots.isEmpty(); k++) {
                int i = EmptySlots.remove(EmptySlots.size() - 1);
                if (table.slotToCard[i] != null) {
                    deck.add(table.slotToCard[i]);
                }
                table.removeCard(i);
            }
            EmptySlots = IntStream.range(0, env.config.tableSize).boxed().collect(Collectors.toList());
        }
//        for(Player player:players){
//            player.playerThread.interrupt();
//            player.SetPenalty(false);
//        }

    }

    /**
     * Check who is/are the winner/s and displays them.
     * 
     * @pre- the loop finished whith timer 0 and no more sets
     * @post- announce who have the most points if the are two or more annouce them
     */
    public void announceWinners() {
        // TODO implement
        int maxscore = -1;
        int counter = 0;

        for (Player player : players) {
            if (player.score() > maxscore) {
                maxscore = player.score();
            }
        }
        for (Player player : players) {
            if (player.score() == maxscore) {
                counter = counter + 1;
            }
        }
        int[] ans = new int[counter];
        for (int i = 0, j = 0; j < counter; i++) {
            if (players[i].score() == maxscore) {
                ans[j] = players[i].id;
                j++;
            }
        }
        env.ui.announceWinner(ans);
    }

    /**
     * @pre- a legal card to check (not null)
     * @post - if the card some one take him before
     */
    public boolean SameCardWasPreseed(int[] cardid) {
        for (int i = 0; i < env.config.featureSize; i++) {
            if (CardsIdWhichDeleted.contains(cardid[i])) {
                return true;
            }
        }
        return false;
    }

    private int[] ChangeToArr(List<Integer> a) {
        int[] ans = new int[env.config.featureSize];
        for (int i = 0; i < a.size(); i++) {
            ans[i] = a.get(i);
        }
        return ans;
    }

    private void RemoveSameToken(int[] slots) {
        for (int i = 0; i < players.length; i++) {
            for (int j = 0; j < env.config.featureSize; j++) {
                if (Press[i][slots[j]]) {
                    players[i].Decrement(slots[j]);
                    table.removeToken(i, slots[j]);
                    Press[i][j] = false;
                    players[i].returnedFromPenalty=false;
                }
            }
        }
    }

}
