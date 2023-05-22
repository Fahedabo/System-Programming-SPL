#include<iostream>
#include "Agent.h"




Agent::Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy) : mAgentId(agentId), mPartyId(partyId), mSelectionPolicy(selectionPolicy), numOfManInC(0),Acoalation(agentId)
{
    // You can change the implementation of the constructor, but not the signature!
}

Agent::~Agent(){
    //destructor
    if (mSelectionPolicy){
        delete mSelectionPolicy;
    }
}
Agent::Agent(const Agent &other):mAgentId(other.mAgentId),mPartyId(other.mPartyId),mSelectionPolicy(other.mSelectionPolicy),numOfManInC(other.numOfManInC),Acoalation(other.Acoalation)
{
// copy constructor

mSelectionPolicy=other.mSelectionPolicy->CloneOnHeap();

}

Agent::Agent(Agent &&other):mAgentId(other.mAgentId),mPartyId(other.mPartyId),mSelectionPolicy(other.mSelectionPolicy),numOfManInC(other.numOfManInC),Acoalation(other.Acoalation)
{
// move constructor
other.mSelectionPolicy=nullptr;
}

Agent &Agent::operator=(const Agent& other){
    //copy Assignment
    if (this!=&other){
    mAgentId=other.mAgentId;
    mPartyId=other.mPartyId;
    numOfManInC=other.numOfManInC;
    Acoalation=other.Acoalation;
    mSelectionPolicy=other.mSelectionPolicy->CloneOnHeap();
    }
    return *this;
}
Agent &Agent::operator=(Agent&& other){
    //move Assignment
    mAgentId=other.mAgentId;
    mPartyId=other.mPartyId;
    numOfManInC=other.numOfManInC;
    Acoalation=other.Acoalation;
    if(mSelectionPolicy) delete mSelectionPolicy;
    mSelectionPolicy=other.mSelectionPolicy->CloneOnHeap();
    other.mSelectionPolicy=nullptr;
    return *this;
}
 
int Agent::getId() const
{
    return mAgentId;
}
void Agent::setId(int id){
    mAgentId=id;
}

int Agent::getPartyId() const
{
    return mPartyId;
}
void Agent::setPartyId(int id){
    mPartyId=id;
}

void Agent::step(Simulation &sim)
{
    // TODO: implement this method
    //numOfManInC=sim.getParty(getPartyId()).getMandates();
    mSelectionPolicy->select(sim,mAgentId);
}
int Agent::getNumOfManInC() const
{
return numOfManInC;
}
int Agent::getcoalation() const{
    return Acoalation;
}
void Agent::setNumOfManInC(int s) 
{
numOfManInC=s;    

}

