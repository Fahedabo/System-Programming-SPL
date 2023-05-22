#pragma once
#include <string>
#include <vector>

using namespace std;
class JoinPolicy;
class Simulation;
enum State
{
    Waiting,
    CollectingOffers,
    Joined
};

class Party
{
public:
   Party(int id, string name, int mandates, JoinPolicy *);
    ~Party();   //destructor
    Party(const Party& other); // copy constructor
    Party(Party&& other); // move constructor
    Party& operator=(const Party& other); // copy assignment
    Party& operator=(Party&& other); // move assignment


    State getState() const;
    void setState(State state);
    int getMandates() const;
    void step(Simulation &s);
    int getId() const;
    const string &getName() const;
    void addtovector(int i);
    int getcoalation() const;
    void setcoalation(int col);
    void setTimer(int t);
    int getTimer();
    bool YourcoalationOfferme(Simulation &s ,int col) const;
    

private:
    int mId;
    string mName;
    int mMandates;
    JoinPolicy *mJoinPolicy;
    State mState;
    vector<int> WhoOfferMe;
    int Pcoalation;
    int timer;
};
