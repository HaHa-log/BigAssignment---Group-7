module common {
    exports com.group7.dto.auction;
    exports com.group7.dto.auth;
    exports com.group7.dto.user;
    exports com.group7.dto.item;
    exports com.group7.dto.bid;

    opens com.group7.dto.auth to com.fasterxml.jackson.databind;
    opens com.group7.dto.user to com.fasterxml.jackson.databind;
    opens com.group7.dto.auction to com.fasterxml.jackson.databind;
    opens com.group7.dto.item to com.fasterxml.jackson.databind;
    opens com.group7.dto.bid to com.fasterxml.jackson.databind;
    exports com.group7.dto.transaction;
    opens com.group7.dto.transaction to com.fasterxml.jackson.databind;

    requires com.fasterxml.jackson.databind;
}