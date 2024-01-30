module harjoitustyo.matopeli {
    requires javafx.controls;
    requires javafx.fxml;


    opens harjoitustyo.matopeli to javafx.fxml;
    exports harjoitustyo.matopeli;
}