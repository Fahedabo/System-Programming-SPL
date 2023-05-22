
from persistence import *
def main():
    #TODO: implement 
    print("Activities")
    ActivitieTable= repo._conn.execute("""SELECT * FROM activities ORDER BY date ASC""").fetchall()
    for output in ActivitieTable :
        Product_id=output[0]
        quantity=output[1]
        activator_id=output[2]
        date=output[3].decode()
        list=(Product_id,quantity,activator_id,date)
        print(list)
    print("Branches")
    BranchesTable= repo._conn.execute("""SELECT * FROM branches ORDER BY id ASC""").fetchall()
    for output in BranchesTable :
        id=output[0]
        location=output[1].decode()
        number_of_employees=output[2]
        list=(id,location,number_of_employees)
        print(list)
    print("Employees")
    EmployeesTable= repo._conn.execute("""SELECT * FROM employees ORDER BY id ASC""").fetchall()
    for output in EmployeesTable :
        id=output[0]
        name=output[1].decode()
        salary=output[2]
        brache=output[3]
        list=(id,name,salary,brache)
        print(list)
    print("Products")
    ProductsTable= repo._conn.execute("""SELECT * FROM products ORDER BY id ASC""").fetchall()
    for output in ProductsTable :
        id=output[0]
        description=output[1].decode()
        price=output[2]
        quantity=output[3]
        list=(id,description,price,quantity)
        print(list)
    print("Suppliers")
    SuppliersTable= repo._conn.execute("""SELECT * FROM suppliers ORDER BY id ASC""").fetchall()
    for output in SuppliersTable :
        id=output[0]
        name=output[1].decode()
        contact_information=output[2].decode()
        list=(id,name,price,contact_information)
        print(list)

    print("\nEmployees report")
    Employees_report_table=repo.execute_command("""SELECT employees.name AS Name,
    employees.salary AS Salary,
    branches.location AS Working_Location,
    COALESCE(SUM(products.price * ABS(activities.quantity)),0) AS total_sales_income
    FROM employees
    LEFT JOIN branches ON employees.branche = branches.id
    LEFT JOIN activities ON employees.id = activities.activator_id
    LEFT JOIN products ON activities.product_id = products.id
    GROUP BY  employees.name
    ORDER BY employees.name ASC
    """)
    for result in Employees_report_table:
        name=result[0].decode()
        salary=result[1]
        location=result[2].decode()
        earn=result[3]
        list=name+' ' +str(salary)+' ' +location+' '+str(earn)
        print(list)
    print("\nActivities report")
    Activities_report_table=repo.execute_command("""SELECT 
    activities.date AS date_of_activity,
    products.description AS item_description, 
    activities.quantity AS quantity,
    employees.name AS name_of_seller,
    suppliers.name AS the_name_of_the_supplier
    FROM activities
    LEFT JOIN employees ON activities.activator_id = employees.id 
    LEFT JOIN suppliers ON activities.activator_id = suppliers.id
    JOIN products ON activities.product_id = products.id
    ORDER BY date ASC
    """)
    for result in Activities_report_table:
        date=result[0].decode()
        product_name=result[1].decode()
        quantity=result[2]
        name=result[3]
        if(result[3]!=None):
            name=result[3].decode()
        Supplier=result[4]
        if(result[4]!=None):
            Supplier=result[4].decode()
        list=(date,product_name,quantity,name,Supplier)
        print(list)
        
    pass

if __name__ == '__main__':
    main()