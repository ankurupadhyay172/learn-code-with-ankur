import java.util.*;

class Product{
    int productId;
    String productName;
    Product(int productId,String productName){
        this.productId = productId;
        this.productName = productName;
    }
}

class ProductCategory{
    int productCategoryId;
    String productCategoryName;
    double price;
    List<Product> productsList;
    
    ProductCategory(int productCategoryId,String productCategoryName,double price){
        this.productCategoryId = productCategoryId;
        this.productCategoryName = productCategoryName;
        this.price = price;
        productsList = new ArrayList();
    }
    
     void addProduct(Product product){
         productsList.add(product);
     }
     
     void removeProductItems(int count){
         for(int i=1;i<=count;i++){
             productsList.remove(0);
         }
     }
}

class Inventory{
    List<ProductCategory> productCategoriesList;
    Inventory(){
        productCategoriesList = new ArrayList();
    }
    
    void addCategory(int productCategoryId,String productCategoryName,double price){
        ProductCategory category = new ProductCategory(productCategoryId,productCategoryName,price);
        productCategoriesList.add(category);
    }
    
    void addCategoryProduct(Product product,int productCategoryId){
        ProductCategory obj = null;
        for(ProductCategory productCategory:productCategoriesList){
            if(productCategory.productCategoryId==productCategoryId){
                obj = productCategory;
            }
        }
        if(obj!=null){
            obj.addProduct(product);
        }
    }
    
    void removeItemFromCategory(Map<Integer,Integer> productCategoryIdVsCountMap){
        for(Map.Entry<Integer,Integer> entry:productCategoryIdVsCountMap.entrySet()){
            ProductCategory category = getProductCategoryFromId(entry.getKey());
            category.removeProductItems(entry.getValue());
        }
    }
    
    ProductCategory getProductCategoryFromId(int productCategoryId){
        for(ProductCategory productCategory:productCategoriesList){
            if(productCategory.productCategoryId==productCategoryId){
                return productCategory;
            }
        }
        return null;
    }
    
    void printInventory(){
        for(ProductCategory category:productCategoriesList){
            System.out.println("Name = "+category.productCategoryName+" , Quan = "+category.productsList.size());
        }
    }
}

class Address{
    int pinCode;
    String city;
    String state;
}

class Warehouse{
    Address address;
    Inventory inventory;
    Warehouse(){
        address = new Address();
        inventory = new Inventory();
    }
    
    void removeItemFromCategory(Map<Integer,Integer> productCategoryIdVsCountMap){
        inventory.removeItemFromCategory(productCategoryIdVsCountMap);
    }
    
    void addItemToCategory(Map<Integer,Integer> productCategoryIdVsCountMap){
        
    }
    
    void printInventory(){
        inventory.printInventory();
    }
    
    
}

//--------------------- order management ---------------------

class Cart{
    Map<Integer,Integer> productCategoryIdVsCountMap;
    
    Cart(){
        productCategoryIdVsCountMap = new HashMap();
    }
    
    void addToCart(int productCategoryId,int count){
        if(productCategoryIdVsCountMap.containsKey(productCategoryId)){
            int prevCount = productCategoryIdVsCountMap.get(productCategoryId);
            productCategoryIdVsCountMap.put(productCategoryId,prevCount+count);
        }else{
            productCategoryIdVsCountMap.put(productCategoryId,count);
        }
    }
    
    void removeFromCart(int productCategoryId,int count){
        if(productCategoryIdVsCountMap.containsKey(productCategoryId)){
            int prevCount = productCategoryIdVsCountMap.get(productCategoryId);
            if(prevCount-count<=0){
                productCategoryIdVsCountMap.remove(productCategoryId);
            }else{
            productCategoryIdVsCountMap.put(productCategoryId,prevCount-count);    
            }
        }
    }
    
    void emptyCart(){
        productCategoryIdVsCountMap = new HashMap();
    }
    
    Map<Integer,Integer> getCart(){
        return productCategoryIdVsCountMap;
    }
}

class User{
    Address address;
    int userId;
    String userName;
    Cart cartDetails;
    List<Integer> ordersList;
    
    User(){
        address = new Address();
        cartDetails = new Cart();
        ordersList = new ArrayList();
    }
    
    Cart getUserCart(){
        return cartDetails;
    }
}

class Invoice{
    double totalAmount;
    int tax;
    double finalAmount;
    
    void printInvoice(Order order){
        for(Map.Entry<Integer,Integer> entry:order.productCategoryIdVsCountMap.entrySet()){
            double totalPrice = 0;
            ProductCategory category = order.warehouse.inventory.getProductCategoryFromId(entry.getKey());
            totalPrice = category.price*entry.getValue();
            System.out.println(""+category.productCategoryName+" X "+entry.getValue()+" = "+totalPrice);
            totalAmount += totalPrice;
        }
        tax = (int)(totalAmount*0.1);
        finalAmount = totalAmount+tax;
        System.out.println("Tax = "+tax);
        System.out.println("Final amount after tax = "+finalAmount);
    }
}

class Order{
    Map<Integer,Integer> productCategoryIdVsCountMap;
    User user;
    Address address;
    Payment payment;
    Warehouse warehouse;
    Invoice invoice;
    Order(User user,Warehouse warehouse){
        this.user = user;
        address = user.address;
        productCategoryIdVsCountMap = user.getUserCart().productCategoryIdVsCountMap;
        this.warehouse = warehouse;
        payment = new Payment(new UpiPaymentMode());
        invoice = new Invoice();
    }
    
    void checkout(){
        //updateInventory
        warehouse.removeItemFromCategory(productCategoryIdVsCountMap);
        
        //make payment
        boolean isPaymentSuccess = payment.mapkePayment();
        
        if(isPaymentSuccess){
            user.getUserCart().emptyCart();
        }else{
            warehouse.addItemToCategory(productCategoryIdVsCountMap);
        }
    }
    
    void printInvoice(){
        invoice.printInvoice(this);
    }
}

class UpiPaymentMode implements PaymentMode{
    public boolean mapkePayment(){
        return true;
    }
}

interface PaymentMode{
    boolean mapkePayment();
}
class Payment{
    PaymentMode paymentMode;
    Payment(PaymentMode paymentMode){
        this.paymentMode = paymentMode;
    }
    
    boolean mapkePayment(){
        return paymentMode.mapkePayment();
    }
}

//--------------------- Administator ---------------------
class NearestWarehouseSelectionStreategy extends WarehouseSelectionStreategy{
    
    public Warehouse selectWarehouse(List<Warehouse> warehouseList){
        return warehouseList.get(0);
    }
}

abstract class WarehouseSelectionStreategy {
    abstract Warehouse selectWarehouse(List<Warehouse> warehouseList);
}

class UserContoller{
    List<User> usersList;
    UserContoller(List<User> usersList){
    this.usersList = usersList;
    }
    
    void addUser(User user){
        usersList.add(user);
    }
    
    void removeUser(User user){
        usersList.remove(user);
    }
    
    User getUser(int userId){
        for(User user:usersList){
            if(user.userId==userId)
            return user;
        }
        return null;
    }
}

class WarehouseController{
    List<Warehouse> warehouseList;
    WarehouseController(List<Warehouse> warehouseList){
        this.warehouseList = warehouseList;
    }
    
    void addWarehouse(Warehouse warehouse){
        warehouseList.add(warehouse);
    }
    
    void removeWarehouse(Warehouse warehouse){
        warehouseList.remove(warehouse);
    }
    
    Warehouse selectWarehouse(WarehouseSelectionStreategy streategy){
        return streategy.selectWarehouse(warehouseList);
    }
}

class OrdersController{
    List<Order> ordersList;
    Map<Integer,List<Order>> userIdVsOrders;
    
    OrdersController(){
        ordersList = new ArrayList();
        userIdVsOrders = new HashMap();
    }
    
    void createNewOrder(User user,Warehouse warehouse){
        Order order = new Order(user,warehouse);
        ordersList.add(order);
    }
}

class ProductDeliverySystem {
    UserContoller userController;
    WarehouseController warehouseController;
    OrdersController orderController;
    ProductDeliverySystem(List<Warehouse> warehouseList,List<User> usersList){
        userController = new UserContoller(usersList);
        warehouseController = new WarehouseController(warehouseList);
        orderController = new OrdersController();
    }
    
    User getUser(int userId){
        return userController.getUser(userId);
    }
    
    Warehouse getWarehouse(WarehouseSelectionStreategy streategy){
        return warehouseController.selectWarehouse(streategy);
    }
    
    void placeOrder(){
        //select user
        User user = getUser(1);
        //get warehouse
        Warehouse warehouse = getWarehouse(new NearestWarehouseSelectionStreategy());
        //get Inventory
        warehouse.printInventory();

        //add to cart 
        Cart cart = user.getUserCart();
        cart.addToCart(1,2);
        Order order = new Order(user,warehouse);
        order.checkout();
        
        System.out.println("--------------------- Invoice ---------------------");
        order.printInvoice();
        
    }
}


public class InventoryManagementSystem
{
	public static void main(String[] args) {
		Main main = new Main();
		System.out.println("--------------------- Inventory ---------------------");
		Warehouse warehouse = main.getWarehouse();
		
		
		
	    List<Warehouse> warehouseList = new ArrayList();
	    warehouseList.add(warehouse);
	    List<User> usersList = new ArrayList();
	    User user = new User();
	    user.userId = 1;
	    user.userName = "Ankur";
	    usersList.add(user);
	    
	    ProductDeliverySystem productDeliverySystem = new ProductDeliverySystem(warehouseList,usersList);
	    productDeliverySystem.placeOrder();
	    
	    
	}
	    
	
	
	Warehouse getWarehouse(){
	    Warehouse warehouse = new Warehouse();
	    Inventory inventory = new Inventory();
	    inventory.addCategory(1,"pepsi",50);
	    inventory.addCategory(2,"kitkat",60);
	    inventory.addCategoryProduct(new Product(1,"pepsi"),1);
	    inventory.addCategoryProduct(new Product(2,"pepsi"),1);
	    inventory.addCategoryProduct(new Product(3,"kitkat"),2);
	    warehouse.inventory = inventory;
	    return warehouse;
	}
}