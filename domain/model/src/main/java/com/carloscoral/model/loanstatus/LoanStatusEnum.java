package com.carloscoral.model.loanstatus;

public enum LoanStatusEnum {
    PENDING_REVIEW("Pendiente de revisión"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado"),
    DISBURSED("Desembolsado"),
    CLOSED("Cerrado"),
    CANCELLED("Cancelado");

    private final String displayName;

    LoanStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static LoanStatusEnum fromDisplayName(String displayName) {
        for (LoanStatusEnum status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name: " + displayName);
    }

    public static String[] getAllDisplayNames() {
        return java.util.Arrays.stream(values())
                .map(LoanStatusEnum::getDisplayName)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
