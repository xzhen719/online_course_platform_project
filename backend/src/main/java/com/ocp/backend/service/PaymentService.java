package com.ocp.backend.service;

import com.ocp.backend.entity.Order;
import com.ocp.backend.entity.OrderStatus;
import com.ocp.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final EnrollmentService enrollmentService;
    private final CouponService couponService;

    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.payment-action-url}")
    private String paymentUrl;

    @Value("${ecpay.backend-url}")
    private String backendUrl;

    // 產生綠界付款表單 HTML
    public String createEcpayForm(Long orderId) {
        // 1. 找訂單
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 1.1 每次重新產生 MerchantTradeNo (避免重複提交被 ECPay 拒絕)
        String newTradeNo = "OCP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", order.getId());
        order.setMerchantTradeNo(newTradeNo);
        orderRepository.save(order);

        // 2. 準備綠界需要的參數 (使用 TreeMap 自動依 Key 排序，綠界要求)
        TreeMap<String, String> params = new TreeMap<>();

        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", order.getMerchantTradeNo());

        // 交易時間格式必須是 yyyy/MM/dd HH:mm:ss
        String tradeDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        params.put("MerchantTradeDate", tradeDate);

        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(order.getFinalAmount()));
        params.put("TradeDesc", "OCP Online Course"); // 交易描述
        // ItemName格式: 課程名稱 $價格#課程名稱2 $價格2
        params.put("ItemName",
                order.getItems().stream()
                        .map(item -> item.getCourse().getName() + " $" + item.getPriceAtPurchase())
                        .collect(Collectors.joining("#")));

        // 綠界交易完成後，瀏覽器要跳轉回哪裡？ (前端頁面)
        // 假設你的前端在 localhost:5173，我們跳回一個 "付款結果" 頁
        params.put("ClientBackURL", "http://localhost:5173/payment/result");

        params.put("ReturnURL", backendUrl + "/api/payment/callback");

        params.put("ChoosePayment", "ALL"); // 信用卡、ATM 都開
        params.put("EncryptType", "1");

        // 3. 產生檢查碼 (CheckMacValue)
        String checkMacValue = generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);

        // 4. 產生 HTML Form (自動送出)
        StringBuilder html = new StringBuilder();
        html.append("<form id='ecpay-form' action='").append(paymentUrl).append("' method='POST'>");

        for (String key : params.keySet()) {
            html.append("<input type='hidden' name='").append(key).append("' value='").append(params.get(key))
                    .append("' />");
        }

        // 加入一段 JavaScript 自動 submit
        html.append("</form>");
        html.append("<script>document.getElementById('ecpay-form').submit();</script>");

        return html.toString();
    }

    // 產生 CheckMacValue
    // 邏輯：排序參數 -> 串接 Key & IV -> URL Encode -> 轉小寫 -> SHA256 -> 轉大寫
    private String generateCheckMacValue(TreeMap<String, String> params) {
        try {
            String raw = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            String hashRaw = "HashKey=" + hashKey + "&" + raw + "&HashIV=" + hashIv;

            // 綠界特殊的 URL Encode 規則
            String urlEncoded = URLEncoder.encode(hashRaw, StandardCharsets.UTF_8)
                    .toLowerCase()
                    .replace("%2d", "-")
                    .replace("%5f", "_")
                    .replace("%2e", ".")
                    .replace("%21", "!")
                    .replace("%2a", "*")
                    .replace("%28", "(")
                    .replace("%29", ")");

            return DigestUtils.sha256Hex(urlEncoded).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CheckMacValue");
        }
    }

    /**
     * 驗證 ECPay 回傳的 CheckMacValue
     * 防止偽造 callback 攻擊
     * 
     * @param params ECPay 回傳的所有參數
     * @return true 如果驗證成功
     */
    public boolean verifyCheckMacValue(Map<String, String> params) {
        try {
            // 1. 取出 ECPay 回傳的 CheckMacValue
            String receivedCheckMac = params.get("CheckMacValue");
            if (receivedCheckMac == null || receivedCheckMac.isEmpty()) {
                log.warn("CheckMacValue is missing from callback");
                return false;
            }

            // 2. 複製參數並移除 CheckMacValue (計算時不包含它)
            TreeMap<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.remove("CheckMacValue");

            // 3. 用我們的 Key 重新計算 CheckMacValue
            String calculatedCheckMac = generateCheckMacValue(sortedParams);

            // 4. 比對（忽略大小寫）
            boolean isValid = receivedCheckMac.equalsIgnoreCase(calculatedCheckMac);

            if (!isValid) {
                log.warn("CheckMacValue mismatch! Received: {}, Calculated: {}",
                        receivedCheckMac, calculatedCheckMac);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error verifying CheckMacValue", e);
            return false;
        }
    }

    @Transactional
    public void processPaymentResult(String merchantTradeNo) {

        Order order = orderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new RuntimeException("Order not found" + merchantTradeNo));

        // 檢查訂單狀態
        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("Order OrderId{} has already been paid", order.getId());
            return;
        }
        // 更新訂單狀態
        order.setStatus(OrderStatus.PAID);
        order.setPaymentDate(LocalDateTime.now());

        // 如果coupon存在的話, 且尚未使用過
        if (order.getCoupon() != null && !order.getCoupon().isUsed()) {
            couponService.markCouponAsUsed(order.getCoupon().getCode());
        }

        orderRepository.save(order);
        log.info("Payment of Order {} is successful", order.getId());

        // 用try-catch -> 避免enrollment失敗時, 訂單付款狀態PAID不會被rollback
        try {
            enrollmentService.enrollUserInCourse(order.getId());
        } catch (Exception e) {
            log.error("Enrollment of Order {} failed", order.getId(), e);
        }

    }
}
