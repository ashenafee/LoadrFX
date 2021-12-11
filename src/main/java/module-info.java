module com.loadrfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.loadrfx to javafx.fxml;
    exports com.loadrfx;
    exports com.loadrfx.entities;
    opens com.loadrfx.entities to javafx.fxml;
    exports com.loadrfx.usecases;
    opens com.loadrfx.usecases to javafx.fxml;
}