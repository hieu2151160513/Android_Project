package employee;

import java.util.Scanner;

public class Main{
    
    static Scanner s = new Scanner(System.in);

    static int ch;

    static int id ;
    static String name;
    static int age ;
    static String department ;
    static String code ;
    static double salaryRate ;

    public static void main(String[] args){
        EmployeeManager manager = new EmployeeManager();
        manager.addEmployee(new Employee(1, "Minh", 20, "IT", "IT001", 1.5));
        manager.addEmployee(new Employee(2, "Quang", 32, "BA", "BA001", 1.3));
        manager.addEmployee(new Employee(3, "Hương", 25, "Marketing", "MK001", 1.2));
        manager.addEmployee(new Employee(4, "Linh", 28, "Kế toán", "KT001", 1.4));
        manager.addEmployee(new Employee(5, "Hoàng", 30, "Quản lý sản xuất", "QLSX001", 1.6));
        manager.addEmployee(new Employee(6, "Thu", 27, "Tài chính", "TC001", 1.3));
        manager.addEmployee(new Employee(7, "Đức", 35, "Kỹ thuật", "KT001", 1.5));
        manager.addEmployee(new Employee(8, "Hà", 29, "R&D", "RD001", 1.2));
        manager.addEmployee(new Employee(9, "Sơn", 31, "Hành chính", "HC001", 1.4));
        manager.addEmployee(new Employee(10, "Hải", 26, "Kinh doanh", "KD001", 1.3));
  
        do{
            System.out.println("1.Thêm nhân viên");
            System.out.println("2.Hiển thị danh sách nhân viên");
            System.out.println("3.Xóa nhân viên");
            System.out.print("Nhập lựa chọn của bạn: ");
            ch = s.nextInt();

            switch (ch) {
                case 1 : 
                    EnterDetailEmployeeToAdd();

                    manager.addEmployee(new Employee(id, name, age, department, code, salaryRate));
                break;

                case 2: 
                    System.out.println("            --------Danh sách nhân viên--------");
                    manager.displayAllEmployees();
                    System.out.println("                    ************        ");
                break;

                case 3: 
                    System.out.print("ID nhân viên muốn xóa: ");
                    int id_to_del = s.nextInt();

                    boolean result = manager.removeEmployee(id_to_del);
                    
                    if(result == true){
                        manager.displayAllEmployees();
                        System.out.println("Xóa thành công");
                        
                    }
                    else{
                        manager.displayAllEmployees();
                        System.out.println("Không có nhân viên có ID là " + id_to_del + " để xóa");
                    }
                    
                break;
            
                default:
                    break;
            }
        }while(ch != 0);
        s.close();

    }

    private static void EnterDetailEmployeeToAdd(){
        System.out.print("ID : ");
        id = s.nextInt();

        s.nextLine();

        System.out.print("Name : ");
        name = s.nextLine();

        System.out.print("Age : ");
        age = s.nextInt();

        s.nextLine();

        System.out.print("Department : ");
        department = s.nextLine();

        System.out.print("Code : ");
        code = s.nextLine();

        System.out.print("salaryRate : ");
        salaryRate = s.nextDouble();
    }

}

    

