#include "Simulation.h"
#include "Termination.h"
#include <vector>
Simulation::Simulation(Graph graph, vector<Agent> agents) : mGraph(graph), mAgents(agents),term(), coalNum(agents.size()),sumofmandatescoalation(),vectorcoalations(coalNum)//
{
    int size=agents.size();
    term.setCounter(size);
    std::cout << "test" << std::endl;
    for(int i=0;i<size;i++){
        mGraph.getParty2(agents[i].getPartyId()).setcoalation(i);
        sumofmandatescoalation.push_back(graph.getParty2(agents[i].getPartyId()).getMandates());
    }
    
    for (int i =mGraph.getNumVertices()-1;i>=0;i--){
        if(mGraph.getParty(i).getcoalation() != -1) //////////////////////
            vectorcoalations[mGraph.getParty(i).getcoalation()].push_back(i);
    }
    
    // You can change the implementation of the constructor, but not the signature!
}
Simulation::~Simulation(){};
void Simulation::step()
{
    // TODO: implement this method
    for(int i=0;i<mGraph.getNumVertices();i++){
        mGraph.getParty2(i).step(*this);
    }

    for(unsigned int i=0;i<mAgents.size();i++){
        mAgents[i].step(*this);
    }
    
}
int Simulation:: getSUM(int i){
    return sumofmandatescoalation[i];
}

bool Simulation::shouldTerminate() const
{
    // TODO implement this method
    return ((term.getCounter() == getGraph().getNumVertices())||term.getSixstyOne());
}

const Graph &Simulation::getGraph() const
{
    return mGraph;
}
Graph &Simulation::getGraph(){
    return mGraph;
}

const vector<Agent> &Simulation::getAgents() const
{
    return mAgents;
}
/////
void Simulation::settoAgents(Agent ag){
    mAgents.push_back(ag);
}

const Party &Simulation::getParty(int partyId) const
{
    return mGraph.getParty(partyId);
}

Termination &Simulation::getTerm(){
    return term;
}
void Simulation::changeState(int paid){
    mGraph.getParty2(paid).setState(CollectingOffers);
}
void Simulation::increaseTime(int paid){
    int current=mGraph.getParty2(paid).getTimer();
    mGraph.getParty2(paid).setTimer(current+1);
}
void Simulation::setMandatesNum(int a,int colId){
    sumofmandatescoalation[colId]=a+sumofmandatescoalation[colId];
}
bool Simulation::conditionsOk(int i , int pa){
    return (i!=pa)&& (mGraph.getParty(pa).getState()!=Joined)&& 
    (mGraph.getEdgeWeight(i,pa)>0);

}
int Simulation::getNumOfMandatesByCoalition(int colId){
    return sumofmandatescoalation[colId];
}
/// This method returns a "coalition" vector, where each element is a vector of party IDs in the coalition.
/// At the simulation initialization - the result will be [[agent0.partyId], [agent1.partyId], ...]
const vector<vector<int>> Simulation::getPartiesByCoalitions() const
{
    // vector<vector<int>> coalations;
    // for(int i =0 ; i <coalNum; i++){
    //     vector<int> vec;
    //     coalations.push_back(vec);
    // }
    // for (int i =0;i<mGraph.getNumVertices();i++){
    //     if(mGraph.getParty(i).getcoalation() != -1)
    //         coalations[mGraph.getParty(i).getcoalation()].push_back(i);
    // }
    // TODO: you MUST implement this method for getting proper output, read the documentation above.
    return vectorcoalations;
}

void Simulation::addOffer(int partyId, int agentId){
    mGraph.getParty2(partyId).addtovector(agentId);
}
// void Simulation::updateMandates(int col,int newsum){
//     int size=mAgents.size();
//     for(int i=0;i<size;i++){
//         if(mAgents[i].getcoalation()==col){
//            mAgents[i].setNumOfManInC(newsum); 
//         }
//     }
// }
vector<vector<int>> Simulation:: makecoalation(int coalNum){
    vector<vector<int>> coalations;
    for(int i =0 ; i <coalNum; i++){
        vector<int> vec;
        coalations.push_back(vec);
    }
    return coalations;
    
}
vector<vector<int>> &Simulation::getvectorcoalations(){
    return vectorcoalations;
}