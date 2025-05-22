module com.fct.we_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires java.sql;
    requires java.persistence;
//    requires org.hibernate.orm.core;

    opens com.fct.we_chat to javafx.fxml;
    exports com.fct.we_chat;
}