#include"SelectionPolicy.h"
#include "Party.h"
#include "Graph.h"
#include "Agent.h"
#include "JoinPolicy.h"
#include <string>

MandatesSelectionPolicy::MandatesSelectionPolicy(){}
MandatesSelectionPolicy::~MandatesSelectionPolicy(){}
EdgeWeightSelectionPolicy::EdgeWeightSelectionPolicy(){}
EdgeWeightSelectionPolicy::~EdgeWeightSelectionPolicy(){}






void EdgeWeightSelectionPolicy::select(Simulation &s,int AgId){
 int max=-1;
 int paid=-1;
 int partyId=s.getAgents()[AgId].getPartyId();
    for (int i=0;i<s.getGraph().getNumVertices();i++){
        State PartyState=s.getGraph().getParty(i).getState();
        //checking niberhood
            if(s.conditionsOk(partyId,i)){
                if(PartyState==Waiting){
                        if(max<s.getGraph().getEdgeWeight(i,partyId)){
                            max=s.getGraph().getEdgeWeight(i,partyId);
                            paid=i;
                        }
                }
                else{//collectingOffers
                    if(!(s.getGraph().getParty(i).YourcoalationOfferme(s, s.getAgents()[AgId].getcoalation()))){
                        //all the condetions ok
                        int edgeweight=s.getGraph().getEdgeWeight(i,partyId);
                           if(max<edgeweight){
                              max=edgeweight;
                              paid=i;
                          }
                }
            }
 
        }
    }
    if(paid!=-1){
    if(s.getGraph().getParty2(paid).getState()==Waiting){
        s.changeState(paid);
    }
    s.addOffer(paid, AgId);
    }
    else{
        return ;
    }
    
}



void MandatesSelectionPolicy::select(Simulation &s, int AgId){
    int max=-1;
    int paid=-1;
    
    int partyId=s.getAgents()[AgId].getPartyId();
    for (int i=0;i<s.getGraph().getNumVertices();i++){
        State PartyState=s.getGraph().getParty(i).getState();
        if(s.conditionsOk(partyId, i)){
            if(PartyState==Waiting){
                    if(max<s.getGraph().getParty(i).getMandates()){
                        max=s.getGraph().getParty(i).getMandates();
                        paid=i;
                    }
            }
            else{//collectingOffers
                if(!(s.getGraph().getParty(i).YourcoalationOfferme(s,s.getAgents()[AgId].getcoalation()))){
                   //all the condetions ok
                    int mandatessum=s.getGraph().getParty(i).getMandates();
                        if(max<mandatessum){
                            max=mandatessum;
                            paid=i;
                        }
                 }
            }
        }
    }
    if(paid!=-1){
        if(s.getGraph().getParty2(paid).getState()==Waiting){
            s.changeState(paid);
        }
        s.addOffer(paid, AgId);
    }
    else{
        return ;
    }
}






SelectionPolicy* MandatesSelectionPolicy:: CloneOnHeap(){
    return new MandatesSelectionPolicy;
}
SelectionPolicy* EdgeWeightSelectionPolicy:: CloneOnHeap(){
    return new EdgeWeightSelectionPolicy;
}
