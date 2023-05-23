#include "Graph.h"

Graph::Graph(vector<Party> vertices, vector<vector<int>> edges) : mVertices(vertices), mEdges(edges) 
{
    // You can change the implementation of the constructor, but not the signature!
}
Graph::~Graph(){//destructor

}
Graph::Graph(const Graph &other):mVertices(other.mVertices),mEdges(other.mEdges){

}//copy construcror

// move constructor 


Graph &Graph:: operator=(const Graph &other){///copy assignment
    mVertices=other.mVertices;
    mEdges=other.mEdges;
    return *this;
}



int Graph::getMandates(int partyId) const
{
    return mVertices[partyId].getMandates();
}

int Graph::getEdgeWeight(int v1, int v2) const
{
    return mEdges[v1][v2];
}

int Graph::getNumVertices() const
{
    return mVertices.size();
}

 const Party &Graph::getParty(int partyId) const
{
    return mVertices[partyId];
}
Party &Graph::getParty2(int partyId){
    return mVertices[partyId];
}