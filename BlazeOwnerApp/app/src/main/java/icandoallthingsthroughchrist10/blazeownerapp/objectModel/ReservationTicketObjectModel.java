package icandoallthingsthroughchrist10.blazeownerapp.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ReservationTicketObjectModel {
    private String tenantId;
    private @ServerTimestamp
    Date timeStamp;
    private String ownerId;
    private String key;
    private String payment;
    private String transactionCode;
    private String paymentMethod;
    private String additionalInformation;
    private String status;

    public ReservationTicketObjectModel(){

    }
    public ReservationTicketObjectModel(String tenantId,
                                        String ownerId,
                                        String payment,
                                        String transactionCode,
                                        String additionalInformation,String status){

        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.payment = payment;
        this.transactionCode = transactionCode;
        this.additionalInformation = additionalInformation;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPayment() {
        return payment;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

}

