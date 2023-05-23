#pragma once

#include <vector>
#include "Graph.h"
#include "Agent.h"

#include "Termination.h"

using std::string;
using std::vector;

class Agent;
class Simulation
{
public:
    Simulation(Graph g, vector<Agent> agents);
    ~Simulation();
    void step();
    bool shouldTerminate() const;
    Termination &getTerm();
    

    const Graph &getGraph() const;
    Graph &getGraph();
    const vector<Agent> &getAgents() const;
    const Party& getParty(int partyId) const;
    const vector<vector<int>> getPartiesByCoalitions() const;
    void settoAgents(Agent ag);
    void addOffer(int partyId, int agentId);
    void updateMandates(int col,int sum);
    void setMandatesNum(int a,int colId);
    int getNumOfMandatesByCoalition(int colId);
    void changeState(int paid);
    void increaseTime(int paid);
    bool conditionsOk(int i , int pa);
    int getSUM(int i);
     vector<vector<int>> makecoalation(int coalNum);
     vector<vector<int>> &getvectorcoalations();
   

private:
    Graph mGraph;
    vector<Agent> mAgents;
    Termination term;
    int coalNum;
    vector<int> sumofmandatescoalation;
    vector<vector<int>> vectorcoalations;
};
