#pragma once
#include <string>
#include <iostream>
#include "Simulation.h"
using namespace std;
class SelectionPolicy { 
    public:
    SelectionPolicy(){};
    virtual ~SelectionPolicy() { };
    virtual void select(Simulation &s,int AgId)=0;
    virtual SelectionPolicy* CloneOnHeap()=0;
};

class MandatesSelectionPolicy: public SelectionPolicy{
    public:
    void select(Simulation &s,int AgId);
    SelectionPolicy* CloneOnHeap();
    MandatesSelectionPolicy();
    ~MandatesSelectionPolicy();
};

class EdgeWeightSelectionPolicy: public SelectionPolicy{
    public:
    void select(Simulation &s,int AgId);
    SelectionPolicy* CloneOnHeap();
    EdgeWeightSelectionPolicy();
    ~EdgeWeightSelectionPolicy();
 };