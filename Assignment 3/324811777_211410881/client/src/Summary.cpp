#include <stdlib.h>
#include <string>
#include <fstream>
#include <iostream>
#include <map>
#include "../include/Summary.h"
#include "../include/StompProtocol.h"
#include <vector>
using std::cerr;
using std::cin;
using std::cout;
using std::endl;
using std::map;
using std::string;
using std::vector;

Summary::Summary(string des, string user, map<string, map<string, std::vector<string>>> sumHash) : HashStates(), TeamAhash(), TeamBhash(), DestinationHash(), destination(des), User(user), SummaryHash(sumHash), FinalSummary("")
{
    int fromto = des.find('_');
    FinalSummary = des.substr(0, fromto) + " VS " + des.substr(fromto + 1) + '\n';
}
Summary::~Summary()
{
}
string Summary::PrintSummary()
{
    if (SummaryHash.find(User) == SummaryHash.end()||(SummaryHash[User]).count(destination) == 0)
    {
        cout << "ERROR\n- - - - -\n\nSomeThing Wrong In Preparing The Summary\n\n- - - - -\nDescription: Make Sure That The User, Destination Is correct\nOr You Did Report" << endl;
        return "ERR0R\n";
    }
    vector<string> &events = SummaryHash[User][destination];
    string teamA = "";
    string teamB = "";
    for (unsigned int i = 0; i < events.size(); i++)
    {
        string msg = events.at(i);
        vector<string> splitmsg = SplitMsG(msg);
        teamA = splitmsg[4];
        int k = teamA.find(":");
        teamA = teamA.substr(k + 1);
        teamB = splitmsg[5];
        int r = teamB.find(":");
        teamB = teamB.substr(r + 1);
        string Event_Name = splitmsg[6];
        string Time = splitmsg[7];
        int counter = 0;
        for (unsigned int j = 0; j < msg.length(); j++)
        {
            if (counter == 8)
            {
                msg = msg.substr(j);
                break;
            }
            else if (msg.at(j) == '\n')
            {
                counter++;
            }
        }
        TheRemain(msg, Event_Name, Time);
    }
    FinalSummary += "Game stats:\nGeneral stats:\n" + printHashMap(HashStates);
    FinalSummary += teamA + " state:\n" + printHashMap(TeamAhash);
    FinalSummary += teamB + " state:\n" + printHashMap(TeamBhash);
    FinalSummary += "Game event reports:\n" + printHashMapForDes(DestinationHash);
    return FinalSummary;
}
void Summary::TheRemain(string msg, string Event_Name, string Time)
{
    vector<string> splitmsg = SplitMsG(msg);
    for (unsigned int i = 0; i < splitmsg.size(); i++)
    {
        if (splitmsg[i] == "general game updates:")
        {
            int k = i + 1;
            while (splitmsg[k] != "team a updates:")
            {
                int a = splitmsg[k].find(':');
                HashStates[splitmsg[k].substr(3, a - 2)] = splitmsg[k].substr(a + 2);
                k++;
            }
        }
        else if (splitmsg[i] == "team a updates:")
        {
            int k = i + 1;
            while (splitmsg[k] != "team b updates:")
            {
                int a = splitmsg[k].find(':');
                TeamAhash[splitmsg[k].substr(3, a - 2)] = splitmsg[k].substr(a + 2);
                k++;
            }
        }
        else if (splitmsg[i] == "team b updates:")
        {
            int k = i + 1;
            while (splitmsg[k] != "description:")
            {
                int a = splitmsg[k].find(':');
                TeamBhash[splitmsg[k].substr(3, a - 2)] = splitmsg[k].substr(a + 2);
                k++;
            }
        }
        else if (splitmsg[i] == "description:")
        {
            int a = Event_Name.find(":");
            Event_Name = Event_Name.substr(a + 1);
            int b = Time.find(":");
            Time = Time.substr(b + 1);
            DestinationHash[Time + "-" + Event_Name + ":\n"] = splitmsg[i + 1] + "\n\n";
            break;
        }
    }
}
vector<string> Summary::SplitMsG(string line)
{
    std::istringstream iss(line);
    std::vector<string> tokens;
    string token;
    while (getline(iss, token, '\n'))
    {
        tokens.push_back(token);
    }
    return tokens;
}
string Summary::printHashMap(map<string, string> a)
{
    string ans = "";
    map<string, string>::iterator itr;
    for (itr = a.begin(); itr != a.end(); itr++)
    {
        ans += itr->first + "" + itr->second + "\n";
    }
    return ans;
}
string Summary::printHashMapForDes(map<string, string> a)
{
    string ans = "";
    map<string, string>::iterator itr;
    for (itr = a.begin(); itr != a.end(); itr++)
    {
        ans += itr->first + "\n" + itr->second;
    }
    return ans;
}
