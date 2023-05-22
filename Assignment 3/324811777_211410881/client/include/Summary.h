#pragma once
#include <stdlib.h>
#include <string>
#include <iostream>
#include <map>
#include <vector>
#include <list>
using std::cerr;
using std::cin;
using std::cout;
using std::endl;
using std::string;
using std::map;
using std::vector;
class Summary{
    private:
    map<string,string> HashStates;
    map<string,string> TeamAhash;
    map<string,string> TeamBhash;
    map<string,string> DestinationHash;
    string destination;
    string User;
    map<string,map<string,std::vector<string>>> SummaryHash;
    string FinalSummary;

    public:
    Summary(string des,string user,map<string,map<string,std::vector<string>>> SumHash);
    virtual ~Summary();
    string PrintSummary();
    void TheRemain(string msg,string Event_Name,string Time);
    vector<string> SplitMsG(string line);
    string printHashMap(map<string, string> a);
    string printHashMapForDes(map<string, string> a);

};