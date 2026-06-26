package gytis.courier.domain.payment.method;

import gytis.courier.domain.payment.ProviderType;

import java.util.Objects;

public class Paypal extends PaymentMethod {
    private String ppEmail;

    protected Paypal() {}

    public static Paypal recover(Long id, boolean saved, String token, String ppEmail) {
        Paypal pp = new Paypal();
        pp.id = id;
        pp.saved = saved;
        pp.token = token;
        pp.ppEmail = ppEmail;
        return pp;
    }

    public Paypal(String email, boolean saved) {
        this.ppEmail = Objects.requireNonNull(email);
        this.setSaves(saved);
    }

    public static Paypal create(String email, boolean saved) {
        return new Paypal(email, saved);
    }

    @Override
    public ProviderType providerType() { return ProviderType.PAYPAL; }

    @Override
    public void validate(String ignoreCvc) { }

    @Override
    public void setToken(String token) {
        super.setToken(token);
    }

    public String getPpEmail() { return ppEmail; }
    public String getToken() { return super.getToken(); }
}
