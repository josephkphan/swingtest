import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

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


    //***************************************************************************************************************//
    //************************************************* CREATING DATA MEMBERS ***************************************//
    //***************************************************************************************************************//

    JDBCHandler jdbc_handler;

    private static final int WINDOW_WIDTH = 1300;
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
            categories
            main_category
            attributes
     */

    /*  Description
        Services -- far left column i.e. Restaurants, Sports
        Categories -- middle column i.e. Mexican, Asian
        Options -- right column i.e. price range
     */

    private String[] string_days_of_week = {"N/A"};
    private String[] string_start_hours_of_day = {"N/A"};
    private String[] string_end_hours_of_day = {"N/A"};
    private String[] string_state = {"N/A"};
    private String[] string_city = {"N/A"};
    private String[] string_search_for = {"OR", "AND"};
    private String[] main_business_categories = {
            "Active Life", "Arts and Entertainment", "Automotive", "Car Rental", "Cafe",
            "Beauty and Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores",
            "Department Stores", "Education", "Event Planning and Services", "Flowers and Gifts",
            "Food", "Health and Medical", "Home Services", "Home and Garden", "Hospitals", "Hotels and Travel",
            "Hardware Stores", "Grocery", "medical Centers", "Nurseries and Gardening", "Nightlife",
            "Restaurants", "Shopping", "Transportation"
    };

    private String[] sub_business_categories = {};
    private String[] attributes = {};
    private String[] result_columns = {"Business", "Address", "City", "State", "Stars", "ID", "# Reviews", "# Check Ins"};

    private final int BUSINESS_COLUMN_COUNT = 8;

    Object[][] data = new Object[][]{};
    ArrayList<String[]> data_arraylist = new ArrayList<>();
    ArrayList<String[]> schedule = new ArrayList<>();
    ArrayList<String> state_arraylist = new ArrayList<>();
    ArrayList<String> city_arraylist = new ArrayList<>();


    Object[][] reviews_data = new Object[][]{};


    //These Variables are controlled by the GUI and will alter the final search results -- builder blocks for SQL query
    private Set<String> main_category_set = new HashSet<>();
    private Set<String> subcategory_set = new HashSet<>();
    private Set<String> attributes_set = new HashSet<>();

    private String day_of_week = "N/A";
    private String start_time = "N/A";
    private String end_time = "N/A";
    private String state = "N/A";
    private String city = "N/A";
    private String search_attribute = "OR";

    private StringBuilder business_id_requested = new StringBuilder();
    private StringBuilder review_business_name = new StringBuilder();


    private int open_every_other_counter = 0; //TODO THIS IS A HOT FIX FOR OPENING REVIEWS
    //Reviews currently open up two every click? this toggle will allow one . %2


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

        String main_category_query = "SELECT DISTINCT(category) FROM MainCategories";
        ArrayList<String[]> temp = jdbc_handler.makeSearchQuery(main_category_query,1);
        main_business_categories = jdbc_handler.arrayListToStringArray(temp);
        Arrays.sort(main_business_categories);
        createLabels();
        createButtons();
        createDropDowns();
        createScrollPanes();

    }

    //***************************************************************************************************************//
    //************************************************* CREATING GUI ELEMENTS ***************************************//
    //***************************************************************************************************************//

    /**
     *
     */
    public void createLabels() {
        // Creating Labels
        labels.put("title", GeneralJStuff.createLabel(pane, "Yelp System", 475, 10));
        labels.put("day_of_week", GeneralJStuff.createLabel(pane, "Day of the Week", 50, 500));
        labels.put("start_time", GeneralJStuff.createLabel(pane, "From:", 200, 500));
        labels.put("end_time", GeneralJStuff.createLabel(pane, "To:", 350, 500));
        labels.put("attributes", GeneralJStuff.createLabel(pane, "Attributes Cond", 500, 500));
        labels.put("state", GeneralJStuff.createLabel(pane, "State", 650, 500));
        labels.put("city", GeneralJStuff.createLabel(pane, "City", 800, 500));

    }

    /**
     *
     */
    public void createButtons() {
        buttons.put("search", GeneralJStuff.createButton(pane, "Search", 925, 500, 100, 25, queryFindBusiness));
        buttons.put("reset", GeneralJStuff.createButton(pane, "Reset", 1050, 500, 100, 25, r_reset));
        buttons.put("close", GeneralJStuff.createButton(pane, "Close", 1175, 500, 100, 25, r_close));
    }

    /**
     *
     */
    public void createDropDowns() {
        drop_downs.put("day_of_week", GeneralJStuff.createDropDown(pane, string_days_of_week, 50, 500, 100, 100, r_day_of_week));
        drop_downs.put("start_time", GeneralJStuff.createDropDown(pane, string_start_hours_of_day, 200, 500, 100, 100, r_start_time));
        drop_downs.put("end_time", GeneralJStuff.createDropDown(pane, string_end_hours_of_day, 350, 500, 100, 100, r_end_time));
        drop_downs.put("attributes", GeneralJStuff.createDropDown(pane, string_search_for, 500, 500, 100, 100, r_attributes));
        drop_downs.put("state", GeneralJStuff.createDropDown(pane, string_state, 650, 500, 100, 100, r_state));
        drop_downs.put("city", GeneralJStuff.createDropDown(pane, string_city, 800, 500, 100, 100, r_city));

    }

    /**
     *
     */
    public void createScrollPanes() {
        scroll_panes.put("main_category", GeneralJStuff.createCheckBoxScrollPane(pane, main_business_categories, 50, 50, 145, 400, main_category_set, queryFindTypes));
        scroll_panes.put("sub_category", GeneralJStuff.createCheckBoxScrollPane(pane, sub_business_categories, 200, 50, 145, 400, subcategory_set, queryFindAttributes));
        scroll_panes.put("attributes", GeneralJStuff.createCheckBoxScrollPane(pane, attributes, 350, 50, 145, 400, attributes_set, r_empty));
        scroll_panes.put("results", GeneralJStuff.createTableScrollPane(pane, result_columns, data, 500, 50, 750, 400, business_id_requested, review_business_name, createReviews));
    }


    /**
     * I guess this doesn't really need to be here.... but I just inherited the class... so I'm just leaving it blank :)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    }


    //***************************************************************************************************************//
    //**************************************** CREATING RUNNABLES FOR GUI Drop Downs ********************************//
    //***************************************************************************************************************//


    Runnable r_close = new Runnable() {
        @Override
        public void run() {
            jdbc_handler.closeConnection();
            System.exit(0);
        }
    };
    Runnable r_reset = new Runnable() {
        @Override
        public void run() {
            clearAll();
        }
    };
    // Creating Drop Downs
    Runnable r_day_of_week = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("day_of_week").getSelectedItem());
            day_of_week = drop_downs.get("day_of_week").getSelectedItem().toString();
        }
    };
    Runnable r_start_time = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("start_time").getSelectedItem());
            start_time = drop_downs.get("start_time").getSelectedItem().toString();
        }
    };
    Runnable r_end_time = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("end_time").getSelectedItem());
            if (drop_downs.get("end_time").getSelectedItem().toString().compareTo("00:00") == 0) {
                end_time = "24:00";
            } else {
                end_time = drop_downs.get("end_time").getSelectedItem().toString();
            }

        }
    };
    Runnable r_attributes = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("attributes").getSelectedItem());
            Boolean changed = search_attribute.compareTo(drop_downs.get("attributes").getSelectedItem().toString()) != 0;
            search_attribute = drop_downs.get("attributes").getSelectedItem().toString();
            //TODO DO SOMETHING HERE? if changed?
//            if (changed) {
//                clearGUI();
//            }
        }
    };
    Runnable r_state = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("state").getSelectedItem());
            state = drop_downs.get("state").getSelectedItem().toString();
        }
    };
    Runnable r_city = new Runnable() {
        @Override
        public void run() {
            System.out.println(drop_downs.get("city").getSelectedItem());
            city = drop_downs.get("city").getSelectedItem().toString();
        }
    };


    //***************************************************************************************************************//
    //********************************************* Runnables with DB Queries ***************************************//
    //***************************************************************************************************************//


    /**
     * Triggered by Search Button
     */
    Runnable queryFindBusiness = new Runnable() {
        @Override
        public void run() {
            if (main_category_set.size() == 0) {
                System.out.println("Nothing Selected!");
                clearAll();
            } else {

                // reference private String[] result_columns = {"Business", "City", "State", "Address", "Stars", "ID", "# Reviews", "# Check Ins"};

                String search_query = "SELECT DISTINCT b.name, b.full_address, b.city,  b.state, b.stars, b.business_id, b.review_count " +
                        ",ci.total, mon_open, mon_close, tue_open, tue_close, wed_open, wed_close " +
                        ", thu_open, thu_close, fri_open, fri_close, sat_open, sat_close, sun_open, sun_close " +
                        "FROM Business b " +
                        "INNER JOIN MainCategories mc ON b.business_id=mc.business_id " +
                        "INNER JOIN SubCategories sc ON b.business_id=sc.business_id " +
                        "INNER JOIN Attributes a ON b.business_id=a.business_id " +
                        "INNER JOIN CheckIn ci ON b.business_id=ci.business_id ";

                //if(main_category_set.size() != 0){
                search_query += "WHERE ";
                //}

                // Adding Main Category Where Conditions
                search_query += "( ";
                search_query += createCategoriesString();
                search_query += ") ";

                // Adding Sub Categories Where Conditions
                if (subcategory_set.size() > 0) {
                    search_query += " AND (";
                    search_query += createSubcategoriesString();
                    search_query += ")";
                }

                if (search_attribute.compareTo("OR")==0){
                    // Adding Attributes Where Conditions
                    if (attributes_set.size() > 0) {
                        search_query += " AND (";
                        search_query += createAttributesString();
                        search_query += ")";
                    }
                }else{
                    int counter = 0;
                    String and_string = "INNER JOIN (";
                    for (String s: attributes_set){
                        and_string+= ("(SELECT att.business_id as b_id from Attributes att WHERE att.attribute=" + singleQuotes(s) + ")");
                        if (counter++ != attributes_set.size() - 1) {
                            and_string += " INTERSECT";
                        }
                    }
                    and_string += ") and_query on and_query.b_id=b.business_id ";
                    search_query=search_query.replaceAll("INNER JOIN Attributes a ON b.business_id=a.business_id",and_string);
                }




                search_query += time_check_string();
                search_query += location_check_string();

                // Querying Database
                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 22);
                System.out.println(Arrays.toString(data));

                data = jdbc_handler.arrayListToObjectArray(results,0,7);
                System.out.println("Businesses Size: " + data.length);
                recreateBusinessResults();

                data_arraylist = results;

                // Does it again to get business IDs (used as reference for Reviews)
//                results = jdbc_handler.makeSearchQuery(search_query, 22);

                for (int i = 0; i < results.size(); i++) {
                    city_arraylist.add(results.get(i)[2]);
                    state_arraylist.add(results.get(i)[3]);
                    schedule.add(Arrays.copyOfRange(results.get(i), 8, 23));
                }

                System.out.println("--------------------");
                System.out.println(schedule.toString());
                if (day_of_week.compareTo("N/A") == 0 &&
                        start_time.compareTo("N/A") == 0 &&
                        end_time.compareTo("N/A") == 0 &&
                        city.compareTo("N/A") == 0 &&
                        state.compareTo("N/A") == 0)
                    updateFilterDropDowns();

            }
        }
    };


    // Second Column: Based off of Category
    Runnable queryFindTypes = new Runnable() {
        @Override
        public void run() {
            attributes_set.clear();
            subcategory_set.clear();
            if (main_category_set.size() == 0) {
                clearSubCategories();
                clearAttributes();
            } else {

                String search_query = "SELECT DISTINCT(sc.category) FROM SubCategories sc JOIN ";
                search_query +=
                        "( SELECT b.business_id as b_id " +
                                "FROM Business b " +
                                "INNER JOIN MainCategories mc ON b.business_id=mc.business_id ";
                search_query += "WHERE ";

                search_query += createCategoriesString();

                search_query += ")query  ON query.b_id=sc.business_id";


                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 1);
                String[] string_results = jdbc_handler.arrayListToStringArray(results);
                System.out.println(Arrays.toString(string_results));

                Set<String> temp = new HashSet<>(Arrays.asList(string_results));
                temp.removeAll(Arrays.asList(main_business_categories));


                sub_business_categories = temp.toArray(new String[temp.size()]);
                Arrays.sort(sub_business_categories);

                System.out.println("SubCategories Size: " + temp.size());

                System.out.println(Arrays.toString(sub_business_categories));

                recreateSubCategories();

            }
        }
    };

    // Second Column: Based off of Category
    Runnable queryFindAttributes = new Runnable() {

        @Override
        public void run() {
            if (subcategory_set.size() == 0) {
                clearAttributes();

            } else if (main_category_set.size() != 0) {
                attributes_set.clear();
                String search_query = "SELECT DISTINCT(att.attribute) FROM Attributes att JOIN ";
                search_query +=
                        "( SELECT b.business_id as b_id, mc.category as mc_category, sc.category as sc_category " +
                                "FROM Business b " +
                                "INNER JOIN MainCategories mc ON b.business_id=mc.business_id " +
                                "INNER JOIN SubCategories sc ON b.business_id=sc.business_id ";


                search_query += "WHERE (";
                search_query += createCategoriesString();
                search_query += ")";

                // Adding Sub Categories Where Conditions
                if (subcategory_set.size() > 0) {
                    search_query += " AND (";
                    search_query += createSubcategoriesString();
                    search_query += ")";
                }

                search_query += ")query  ON query.b_id=att.business_id";

                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 1);
                String[] string_results = jdbc_handler.arrayListToStringArray(results);
                attributes = string_results;
                Arrays.sort(attributes);


                System.out.println("Attribute Size: " + attributes.length);

                recreateAttributes();
            }
        }
    };

    Runnable r_empty = new Runnable() {
        @Override
        public void run() {

        }
    };


    // Second Column: Based off of Category
    Runnable createReviews = new Runnable() {
        @Override
        public void run() {
            if (open_every_other_counter++ % 2 == 0) {//TODO git staHOT FIX
                String search_query = "SELECT r.date_string, r.stars, r.text, r.user_id, r.v_useful " +
                        "FROM Business b " +
                        "INNER JOIN Review r ON b.business_id=r.business_id " +
                        "WHERE  r.business_id=" + singleQuotes(business_id_requested.toString());

                ArrayList<String[]> results = jdbc_handler.makeSearchQuery(search_query, 5);
                Object[][] obj_results = jdbc_handler.arrayListToObjectArray(results);
                System.out.println(Arrays.toString(obj_results));

                reviews_data = obj_results;

                new Reviews(review_business_name.toString(), reviews_data);
            }

        }
    };

    //***************************************************************************************************************//
    //***************************************** Query Making Helper Functions ***************************************//
    //***************************************************************************************************************//


    private String createCategoriesString() {
        String string = "";
        int counter = 0;
        for (String str : main_category_set) {
            string += " mc.category=";
            string += singleQuotes(str);
            if (counter++ != main_category_set.size() - 1) {
                string += " " + search_attribute + " ";
            }
        }
        return string;
    }

    private String createSubcategoriesString() {
        String string = "";
        int counter = 0;
        for (String str : subcategory_set) {
            string += " sc.category=";
            string += singleQuotes(str);
            if (counter++ != subcategory_set.size() - 1) {
                string += " " + search_attribute + " ";
            }
        }
        return string;
    }

    private String createAttributesString() {
        int counter = 0;
        String string = "";
        for (String str : attributes_set) {
            string += " a.attribute=";
            string += singleQuotes(str);
            if (counter++ != attributes_set.size() - 1) {
                string += " " + search_attribute + " ";
            }
        }
        return string;
    }

    private void updateFilterDropDowns() {
        Set<String> days_found = new HashSet<>();
        Set<String> start_times_found = new HashSet<>();
        Set<String> end_times_found = new HashSet<>();
        Set<String> cities_found = new HashSet<>();
        Set<String> states_found = new HashSet<>();

        for (String[] arr : schedule) {
            if (arr[0].compareTo("null") != 0) {
                days_found.add("Monday");
            }

            if (arr[2].compareTo("null") != 0) {
                days_found.add("Tuesday");
            }

            if (arr[4].compareTo("null") != 0) {
                days_found.add("Wednesday");
            }

            if (arr[6].compareTo("null") != 0) {
                days_found.add("Thursday");
            }

            if (arr[8].compareTo("null") != 0) {
                days_found.add("Friday");
            }

            if (arr[10].compareTo("null") != 0) {
                days_found.add("Saturday");
            }

            if (arr[12].compareTo("null") != 0) {
                days_found.add("Sunday");
            }

            for (int i = 0; i < 14; i += 2) {
                if (arr[i].compareTo("null") != 0) {
                    start_times_found.add(arr[i]);
                }
            }

            for (int i = 1; i < 14; i += 2) {
                if (arr[i].compareTo("null") != 0) {
                    end_times_found.add(arr[i]);
                }
            }

        }
        cities_found.addAll(city_arraylist);
        states_found.addAll(state_arraylist);

        System.out.println("!!!!!!!!!");
        System.out.println(cities_found);
        System.out.println(states_found);

        ArrayList<String> list_days_of_week = new ArrayList<>();
        ArrayList<String> list_start_hours_of_day = new ArrayList<>();
        ArrayList<String> list_end_hours_of_day = new ArrayList<>();
        ArrayList<String> list_cities = new ArrayList<>();
        ArrayList<String> list_states = new ArrayList<>();


        list_days_of_week.addAll(days_found);
        Collections.sort(list_days_of_week);
        list_days_of_week.add(0, "N/A");

        list_start_hours_of_day.addAll(start_times_found);
        Collections.sort(list_start_hours_of_day);
        list_start_hours_of_day.add(0, "N/A");

        list_end_hours_of_day.addAll(end_times_found);
        Collections.sort(list_end_hours_of_day);
        list_end_hours_of_day.add(0, "N/A");

        list_cities.addAll(cities_found);
        Collections.sort(list_cities);
        list_cities.add(0, "N/A");

        list_states.addAll(states_found);
        Collections.sort(list_states);
        list_states.add(0, "N/A");

        string_days_of_week = list_days_of_week.toArray(new String[list_days_of_week.size()]);
        string_start_hours_of_day = list_start_hours_of_day.toArray(new String[list_start_hours_of_day.size()]);
        string_end_hours_of_day = list_end_hours_of_day.toArray(new String[list_end_hours_of_day.size()]);
        string_city = list_cities.toArray(new String[list_cities.size()]);
        string_state = list_states.toArray(new String[list_states.size()]);

        recreateDropDowns();
    }


    private String time_check_string() {
        String day = "";
        switch (day_of_week) {
            case "Monday":
                day = "mon";
                break;
            case "Tuesday":
                day = "tue";
                break;
            case "Wednesday":
                day = "wed";
                break;
            case "Thursday":
                day = "thu";
                break;
            case "Friday":
                day = "fri";
                break;
            case "Saturday":
                day = "sat";
                break;
            case "Sunday":
                day = "sun";
                break;
            default:
                return "";
        }
        String filter = "";
        if (day_of_week.compareTo("N/A") != 0 && start_time.compareTo("N/A") != 0 && end_time.compareTo("N/A") != 0) {
            // BETWEEN
            filter = " AND " + singleQuotes(start_time) + " >= " + "b." + day + "_open" + " AND " + singleQuotes(end_time) + " <= " + "b." + day + "_close" + " AND  b." + day + "_open not like '%null%'" + " AND  b." + day + "_close not like '%null%'";
        } else if (day_of_week.compareTo("N/A") != 0 && start_time.compareTo("N/A") != 0) {
            // String comparison
            filter = " AND " + singleQuotes(start_time) + " >= " + "b." + day + "_open" + " AND  b." + day + "_open not like '%null%'";
        } else if (day_of_week.compareTo("N/A") != 0 && end_time.compareTo("N/A") != 0) {
            // String Comparison
            filter = " AND " + singleQuotes(end_time) + " <= " + "b." + day + "_close" + " AND  b." + day + "_close not like '%null%'";
        } else {
            filter = " AND  b." + day + "_open not like '%null%'";
        }

        return filter;
    }

    private String location_check_string(){
        String filter = "";
        if(city.compareTo("N/A") != 0 ){
            filter += " AND b.city=" + singleQuotes(city);
        }
        if(state.compareTo("N/A") != 0 ){
            filter += " AND b.state=" + singleQuotes(state);
        }
        return filter;
    }


    //***************************************************************************************************************//
    //*********************************************** Random Helper Functions ***************************************//
    //***************************************************************************************************************//

    private String singleQuotes(String s) {
        return "'" + s + "'";
    }

    private String[] convertStringArrayListToArray(ArrayList<String> al) {
        String[] arr = new String[al.size()];
        return al.toArray(arr);
    }

    //***************************************************************************************************************//
    //****************************************** Clear GUI Components Section ***************************************//
    //***************************************************************************************************************//


    private void clearAll() {
        clearDropdowns();
        clearSubCategories();
        clearAttributes();
        clearBusinessResults();

    }

    private void clearDropdowns() {
        day_of_week = "N/A";
        start_time = "N/A";
        end_time = "N/A";
        city = "N/A";
        state = "N/A";

        string_start_hours_of_day = new String[]{"N/A"};
        string_end_hours_of_day = new String[]{"N/A"};
        string_days_of_week = new String[]{"N/A"};
        string_city = new String[]{"N/A"};
        string_state = new String[]{"N/A"};

        recreateDropDowns();
    }

    private void recreateDropDowns() {
        drop_downs.get("day_of_week").setVisible(false);
        pane.remove(drop_downs.get("day_of_week"));
        drop_downs.put("day_of_week", GeneralJStuff.createDropDown(pane, string_days_of_week, 50, 500, 100, 100, r_day_of_week));

        drop_downs.get("start_time").setVisible(false);
        pane.remove(drop_downs.get("start_time"));
        drop_downs.put("start_time", GeneralJStuff.createDropDown(pane, string_start_hours_of_day, 200, 500, 100, 100, r_start_time));

        drop_downs.get("end_time").setVisible(false);
        pane.remove(drop_downs.get("end_time"));
        drop_downs.put("end_time", GeneralJStuff.createDropDown(pane, string_end_hours_of_day, 350, 500, 100, 100, r_end_time));

        drop_downs.get("state").setVisible(false);
        pane.remove(drop_downs.get("state"));
        drop_downs.put("state", GeneralJStuff.createDropDown(pane, string_state, 650, 500, 100, 100, r_state));

        drop_downs.get("city").setVisible(false);
        pane.remove(drop_downs.get("city"));
        drop_downs.put("city", GeneralJStuff.createDropDown(pane, string_city, 800, 500, 100, 100, r_city));
    }

    // Sub Categories
    private void clearSubCategories() {
        subcategory_set.clear();
        sub_business_categories = new String[0];
        recreateSubCategories();
    }

    private void recreateSubCategories() {
        scroll_panes.get("sub_category").setVisible(false);
        pane.remove(scroll_panes.get("sub_category"));
        scroll_panes.put("sub_category", GeneralJStuff.createCheckBoxScrollPane(pane, sub_business_categories, 200, 50, 145, 400, subcategory_set, queryFindAttributes));

    }

    // Attributes
    private void clearAttributes() {
        attributes_set.clear();
        attributes = new String[0];
        recreateAttributes();
    }

    private void recreateAttributes() {
        scroll_panes.get("attributes").setVisible(false);
        pane.remove(scroll_panes.get("attributes"));
        scroll_panes.put("attributes", GeneralJStuff.createCheckBoxScrollPane(pane, attributes, 350, 50, 145, 400, attributes_set, r_empty));
    }


    // Business Results
    private void clearBusinessResults() {
        data = new Object[0][0];
        data_arraylist.clear();
        schedule.clear();
        city_arraylist.clear();
        state_arraylist.clear();

        recreateBusinessResults();
    }

    private void recreateBusinessResults() {
        scroll_panes.get("results").setVisible(false);
        pane.remove(scroll_panes.get("results"));
        scroll_panes.put("results", GeneralJStuff.createTableScrollPane(pane, result_columns, data, 500, 50, 750, 400, business_id_requested, review_business_name, createReviews));
    }
}

