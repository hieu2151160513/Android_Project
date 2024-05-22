package employee;

import java.util.ArrayList;
import java.util.Collection;


public class EmployeeManager {
    Collection<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public boolean removeEmployee(int id) {
        return employees.removeIf(emp -> emp.getId() == id);
    }

    public void displayAllEmployees() {
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
}
