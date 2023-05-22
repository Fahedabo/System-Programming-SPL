#pragma once

#include <vector>

class Termination
{
    public:
        Termination();
        void setCounter(int c);
        int getCounter() const; 
        void setSixstyOne(bool b);
        bool getSixstyOne() const;

    private:
        int counter;
        bool sixstyOne;
};