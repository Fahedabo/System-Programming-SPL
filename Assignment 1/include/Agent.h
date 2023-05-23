#pragma once
#include <vector>
#include "SelectionPolicy.h"
using namespace std;
#include "Graph.h"
class SelectionPolicy;

class Agent
{
public:
    
    Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy);
    ~Agent();//destructor
    Agent(const Agent &other); //copy
    Agent(Agent&& other); //move
    Agent& operator=(const Agent& other);//copy Assignment
    Agent& operator=(Agent&& other);//move Assignment

    int getPartyId() const;
    void setPartyId(int id);
    int getId() const;
    void setId(int id);
    void step(Simulation &);
    int getNumOfManInC() const;
    void setNumOfManInC(int s);
    int getcoalation() const;
    


private:
    int mAgentId;
    int mPartyId;
    SelectionPolicy *mSelectionPolicy;
    int numOfManInC;
    int Acoalation;
    
};
