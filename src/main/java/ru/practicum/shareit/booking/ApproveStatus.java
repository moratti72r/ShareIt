package ru.practicum.shareit.booking;

public enum ApproveStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static ApproveStatus toApproveStatus(String parameter) {
        for (ApproveStatus approveStatus : values()) {
            if (approveStatus.name().equalsIgnoreCase(parameter)) {
                return approveStatus;
            }
        }
        return null;
    }

}
