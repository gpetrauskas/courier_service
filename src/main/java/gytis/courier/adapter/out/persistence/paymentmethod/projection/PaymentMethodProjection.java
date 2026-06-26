package gytis.courier.adapter.out.persistence.paymentmethod.projection;

public interface PaymentMethodProjection {
    Long getId();
    String getPaymentType();
    boolean isSaved();
    String getLast4();
    String getPpEmail();
}
