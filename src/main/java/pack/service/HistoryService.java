package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.HistoryResponse;
import pack.model.ProductResponse;
import pack.model.ReceiptResponse;
import pack.model.custom.HistoryItem;
import pack.service.api.UberApiService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {
    @Autowired
    private UberApiService uberApiService;

    // Get custom HistoryItems
    public List<HistoryItem> getHistoryItemList(User user) {
        List<HistoryItem> result = new ArrayList<>();
        // Get elements of HistoryResponse
        List<HistoryResponse.History> responseList = uberApiService.getHistoryList(user);
        responseList.forEach(h -> {
            HistoryItem historyItem = new HistoryItem(h);
            // Get corresponding Product and Receipt
            ProductResponse.Product product = uberApiService.getProductById(user, h.getProduct_id());
            Optional<ReceiptResponse> receipt = uberApiService.getReceiptResponse(user, h.getRequest_id());
            // Set them to HistoryItem
            historyItem.setProduct(product);
            historyItem.setReceipt(receipt);
            // Add to HistoryItem list
            result.add(historyItem);
        });
        return result;
    }
}
