module common {
    exports com.group7.dto.auction;
    exports com.group7.dto.auth;
    exports com.group7.dto.user;
    exports com.group7.dto.item;

    opens com.group7.dto.auth to com.fasterxml.jackson.databind;
    opens com.group7.dto.user to com.fasterxml.jackson.databind;
    opens com.group7.dto.auction to com.fasterxml.jackson.databind;
    opens com.group7.dto.item to com.fasterxml.jackson.databind;

    // This requires the Jackson dependency from your pom.xml
    requires com.fasterxml.jackson.databind;
}