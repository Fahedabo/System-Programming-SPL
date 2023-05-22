
#include "JoinPolicy.h"




MandatesJoinPolicy::MandatesJoinPolicy(){}
LastOfferJoinPolicy::LastOfferJoinPolicy(){}
MandatesJoinPolicy::~MandatesJoinPolicy(){}
LastOfferJoinPolicy::~LastOfferJoinPolicy(){} 

int LastOfferJoinPolicy::join(Simulation &s, vector<int> offers){
return offers.back();
}





int MandatesJoinPolicy::join(Simulation &s ,vector<int> offers){
    int max=-1;
    int ans=-1;
    for (unsigned int i=0; i<offers.size();i++){
        int a=s.getSUM(s.getAgents()[offers[i]].getcoalation());
        if(max<a){
            max=a;
            ans=offers[i];
        }
    }
    return ans;
}

JoinPolicy* LastOfferJoinPolicy::CloneOnHeap(){
    return new LastOfferJoinPolicy;
}
JoinPolicy* MandatesJoinPolicy::CloneOnHeap(){
    return new MandatesJoinPolicy;;
}

