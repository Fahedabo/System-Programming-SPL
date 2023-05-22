from persistence import *

import sys

def main(args : list):
    inputfilename : str = args[1]
    with open(inputfilename) as inputfile:
        for line in inputfile:
            splittedline : list[str] = line.strip().split(", ")
            #TODO: apply the action (and insert to the table) if possible
            activitieObject=Activitie(splittedline[0],splittedline[1],splittedline[2],splittedline[3])
            if(int(activitieObject.quantity)>0):
                repo.activities.insert(activitieObject)
                # update the num of products that increase by the supplier
                add="UPDATE products SET quantity = quantity + '{}' WHERE id = '{}'".format(activitieObject.quantity,activitieObject.product_id)
                repo.execute_command(add)
            elif (int(activitieObject.quantity)<0):
                SaleProcess(activitieObject)

def SaleProcess(activitieObject):
    temp=repo.products.find(id=activitieObject.product_id)
    Sum=int(temp[0].quantity) + int(activitieObject.quantity)
    if(Sum>=0):
        repo.activities.insert(activitieObject)
        # update the num of products that decrease and he is a real number
        add="UPDATE products SET quantity = quantity + '{}' WHERE id = '{}'".format(activitieObject.quantity,activitieObject.product_id)
        repo.execute_command(add)
        SumAfterUpdated=repo.products.find(id=activitieObject.product_id)
        if(SumAfterUpdated==0):
            # the product finish
            #check if we should delete it or not 
            pass
    pass
if __name__ == '__main__':
    main(sys.argv)