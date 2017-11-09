import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * Provides a nice visual for users. Regular users can use the RCMs while admins can view the
 * RMOS window after first logging in.
 */

public class Home extends JFrame implements ActionListener {

    JDBCHandler jdbc_handler;

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;

    private static Container pane;

    // NOTE*** Maps mean it is static objects
    // NOTE*** ArrayList means that It will be dynamic (gathered from database)
    private Map<String, JButton> buttons;
    /*  List of Buttons:
            search
            close
    */

    private Map<String, JLabel> labels;
    /*  List of Buttons:
            title
            day_of_week
            start_time
            end_time
            attributes
    */
    private Map<String, JComboBox> drop_downs;
    /*  List of Drop downs:
            day_of_week
            start_time
            end_time
            attributes
     */

    private Map<String, JScrollPane> scroll_panes;
    /*  List of Scroll Panes:
            services
            categories
            options
     */

    /*  Description
        Services -- far left column i.e. Restaurants, Sports
        Categories -- middle column i.e. Mexican, Asian
        Options -- right column i.e. price range
     */

    //TODO THESE STRINGS SHOULD BE CREATED DYNAMICALLY
    private String[] string_days_of_week = {"N/A", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private String[] string_hours_of_day =
            {"N/A", "12:00AM", "1:00AM", "2:00AM", "3:00AM", "4:00AM", "5:00AM", "6:00AM", "7:00AM", "8:00AM", "9:00AM", "10:00AM", "11:00AM",
                    "12:00PM", "1:00PM", "2:00PM", "3:00PM", "4:00PM", "5:00PM", "6:00PM", "7:00PM", "8:00PM", "9:00PM", "10:00PM", "11:00PM"
            };

    private String[] main_business_categories = {
            "Active Life", "Arts & Entertainment", "Automotive", "Car Rental", "Cafe",
            "Beauty & Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores",
            "Department Stores", "Education", "Event Planning & Services", "Flowers & Gifts",
            "Food", "Health & Medical", "Home Services", "Home & Garden", "Hospitals", "Hotels & Travel",
            "Hardware Stores", "Grocery", "medical Centers", "Nurseries & Gardening", "Nightlife",
            "Restaurants", "Shopping", "Transportation"
    };

    private String[] result_columns = {"Business", "City", "State", "Stars"};

    Object[][] test_data = new Object[][]{
            {"R1", "San Jose", "CA", "3"},
            {"R2", "San Jose", "CA", "4"},
            {"R3", "San Jose", "CA", "5"}
    };




    //These Variables are controlled by the GUI and will alter the final search results -- builder blocks for SQL query
    private Set<String> services = new HashSet<>();

    private Set<String> categories = new HashSet<>();

    private Set<String> options = new HashSet<>();

    private String day_of_week = "";
    private String start_time = "";
    private String end_time = "";
    private String attributes = "";



    public Home(JDBCHandler jdbc_handler) {
        this.jdbc_handler = jdbc_handler;
        //Gui Stuff
        JFrame frame = new JFrame("Home Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();

        //Size and display the window.
        Insets frameInsets = frame.getInsets();
        frame.setSize(WINDOW_WIDTH + frameInsets.left + frameInsets.right,
                WINDOW_HEIGHT + frameInsets.top + frameInsets.bottom);
        frame.setVisible(true);
        pane.setLayout(null);


        //Initialize Components
        buttons = new HashMap<>();
        labels = new HashMap<>();
        drop_downs = new HashMap<>();
        buttons = new HashMap<>();
        scroll_panes = new HashMap<>();

        createLabels();
        createButtons();
        createDropDowns();
        createScrollPanes();

    }

    /**
     *
     */
    public void createLabels() {
        // Creating Labels
        labels.put("title", GeneralJStuff.createLabel(pane, "Yelp System", 475, 10));
        labels.put("day_of_week", GeneralJStuff.createLabel(pane, "Day of the Week", 50, 500));
        labels.put("start_time", GeneralJStuff.createLabel(pane, "From:", 200, 500));
        labels.put("end_time", GeneralJStuff.createLabel(pane, "To:", 350, 500));
        labels.put("attributes", GeneralJStuff.createLabel(pane, "Search For:", 500, 500));

    }

    /**
     *
     */
    public void createButtons() {
        Runnable r_search = new Runnable() {
            @Override
            public void run() {
                updateDropDown("attributes"); //TODO CHANGE THIS LATER
            }
        };
        Runnable r_close = new Runnable() {
            @Override
            public void run() {
                updateScrollPane("results"); //TODO REMOVE THIS LATER
                //TODO SHOULD BE System.exit(0);
            }
        };

        buttons.put("search", GeneralJStuff.createButton(pane, "Search", 700, 500, 100, 25, r_search));
        buttons.put("close", GeneralJStuff.createButton(pane, "Close", 850, 500, 100, 25, r_close));
    }

    /**
     *
     */
    public void createDropDowns() {
        // Creating Drop Downs
        Runnable r_day_of_week = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("day_of_week").getSelectedItem());
            }
        };

        Runnable r_start_time = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("day_of_week").getSelectedItem());
            }
        };
        Runnable r_end_time = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("day_of_week").getSelectedItem());
            }
        };
        Runnable r_attributes = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get("day_of_week").getSelectedItem());

            }
        };
        drop_downs.put("day_of_week", GeneralJStuff.createDropDown(pane, string_days_of_week, 50, 500, 100, 100, r_day_of_week));
        drop_downs.put("start_time", GeneralJStuff.createDropDown(pane, string_hours_of_day, 200, 500, 100, 100, r_start_time));
        drop_downs.put("end_time", GeneralJStuff.createDropDown(pane, string_hours_of_day, 350, 500, 100, 100, r_end_time));
        drop_downs.put("attributes", GeneralJStuff.createDropDown(pane, string_hours_of_day, 500, 500, 100, 100, r_attributes));

    }

    /**
     *
     */
    public void createScrollPanes() {
        scroll_panes.put("categories", GeneralJStuff.createCheckBoxScrollPane(pane, main_business_categories, 50, 50, 145, 400, categories) );
        scroll_panes.put("service", GeneralJStuff.createCheckBoxScrollPane(pane, main_business_categories, 200, 50, 145, 400, services));
        scroll_panes.put("options", GeneralJStuff.createCheckBoxScrollPane(pane, main_business_categories, 350, 50, 145, 400,options));
        scroll_panes.put("results", GeneralJStuff.createTableScrollPane(pane, result_columns, test_data, 500, 50, 450, 400));
    }


    /**
     * I guess this doesn't really need to be here.... but I just inherited the class... so I'm just leaving it blank :)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }


    /**
     * This should
     *
     * @param scroll_pane_key
     */
    public void updateScrollPane(String scroll_pane_key) {
        String[] test = {"123", "123", "123", "123"};      //TODO SHOULD MAKE DB QUERY HERE TO CREATE THESE STRINGS
        //TODO OR THIS CAN BE CALLED AFTER A "QUERY-DB METHOD"
        Object[][] test_data124 = new Object[][]{
                {"123", "San 123", "123", "123"}
        };

        scroll_panes.get(scroll_pane_key).setVisible(false);
        pane.remove(scroll_panes.get(scroll_pane_key));
        scroll_panes.put(scroll_pane_key, GeneralJStuff.createTableScrollPane(pane, test, test_data124, 500, 50, 450, 400));

    }

    public void updateDropDown(String drop_down_key) {

        String[] array = {"N/A", "test"}; //TODO CHANGE THIS TO BE FROM DB QUERY .. REMEMBER TO HAVE N/A IN THE BEGINNING
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(drop_downs.get(drop_down_key).getSelectedItem());    //TODO CHANGE THIS
            }
        };
        drop_downs.get(drop_down_key).setVisible(false);
        pane.remove(drop_downs.get(drop_down_key));
        drop_downs.put("attributes", GeneralJStuff.createDropDown(pane, array, 500, 500, 100, 100, r));

    }

    /**
     * Triggered by Search Button
     */
    private void queryFindBusiness(){

    }

    private void queryFind(){

    }




}