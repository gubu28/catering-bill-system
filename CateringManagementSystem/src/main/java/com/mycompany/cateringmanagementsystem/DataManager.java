package com.mycompany.cateringmanagementsystem;

import java.io.*;
import java.util.*;

public class DataManager {
    private List<MenuItem> menuItems;
    private List<AddOnService> addOnServices;
    private List<Order> orders;
    private final String DATA_FILE = "catering_data.dat";

    public DataManager() {
        menuItems = new ArrayList<>();
        addOnServices = new ArrayList<>();
        orders = new ArrayList<>();
        loadData();
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public List<AddOnService> getAddOnServices() { return addOnServices; }
    public List<Order> getOrders() { return orders; }

    public void addMenuItem(MenuItem item) { menuItems.add(item); saveData(); }
    public void deleteMenuItem(MenuItem item) { menuItems.remove(item); saveData(); }

    public void addOrder(Order order) {
        orders.add(0, order);
        saveData();
    }

    public void deleteOrder(Order order) { orders.remove(order); saveData(); }

    public String generateNextOrderId() {
        int maxId = 1000;
        for (Order o : orders) {
            if (o.getOrderId() != null && o.getOrderId().startsWith("ORD-")) {
                try {
                    int id = Integer.parseInt(o.getOrderId().replace("ORD-", ""));
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "ORD-" + (maxId + 1);
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                menuItems = (List<MenuItem>) ois.readObject();
                orders = (List<Order>) ois.readObject();
            } catch (Exception e) {
                generate300PlusMenu();
            }
        } else {
            generate300PlusMenu();
        }

        if (menuItems.size() < 100) {
            generate300PlusMenu();
        }
        initAddOns();
    }

    private void generate300PlusMenu() {
        menuItems.clear();

        // 1. VEG DELICACIES (75 Items)
        String[] vegBase = {
            "Paneer Tikka Starter", "Paneer Butter Masala", "Kadhai Paneer", "Shahi Paneer", "Paneer Pasanda",
            "Palak Paneer", "Paneer Do Pyaza", "Paneer Lababdar", "Malai Kofta", "Veg Kofta Curry",
            "Dal Makhani Special", "Dal Tadka Yellow", "Dal Punjabi Tadka", "Chana Masala Amritsari", "Rajma Jammu Special",
            "Aloo Gobi Adraki", "Dum Aloo Kashmiri", "Aloo Jeera", "Mixed Vegetable Curry", "Veg Jalfrezi",
            "Navratan Korma", "Mushroom Masala", "Matar Mushroom", "Bhindi Masala", "Baingan Bharta",
            "Corn Palak Curry", "Veg Kolhapuri", "Methi Matar Malai", "Veg Handi Special", "Paneer Bhurji",
            "Royal Veg Biryani", "Hyderabadi Veg Dum Biryani", "Jeera Rice", "Veg Pulao", "Kashmiri Pulao",
            "Matar Pulao", "Steamed Basmati Rice", "Curd Rice South Indian", "Lemon Rice", "Tamarind Rice",
            "Butter Naan", "Garlic Naan", "Cheese Naan", "Tandoori Roti", "Butter Roti",
            "Missi Roti", "Laccha Paratha", "Pudina Paratha", "Stuff Aloo Paratha", "Paneer Paratha",
            "Hara Bhara Kebab", "Crispy Corn Cheese Balls", "Veg Spring Rolls", "Crispy Chilli Baby Corn", "Veg Manchurian Dry",
            "Gobi Manchurian", "Paneer 65", "Veg Cutlet", "Dahi Ke Kebab", "Corn Seekh Kebab",
            "South Indian Idli Sambhar", "Medu Vada", "Plain Dosa", "Masala Dosa", "Onion Rava Dosa",
            "Mysore Masala Dosa", "Uttapam Mixed Veg", "Dhokla Gujarati", "Khandvi", "Pav Bhaji Special",
            "Misal Pav Pune", "Chole Bhature", "Poori Bhaji", "Kachori Aloo Sabzi", "Samosa Ragda Chaat"
        };

        for (int i = 0; i < vegBase.length; i++) {
            double price = 120.0 + (i % 15) * 15.0;
            menuItems.add(new MenuItem(vegBase[i], "Veg", price, "Chef's special authentic vegetarian delight"));
        }

        // 2. NON-VEG DELICACIES (75 Items)
        String[] nonVegBase = {
            "Chicken Tikka Starter", "Butter Chicken Special", "Kadhai Chicken", "Chicken Chettinad", "Chicken Korma",
            "Chicken Seekh Kebab", "Chicken Reshmi Kebab", "Tandoori Chicken Full", "Chicken Lollipop", "Chicken Malai Tikka",
            "Chicken Do Pyaza", "Chicken Handi", "Chicken Lababdar", "Chicken Curry Home Style", "Chicken Changezi",
            "Chicken 65", "Chicken Manchurian", "Chilli Chicken Dry", "Chicken Wings BBQ", "Chicken Shawarma",
            "Royal Chicken Biryani", "Hyderabadi Chicken Dum Biryani", "Kolkata Chicken Biryani", "Chicken Tikka Biryani", "Chicken Pulao",
            "Mutton Dum Biryani", "Hyderabadi Mutton Biryani", "Mutton Rogan Josh", "Mutton Korma", "Mutton Keema Matar",
            "Mutton Seekh Kebab", "Mutton Rara", "Mutton Sukka", "Mutton Curry Home Style", "Bhuna Gosht",
            "Mutton Nalli Nihari", "Mutton Kadhai", "Mutton Chap Fry", "Mutton Haleem Special", "Mutton Galouti Kebab",
            "Fish Amritsari Fry", "Fish Curry Mustard", "Fish Tikka Tandoori", "Fish Fry Crispy", "Goan Fish Curry",
            "Fish Masala Gravy", "Fish Finger Snippets", "Grilled Fish Lemon Butter", "Fish Biryani", "Salmon Tandoori",
            "Prawns Curry South Style", "Prawns Fry Crispy", "Prawns Biryani", "Tandoori Prawns", "Garlic Butter Prawns",
            "Prawns Koliwada", "Prawns Masala Gravy", "Egg Curry Special", "Egg Biryani", "Egg Bhurji",
            "Egg Roast Kerala", "Egg Masala Gravy", "Tandoori Crab Masala", "Crab Sukka", "Squid Ring Fry",
            "Chicken Hakka Noodles", "Chicken Fried Rice", "Chicken Schezwan Noodles", "Chicken Momos Steamed", "Chicken Momos Fried",
            "Mutton Keema Paratha", "Chicken Stuff Naan", "Tandoori Chicken Tangdi", "Chicken Roast Kerala", "Chicken Ghee Roast"
        };

        for (int i = 0; i < nonVegBase.length; i++) {
            double price = 220.0 + (i % 20) * 20.0;
            menuItems.add(new MenuItem(nonVegBase[i], "Non Veg", price, "Fresh succulent non-vegetarian delicacy"));
        }

        // 3. DESSERTS & SWEETS (75 Items)
        String[] dessertBase = {
            "Royal Gulab Jamun", "Rasgulla Bengali", "Rasmalai Saffron", "Crispy Jalebi", "Rabri Jalebi Combo",
            "Gajar Ka Halwa", "Moong Dal Halwa", "Kaju Katli Special", "Kheer Rice Almond", "Phirni Matka",
            "Vanilla Scoop Ice Cream", "Chocolate Truffle Ice Cream", "Mango Delight Ice Cream", "Kulfi Falooda Royal", "Shahi Tukda",
            "Chocolate Mousse", "New York Cheesecake", "Hot Brownie Sizzler", "Pineapple Pastry", "Mysore Pak",
            "Motichoor Laddoo", "Besan Laddoo", "Badam Halwa", "Soan Papdi", "Tiramisu Italian",
            "Belgian Waffles Syrup", "Glazed Donuts", "French Macarons", "Chocolate Lava Cake", "Apple Pie Cinnamon",
            "Gulab Jamun Ice Cream Sundae", "Italian Gelato Dark Chocolate", "Crème Brûlée", "Baklava Turkish", "Churros Chocolate Dip",
            "Milk Cake Special", "Peda Mathura", "Cham Cham", "Bengali Sandesh", "Kalakand Soft",
            "Gujiya Festival Special", "Ghevar Rajasthani", "Mathura Peta", "Modak Ganesh Special", "Balushahi Royal",
            "Imarti Sugar Syrup", "Sugarfree Dates Kheer", "Sugarfree Almond Halwa", "Strawberry Shortcake", "Red Velvet Cake",
            "Black Forest Gateau", "Fruit Custard Creamy", "Caramel Pudding", "Mango Panacotta", "Strawberry Cheesecake",
            "Cupcakes Assorted", "Marshmallow Sundae", "Brownie Fudge Supreme", "Cassata Ice Cream Slice", "Matka Kulfi Kesar",
            "Tender Coconut Ice Cream", "Pistachio Kulfi", "Butterscotch Ice Cream Sundae", "Chocolate Fudge Sundae", "Banana Split Sundae",
            "Almond Biscotti", "Chocolate Croissant", "Blueberry Muffin", "Choco Chip Cookie", "Red Velvet Cupcake",
            "Walnut Pie", "Lemon Tart", "Custard Apple Ice Cream", "Rabri Falooda Glass", "Sweet Paan Special"
        };

        for (int i = 0; i < dessertBase.length; i++) {
            double price = 80.0 + (i % 10) * 12.0;
            menuItems.add(new MenuItem(dessertBase[i], "Dessert", price, "Sweet indulgence for grand celebrations"));
        }

        // 4. BEVERAGES & MOCKTAILS (75 Items)
        String[] beverageBase = {
            "Masala Chai Tapri", "South Indian Filter Coffee", "Fresh Lime Soda Sweet", "Fresh Lime Soda Salted", "Mango Lassi Royal",
            "Sweet Lassi Punjabi", "Salted Mint Lassi", "Cold Coffee Caramel", "Fresh Orange Juice", "Fresh Watermelon Juice",
            "Fresh Pineapple Juice", "Mixed Fruit Punch", "Virgin Mojito Mint", "Blue Lagoon Mocktail", "Virgin Pina Colada",
            "Jaljeera Refreshing", "Kesar Thandai", "Badam Milk Hot", "Rose Milk Chilled", "Tender Coconut Water",
            "Lemon Iced Tea", "Peach Iced Tea", "Apple Cider Drink", "Strawberry Smoothie", "Banana Nut Smoothie",
            "Green Tea Mint", "Espresso Shot", "Cappuccino Creamy", "Café Latte", "Hot Chocolate Fudge",
            "Soft Drink Cola", "Soft Drink Lemon", "Soft Drink Orange", "Sparkling Soda Water", "Detox Cucumber Mint Water",
            "Jeera Soda Fizz", "Aam Panna Summer Special", "Sol Kadhi Kokum", "Neeru Majjige Buttermilk", "Kokum Sharbat",
            "Sugarcane Juice Ginger", "Ginger Ale Soda", "Sunrise Mocktail", "Passion Fruit Fizz", "Watermelon Basil Cooler",
            "Cucumber Mint Splash", "Berry Blast Mocktail", "Guava Chilli Sparkler", "Blue Hawaiian Virgin", "Spicy Jamun Shot",
            "Lychee Blossom Mocktail", "Kiwi Delight Fizz", "Green Apple Sparkler", "Pomegranate Juice Fresh", "Grape Juice Chilled",
            "Muskmelon Juice", "Sweet Lime Mosambi Juice", "Iced Cold Chocolate", "Iced Hazelnut Latte", "Chai Latte Spiced",
            "Matcha Green Tea Latte", "Avocado Smoothie", "Berry Smoothie", "Mango Milkshake", "Chocolate Milkshake",
            "Oreo Fudge Milkshake", "KitKat Milkshake", "Nutella Hazelnut Shake", "Vanilla Bean Milkshake", "Strawberry Milkshake",
            "Blueberry Smoothie", "Lemonade Pink", "Energy Drink Nitro", "Almond Milk Saffron", "Royal Sharbati Rose"
        };

        for (int i = 0; i < beverageBase.length; i++) {
            double price = 60.0 + (i % 10) * 10.0;
            menuItems.add(new MenuItem(beverageBase[i], "Beverage", price, "Refreshing drink & gourmet beverage"));
        }

        saveData();
    }

    private void initAddOns() {
        addOnServices.clear();
        addOnServices.add(new AddOnService("👨‍🍳 Live Tandoor & Grill Counter", 5000.0, 0.0));
        addOnServices.add(new AddOnService("🍹 Luxury Mocktail & Beverage Bar", 3500.0, 0.0));
        addOnServices.add(new AddOnService("🍽️ Premium Cutlery & Crockery", 0.0, 30.0));
        addOnServices.add(new AddOnService("🕴️ Professional Service Staff", 4000.0, 0.0));
        addOnServices.add(new AddOnService("🌸 Table Flower & Stage Setup", 2500.0, 0.0));
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(menuItems);
            oos.writeObject(orders);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
}
