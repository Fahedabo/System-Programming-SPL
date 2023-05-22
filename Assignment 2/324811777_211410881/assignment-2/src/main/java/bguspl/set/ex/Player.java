package bguspl.set.ex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import bguspl.set.Env;

/**
 * This class manages the players' threads and data
 *
 * @inv id >= 0
 * @inv score >= 0
 */
public class Player implements Runnable {
    protected LinkedBlockingQueue<Integer> keys;
    protected volatile List<Integer> Token;
    private Dealer dealer;
    public volatile int givePoint;
    private  boolean penalty;
    public volatile boolean start;

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;

    /**
     * The id of the player (starting from 0).
     */
    public final int id;

    /**
     * The thread representing the current player.
     */
    public Thread playerThread;

    /**
     * The thread of the AI (computer) player (an additional thread used to generate
     * key presses).
     */
    private Thread aiThread;

    /**
     * True iff the player is human (not a computer player).
     */
    private final boolean human;

    /**
     * True iff game should be terminated.
     */
    public volatile boolean terminate;

    /**
     * The current score of the player.
     */
    private int score;
    protected volatile boolean returnedFromPenalty;
    private boolean blockAi;

    /**
     * The class constructor.
     *
     * @param env    - the environment object.
     * @param dealer - the dealer object.
     * @param table  - the table object.
     * @param id     - the id of the player.
     * @param human  - true iff the player is a human player (i.e. input is provided
     *               manually, via the keyboard).
     */
    public Player(Env env, Dealer dealer, Table table, int id, boolean human) {
        this.env = env;
        this.table = table;
        this.id = id;
        this.human = human;
        keys = new LinkedBlockingQueue<Integer>();
        Token = new ArrayList<>();
        this.dealer = dealer;
        givePoint = 0;
        penalty = false;
        returnedFromPenalty = false;
        blockAi = false;
        start = false;

    }

    /**
     * The main player thread of each player starts here (main loop for the player
     * thread).
     */
    @Override
    public void run() {
        playerThread = Thread.currentThread();
        env.logger.info("Thread " + Thread.currentThread().getName() + " starting.");
        if (!human)
            createArtificialIntelligence();

        while (!terminate) {
            int KeySlot = -1;
            try {
                KeySlot = keys.take();
                if (terminate) {
                    continue;
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }

            if (KeySlot == -1) {
                System.out.println("-1");
                return;
            }

            Check(KeySlot);

            if (!returnedFromPenalty && Token.size() == 3) {

                blockAi = true;
                Offer offer = new Offer(Token, id);

                try {
                    dealer.PlayerSet.put(offer);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                synchronized (this) {
                    try {
                        wait();
                        if (terminate) {
                            continue;
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                    }
                }
                if (givePoint == 1) {
                    point();
                    givePoint = 0;

                } else if (givePoint == -1) {
                    penalty();
                    givePoint = 0;
                }
                blockAi = false;
                if (!human)
                    aiThread.interrupt();
            }

        }
        if (!human)
            try {
                aiThread.join();
            } catch (InterruptedException ignored) {
            }
        env.logger.info("Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of
     * this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it
     * is not full.
     */
    private void createArtificialIntelligence() {
        // note: this is a very, very smart AI (!)
        aiThread = new Thread(() -> {
            env.logger.info("Thread " + Thread.currentThread().getName() + " starting.");
            while (!terminate) {
                int randomslot = (int) (Math.random() *env.config.tableSize);
                keyPressed(randomslot);
                // System.out.println("pressed");

                try {
                    if (blockAi)
                        synchronized (this) {
                            wait();
                            if (terminate)
                                continue;
                        }
                } catch (InterruptedException ignored) {
                }
            }
            env.logger.info("Thread " + Thread.currentThread().getName() + " terminated.");
        }, "computer-" + id);
        aiThread.start();
    }

    /**
     * Called when the game should be terminated.
     * 
     * @pre - the players in proccecs
     * @post- all the players should be finish ther job
     */
    public void terminate() {
        // TODO implement

        terminate = true;
        synchronized (this) {
            notifyAll();
        }
        
        System.out.println("player " + id + " is terminated");
    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        synchronized(this){
        if (table.slotToCard[slot] == null) {
            return;
        }
        if (start && dealer.ok && !penalty && dealer.canPress) {
            try {
                keys.put(slot);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
            }
        }
    }

    }

    /**
     * Award a point to a player and perform other related actions.
     * 
     * @pre -the player should be able to press on the table to legal set
     * @post - the player's score is increased by 1.
     * @post - the player's score is updated in the ui.
     */
    public void point() {
        // TODO implement
        penalty=true;
        int ignored = table.countCards(); // this part is just for demonstration in the unit tests
        env.ui.setScore(id, ++score);
        long freezTime = env.config.pointFreezeMillis;
        while (freezTime > 0) {
            env.ui.setFreeze(id, freezTime);
            if(freezTime<1&&freezTime>0){
                synchronized (Thread.currentThread()) {
                    try {
                        //wait(freezTime);
                       Thread.sleep(freezTime);
    
                    } catch (InterruptedException e) {
                    }
                    freezTime=0;
                }
                return;
            }
            else{
            synchronized (Thread.currentThread()) {
                try {
                   // wait(1000);
                   Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            }
            freezTime = freezTime - 1000;

        }
        env.ui.setFreeze(id, 0);
        penalty=false;



    }

    /**
     * Penalize a player and perform other related actions.
     * 
     * @pre - the set should be illegal
     * @post- the player cant press for 3 seconds
     * 
     */
    public void penalty() {
        // TODO implement
        penalty = true;
        returnedFromPenalty = true;
        long freezTime = env.config.penaltyFreezeMillis;
        while (freezTime > 0) {
            env.ui.setFreeze(id, freezTime);
            if(freezTime<1&&freezTime>0){
                synchronized (Thread.currentThread()) {
                    try {
                       //wait(freezTime);
                        Thread.sleep(freezTime);
    
                    } catch (InterruptedException e) {
                    }
                    freezTime=0;
                }
                return;
            }
            else{
            synchronized (this) {
                try {
                    //wait(1000);
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                }
            }
            }
            freezTime = freezTime - 1000;
        }
        env.ui.setFreeze(id, 0);
        penalty = false;
    }

    public int score() {
        return score;
    }

    public void Decrement(int slot) {
        Token.remove((Object) slot);
    }

    private void Check(int KeySlot) {
        if (table.slotToCard[KeySlot] == null) {
            return;
        }
        if (returnedFromPenalty && !Token.contains((Object) (KeySlot))) {
            return;
        } else
            returnedFromPenalty = false;
        if (Token.contains((Object) (KeySlot))) {
            table.removeToken(id, KeySlot);
            dealer.Press[id][KeySlot] = false;
            Token.remove((Object) KeySlot);
        } else if (Token.size() < 3) {
            table.placeToken(id, KeySlot);
            dealer.Press[id][KeySlot] = true;
            Token.add(KeySlot);
        }
    }

    /**
     * @pre - player should have size 3 tokens
     * @post- player tokens should be size 0
     * 
     */
    public void ResetTokens() {
        Token.clear();
    }

    /**
     * @pre - player returnedFromPenalty varible should be false
     * @post- player returnedFromPenalty varible should be changed according to what
     *        sending to him
     * 
     */
    public void SetPenalty(boolean a) {
        returnedFromPenalty = a;
    }

    /**
     * @pre - player should have some score
     * @post- player score shoud be changed
     * 
     */
    public void setScore(int a) {
        score = a;

    }

    /**
     * @pre - player givepoint varible should be -1/1 to take point or not
     * @post- player take the order according to which parameter sending to him
     * 
     */
    public void setPoint(int a) {
        givePoint = a;
    }

    public void SetTokens(int a, int b, int c) {
        Token.add(a);
        Token.add(b);
        Token.add(c);
    }

    public void setStart(boolean s) {
        start = s;
    }

}
