
#include "Party.h"
#include <vector>
#include "JoinPolicy.h"
#include "Simulation.h"
#include "Agent.h"

Party::Party(int id, string name, int mandates, JoinPolicy *jp) : mId(id), mName(name), mMandates(mandates), mJoinPolicy(jp), mState(Waiting),WhoOfferMe(),Pcoalation(-1),timer(0)
{
    // You can change the implementation of the constructor, but not the signature!
}

Party::~Party(){

if(mJoinPolicy) delete mJoinPolicy;
}
Party:: Party(const Party &other):mId(other.mId),mName(other.mName),mMandates(other.mMandates),mJoinPolicy(other.mJoinPolicy),mState(other.mState),WhoOfferMe(other.WhoOfferMe),Pcoalation(other.Pcoalation),timer(other.timer)
{//copy counstructor
   
mJoinPolicy=other.mJoinPolicy->CloneOnHeap();
  
}



Party:: Party(Party&& other):mId(other.mId),mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy) ,mState(other.mState),WhoOfferMe(other.WhoOfferMe),Pcoalation(other.Pcoalation),timer(other.timer){
  other.mJoinPolicy=nullptr;
    
}//move constructor

Party& Party::operator=(const Party& other){//copy assignment
    if (this!=&other){
    mId = other.mId;
    mName = other.mName;
    mMandates = other.mMandates;
    mJoinPolicy=other.mJoinPolicy->CloneOnHeap();
    mState = other.mState;
    WhoOfferMe = other.WhoOfferMe;
    Pcoalation = other.Pcoalation;
    timer = other.timer;
    }
    return *this;
}

Party& Party::operator=( Party&& other){//move assignment
mId = other.mId;
mName = other.mName;
mMandates = other.mMandates;
if(mJoinPolicy) delete mJoinPolicy;
mJoinPolicy=other.mJoinPolicy;
other.mJoinPolicy = nullptr;
mState = other.mState;
WhoOfferMe = other.WhoOfferMe;
Pcoalation = other.Pcoalation;
timer = other.timer;
return *this;
}



State Party::getState() const
{
    return mState;
}
void Party::setcoalation(int col) {
    Pcoalation=col;
}


void Party::setState(State state)
{
    mState = state;
}
int Party::getId() const{
    return mId;
}
void Party::addtovector(int i) {
    WhoOfferMe.push_back(i);
}
int Party::getcoalation() const{
    return Pcoalation;
}


int Party::getMandates() const
{
    return mMandates;
}

const string & Party::getName() const
{
    return mName;
}
void Party::setTimer(int t){
    timer=t;
}
int Party::getTimer(){
    return timer;
}
bool Party::YourcoalationOfferme(Simulation &s ,int col ) const{
    for (unsigned int j=0;j<WhoOfferMe.size();j++){
        if ((s.getAgents())[WhoOfferMe[j]].getcoalation()==col){
            return true;
        }
    }
    return false;
}

void Party::step(Simulation &s)
{
    // TODO: implement this method
    if(timer<3){
        if(mState==CollectingOffers){
            timer++;

        }
        //waiting
    }
    if ((timer == 3) && (mState!=Joined)){
        //the clone
        
        Agent ag2(s.getAgents()[mJoinPolicy->join(s,WhoOfferMe)]);
         ag2.setId(s.getAgents().size());
        int col=ag2.getcoalation();
        ag2.setPartyId(mId);
        Pcoalation=col;
        s.setMandatesNum(mMandates,Pcoalation);
        s.getTerm().setSixstyOne(s.getNumOfMandatesByCoalition(Pcoalation)>=61);
        s.settoAgents(ag2);
        setState(Joined);
        s.getTerm().setCounter((s.getTerm().getCounter())+1);
        // uptate the num of mandates for all the agents from the same col
        s.getvectorcoalations()[Pcoalation].push_back(mId);//
        
    }
    
}
