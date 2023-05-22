#pragma once
#include <vector>
#include "../include/Simulation.h"
#include <string>

using namespace std;
class Simulation;
class JoinPolicy {
    public:
    JoinPolicy(){};
    virtual ~JoinPolicy(){};
    virtual int join(Simulation &s, std::vector<int> offers)=0; 
    virtual JoinPolicy* CloneOnHeap()=0;
    
};

class MandatesJoinPolicy : public JoinPolicy {
    public:
    int join(Simulation &s ,std::vector<int> offers);
    JoinPolicy* CloneOnHeap();
    MandatesJoinPolicy();
    ~MandatesJoinPolicy();
};

class LastOfferJoinPolicy : public JoinPolicy {
    public:
    int join(Simulation &s, std::vector<int> offers);
    JoinPolicy* CloneOnHeap();
    LastOfferJoinPolicy();
    ~LastOfferJoinPolicy();
};