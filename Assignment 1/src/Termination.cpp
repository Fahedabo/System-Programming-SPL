#include "Termination.h"
#include <iostream>
Termination::Termination():counter(0), sixstyOne(false){
}
void Termination::setCounter(int c){
   counter=c;
}
int Termination::getCounter() const 
{
    return counter;
}
void Termination::setSixstyOne(bool b){
    sixstyOne=b;
}
bool Termination::getSixstyOne() const{
    return sixstyOne;
}
    
