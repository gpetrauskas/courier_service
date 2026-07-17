package gytis.courier.domain.payment.method;

import gytis.courier.domain.payment.ProviderType;

public abstract class PaymentMethod {
    protected Long id;
    protected boolean saved;
    protected String token;

    protected void setId(Long id) {
        this.id = id;
    }

    protected PaymentMethod() {}

    public void clearSensitive() {}

    void setSaves(boolean saved) { this.saved = saved; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public boolean isSaved() { return saved; }
    public abstract ProviderType providerType();
    public abstract void validate(String cvc);
    public String getToken() { return token; }
    public boolean hasToken() { return token != null && !token.isBlank(); }
}


