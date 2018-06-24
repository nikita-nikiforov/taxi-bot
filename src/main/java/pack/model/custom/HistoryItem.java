package pack.model.custom;

import pack.model.HistoryResponse;
import pack.model.ProductResponse;
import pack.model.ReceiptResponse;
import java.util.Optional;

// Class extends History from HistoryResponse and add POJO fields
// in addition to their String _id
public class HistoryItem extends HistoryResponse.History {
    private ProductResponse.Product product;
    private Optional<ReceiptResponse> receipt;

    public HistoryItem() {
    }

    public HistoryItem(HistoryResponse.History history) {
        this.status = history.getStatus();
        this.distance = history.getDistance();
        this.start_time = history.getStart_time();
        this.start_city = history.getStart_city();
        this.end_time = history.getEnd_time();
        this.request_time = history.getRequest_time();
    }

    public ProductResponse.Product getProduct() {
        return product;
    }

    public void setProduct(ProductResponse.Product product) {
        this.product = product;
    }

    public Optional<ReceiptResponse> getReceipt() {
        return receipt;
    }

    public void setReceipt(Optional<ReceiptResponse> receipt) {
        this.receipt = receipt;
    }
}
